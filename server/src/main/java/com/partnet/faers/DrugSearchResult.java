
package com.partnet.faers;

import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * @author svanderveen
 *
 */
public class DrugSearchResult 
{
  private final Drug drug;
  private final List<IndicationCount> indications;
  private final Double averageTreatmentDuration;
  private final Double minTreatmentDuration;
  private final Double maxTreamentDuration;
  
  public DrugSearchResult(Drug drug, List<IndicationCount> indications, List<Double> treatmentDurations) {
    super();
    this.drug = drug;
    this.indications = indications;
    DoubleSummaryStatistics stats = treatmentDurations.stream().mapToDouble((x) -> x).summaryStatistics();
    this.averageTreatmentDuration = stats.getAverage();
    this.maxTreamentDuration = stats.getMax();
    this.minTreatmentDuration = stats.getMin();
  }
  
  public static class IndicationCount
  {
    private final String indication;
    private final int count;
    
    public IndicationCount(String indication, int count) 
    {
      this.indication = indication;
      this.count = count;
    }
    
  }
}
