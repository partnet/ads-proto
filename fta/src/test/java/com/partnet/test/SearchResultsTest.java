package com.partnet.test;

import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.PhantomJs;
import com.partnet.page.search.SearchPage;
import com.partnet.step.SearchSteps;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * @author bbarker
 */
@RunWith(SeAuto.class)
public class SearchResultsTest {


  @Inject
  SearchSteps searchSteps;

  @Test
  @PhantomJs
  public void test_validateSunburstViewIsPresent()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResultsContent();
    searchSteps.whenIPerformAGenericSearch();
    searchSteps.thenIWillHaveSearchResults();
    searchSteps.whenISwitchMyTabTo(SearchPage.NavTab.SUNBURST);
    searchSteps.thenIWillSeeTheSunburstGraphic();
  }

  @Test
  @PhantomJs
  public void test_validateBubbleViewIsPresent()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveAnySearchResultsContent();
    searchSteps.whenIPerformAGenericSearch();
    searchSteps.thenIWillHaveSearchResults();
    searchSteps.whenISwitchMyTabTo(SearchPage.NavTab.BUBBLE);
    searchSteps.thenIWillSeeTheBubbleGraphic();
  }
}
