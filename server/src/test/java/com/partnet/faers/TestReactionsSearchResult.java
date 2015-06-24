/**
 * 
 */
package com.partnet.faers;

import org.junit.Assert;
import org.junit.Test;

import com.partnet.faers.ReactionsSearchResult.ReactionCount;

/**
 * Tests {@link ReactionsSearchResult}
 * @author svanderveen
 *
 */
public class TestReactionsSearchResult 
{
  @Test
  public void testPrr()
  {
    ReactionCount reactCnt1 = new ReactionCount("DIZZYNESS", null, 5, 10, 15, 50);
    Assert.assertEquals(new Double(2), reactCnt1.getPrr());
    
    //tests / by 0: all values 0
    ReactionCount reactCnt2 = new ReactionCount("DIZZYNESS", null,0, 0, 0, 0);
    Assert.assertEquals(new Double(0), reactCnt2.getPrr());
    //tests / by 0: numReports == numReportsDrug
    ReactionCount reactCnt3 = new ReactionCount("DIZZYNESS", null,3, 5, 4, 5);
    Assert.assertEquals(new Double(0), reactCnt3.getPrr());
    //tests / by 0: numReportsEvent == numReportsDrugEvent
    ReactionCount reactCnt4 = new ReactionCount("DIZZYNESS", null,5, 8, 5, 10);
    Assert.assertEquals(new Double(0), reactCnt4.getPrr());
  }
  
  @Test
  public void testRor()
  {
    ReactionCount reactCnt1 = new ReactionCount("DIZZYNESS", null,6, 10, 15, 50);
    Assert.assertEquals(new Double(3.5), reactCnt1.getRor());
    
    //tests / by 0: all values 0
    ReactionCount reactCnt2 = new ReactionCount("DIZZYNESS", null, 0, 0, 0, 0);
    Assert.assertEquals(new Double(0), reactCnt2.getRor());
    //tests / by 0: numReportsDrug - numReportsDrugEvent
    ReactionCount reactCnt3 = new ReactionCount("DIZZYNESS", null, 3, 5, 10, 10);
    Assert.assertEquals(new Double(0), reactCnt3.getRor());
    //tests / by 0: numReports == numReportsEvent
    ReactionCount reactCnt4 = new ReactionCount("DIZZYNESS", null, 5, 5, 8, 10);
    Assert.assertEquals(new Double(0), reactCnt4.getRor());
  }
}
