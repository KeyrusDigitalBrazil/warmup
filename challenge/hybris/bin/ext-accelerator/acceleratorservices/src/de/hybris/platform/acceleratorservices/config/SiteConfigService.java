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
package de.hybris.platform.acceleratorservices.config;

/**
 * Site config service is used to lookup a site specific configuration property.
 * The configuration property is looked up relative to the current site.
 */
public interface SiteConfigService extends ConfigLookup
{
	/**
	 * Get property for current base site
	 * 
	 * @param property the property to get
	 * @return the property value
	 */
	String getProperty(String property);
}
