/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata;

import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * The EdmProviderFactory creates EdmProviders
 */
public interface EdmProviderFactory
{
	/**
	 * Creates a new instance of the EdmProvider
	 *
	 * @param context Context used to create the provider
	 * @return An EdmProvider
	 */
	EdmProvider createInstance(ODataContext context);
}
