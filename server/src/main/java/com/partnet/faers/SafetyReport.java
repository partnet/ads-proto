package com.partnet.faers;

/**
 * Data holder for safety report information parsed from FAERS xml. Used to serialize to JSON
 *
 * @author jwhite
 */
public class SafetyReport {
  public Integer safetyreportid;
  public String occurcountry;
  public Integer serious;
  public Integer seriousnessdeath;
  public Integer seriousnesslifethreatening;
  public Integer seriousnesshospitalization;
  public Integer seriousnessdisabling;
  public Integer seriousnesscongenitalanomali;
  public Integer seriousnessother;


  public Patient patient;
}
