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
import com.partnet.page.search.SearchPage;
import org.junit.Assert;

import java.util.Map;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class SearchSteps
{
  
  @Inject
  private StoryContext context;
  
  public void givenIAmOnTheSearchPage() {
    context.site().open();
    context.getPage(SearchPage.class).verify();
  }


  public void thenIWillReceiveANoResultErrorMessage(){
    String msg = context.getPage(SearchPage.class).waitForSearchResultsErrorMsg();
    Assert.assertEquals("No results were found for the search criteria submitted.", msg);
  }

  public void thenIWillNotHaveAnySearchResults(){
    Map<String, Integer> results = context.getPage(SearchPage.class).getTabularSearchResults();
    Assert.assertEquals("Unexpected search results were found!", 0, results.size());
  }

  public void thenIWillHaveSearchResults(){
    Map<String, Integer> results = context.getPage(SearchPage.class).waitForTabularSearchResults().getTabularSearchResults();
    Assert.assertTrue("No search results were found!", results.size() > 0);
  }

  public void whenIPerformAGenericSearch() {
    context.getPage(SearchPage.class)
        .setDrug(SearchPage.DrugOptions.CELEBREX)
        .setAge("34")
        .setGender(SearchPage.Gender.FEMALE)
        .setWeight("150")
        .clickSearch();
  }

  public void whenIPerformASearchForOnlyTheDrugName() {
    context.getPage(SearchPage.class)
        .setDrug(SearchPage.DrugOptions.ADVAIR_DISKUS)
        .clickSearch();
  }

  public void whenIPerformASearchWithNoResults() {
    context.getPage(SearchPage.class)
        .setDrug(SearchPage.DrugOptions.CELEBREX)
        .setAge("34")
        .setGender(SearchPage.Gender.FEMALE)
        .setWeight("1500")
        .clickSearch();
  }
}
