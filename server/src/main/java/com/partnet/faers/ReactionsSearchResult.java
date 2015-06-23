package com.partnet.faers;

import java.util.Date;
import java.util.List;

/**
 * Collects search results for queries regarding a particular drugs reactions.  Provides a list of reactions found that 
 * match the query parameters, counts for how many events occurred for each reaction, and calculated PRR 
 * (proportional reporting ratio) and ROR (reporting odds ratio) reporting ratios.
 * 
 * @author svanderveen
 *
 */
public class ReactionsSearchResult 
{
  private final MetaData meta;
  private final List<ReactionCount> results;
  private final Drug drug;
  
  
  public ReactionsSearchResult(MetaData meta, List<ReactionCount> results, Drug drug) {
    super();
    this.meta = meta;
    this.results = results;
    this.drug = drug;
  }

  public static class ReactionCount
  {
    private String term;
    private int count;
    private Double prr;
    private Double ror;
    
    public ReactionCount(String term, int count) {
      super();
      this.term = term;
      this.count = count;
      this.prr = computePRR();
      this.ror = computeROR();
      
    }

    public int getCount() {
      return count;
    }

    public void incrementCount() {
      count++;
    }

    /**
     *  PRR = (m/n)/( (M-m)/(N-n) )
     *  Where
     *   m = #reports with drug and event
     *   n = #reports with drug
     *   M = #reports with event in database
     *   N = #reports in database
     */
    public final Double computePRR()
    {
      //TODO implement
      return null;
    }
    
    
    
    /**
     * ROR = (m/d)/(M/D)
     * Where
     *  m = #reports with drug and event
     *  d = n-m
     *  M = #reports with event in database
     *  D = N-M
     * 
     */
    public final Double computeROR()
    {
      //TODO implement
      return null;
    }


  }
  
  public static class MetaData
  {
    private final String disclaimer ="openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.";
    private final String license = "http://open.fda.gov/license";
    private final Date lastUpdated;
    
    public MetaData(Date lastUpdated) {
      super();
      this.lastUpdated = lastUpdated;
    }
  }
}
