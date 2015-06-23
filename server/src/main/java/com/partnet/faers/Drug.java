package com.partnet.faers;

/**
 * Data holder for drug information parsed from FAERS xml. Used to serialize to JSON
 *
 * @author jwhite
 */
public class Drug {

  public Integer drugcharacterization;
  public String medicinalproduct;
  public String activesubstancename;
  public Float drugstructuredosagenumb;
  public Integer drugstructuredosageunit;
  public String drugindication;
  public Integer drugstartdateformat;
  public Integer drugstartdate;
  public Integer drugenddateformat;
  public Integer drugenddate;
  public Float drugtreatmentduration;
  public Integer drugtreatmentdurationunit;

  public Drug()
  {
    
  }
  
  public Drug(String medicinalproduct) {
    this.medicinalproduct = medicinalproduct;
  }
}
