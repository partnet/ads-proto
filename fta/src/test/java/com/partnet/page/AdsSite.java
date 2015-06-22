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

package com.partnet.page;


import org.openqa.selenium.By;

import com.partnet.automation.RuntimeConfiguration;
import com.partnet.automation.page.Site;
import com.partnet.automation.selenium.DriverProvider;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * This object represents the ads site
 */
public class AdsSite
  extends Site
{
  
  private RuntimeConfiguration runConfig;

  @FindBy(css = ".auth a")
  private WebElement loginBtn;

  public AdsSite(DriverProvider driverProvider, RuntimeConfiguration runConfig)
  {
    super(driverProvider.get());
    this.runConfig = runConfig;
  }

  @Override
  public String getDefaultUrl()
  {
    return runConfig.getUrl();
  }

  @Override
  protected By getPrimaryWindowSelector()
  {
    /*
     *  In the case of a link on the original window that spawns another window,
     *  This is a global selector used to identify the original primary window
     *  For our case of searching Bing, it is not necessary.
     */
    return null;
  }


  public void clickLogin() {
    loginBtn.click();
  }


}
