/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.frontend.breadcrumb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class ProductConfigureDummyBreadcrumbBuilder extends ProductConfigureBreadcrumbBuilder
{
	private Map<String, String> dummyLocalisation = Collections.emptyMap();
	private final static Logger LOG = Logger.getLogger(ProductConfigureDummyBreadcrumbBuilder.class);

	public void addLocalizationForTest(final String key, final String localizedText)
	{
		if (dummyLocalisation.size() == 0)
		{
			dummyLocalisation = new HashMap<>();
		}
		dummyLocalisation.put(key, localizedText);
	}

	@Override
	protected String callLocalization(final String key, final Object[] args)
	{
		LOG.warn("ProductConfigureDummyBreadcrumbBuilder should only be used for unittets");
		String localizedText = dummyLocalisation.get(key);
		if (localizedText == null)
		{
			localizedText = key;
		}
		return localizedText;
	}


}
