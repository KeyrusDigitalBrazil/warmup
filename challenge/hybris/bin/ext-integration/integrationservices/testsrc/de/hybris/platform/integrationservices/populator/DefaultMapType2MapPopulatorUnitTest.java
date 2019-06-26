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
package de.hybris.platform.integrationservices.populator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMapType2MapPopulatorUnitTest
{
	private static final String ATTRIBUTE_NAMES = "names";
	@InjectMocks
	private DefaultMapType2MapPopulator populator;
	@Mock
	private ItemModel itemModel;
	@Mock
	private IntegrationObjectItemModel integrationObjectItemModel;
	@Mock
	private IntegrationObjectItemAttributeModel integrationObjectItemAttributeModel;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private I18NService i18NService;
	@Mock
	private ModelService modelService;

	@Before
	public void setup()
	{
		final String qualifierName = "name";
		final String itemType = "Product";

		when(integrationObjectItemModel.getAttributes()).thenReturn(Collections.singleton(integrationObjectItemAttributeModel));
		when(integrationObjectItemModel.getType()).thenReturn(composedTypeModel);
		when(integrationObjectItemAttributeModel.getAttributeDescriptor()).thenReturn(attributeDescriptor);
		when(integrationObjectItemAttributeModel.getAttributeName()).thenReturn(ATTRIBUTE_NAMES);
		when(composedTypeModel.getCode()).thenReturn(itemType);
		when(attributeDescriptor.getQualifier()).thenReturn(qualifierName);
		when(attributeDescriptor.getAttributeType()).thenReturn(new MapTypeModel());
		when(itemModel.getItemtype()).thenReturn(itemType);
		when(modelService.getAttributeValue(itemModel, qualifierName, Locale.ENGLISH)).thenReturn("");
		when(modelService.getAttributeValue(itemModel, qualifierName, Locale.FRENCH)).thenReturn("france text");
		when(modelService.getAttributeValue(itemModel, qualifierName, Locale.GERMAN)).thenReturn(null);

		when(i18NService.getCurrentLocale()).thenReturn(Locale.FRENCH);
	}

	@Test
	public void shouldOnlyPopulateToMapCurrentSessionLocalValue()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getLocalized()).thenReturn(true);
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTRIBUTE_NAMES, "france text"));
	}

	@Test
	public void shouldNotPopulateToMap_whenNoLocalizedValueFound()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getLocalized()).thenReturn(false);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).isEmpty();
	}

	@Test
	public void shouldNotPopulateToMap_whenLocalizedValueIsNull()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getLocalized()).thenReturn(true);
		when(i18NService.getCurrentLocale()).thenReturn(Locale.GERMAN);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).isEmpty();
	}

	@Test
	public void shouldNotPopulateToMap_whenLocalizedValueIsEmptyString()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getLocalized()).thenReturn(true);
		when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTRIBUTE_NAMES, ""));
	}

	private ItemToMapConversionContext giveConversionContext()
	{
		final ItemToMapConversionContext context = new ItemToMapConversionContext();
		context.setItemModel(itemModel);
		context.setIntegrationObjectItemModel(integrationObjectItemModel);
		return context;
	}
}
