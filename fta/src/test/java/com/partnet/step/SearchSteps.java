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

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class SearchSteps
{
  
  @Inject
  private StoryContext context;
  
  public void givenIAmOnTheSearchPage() {
    context.getPage(SearchPage.class).goTo();
  }

  public void thenIWillNotHaveSearchResults() {
    Assert.assertFalse(context.getPage(SearchPage.class).hasSearchResults());

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

  public void thenIWillHaveSearchResults(){
    context.getPage(SearchPage.class).waitForSearchResults();
    Assert.assertTrue(context.getPage(SearchPage.class).hasSearchResults());
  }
}
