package com.partnet.page.search;

import com.partnet.automation.DependencyContainer;
import com.partnet.automation.RuntimeConfiguration;
import com.partnet.automation.page.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by brent on 6/22/15.
 */
public class SearchPage
  extends Page
{

  @FindBy(id = "drug-id")
  private WebElement drugNameTxtBox;

  @FindBy(id = "age-id")
  private WebElement ageTxtBox;

  @FindBy(css = "input[ng-model='drugEvents.gender'][value='1']")
  private WebElement maleRadio;

  @FindBy(css = "input[ng-model='drugEvents.gender'][value='2']")
  private WebElement femaleRadio;

  @FindBy(id = "weight-id")
  private WebElement weightTxtBox;

  @FindBy(id = "reg-button-id")
  private WebElement searchBtn;

  private By searchResultsBubbleLocator = By.cssSelector("svg.bubble");

  private static final Logger LOG = LoggerFactory.getLogger(SearchPage.class);

  /**
   * The superclass for all Page Objects. It allows access to the
   * {@link DependencyContainer} and initialized @FindBy elements
   *
   * @param depContainer TODO
   */
  public SearchPage(DependencyContainer depContainer) {
    super(depContainer);
  }

  public enum Gender {MALE, FEMALE};

  public void goTo() {
    super.webDriver.get(RuntimeConfiguration.getInstance().getUrl() + "/");
  }

  @Override
  public void verify() throws IllegalStateException {
    //super.verifyByWebElement(searchBtn);
  }

  public SearchPage setDrugName(String name) {
    super.setValue(drugNameTxtBox, name);
    return this;
  }

  public SearchPage setAge(String age) {
    super.setValue(ageTxtBox, age);
    return this;
  }

  public SearchPage setGender(Gender gender) {
    switch (gender) {
      case MALE:
        maleRadio.click();
        break;
      case FEMALE:
        femaleRadio.click();
        break;
    }
    return this;
  }


  public SearchPage setWeight(String age) {
    super.setValue(weightTxtBox, age);
    return this;
  }

  public void clickSearch() {
    super.clickAndWait(searchBtn);
  }


  public void waitForSearchResults() {
    super.waitForPresenceOfElement(searchResultsBubbleLocator, 30);
  }

  public boolean hasSearchResults() {
    List<WebElement> searchResultsBubble = super.webDriver.findElements(searchResultsBubbleLocator);
    if(searchResultsBubble.size() > 0) {
      LOG.error(searchResultsBubble.get(0).getText());
    }
    return searchResultsBubble.size() == 1;
  }

}
