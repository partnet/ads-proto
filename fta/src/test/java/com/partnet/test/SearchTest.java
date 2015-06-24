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
public class SearchTest
{

  @Inject
  private SearchSteps searchSteps;
  
  @Test
  @PhantomJs
  public void test_basicSearch()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResults();
    searchSteps.whenIPerformASearchForOnlyTheDrugName();
    searchSteps.thenIWillHaveSearchResults();
  }

  @Test
  @PhantomJs
  public void test_genericSearch()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResults();
    searchSteps.whenIPerformAGenericSearch();
    searchSteps.thenIWillHaveSearchResults();
  }


  @Test
  @PhantomJs
  public void test_noSearchResults()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResults();
    searchSteps.whenIPerformASearchWithNoResults();
    searchSteps.thenIWillReceiveANoResultErrorMessage();
    searchSteps.thenIWillNotHaveAnySearchResults();
  }
}
