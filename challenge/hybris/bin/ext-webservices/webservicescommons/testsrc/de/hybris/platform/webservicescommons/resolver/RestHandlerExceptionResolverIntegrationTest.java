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
/**
 *
 */
package de.hybris.platform.webservicescommons.resolver;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class RestHandlerExceptionResolverIntegrationTest extends ServicelayerBaseTest
{
	@Test
	public void shouldHaveDefaultOrderSetOnResolver()
	{
		final RestHandlerExceptionResolver bean = Registry.getApplicationContext()
				.getBean("dummyRestHandlerExceptionResolverDefaultOrder", RestHandlerExceptionResolver.class);
		Assert.assertEquals(0, bean.getOrder());
	}

	@Test
	public void shouldHaveOrderSetOnResolver()
	{
		final RestHandlerExceptionResolver bean = Registry.getApplicationContext()
				.getBean("dummyRestHandlerExceptionResolverOrderTen", RestHandlerExceptionResolver.class);
		Assert.assertEquals(10, bean.getOrder());
	}

}
