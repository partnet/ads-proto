package com.partnet.faers;

/**
 * Data holder for drug information parsed from FAERS xml. Used to serialize to JSON
 *
 * @author jwhite
 */
public class Drug {
  public Integer drugcharacterization;
  public String medicinalproduct;
  public String drugindication;
}
