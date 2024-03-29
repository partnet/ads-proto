/*
 * Copyright 2015 Partnet, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnet.step;

import javax.inject.Inject;

import com.partnet.config.framework.StoryContext;
import com.partnet.model.Reaction;
import com.partnet.page.search.SearchPage;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class SearchSteps
{
  public enum Result {DIFFERENT, SAME}

  @Inject
  private StoryContext context;

  private static final String SEARCH_KEY = "Search Key";
  
  public void givenIAmOnTheSearchPage() {
    context.site().open();
    context.getPage(SearchPage.class).verify();
  }


  public void thenIWillReceiveANoResultErrorMessage(){
    String msg = context.getPage(SearchPage.class).waitForSearchResultsErrorMsg();
    Assert.assertEquals("No results were found for the search criteria submitted.", msg);
  }

  public void thenIWillNotHaveAnySearchResultsContent(){
    boolean hasContent = context.getPage(SearchPage.class).hasResultsContent();
    Assert.assertFalse("Unexpected content was found!", hasContent);
  }

  public void thenIWillHaveSearchResults(){
    List<Reaction> results = context.getPage(SearchPage.class).waitForTabularSearchResults().getTabularSearchResults(false);
    Assert.assertTrue("No search results were found!", results.size() > 0);
  }

  public void whenIPerformASearchFor(SearchPage.DrugOptions drugOpt, String age, SearchPage.Gender gender, String weight) {
    context.getPage(SearchPage.class)
        .setDrug(drugOpt)
        .setAge(age)
        .setGender(gender)
        .setWeight(weight)
        .clickSearch();
  }


  public void whenIPerformAGenericSearch() {
    whenIPerformASearchFor(SearchPage.DrugOptions.ADVIL, "34", SearchPage.Gender.FEMALE, "150");
  }

  /**
   * Performs a search for one of the random drug options
   */
  public void whenIPerformASearchForOnlyTheDrugName() {
    context.getPage(SearchPage.class)
        .setDrug(SearchPage.DrugOptions.getRandomOption())
        .clickSearch();
  }

  public void whenIPerformASearchWithNoResults() {
    whenIPerformASearchFor(SearchPage.DrugOptions.ADVIL, "34", SearchPage.Gender.FEMALE, "1500");
  }

  public void thenValidateListOfDrugs() {

    List<String> actualOptions = context.getPage(SearchPage.class)
        .getListOfDrugs();

    Assert.assertEquals(String.format(
        "Actual drug options is not the same size as the expected list of options! actual drugs: %s\n", actualOptions),
        SearchPage.DrugOptions.values().length, actualOptions.size());

    for(SearchPage.DrugOptions expected : SearchPage.DrugOptions.values()) {
      Assert.assertTrue(
        String.format("List of actual drug options does not contain expected drug option %s!", expected.getValue()),
        actualOptions.contains(expected.getValue()));
    }
  }

  public void thenTheResultsShouldBe(Result result, List<Reaction> expectedResult){
    List<Reaction> current = context.getPage(SearchPage.class)
        .waitForTabularSearchResults()
        .getTabularSearchResults(true);
    compareSearchResults(result, expectedResult, current);
  }

  private void compareSearchResults(Result result, List<Reaction> expected, List<Reaction> actual)
  {
    switch(result) {

      case DIFFERENT:
        Assert.assertNotEquals(expected, actual);
        break;
      case SAME:
        Assert.assertEquals(expected, actual);
        break;
      default:
        throw new IllegalArgumentException(String.format(
            "Result type %s is not valid for comparing the search results!", result));
    }
  }

  public void whenISwitchMyTabTo(SearchPage.NavTab tab) {
    context.getPage(SearchPage.class).switchToTab(tab);
  }


  public void thenIWillSeeTheSunburstGraphic() {
    Assert.assertFalse(context.getPage(SearchPage.class).isBubbleGraphicVisible());
    Assert.assertTrue(context.getPage(SearchPage.class).isSunburstGraphicVisible());
  }

  public void thenIWillSeeTheBubbleGraphic() {
    Assert.assertTrue(context.getPage(SearchPage.class).isBubbleGraphicVisible());
    Assert.assertFalse(context.getPage(SearchPage.class).isSunburstGraphicVisible());
  }


}
