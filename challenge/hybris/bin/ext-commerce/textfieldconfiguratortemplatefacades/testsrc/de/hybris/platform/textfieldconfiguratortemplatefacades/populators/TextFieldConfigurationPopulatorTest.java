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
package de.hybris.platform.textfieldconfiguratortemplatefacades.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguredProductInfoModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
public class TextFieldConfigurationPopulatorTest
{
	private final TextFieldConfigurationsPopulator<AbstractOrderEntryProductInfoModel> populator
			= new TextFieldConfigurationsPopulator<>();

	@Test
	public void shouldPopulateFields()
	{
		final TextFieldConfiguredProductInfoModel model = mock(TextFieldConfiguredProductInfoModel.class);
		when(model.getConfiguratorType()).thenReturn(ConfiguratorType.TEXTFIELD);
		when(model.getConfigurationValue()).thenReturn("value");
		when(model.getConfigurationLabel()).thenReturn("label");
		when(model.getProductInfoStatus()).thenReturn(ProductInfoStatus.INFO);
		final List<ConfigurationInfoData> data = new ArrayList<>();
		populator.populate(model, data);
		assertThat(data, iterableWithSize(1));
		assertEquals("label", data.get(0).getConfigurationLabel());
		assertEquals("value", data.get(0).getConfigurationValue());
		assertEquals(ConfiguratorType.TEXTFIELD, data.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.INFO, data.get(0).getStatus());
	}

	@Test
	public void shouldSkipUnknownTypes()
	{
		final TextFieldConfiguredProductInfoModel model = mock(TextFieldConfiguredProductInfoModel.class);
		when(model.getConfiguratorType()).thenReturn(ConfiguratorType.valueOf("SOMETHING"));
		final List<ConfigurationInfoData> data = new ArrayList<>();
		populator.populate(model, data);
		assertThat(data, iterableWithSize(0));
	}

	@Test(expected = ConversionException.class)
	public void shouldCheckSourceClass()
	{
		final AbstractOrderEntryProductInfoModel model = new AbstractOrderEntryProductInfoModel();
		model.setConfiguratorType(ConfiguratorType.TEXTFIELD);
		final List<ConfigurationInfoData> data = new ArrayList<>();
		populator.populate(model, data);
	}
}
