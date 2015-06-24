package com.partnet.page.search;

import com.partnet.automation.DependencyContainer;
import com.partnet.automation.page.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by brent on 6/22/15.
 */
public class SearchPage
  extends Page
{

  @FindBy(id = "drug-id")
  private WebElement drugNameDrpDwn;

  @FindBy(id = "age-id")
  private WebElement ageTxtBox;

  @FindBy(css = "input[ng-model='drugEvents.gender'][value='1']")
  private WebElement maleRadio;

  @FindBy(css = "input[ng-model='drugEvents.gender'][value='2']")
  private WebElement femaleRadio;

  @FindBy(id = "weight-id")
  private WebElement weightTxtBox;

  @FindBy(id = SEARCH_BTN_ID)
  private WebElement searchBtn;

  @FindBy(css = ".nav.nav-tabs li.active")
  private WebElement activeTab;

  @FindBy(css = ".nav.nav-tabs li[heading='Table'] a")
  private WebElement tableTab;

  @FindBy(css = ".nav.nav-tabs li[heading='Reaction to Outcome'] a")
  private WebElement sunburstTab;

  @FindBy(css = ".nav.nav-tabs li[heading='Reaction to Outcome Aggregation'] a")
  private WebElement bubbleTab;

  @FindBy(css = "div.tab-content .tab-pane.active")
  private WebElement activeTabContent;

  @FindBy(css = "div.tab-content .tab-pane.active")
  private List<WebElement> activeTabContentValidation;


  private By tabTableContentRowsLocator = By.cssSelector("ads-d-e-results-table tbody tr");
  private By tabSunburstContentLocator = By.cssSelector("ads-zoom-sunburst svg");
  private By tabBubbleContentLocator = By.cssSelector("ads-bubble svg.bubble");
  private By searchResultsAlertLocator = By.cssSelector("div.alert div span");
  private final String SEARCH_BTN_ID = "reg-button-id";
  private By searchBtnLocator = By.id(SEARCH_BTN_ID);

  private static final Logger LOG = LoggerFactory.getLogger(SearchPage.class);

  private static final int REACTION_COL = 0;
  private static final int COUNT_COL = 1;
  private static final int TIMEOUT = 30;


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

  public enum DrugOptions {
    SYNTHROID("Synthroid"),
    CRESTOR("Crestor"),
    NEXIUM("Nexium"),
    VENTOLIN_HFA("Ventolin HFA"),
    ADVAIR_DISKUS("Advair Diskus"),
    DIOVAN("Diovan"),
    LANTUS_SOLOSTAR("Lantus Solostar"),
    CYMBALTA("Cymbalta"),
    VYVANSE("Vyvanse"),
    LYRICA("Lyrica"),
    SPIRIVA_HANDIHALER("Spiriva Handihaler"),
    LANTUS("Lantus"),
    CELEBREX("Celebrex"),
    ABILIFY("Abilify"),
    JANUVIA("Januvia"),
    NAMENDA("Namenda"),
    VIAGRA("Viagra"),
    CIALIS("Cialis");

    private final String value;

    private static final Random RANDOM = new Random();
    private static final int SIZE = DrugOptions.values().length;
    DrugOptions(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static DrugOptions getRandomOption(){
      return DrugOptions.values()[RANDOM.nextInt(SIZE)];
    }
  }

  public enum NavTab {
    TABLE("Table"),
    SUNBURST("Sunburst"),
    BUBBLE("Bubble");

    private final String tabName;
    NavTab(String tabName){
      this.tabName = tabName;
    }

    private static NavTab valueOfByTabName(String tabName) {
      for(NavTab tab : NavTab.values()) {
       if(tab.tabName.equals(tabName)){
         return tab;
       }
      }
      throw new IllegalArgumentException(String.format(
          "The tab '%s' can not be mapped to a NavTab enum value!", tabName));
    }
  }

  @Override
  public void verify() throws IllegalStateException {
    super.verifyByWebElement(searchBtn);
  }

  @Override
  protected void ready() {
    //the page is not ready until the query button turns back into the search btn
    super.waitForPresenceOfElement(searchBtnLocator, TIMEOUT);
  }

  public SearchPage setDrug(DrugOptions option) {
    Select dropdown = new Select(drugNameDrpDwn);
    super.selectByVisibleText(drugNameDrpDwn, option.getValue());
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

  private NavTab getActiveTab() {
    return NavTab.valueOfByTabName(activeTab.getText());
  }


  public SearchPage setWeight(String age) {
    super.setValue(weightTxtBox, age);
    return this;
  }

  public void clickSearch() {
    super.clickAndWait(searchBtn);
  }


  public SearchPage waitForTabularSearchResults(){
    super.waitForPresenceOfAllElements(tabTableContentRowsLocator, TIMEOUT);
    return this;
  }


  /**
   * Returns true if there is results content. False otherwise
   * @return
   */
  public boolean hasResultsContent()
  {
    return activeTabContentValidation.size() == 1;
  }

  /**
   * Ensures the current selected tab is the Table view, then returns the list of search results.
   * Note: this method will NOT wait for elements to appear on the page. Use {@link #waitForTabularSearchResults} to
   * wait for results if they are expected.
   * @return
   */
  public Map<String, Integer> getTabularSearchResults() {

    //Ensure we are on the correct tab.
    switchToTab(NavTab.TABLE);

    //get the data
    Map<String, Integer> results = new HashMap<>();

    for(WebElement row : webDriver.findElements(tabTableContentRowsLocator)) {
      List<WebElement> col = row.findElements(By.tagName("td"));
        results.put(col.get(REACTION_COL).getText(), Integer.parseInt(col.get(COUNT_COL).getText()));
      }

    return results;
  }

  public String waitForSearchResultsErrorMsg() {
    return super.waitForPresenceOfElement(searchResultsAlertLocator, TIMEOUT).getText();
  }

  /**
   * Gets the string representation of all of the drug options, except for the first --Select-- option
   * @return
   */
  public List<String> getListOfDrugs() {
    List<String> options = new ArrayList<>();

    Select drugOpts = new Select(drugNameDrpDwn);

    for(WebElement opt : drugOpts.getOptions()) {
      options.add(opt.getText());
    }

    options.remove("--Select--");
    return options;
  }


  /**
   * This will switch to the given tab. If the active tab is already the current tab, there will be no change.
   * @param tab
   */
  public void switchToTab(NavTab tab) {
    NavTab activeTab = getActiveTab();

    if(activeTab != tab) {
      switch(tab) {

        case TABLE:
          tableTab.click();
          break;
        case SUNBURST:
          sunburstTab.click();
          break;
        case BUBBLE:
          bubbleTab.click();
          break;
      }
    }
  }

  public boolean isSunburstGraphicVisible() {
    return isVisible(tabSunburstContentLocator);
  }

  public boolean isBubbleGraphicVisible() {
    return isVisible(tabBubbleContentLocator);
  }

  private boolean isVisible(By locator){
    boolean result;
    List<WebElement> elms = activeTabContent.findElements(locator);
    if(elms.size() > 0){
      result = elms.get(0).isDisplayed();
    } else {
      result = false;
    }
    return result;
  }

}
