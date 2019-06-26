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
package de.hybris.platform.textfieldconfiguratortemplateservices.order.hook;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguratorSettingModel;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguredProductInfoModel;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
public class TextFieldConfigurationHandlerTest
{
	private final TextFieldConfigurationHandler handler = new TextFieldConfigurationHandler();

	@Test
	public void shouldCreateFromTextFieldConfiguratorSettingModel()
	{
		final TextFieldConfiguratorSettingModel model = mock(TextFieldConfiguratorSettingModel.class);
		final List<AbstractOrderEntryProductInfoModel> result = handler.createProductInfo(model);
		assertNotNull(result);
		assertThat(result, iterableWithSize(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldReportAboutNullArg()
	{
		handler.createProductInfo(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createShouldAcceptOnlyTextFieldConfiguratorSettingModel()
	{
		final AbstractConfiguratorSettingModel model = mock(AbstractConfiguratorSettingModel.class);
		handler.createProductInfo(model);
	}

	@Test
	public void createShouldTakeValuesFromModel()
	{
		final TextFieldConfiguratorSettingModel model = mock(TextFieldConfiguratorSettingModel.class);
		when(model.getTextFieldLabel()).thenReturn("label");
		when(model.getTextFieldDefaultValue()).thenReturn("value");
		final List<AbstractOrderEntryProductInfoModel> settings = handler.createProductInfo(model);
		assertThat(settings, iterableWithSize(1));
		assertTrue(settings.get(0) instanceof TextFieldConfiguredProductInfoModel);
		final TextFieldConfiguredProductInfoModel setting = (TextFieldConfiguredProductInfoModel) settings.get(0);
		assertNotNull(setting);
		assertEquals("label", setting.getConfigurationLabel());
		assertEquals("value", setting.getConfigurationValue());
		assertEquals(ConfiguratorType.TEXTFIELD, setting.getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, setting.getProductInfoStatus());
	}

	@Test
	public void createShouldValidateModel()
	{
		final TextFieldConfiguratorSettingModel model = mock(TextFieldConfiguratorSettingModel.class);
		when(model.getTextFieldLabel()).thenReturn("label");
		when(model.getTextFieldDefaultValue()).thenReturn("");
		final List<AbstractOrderEntryProductInfoModel> settings = handler.createProductInfo(model);
		assertThat(settings, iterableWithSize(1));
		assertTrue(settings.get(0) instanceof TextFieldConfiguredProductInfoModel);
		final TextFieldConfiguredProductInfoModel setting = (TextFieldConfiguredProductInfoModel) settings.get(0);
		assertNotNull(setting);
		assertEquals(ProductInfoStatus.ERROR, setting.getProductInfoStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertShouldReportAboutNullList()
	{
		handler.convert(null, new AbstractOrderEntryModel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertShouldReportAboutNullItem()
	{
		handler.convert(Collections.singletonList(null), new AbstractOrderEntryModel());
	}

	@Test
	public void convertShouldHandleEmptyList()
	{
		final List<AbstractOrderEntryProductInfoModel> list
				= handler.convert(Collections.emptyList(), new AbstractOrderEntryModel());
		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	public void convertShouldAssignAllValues()
	{
		final ProductConfigurationItem source1 = new ProductConfigurationItem();
		source1.setKey("k1");
		source1.setValue("v1");
		final ProductConfigurationItem source2 = new ProductConfigurationItem();
		source2.setKey("k2");
		source2.setValue("v2");
		final List<AbstractOrderEntryProductInfoModel> list
				= handler.convert(Arrays.asList(source1, source2), new AbstractOrderEntryModel());
		assertThat(list, iterableWithSize(2));
		assertEquals("k1", ((TextFieldConfiguredProductInfoModel) list.get(0)).getConfigurationLabel());
		assertEquals("v1", ((TextFieldConfiguredProductInfoModel) list.get(0)).getConfigurationValue());
		assertEquals(ConfiguratorType.TEXTFIELD, list.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, list.get(0).getProductInfoStatus());
		assertEquals("k2", ((TextFieldConfiguredProductInfoModel) list.get(1)).getConfigurationLabel());
		assertEquals("v2", ((TextFieldConfiguredProductInfoModel) list.get(1)).getConfigurationValue());
		assertEquals(ConfiguratorType.TEXTFIELD, list.get(1).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, list.get(1).getProductInfoStatus());
	}

	@Test
	public void convertShouldValidateEmptyValues()
	{
		final ProductConfigurationItem source = new ProductConfigurationItem();
		source.setKey("k1");
		source.setValue("");
		final List<AbstractOrderEntryProductInfoModel> list
				= handler.convert(Collections.singletonList(source), new AbstractOrderEntryModel());
		assertEquals(ProductInfoStatus.ERROR, list.get(0).getProductInfoStatus());
	}

	@Test
	public void convertShouldValidateNullValues()
	{
		final ProductConfigurationItem source = new ProductConfigurationItem();
		source.setKey("k1");
		source.setValue(null);
		final List<AbstractOrderEntryProductInfoModel> list
				= handler.convert(Collections.singletonList(source), new AbstractOrderEntryModel());
		assertEquals(ProductInfoStatus.ERROR, list.get(0).getProductInfoStatus());
	}
}
