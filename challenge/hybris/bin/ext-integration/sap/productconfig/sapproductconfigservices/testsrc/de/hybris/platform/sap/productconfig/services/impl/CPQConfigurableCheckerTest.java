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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.model.CPQConfiguratorSettingsModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPQConfigurableCheckerTest
{

	private final CPQConfigurableChecker classUnderTest = new CPQConfigurableChecker();

	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;
	@Mock
	private ProductModel product;
	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;

	private final AbstractConfiguratorSettingModel configuratorSetting = new CPQConfiguratorSettingsModel();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest.setConfiguratorSettingsService(configuratorSettingsService);
		classUnderTest.setConfigurationVariantUtil(configurationVariantUtil);

		when(configuratorSettingsService.getConfiguratorSettingsForProduct(product))
				.thenReturn(Collections.singletonList(configuratorSetting));
		configuratorSetting.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
	}

	@Test
	public void testIsProductConfigurableNullProduct()
	{
		try
		{
			classUnderTest.isProductConfigurable(null);
			Assert.fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			verifyNoMoreInteractions(configuratorSettingsService);
		}
	}

	@Test
	public void testIsUseableWithCPQConfigurator()
	{

		assertTrue(classUnderTest.isUseableWithCPQConfigurator(product));
	}

	@Test
	public void testIsUseableWithCPQConfiguratorFalse()
	{
		configuratorSetting.setConfiguratorType(null);
		assertFalse(classUnderTest.isUseableWithCPQConfigurator(product));
	}

	@Test
	public void testProductCPQConfigurable()
	{
		assertTrue(classUnderTest.isCPQConfigurableProduct(product));
	}

	@Test
	public void testProductNotCPQConfigurable()
	{
		final VariantProductModel productVariant = new VariantProductModel();
		assertFalse(classUnderTest.isCPQConfigurableProduct(productVariant));
	}

	@Test
	public void testProductNotCPQConfigurableWrongConfigurator()
	{
		final List<AbstractConfiguratorSettingModel> list = new ArrayList<>();
		final AbstractConfiguratorSettingModel configuratorSetting = mock(AbstractConfiguratorSettingModel.class);
		list.add(configuratorSetting);

		when(configurationVariantUtil.isCPQBaseProduct(product)).thenReturn(true);
		when(configuratorSettingsService.getConfiguratorSettingsForProduct(product)).thenReturn(list);

		assertFalse(classUnderTest.isCPQConfigurableProduct(product));
	}

	@Test
	public void testProductCPQChangeableVariantProduct()
	{
		when(configurationVariantUtil.isCPQChangeableVariantProduct(product)).thenReturn(true);
		assertTrue(classUnderTest.isCPQChangeableVariantProduct(product));
	}

	@Test
	public void testProductNotCPQChangeableVariantProduct()
	{
		when(configurationVariantUtil.isCPQChangeableVariantProduct(product)).thenReturn(false);
		assertFalse(classUnderTest.isCPQChangeableVariantProduct(product));
	}

	@Test
	public void testProductNotCPQChangeableVariantProductWrongConfigurator()
	{
		configuratorSetting.setConfiguratorType(null);
		when(configurationVariantUtil.isCPQChangeableVariantProduct(product)).thenReturn(true);
		assertFalse(classUnderTest.isCPQChangeableVariantProduct(product));
	}

	@Test
	public void testIsCPQNotChangeableVariantProductWithNotChangeableVariant()
	{
		when(configurationVariantUtil.isCPQNotChangeableVariantProduct(product)).thenReturn(true);
		assertTrue(classUnderTest.isCPQNotChangeableVariantProduct(product));
	}

	@Test
	public void testIsCPQNotChangeableVariantProductWithChangeableVariant()
	{
		when(configurationVariantUtil.isCPQNotChangeableVariantProduct(product)).thenReturn(false);
		assertFalse(classUnderTest.isCPQNotChangeableVariantProduct(product));
	}

	@Test
	public void testIsCPQConfiguratorApplicableProductTrueForCPQConfigurable()
	{
		assertTrue(classUnderTest.isCPQConfiguratorApplicableProduct(product));
	}

	@Test
	public void testIsCPQConfiguratorApplicableProductTrueForCPQChangeableVariant()
	{
		when(configurationVariantUtil.isCPQChangeableVariantProduct(product)).thenReturn(true);
		assertTrue(classUnderTest.isCPQConfiguratorApplicableProduct(product));
	}

	@Test
	public void testIsCPQConfiguratorApplicableProductFalseForNonCPQProduct()
	{
		configuratorSetting.setConfiguratorType(null);
		assertFalse(classUnderTest.isCPQConfiguratorApplicableProduct(product));
	}


}
