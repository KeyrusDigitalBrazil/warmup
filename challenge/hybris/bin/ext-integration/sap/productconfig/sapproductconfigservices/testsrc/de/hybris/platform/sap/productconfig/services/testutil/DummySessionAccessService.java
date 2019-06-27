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
package de.hybris.platform.sap.productconfig.services.testutil;

import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.services.impl.SessionAccessServiceImpl;


public class DummySessionAccessService extends SessionAccessServiceImpl
{

	public static final String SESSION_ID = "123456";

	@Override
	public String getSessionId()
	{
		return SESSION_ID;
	}

	public void reset()
	{
		attributeContainer = null;
	}

	private ProductConfigSessionAttributeContainer attributeContainer;

	@Override
	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer(final boolean createLazy)
	{
		if (attributeContainer == null && createLazy)
		{
			attributeContainer = new ProductConfigSessionAttributeContainer();
		}
		return attributeContainer;
	}


	public ProductConfigSessionAttributeContainer getAttributeContainer()
	{
		return attributeContainer;
	}

}
