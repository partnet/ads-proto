package com.partnet.faers;

/**
 * Data holder for safety report information parsed from FAERS xml. Used to serialize to JSON
 *
 * @author jwhite
 */
public class SafetyReport {
  public Integer safetyreportid;
  public Integer serious;
  public Integer seriousnesshospitalization;
  public Integer seriousnessdeath;

  public Patient patient;
}
