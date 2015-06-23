package com.partnet.es;

import com.google.gson.Gson;
import com.partnet.faers.Drug;
import com.partnet.faers.Reaction;
import com.partnet.faers.ReactionsSearchResult;
import com.partnet.faers.SafetyReport;
import com.partnet.util.Range;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;

/**
 * Elastic Search implementation of search engine client, creates facade over Elastic Search specific api.
 *
 * @author jwhite
 */
@ApplicationScoped
public class ElasticSearchClient {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClient.class);

  private String serverAddress;
  private Integer serverPort;
  private String clusterName;
  private Client client;
  private String indexName;
  private String docType;

  public ElasticSearchClient() {
    // todo: config these:
    this.serverAddress = "192.168.7.2";
    this.serverPort = 9300;
    this.clusterName="product-data";
    this.indexName="safetyreports";
    this.docType="safetyreport";
  }

  public ElasticSearchClient(String serverAddress, Integer serverPort, String clusterName, String indexName, String docType) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
    this.clusterName = clusterName;
    this.indexName = indexName;
    this.docType = docType;

    setUp();
  }

  @PostConstruct
  public void setUp() {
    Settings settings = ImmutableSettings.settingsBuilder()
        .put("cluster.name", clusterName).build();

    client = new TransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress(serverAddress, serverPort));

    final ClusterHealthResponse clusterIndexHealths = client.admin().cluster().health(new ClusterHealthRequest()).actionGet();
    final ClusterHealthStatus status = clusterIndexHealths.getStatus();
    LOG.info("Cluster Health Status: " + status.toString());
  }

  @PreDestroy
  public void closeClient() {
    LOG.info("Closing Elastic Search Transport client");
    client.close();
  }

  public boolean indexExists() {
    return client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
  }

  public void indexSafetyReport(SafetyReport safetyReport) {
    final IndexRequest indexRequest = new IndexRequest(indexName, docType, safetyReport.safetyreportid.toString());
    final String json = new Gson().toJson(safetyReport);
    indexRequest.source(json);
    final IndexResponse indexResponse = client.index(indexRequest).actionGet();

    LOG.debug("safety report indexed with id: " + indexResponse.getId());
  }

  public SafetyReport getSafetyReport(String id) {
    GetResponse response = client.prepareGet(indexName, docType, id)
        .setOperationThreaded(false)
        .execute()
        .actionGet();

    LOG.debug("safety report retrieved:\n" + response.getSourceAsString());

    return new Gson().fromJson(response.getSourceAsString(), SafetyReport.class);
  }

  public List<SafetyReport> getSafetyReports(String medicinalproduct, Integer patientsex,
                                             Range ageRange, Range weightRange) {

    BoolFilterBuilder filter = boolFilter();

    if (patientsex != null) {
      filter.must(termFilter("patientsex", patientsex));
    }
    if (ageRange != null) {
      filter.must(rangeFilter("patientonsetage").from(ageRange.getLow()).to(ageRange.getHigh()));
    }
    if (weightRange != null) {
      filter.must(rangeFilter("patientweight").from(weightRange.getLow()).to(weightRange.getHigh()));
    }

    QueryBuilder qb = QueryBuilders.termQuery("medicinalproduct", medicinalproduct);

    SearchResponse searchResponse = client.prepareSearch(indexName)
        .setTypes(docType)
        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
        .setQuery(qb)
        .setScroll(new TimeValue(60000))
        .setSize(100)
        .setPostFilter(filter.hasClauses() ? filter : null)
        .execute()
        .actionGet();

    LOG.info("Total hits: " + searchResponse.getHits().getTotalHits());


    List<SafetyReport> safetyReports = new ArrayList<>();

    while (true) {
      for (SearchHit hit : searchResponse.getHits().getHits()) {
        final SafetyReport safetyReport = new Gson().fromJson(hit.getSourceAsString(), SafetyReport.class);
        safetyReports.add(safetyReport);
      }

      searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();

      if (searchResponse.getHits().getHits().length == 0) {
        break;
      }
    }

    return safetyReports;
  }

  public ReactionsSearchResult getReactions(String medicinalproduct, Integer patientsex,
                                            Range ageRange, Range weightRange) {

    final List<SafetyReport> safetyReports = getSafetyReports(medicinalproduct, patientsex, ageRange, weightRange);

    Map<String, ReactionsSearchResult.ReactionCount> reactionCounts = new HashMap<>();

    for(SafetyReport safetyReport : safetyReports) {
      for (Reaction reaction : safetyReport.patient.reactions) {
        ReactionsSearchResult.ReactionCount reactionCount = reactionCounts.get(reaction.reactionmeddrapt);
        if (reactionCount == null) {
          reactionCount = new ReactionsSearchResult.ReactionCount(reaction.reactionmeddrapt, 0);
          reactionCounts.put(reaction.reactionmeddrapt, reactionCount);
        }
        reactionCount.incrementCount();
      }
    }

    ReactionsSearchResult.MetaData meta = new ReactionsSearchResult.MetaData(new Date());
    Drug drug = new Drug(medicinalproduct);

    final List<ReactionsSearchResult.ReactionCount> reactionCnts = new ArrayList<>(reactionCounts.values());

    // sort greatest count to least
    Collections.sort(reactionCnts, (o1, o2) -> o2.getCount() - o1.getCount());

    return new ReactionsSearchResult(meta, reactionCnts, drug);
  }


}
