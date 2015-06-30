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

/**
 * @author bbarker
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
    List<Reaction> reactions = context.getPage(SearchPage.class).waitForTabularSearchResults().getTabularSearchResults(false);
    Assert.assertEquals("No search results were expected!", 0, reactions.size());
  }

  /**
   * Used to verify no search results are on the current page.
   */
  public void thenIWillNotHaveAnySearchResultsContent(){
    boolean hasContent = context.getPage(SearchPage.class).hasResultsContent();
    Assert.assertFalse("Unexpected content was found!", hasContent);
  }

  public void thenIWillHaveSearchResults(){
    List<Reaction> results = context.getPage(SearchPage.class).waitForTabularSearchResults().getTabularSearchResults(false);
    Assert.assertTrue("No search results were found!", results.size() > 0);
  }

  /**
   * Used to verify there are no search results. This is done after a search has been performed.
   */
  public void thenIWillNotHaveSearchResults(){
    List<Reaction> results = context.getPage(SearchPage.class).waitForTabularSearchResults().getTabularSearchResults(false);
    Assert.assertEquals("Unexpected search results found!", 0, results.size());
  }

  public void whenIPerformASearchFor(SearchPage.DrugOptions drugOpt, String age, SearchPage.Gender gender, String weight) {
    context.getPage(SearchPage.class)
        .setDrugAndSelectFirstOption(drugOpt)
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
        .setDrugAndSelectFirstOption(SearchPage.DrugOptions.getRandomOption())
        .clickSearch();
  }

  public void whenIPerformASearchWithNoResults() {
    whenIPerformASearchFor(SearchPage.DrugOptions.ADVIL, "34", SearchPage.Gender.FEMALE, "1500");
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
