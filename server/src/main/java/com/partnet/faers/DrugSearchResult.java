package com.partnet.faers;

import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Search result holder for queries on drugs with indication counts
 *
 * @author jwhite
 */
public class DrugSearchResult {

  private String medicinalproduct;
  private List<IndicationCount> indicationCounts;
  private Double averageTreatmentDuration;
  private Double minTreatmentDuration;
  private Double maxTreatmentDuration;

  public DrugSearchResult(String medicinalproduct, List<IndicationCount> indicationCounts, List<Double> treatmentDurations) {
    this.medicinalproduct = medicinalproduct;
    this.indicationCounts = indicationCounts;
    if(treatmentDurations != null && !treatmentDurations.isEmpty()) {
      DoubleSummaryStatistics stats = treatmentDurations.stream().mapToDouble((x) -> x).summaryStatistics();
      this.averageTreatmentDuration = stats.getAverage();
      this.maxTreatmentDuration = stats.getMax();
      this.minTreatmentDuration = stats.getMin();
    }
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
