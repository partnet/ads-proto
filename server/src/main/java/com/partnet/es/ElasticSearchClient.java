package com.partnet.es;

import com.google.gson.Gson;
import com.partnet.faers.SafetyReport;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Elastic Search implementation of search engine client, creates facade over Elastic Search specific api.
 *
 * @author jwhite
 */
public class ElasticSearchClient {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClient.class);

  private Client client;
  private String indexName;
  private String docType;

  public ElasticSearchClient(String serverAddress, Integer serverPort, String clusterName, String indexName, String docType) {
    this.indexName = indexName;
    this.docType = docType;

    Settings settings = ImmutableSettings.settingsBuilder()
        .put("cluster.name", clusterName).build();

    client = new TransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress(serverAddress, serverPort));

    final ClusterHealthResponse clusterIndexHealths = client.admin().cluster().health(new ClusterHealthRequest()).actionGet();
    final ClusterHealthStatus status = clusterIndexHealths.getStatus();
    LOG.info("Cluster Health Status: " + status.toString());
  }

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


}
