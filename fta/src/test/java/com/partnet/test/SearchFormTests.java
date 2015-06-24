package com.partnet.test;

import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.Firefox;
import com.partnet.junit.annotations.browser.PhantomJs;
import com.partnet.step.SearchSteps;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * @author bbarker
 */
@RunWith(SeAuto.class)
public class SearchFormTests {

  @Inject
  private SearchSteps searchSteps;

  @Test
  @PhantomJs
  public void test_ensureListOfDrugsIsAccurate()
  {
    searchSteps.givenIAmOnTheSearchPage();
    searchSteps.thenValidateListOfDrugs();
  }
}
