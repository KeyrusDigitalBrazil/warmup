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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationServiceImplNoPartialMockTest
{
	private static final String baseProductCode = "Base";

	private static final String variantProductCode = "Variant";

	ProductConfigurationServiceImpl classUnderTest = new ProductConfigurationServiceImpl();

	@Mock
	private ProviderFactory providerFactory;

	@Mock
	private ConfigurationProvider configurationProvider;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(providerFactory.getConfigurationProvider()).thenReturn(configurationProvider);
		classUnderTest.setProviderFactory(providerFactory);
	}

	@Test
	public void testConfigureVariantSupported()
	{
		Mockito.when(Boolean.valueOf(configurationProvider.isConfigureVariantSupported())).thenReturn(Boolean.TRUE);
		final boolean isConfigureVariantSupported = classUnderTest.isConfigureVariantSupported();
		assertTrue(isConfigureVariantSupported);
	}

	@Test
	public void testConfigureVariantSupportedNotSupported()
	{
		Mockito.when(Boolean.valueOf(configurationProvider.isConfigureVariantSupported())).thenReturn(Boolean.FALSE);
		final boolean isConfigureVariantSupported = classUnderTest.isConfigureVariantSupported();
		assertFalse(isConfigureVariantSupported);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationForVariantNotSupported()
	{
		Mockito.when(Boolean.valueOf(configurationProvider.isConfigureVariantSupported())).thenReturn(Boolean.FALSE);
		classUnderTest.createConfigurationForVariant(baseProductCode, variantProductCode);
	}
}
