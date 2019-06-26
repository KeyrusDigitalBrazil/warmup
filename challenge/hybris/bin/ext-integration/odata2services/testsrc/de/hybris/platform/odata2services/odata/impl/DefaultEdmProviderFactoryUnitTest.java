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
package de.hybris.platform.odata2services.odata.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.EdmProviderFactory;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.junit.Test;

@UnitTest
public class DefaultEdmProviderFactoryUnitTest
{
	private static final String ENTITY_TYPE = "entityType";
	private static final String SERVICE = "service";
	
	private final EdmProviderFactory edmProviderFactory = new DefaultEdmProviderFactory();

	@Test
	public void testCreateInstance()
	{
		assertNotNull(edmProviderFactory.createInstance(givenContext()));
	}

	@Test(expected = NullPointerException.class)
	public void testCreateInstanceWithNullContext()
	{
		edmProviderFactory.createInstance(null);
	}

	private static ODataContext givenContext()
	{
		final ODataContext context = mock(ODataContext.class);
		when(context.getParameter(ENTITY_TYPE)).thenReturn("Product");
		when(context.getParameter(SERVICE)).thenReturn("InboundProduct");
		when(context.getHttpMethod()).thenReturn("GET");
		return context;
	}
}