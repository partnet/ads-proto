package com.partnet.es;

import com.partnet.faers.FaersXmlReader;
import com.partnet.faers.SafetyReport;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Utilizes FaersXmlReader functionality to read and convert safety report xml data to JSON documents
 * which are then stored in the given Elastic Search index.
 *
 * @author jwhite
 */
public class ElasticSearchIndexer extends FaersXmlReader.SafetyReportHandler {

  private ElasticSearchClient searchClient;

  public ElasticSearchIndexer(ElasticSearchClient searchClient) {
    this.searchClient = searchClient;
  }

  @Override
  public void handleSafetyReport(SafetyReport safetyReport) {
    searchClient.indexSafetyReport(safetyReport);
  }


  public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

    final String INPUT_FILE = "/home/jwhite/faers/xml/ADR14Q4.xml";

    String serverAddress = "192.168.7.2";
    Integer serverPort = 9300;
    String clusterName="product-data";
    String indexName="safetyreports";
    String docType="safetyreport";

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();

    final ElasticSearchClient client = new ElasticSearchClient(serverAddress, serverPort,
        clusterName, indexName, docType);

    final ElasticSearchIndexer handler = new ElasticSearchIndexer(client);

    final long start = System.currentTimeMillis();
    saxParser.parse(INPUT_FILE, handler);
    final long end = System.currentTimeMillis();

    client.closeClient();

    System.out.println("total count: " + handler.count);

    long time = (end - start) / 1000;

    System.out.println("Total seconds: " + time);

  }
}
