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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.json.spi.JsonProvider;

import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class JSONProviderFactoryTest
{

	@Test
	public void testGetJsonProvoder()
	{
		final JsonProvider provider = JSONProviderFactory.getJSONProvider();
		assertNotNull(provider);
		assertSame(provider, JSONProviderFactory.getJSONProvider());
	}

}
