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
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMockFactory;


public class RunTimeConfigMockFactory implements ConfigMockFactory
{

	@Override
	public ConfigMock createConfigMockForProductCode(final String productCode, final String variantProductCode)
	{
		ConfigMock mock = null;

		switch (productCode)
		{
			case "CPQ_HOME_THEATER":
				mock = new CPQHomeTheaterPocConfigMockImpl();
				break;

			case "CPQ_LAPTOP":
				mock = new CPQLaptopPocConfigMockImpl();
				break;

			default:
				if (productCode.startsWith("CONF_PIPE"))
				{
					mock = createConfPipeMock(variantProductCode);
				}
				else
				{
					mock = new YSapSimplePocConfigMockImpl();
				}
				break;
		}

		return mock;
	}

	protected ConfigMock createConfPipeMock(final String variantProductCode)
	{
		final ConfPipeMockImpl pipeMock = new ConfPipeMockImpl();
		pipeMock.setVariantCode(variantProductCode);
		return pipeMock;
	}

	@Override
	public ConfigMock createConfigMockForProductCode(final String productCode)
	{
		return createConfigMockForProductCode(productCode, null);
	}
}
