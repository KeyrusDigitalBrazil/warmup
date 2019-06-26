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
package de.hybris.platform.addonsupport.config.bundlesources;

import java.util.Locale;
import java.util.Map;


/**
 * Add-on resource bundle source interface. By default used by DefaultAddonResourceBundleSource.
 */
public interface JavaScriptMessageResourcesAccessor
{

	/**
	 * Getting messages from all sources
	 * 
	 * @return java.util.Map
	 */
	Map<String, String> getAllMessages(Locale locale);

	/**
	 * Getting addOnName for given Resources
	 * 
	 * @return String
	 */
	String getAddOnName();

}
