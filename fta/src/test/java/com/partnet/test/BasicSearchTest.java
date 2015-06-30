package com.partnet.test;

import javax.inject.Inject;

import com.partnet.step.SearchSteps;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.PhantomJs;

/**
 * @author bbarker
 */
@RunWith(SeAuto.class)
public class BasicSearchTest
{

  @Inject
  private SearchSteps searchSteps;
  
  @Test
  @PhantomJs
  public void test_basicSearch()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResultsContent();
    searchSteps.whenIPerformASearchForOnlyTheDrugName();
    searchSteps.thenIWillHaveSearchResults();
  }

  @Test
  @PhantomJs
  public void test_genericSearch()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResultsContent();
    searchSteps.whenIPerformAGenericSearch();
    searchSteps.thenIWillHaveSearchResults();
  }


  @Test
  @PhantomJs
  public void test_noSearchResults()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResultsContent();
    searchSteps.whenIPerformASearchWithNoResults();
    searchSteps.thenIWillReceiveANoResultErrorMessage();
    searchSteps.thenIWillNotHaveSearchResults();
  }
}
