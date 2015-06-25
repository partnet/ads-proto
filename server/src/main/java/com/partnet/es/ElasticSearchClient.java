package com.partnet.es;

import com.google.gson.Gson;
import com.partnet.config.Config;
import com.partnet.faers.Drug;
import com.partnet.faers.DrugSearchResult;
import com.partnet.faers.Reaction;
import com.partnet.faers.ReactionsSearchResult;
import com.partnet.faers.SafetyReport;
import com.partnet.util.Range;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.count.CountResponse;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Elastic Search implementation of search engine client, creates facade over Elastic Search specific api.
 *
 * @author jwhite
 */
@ApplicationScoped
public class ElasticSearchClient {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClient.class);

  private Client client;

  @Inject
  @Config
  private String serverAddress;

  @Inject @Config
  private Integer serverPort;

  @Inject @Config
  private String clusterName;

  @Inject @Config
  private String indexName;

  @Inject @Config
  private String docType;


  public ElasticSearchClient() {
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
    return getSafetyReports(medicinalproduct, null, patientsex, ageRange, weightRange);
  }


  public List<SafetyReport> getSafetyReports(String medicinalproduct, String reactionmeddrapt, Integer patientsex,
                                             Range ageRange, Range weightRange) {

    final BoolQueryBuilder qb = boolQuery();

    if (medicinalproduct != null) {
      qb.must(termQuery("medicinalproduct", medicinalproduct));
    }
    if (reactionmeddrapt != null) {
      qb.must(termQuery("reactionmeddrapt", reactionmeddrapt));
    }

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

    final long countAllReports = getReportCount(null, null, patientsex, ageRange, weightRange);
    final long countReportsWithDrug = getReportCount(medicinalproduct, null, patientsex, ageRange, weightRange);

    final List<SafetyReport> safetyReports = getSafetyReports(medicinalproduct, patientsex, ageRange, weightRange);

    Map<String, ReactionsSearchResult.ReactionCount> reactionCounts = new HashMap<>();

    Map<String, Map<Integer, ReactionsSearchResult.OutcomeCount>> outcomeMap = new HashMap<>();

    for(SafetyReport safetyReport : safetyReports) {
      for (Reaction reaction : safetyReport.patient.reactions) {
        ReactionsSearchResult.ReactionCount reactionCount = reactionCounts.get(reaction.reactionmeddrapt);
        if (reactionCount == null) {
          final long countReportsWithDrugAndReaction = getReportCount(medicinalproduct, reaction.reactionmeddrapt, patientsex, ageRange, weightRange);
          final long countReportsWithReaction = getReportCount(null, reaction.reactionmeddrapt, patientsex, ageRange, weightRange);
          reactionCount = new ReactionsSearchResult.ReactionCount(reaction.reactionmeddrapt, countReportsWithDrugAndReaction, countReportsWithDrug, countReportsWithReaction, countAllReports);
          reactionCounts.put(reaction.reactionmeddrapt, reactionCount);
        }
        reactionCount.incrementCount();

        Map<Integer, ReactionsSearchResult.OutcomeCount> outcomeCountMap = outcomeMap.get(reaction.reactionmeddrapt);
        if (outcomeCountMap == null) {
          outcomeCountMap = new HashMap<>();
          outcomeMap.put(reaction.reactionmeddrapt, outcomeCountMap);
        }
        ReactionsSearchResult.OutcomeCount outcomeCount = outcomeCountMap.get(reaction.reactionoutcome);
        if (outcomeCount == null) {
          outcomeCount =  new ReactionsSearchResult.OutcomeCount(reaction.reactionoutcome, 0);
          outcomeCountMap.put(reaction.reactionoutcome, outcomeCount);
        }
        outcomeCount.incrementCount();
      }
    }

    // put outcome counts as list in the reaction counts
    final Set<Map.Entry<String, Map<Integer, ReactionsSearchResult.OutcomeCount>>> entries = outcomeMap.entrySet();
    for (Map.Entry<String, Map<Integer, ReactionsSearchResult.OutcomeCount>> entry : entries) {
      final String key = entry.getKey();
      final Map<Integer, ReactionsSearchResult.OutcomeCount> value = entry.getValue();

      final List<ReactionsSearchResult.OutcomeCount> outcomeCountList = new ArrayList<>(value.values());
      Collections.sort(outcomeCountList, (o1, o2) -> (int)(o2.getCount() - o1.getCount()));

      final ReactionsSearchResult.ReactionCount reactionCount = reactionCounts.get(key);
      reactionCount.setOutcomes(outcomeCountList);
    }



    ReactionsSearchResult.MetaData meta = new ReactionsSearchResult.MetaData(new Date());
    Drug drug = new Drug(medicinalproduct);

    final List<ReactionsSearchResult.ReactionCount> reactionCnts = new ArrayList<>(reactionCounts.values());

    // sort greatest count to least
    Collections.sort(reactionCnts, (o1, o2) -> (int)(o2.getCount() - o1.getCount()));

    return new ReactionsSearchResult(meta, reactionCnts, drug);
  }

  public String getDrugs() {

    SearchResponse searchResponse = client.prepareSearch(indexName)
        .setTypes(docType)
        .addAggregation(AggregationBuilders.terms("agg").field("medicinalproduct").subAggregation(AggregationBuilders.topHits("top")))
        .execute()
        .actionGet();

    LOG.info("Total hits: " + searchResponse.getHits().getTotalHits());

    Terms agg = searchResponse.getAggregations().get("agg");

    for (Terms.Bucket entry : agg.getBuckets()) {
      String key = entry.getKey();                    // bucket key
      long docCount = entry.getDocCount();            // Doc count
      LOG.info("key [{}], doc_count [{}]", key, docCount);

      // We ask for top_hits for each bucket
      TopHits topHits = entry.getAggregations().get("top");
      for (SearchHit hit : topHits.getHits().getHits()) {
        LOG.info(" -> id [{}], _source [{}]", hit.getId(), hit.getSourceAsString());
      }
    }


    return "nothing";
  }

  public DrugSearchResult getDrugs(String medicinalproduct) {
    final List<SafetyReport> safetyReports = getSafetyReports(medicinalproduct, null, null, null);

    Map<String, DrugSearchResult.IndicationCount> indicationCounts = new HashMap<>();
    List<Double> treatmentDurations = new ArrayList<>();

    for(SafetyReport safetyReport : safetyReports) {
      for (Drug drug : safetyReport.patient.drugs) {
        DrugSearchResult.IndicationCount indicationCount = indicationCounts.get(drug.drugindication);
        if (indicationCount == null) {
          indicationCount = new DrugSearchResult.IndicationCount(drug.drugindication);
          indicationCounts.put(drug.drugindication, indicationCount);
        }
        indicationCount.incrementCount();
        if (isFiniteNumber(drug.drugtreatmentduration)) {
          treatmentDurations.add(new Double(drug.drugtreatmentduration));
        }
      }
    }

    final List<DrugSearchResult.IndicationCount> indicationCountsList = new ArrayList<>(indicationCounts.values());

    // sort greatest count to least
    Collections.sort(indicationCountsList, (o1, o2) -> o2.getCount() - o1.getCount());
    return new DrugSearchResult(medicinalproduct, indicationCountsList, treatmentDurations);
  }

  private static boolean isFiniteNumber(Float val) {
    return (val != null && !val.isNaN() && !val.isInfinite());
  }


  public long getReportCount(String medicinalproduct, String reactionmeddrapt, Integer patientsex,
                             Range ageRange, Range weightRange) {

    final BoolQueryBuilder qb = boolQuery();

    if (medicinalproduct != null) {
      qb.must(termQuery("medicinalproduct", normalize(medicinalproduct)));
    }
    if (reactionmeddrapt != null) {
      qb.must(termQuery("reactionmeddrapt", normalize(reactionmeddrapt)));
    }
    if (patientsex != null) {
      qb.must(termQuery("patientsex", patientsex));
    }
    if (ageRange != null) {
      qb.must(rangeQuery("patientonsetage").from(ageRange.getLow()).to(ageRange.getHigh()));
    }
    if (weightRange != null) {
      qb.must(rangeQuery("patientweight").from(weightRange.getLow()).to(weightRange.getHigh()));
    }

    CountResponse response = client.prepareCount(indexName)
        .setQuery(qb)
        .execute()
        .actionGet();

    return response.getCount();
  }

  private String normalize(String str) {
    return str.toLowerCase();
  }
}
