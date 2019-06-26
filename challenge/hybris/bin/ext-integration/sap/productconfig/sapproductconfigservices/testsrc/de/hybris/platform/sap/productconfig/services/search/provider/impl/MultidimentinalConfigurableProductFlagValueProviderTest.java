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
package de.hybris.platform.sap.productconfig.services.search.provider.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.sap.productconfig.services.model.CPQConfiguratorSettingsModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MultidimentinalConfigurableProductFlagValueProviderTest
{
	private final MultidimentionalConfigurableProductFlagValueProvider classUnderTest = new MultidimentionalConfigurableProductFlagValueProvider();

	@Mock
	private ModelService modelService;

	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;

	@Mock
	private CPQConfiguratorSettingsModel cpqConfiguratorSettingModel;


	private ProductModel model;
	private List<VariantProductModel> variants;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfiguratorSettingsService(configuratorSettingsService);
		classUnderTest.setModelService(modelService);

		model = new ProductModel();
		variants = new ArrayList<>();
		variants.add(new VariantProductModel());
		model.setVariants(variants);

		Mockito.when(modelService.getAttributeValue(model, "variants")).thenReturn(variants);
		Mockito.when(configuratorSettingsService.getConfiguratorSettingsForProduct(model)).thenReturn(Collections.emptyList());

	}

	@Test
	public void testHasNoVariantsIsNotConsideredMultiDimensional() throws FieldValueProviderException
	{
		variants.clear();
		final Boolean result = (Boolean) classUnderTest.getFieldValue(model);

		assertFalse(result.booleanValue());
	}


	@Test
	public void testHasNoneConfigurableVariantsIsConsideredMultiDimensional() throws FieldValueProviderException
	{
		final Boolean result = (Boolean) classUnderTest.getFieldValue(model);

		assertTrue(result.booleanValue());
	}

	@Test
	public void testHasConfigurableVariantsIsNotConsideredMultiDimensional() throws FieldValueProviderException
	{

		Mockito.when(cpqConfiguratorSettingModel.getConfiguratorType()).thenReturn(ConfiguratorType.CPQCONFIGURATOR);
		final List<AbstractConfiguratorSettingModel> configSettingModels = new ArrayList<>();
		configSettingModels.add(cpqConfiguratorSettingModel);
		Mockito.when(configuratorSettingsService.getConfiguratorSettingsForProduct(model)).thenReturn(configSettingModels);

		final Boolean result = (Boolean) classUnderTest.getFieldValue(model);

		assertFalse(result.booleanValue());
	}
}
