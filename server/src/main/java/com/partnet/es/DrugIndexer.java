package com.partnet.es;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Indexes drug names from FAERS data for client autocomplete lookup
 *
 * @author jwhite
 */
public class DrugIndexer extends DefaultHandler {

  public int count = 0;
  public Map<String, DrugCounter> drugCountMap = new HashMap<>();

  private boolean medicinalproduct;

  public DrugIndexer() {
  }

  @Override
  public void startElement(String uri, String localName,String qName,
                           Attributes attributes) throws SAXException {

    switch (qName) {
      case "medicinalproduct":
        medicinalproduct = true;
        count++;
        if (count % 1000 == 0) System.out.println("count: " + count);
        break;
      default:
        // do nothing
        break;
    }

  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

    if (medicinalproduct) {
      String drugName = new String(ch, start, length);
      drugName = normalize(drugName);
      DrugCounter drugCounter = drugCountMap.get(drugName);
      if (drugCounter == null) {
        drugCounter = new DrugCounter(drugName);
        drugCountMap.put(drugName, drugCounter);
      }
      drugCounter.count++;
      medicinalproduct = false;
    }
  }

  private static String normalize(String drugName) {

    drugName = drugName.replace(".", " ");
    drugName = drugName.replace("^", "");
    drugName = drugName.replace("+", " ");
    drugName = drugName.trim();
    drugName = drugName.replaceAll(" +", " ");

    return drugName;
  }


  private static class DrugCounter implements Comparable<DrugCounter> {
    public String drug;
    public int count;

    public DrugCounter(String drug) {
      this.drug = drug;
    }

    @Override
    public int compareTo(DrugCounter o) {
      if (this.equals(o)) return 0;
      else return this.count - o.count;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DrugCounter that = (DrugCounter) o;

      if (count != that.count) return false;
      return drug.equals(that.drug);

    }

    @Override
    public int hashCode() {
      int result = drug.hashCode();
      result = 31 * result + count;
      return result;
    }

    @Override
    public String toString() {
      return "DrugCounter{" +
          "drug='" + drug + '\'' +
          ", count=" + count +
          '}';
    }
  }


  public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

    final String INPUT_FILE = "/home/jwhite/faers/xml/ADR14Q4.xml";

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    String serverAddress = "192.168.7.2";
    Integer serverPort = 9300;
    String clusterName="product-data";
    String indexName="safetyreports";
    String docType="drug";

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();

    final ElasticSearchClient client = new ElasticSearchClient(serverAddress, serverPort,
        clusterName, indexName, docType);

    final DrugIndexer handler = new DrugIndexer();

    final long start = System.currentTimeMillis();
    saxParser.parse(INPUT_FILE, handler);
    final long end = System.currentTimeMillis();

    System.out.println("total count: " + handler.count);

    long time = (end - start) / 1000;

    System.out.println("Total seconds: " + time);

    System.out.println("Unique medicinalproduct: " + handler.drugCountMap.size());

    final ArrayList<DrugCounter> values = new ArrayList<>(handler.drugCountMap.values());
    Collections.sort(values);

    List<String> indexableDrugNames = new ArrayList<>();
    int countThreshold = 5;

    for(DrugCounter counter : values) {
      if (counter.count >= countThreshold) {
        indexableDrugNames.add(counter.drug);
      }
    }

    System.out.println("Count of indexable drugs: " + indexableDrugNames.size());

    client.createDrugDocMapping();

    for (String drugName : indexableDrugNames) {
      client.indexDrugName(drugName);
    }

    client.closeClient();
  }



}
