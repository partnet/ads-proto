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

package com.partnet.test;

import javax.inject.Inject;

import com.partnet.junit.annotations.browser.Firefox;
import com.partnet.step.SearchSteps;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.Chrome;
import com.partnet.junit.annotations.browser.HTMLUnit;
import com.partnet.junit.annotations.browser.PhantomJs;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@RunWith(SeAuto.class)
public class SearchTest
{

  @Inject
  private SearchSteps searchSteps;
  
  @Test
  @PhantomJs
  public void test_emptySearch()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveSearchResults();
    searchSteps.whenIPerformASearchForOnlyTheDrugName();
    //searchSteps.thenIWillHaveSearchResults();
  }

  @Test
  @PhantomJs
  public void test_genericSearch()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveSearchResults();
    searchSteps.whenIPerformAGenericSearch();
    //searchSteps.thenIWillHaveSearchResults();
  }


  @Test
  @PhantomJs
  public void test_noSearchResults()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenIWillNotHaveSearchResults();
    searchSteps.whenIPerformASearchWithNoResults();
    searchSteps.thenIWillNotHaveAnySearchResults();
  }
}
