package com.partnet.test;

import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.PhantomJs;
import com.partnet.page.search.SearchPage;
import com.partnet.step.SearchSteps;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bbarker
 */
@RunWith(SeAuto.class)
public class SearchResultsDataTests {

  @Inject
  private SearchSteps searchSteps;


  @Test
  @PhantomJs
  public void test_validateTabularData(){
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.whenIPerformASearchFor(SearchPage.DrugOptions.ABILIFY, "12", SearchPage.Gender.MALE, "169");
    Map<String, Integer> expectedResults = new HashMap<>();
    expectedResults.put("DIABETIC KETOACIDOSIS", 1);
    expectedResults.put("CONVULSION", 1);
    expectedResults.put("ABNORMAL BEHAVIOUR", 1);
    searchSteps.thenTheResultsShouldBe(SearchSteps.Result.SAME, expectedResults);


  }

}
