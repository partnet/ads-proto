package com.partnet.faers;

import java.util.Date;
import java.util.List;

/**
 * Collects search results for queries regarding a particular drugs reactions.  Provides a list of reactions found that 
 * match the query parameters, counts for how many events occurred for each reaction, and calculated PRR 
 * (proportional reporting ratio) and ROR (reporting odds ratio) reporting ratios. Each reaction also contains a listing of 
 * outcomes and their occurences for that reaction.
 * 
 * @author svanderveen
 *
 */
public class ReactionsSearchResult 
{
  private final MetaData meta;
  private final List<ReactionCount> reactions;
  private final Drug drug;
  
  
  public ReactionsSearchResult(MetaData meta, List<ReactionCount> reactions, Drug drug) {
    super();
    this.meta = meta;
    this.reactions = reactions;
    this.drug = drug;
  }

  public static class ReactionCount
  {
    List<OutcomeCount> outcomes;
    private final String reactionmeddrapt;
    private long count;
    
    /**
     * #reports with reaction event
     */
    private final long numReportsEvent;
    
    /**
     * #reports with drug
     */
    private final long numReportsDrug;
    private final Double prr;
    private final Double ror;

    public ReactionCount(String reactionmeddrapt, long numReportsDrugEvent, long numReportsDrug, long numReportsEvent,
                         long numReports)
    {
      this.reactionmeddrapt = reactionmeddrapt;
      this.count = numReportsDrugEvent;
      this.numReportsDrug = numReportsDrug;
      this.numReportsEvent = numReportsEvent;
      this.prr = computePRR(numReportsDrugEvent, numReportsDrug, numReportsEvent, numReports);
      this.ror = computeROR(numReportsDrugEvent, numReportsDrug, numReportsEvent, numReports);

    }

    public long getCount() {
      return count;
    }

    public void incrementCount() {
      count++;
    }

    public List<OutcomeCount> getOutcomes() { return outcomes; }

    public void setOutcomes(List<OutcomeCount> outcomes) { this.outcomes = outcomes; }

    /**
     * The proportional reporting ratio (PRR) is a simple way to get a measure of how common an adverse event 
     * for a particular drug is compared to how common the event is in the overall database.
     * A PRR > 1 for a drug-event combination indicates that a greater proportion of the reports for the drug are for the 
     * event than the proportion of events in the rest of the database. 
     * 
     * For example, a PRR of 2 for a drug event combination indicates that the proportion of reports for the drug-event 
     * combination is twice the proportion of the event in the overall database.
     * 
     *  PRR = (m/n)/((M-m)/(N-n))
     * @param numReportsDrugEvent m = #reports with drug and event
     * @param numReportsDrug n = #reports with drug
     * @param numReportsEvent M = #reports with event
     * @param numReports N = #reports in database
     * @return PRR
     */
    protected final Double computePRR(double numReportsDrugEvent, double numReportsDrug, double numReportsEvent, double numReports)
    {
      if(numReportsDrug == 0 || numReports == numReportsDrug || numReportsEvent == numReportsDrugEvent) return 0d;
      
      double prr = (numReportsDrugEvent / numReportsDrug) / ((numReportsEvent - numReportsDrugEvent) / (numReports - numReportsDrug));
      return prr;
    }
    
    
    
    /**
     * Reporting Odds Ratio, similar to PRR.
     * 
     * ROR = (m/d)/(M/D)
     * Where
     *  d = n-m
     *  D = N-M
     *  
     * @param numReportsDrugEvent m = #reports with drug and event
     * @param numReportsDrug n = #reports with drug
     * @param numReportsEvent M = #reports with event in database
     * @param numReports N = #reports in database
     * 
     */
    protected final Double computeROR(double numReportsDrugEvent,double numReportsDrug, double numReportsEvent, double numReports)
    {
      double d = numReportsDrug - numReportsDrugEvent;
      double D = numReports - numReportsEvent;
      if(d == 0 || D == 0  || numReportsEvent == 0) return 0d;
      double ror = (numReportsDrugEvent / d) / (numReportsEvent / D);
      return ror;
    }

    public String getReactionmeddrapt() {
      return reactionmeddrapt;
    }

    public Double getPrr() {
      return prr;
    }

    public Double getRor() {
      return ror;
    }

  }
  
  public static class OutcomeCount
  {
    private final Integer reactionoutcome;
    private long count;
    public OutcomeCount(Integer reactionoutcome, long count) {
      super();
      this.reactionoutcome = reactionoutcome;
      this.count = count;
    }

    public long getCount() {
      return count;
    }

    public void incrementCount() {
      count++;
    }
    
  }
  /**
   * Provides metadata about the requested data for consumers of the API.
   * @author svanderveen
   *
   */
  public static class MetaData
  {
    private final static String disclaimer ="openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.";
    private final static String license = "http://open.fda.gov/license";
    private final Date lastUpdated;
    
    /**
     * @param lastUpdated Date the data available was last updated.
     */
    public MetaData(Date lastUpdated) {
      super();
      this.lastUpdated = (Date) lastUpdated.clone();
    }
  }
}
