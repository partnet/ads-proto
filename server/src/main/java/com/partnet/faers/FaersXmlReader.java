package com.partnet.faers;

import com.google.gson.Gson;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Sax parser that reads FAERS xml data downloaded from http://www.fda.gov/
 *
 * SafetyReportHandler generates a SafetyReport object that can be used to serialize
 * to JSON or other desired format.
 *
 * @author jwhite
 */
public class FaersXmlReader {

  public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

    final String INPUT_FILE = "/home/jwhite/faers/xml/ADR14Q4.xml";
    final String OUTPUT_FILE = "/home/jwhite/faers/xml/ADR14Q4.json";

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();

    final PrintWriter writer = new PrintWriter(OUTPUT_FILE, "UTF-8");

    final SafetyReportHandler handler = new JsonConverter(writer);

    final long start = System.currentTimeMillis();

    try {
      writer.println("{\"safetyreports\":[");
      saxParser.parse(INPUT_FILE, handler);
      writer.println("]}");
    }
    finally {
      writer.close();
    }
    final long end = System.currentTimeMillis();

    System.out.println("total count: " + handler.count);

    long time = (end - start) / 1000;

    System.out.println("Total seconds: " + time);


  }

  public static class JsonConverter extends SafetyReportHandler {
    private PrintWriter writer;

    public JsonConverter(PrintWriter writer) {
      this.writer = writer;
    }

    @Override
    public void handleSafetyReport(SafetyReport safetyReport) {
      final String json = new Gson().toJson(safetyReport);
      writer.println(json + ",");
    }

  }

  public static abstract class SafetyReportHandler extends DefaultHandler {

    public int count = 0;

    private SafetyReport sr;

    private Reaction currentReaction;
    private Drug currentDrug;

    // flags for start of these elements
    private boolean safetyreportid;
    private boolean serious;
    private boolean seriousnesshospitalization;
    private boolean seriousnessdeath;
    private boolean patientonsetage;
    private boolean patientweight;
    private boolean patientsex;
    private boolean reactionmeddrapt;
    private boolean reactionoutcome;
    private boolean drugcharacterization;
    private boolean medicinalproduct;
    private boolean drugindication;



    @Override
    public void startElement(String uri, String localName,String qName,
                             Attributes attributes) throws SAXException {


      switch (qName) {
        case "safetyreport":
          count++;
          sr = new SafetyReport();
          if (count % 1000 == 0) System.out.println("count: " + count);
          break;
        case "safetyreportid":
          safetyreportid = true;
          break;
        case "serious":
          serious = true;
          break;
        case "seriousnesshospitalization":
          seriousnesshospitalization = true;
          break;
        case "seriousnessdeath":
          seriousnessdeath = true;
          break;
        case "patient":
          sr.patient = new Patient();
          break;
        case "patientonsetage":
          patientonsetage = true;
          break;
        case "patientweight":
          patientweight = true;
          break;
        case "patientsex":
          patientsex = true;
          break;
        case "reaction":
          currentReaction = new Reaction();
          sr.patient.reactions.add(currentReaction);
          break;
        case "reactionmeddrapt":
          reactionmeddrapt = true;
          break;
        case "reactionoutcome":
          reactionoutcome = true;
          break;
        case "drug":
          currentDrug = new Drug();
          sr.patient.drugs.add(currentDrug);
          break;
        case "drugcharacterization":
          drugcharacterization = true;
          break;
        case "medicinalproduct":
          medicinalproduct = true;
          break;
        case "drugindication":
          drugindication = true;
          break;
      }


    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

      String value = new String(ch, start, length);

      if (safetyreportid) {
        sr.safetyreportid = Integer.valueOf(value);
        safetyreportid = false;
      }
      else if (serious) {
        sr.serious = Integer.valueOf(value);
        serious = false;
      }
      else if (seriousnesshospitalization) {
        sr.seriousnesshospitalization = Integer.valueOf(value);
        seriousnesshospitalization = false;
      }
      else if (seriousnessdeath) {
        sr.seriousnessdeath = Integer.valueOf(value);
        seriousnessdeath = false;
      }
      else if (patientonsetage) {
        sr.patient.patientonsetage = Float.valueOf(value);
        patientonsetage = false;
      }
      else if (patientweight) {
        sr.patient.patientweight = Float.valueOf(value);
        patientweight = false;
      }
      else if (patientsex) {
        sr.patient.patientsex = Integer.valueOf(value);
        patientsex = false;
      }
      else if (reactionmeddrapt) {
        currentReaction.reactionmeddrapt = value;
        reactionmeddrapt = false;
      }
      else if (reactionoutcome) {
        currentReaction.reactionoutcome = Integer.valueOf(value);
        reactionoutcome = false;
      }
      else if (drugcharacterization) {
        currentDrug.drugcharacterization = Integer.valueOf(value);
        drugcharacterization = false;
      }
      else if (medicinalproduct) {
        currentDrug.medicinalproduct = value;
        medicinalproduct = false;
      }
      else if (drugindication) {
        currentDrug.drugindication = value;
        drugindication = false;
      }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("safetyreport")) {
        handleSafetyReport(sr);
      }
    }

    public abstract void handleSafetyReport(SafetyReport safetyReport);
  }




}
