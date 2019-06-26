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
 *
 */
package de.hybris.platform.ordermanagementfacades;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import org.junit.Before;

@IntegrationTest
public class BaseOrdermanagementFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Before
	public void setup()
	{
		try
		{
			importCsv("/test/OrderTestData.csv", "UTF-8");
		}
		catch (final ImpExException e)
		{
			e.printStackTrace();
		}
	}
}
