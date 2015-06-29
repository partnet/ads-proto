package com.partnet.test;

import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.PhantomJs;
import com.partnet.model.Reaction;
import com.partnet.page.search.SearchPage;
import com.partnet.step.SearchSteps;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    searchSteps.whenIPerformASearchFor(SearchPage.DrugOptions.ALEVE, "70", SearchPage.Gender.MALE, "83");
    List<Reaction> expectedResults = new ArrayList<>();
    expectedResults.add(new Reaction("MALIGNANT NEOPLASM PROGRESSION", 1, Collections.singletonMap("Unknown", 1)));
    expectedResults.add(new Reaction("LUNG ADENOCARCINOMA", 1, Collections.singletonMap("Unknown", 1)));
    expectedResults.add(new Reaction("GASTRIC ULCER HAEMORRHAGE", 1, Collections.singletonMap("Unknown", 1)));
    expectedResults.add(new Reaction("ANAEMIA", 1, Collections.singletonMap("Unknown", 1)));
    searchSteps.thenTheResultsShouldBe(SearchSteps.Result.SAME, expectedResults);


  }

}
