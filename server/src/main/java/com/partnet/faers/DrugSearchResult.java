package com.partnet.faers;

import java.util.List;

/**
 * Search result holder for queries on drugs with indication counts
 *
 * @author jwhite
 */
public class DrugSearchResult {

  private String medicinalproduct;
  private List<IndicationCount> indicationCounts;

  public DrugSearchResult(String medicinalproduct, List<IndicationCount> indicationCounts) {
    this.medicinalproduct = medicinalproduct;
    this.indicationCounts = indicationCounts;
  }

  public static class IndicationCount {
    private String drugindication;
    private int count;

    public IndicationCount(String drugindication) {
      this.drugindication = drugindication;
    }

    public void incrementCount() {
      count++;
    }

    public String getDrugindication() {
      return drugindication;
    }

    public int getCount() {
      return count;
    }
  }

}
