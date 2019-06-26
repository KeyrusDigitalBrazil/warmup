/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.DesiredCapabilities


//*************************************************
//***********   HTMLUnit Driver  ******************
//*************************************************
DesiredCapabilities caps = DesiredCapabilities.firefox();
driver={new HtmlUnitDriver(caps)}


//=================================================
//*********Working*********************************
//*************************************************

//*************************************************
//************* Firefox Driver ********************
//*************************************************
//driver={new FirefoxDriver()}


waiting { timeout=30 }

reportsDir="geb-reports"