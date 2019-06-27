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
package de.hybris.platform.commerceservices.product.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests for {@link DefaultProductConfigurableChecker}
 */
@UnitTest
public class DefaultProductConfigurableCheckerTest
{
	private static final String CONFIGURATOR_TYPE_STRING = "MY_CONFIGURATOR_TYPE";

	@InjectMocks
	private final DefaultProductConfigurableChecker productConfigurableChecker = new DefaultProductConfigurableChecker();

	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;
	private final List<AbstractConfiguratorSettingModel> settingsList = new ArrayList<>();
	@Mock
	private AbstractConfiguratorSettingModel configuratorSettings;
	@Mock
	private ConfiguratorType configuratorType;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		settingsList.add(configuratorSettings);
		Mockito.when(configuratorSettings.getConfiguratorType()).thenReturn(configuratorType);
		Mockito.when(configuratorType.getCode()).thenReturn(CONFIGURATOR_TYPE_STRING);
	}

	@Test
	public void testIsProductConfigurableNullProduct()
	{
		try
		{
			productConfigurableChecker.isProductConfigurable(null);
			Assert.fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			verifyNoMoreInteractions(configuratorSettingsService);
		}
	}

	@Test
	public void testIsProductConfigurableTrue()
	{
		final AbstractConfiguratorSettingModel settingModel = mock(AbstractConfiguratorSettingModel.class);
		final ProductModel product = mock(ProductModel.class);
		BDDMockito.given(configuratorSettingsService.getConfiguratorSettingsForProduct(any(ProductModel.class)))
				.willReturn(Collections.singletonList(settingModel));

		final boolean isConfigurable = productConfigurableChecker.isProductConfigurable(product);

		assertThat(isConfigurable).isTrue();
		verify(configuratorSettingsService).getConfiguratorSettingsForProduct(product);
		verifyNoMoreInteractions(configuratorSettingsService);
	}

	@Test
	public void testIsProductConfigurableFalse()
	{
		final ProductModel product = mock(ProductModel.class);
		BDDMockito.given(configuratorSettingsService.getConfiguratorSettingsForProduct(any(ProductModel.class)))
				.willReturn(Collections.emptyList());

		final boolean isConfigurable = productConfigurableChecker.isProductConfigurable(product);

		assertThat(isConfigurable).isFalse();
		verify(configuratorSettingsService).getConfiguratorSettingsForProduct(product);
		verifyNoMoreInteractions(configuratorSettingsService);
	}

	@Test
	public void testGetFirstConfiguratorType()
	{
		final ProductModel product = mock(ProductModel.class);
		BDDMockito.given(configuratorSettingsService.getConfiguratorSettingsForProduct(any(ProductModel.class)))
				.willReturn(settingsList);
		assertEquals(CONFIGURATOR_TYPE_STRING, productConfigurableChecker.getFirstConfiguratorType(product));
	}

	@Test
	public void testGetFirstConfiguratorTypeNoSettings()
	{
		final ProductModel product = mock(ProductModel.class);
		BDDMockito.given(configuratorSettingsService.getConfiguratorSettingsForProduct(any(ProductModel.class))).willReturn(null);
		assertNull(productConfigurableChecker.getFirstConfiguratorType(product));
	}

	@Test
	public void testGetFirstConfiguratorTypeEmptySettings()
	{
		final ProductModel product = mock(ProductModel.class);
		BDDMockito.given(configuratorSettingsService.getConfiguratorSettingsForProduct(any(ProductModel.class)))
				.willReturn(settingsList);
		settingsList.clear();
		assertNull(productConfigurableChecker.getFirstConfiguratorType(product));
	}
}
