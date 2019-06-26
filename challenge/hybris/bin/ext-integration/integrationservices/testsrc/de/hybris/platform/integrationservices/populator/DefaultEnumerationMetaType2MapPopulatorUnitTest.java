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
import java.util.Map;

import com.google.common.collect.Maps;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEnumerationMetaType2MapPopulatorUnitTest
{
	private static final String QUALIFIER_TYPE = "attributeType";
	@InjectMocks
	private DefaultEnumerationMetaType2MapPopulator populator;
	@Mock
	private ItemModel itemModel;
	@Mock
	private IntegrationObjectItemModel integrationObjectItemModel;
	@Mock
	private IntegrationObjectItemModel enumIntegrationObjectItemModel;
	@Mock
	private IntegrationObjectItemAttributeModel integrationObjectItemAttributeModel;
	@Mock
	private IntegrationObjectItemAttributeModel enumIntegrationObjectItemAttributeModel;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private AttributeDescriptorModel enumAttributeDescriptor;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private ModelService modelService;

	@Before
	public void setup()
	{
		when(integrationObjectItemModel.getAttributes()).thenReturn(Collections.singleton(integrationObjectItemAttributeModel));
		when(integrationObjectItemModel.getType()).thenReturn(composedTypeModel);
		when(integrationObjectItemAttributeModel.getAttributeDescriptor()).thenReturn(attributeDescriptor);
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(enumIntegrationObjectItemModel);
		when(enumIntegrationObjectItemModel.getAttributes()).thenReturn(Collections.singleton(enumIntegrationObjectItemAttributeModel));
		when(enumIntegrationObjectItemAttributeModel.getAttributeDescriptor()).thenReturn(enumAttributeDescriptor);
		when(enumIntegrationObjectItemAttributeModel.getAttributeName()).thenReturn("enumCodex");
		when(enumAttributeDescriptor.getQualifier()).thenReturn("code");
		when(integrationObjectItemAttributeModel.getAttributeName()).thenReturn(QUALIFIER_TYPE);
		when(composedTypeModel.getCode()).thenReturn("ClassAttributeAssignment");
		when(attributeDescriptor.getQualifier()).thenReturn(QUALIFIER_TYPE);
		when(attributeDescriptor.getAttributeType()).thenReturn(new EnumerationMetaTypeModel());
		when(itemModel.getItemtype()).thenReturn("ClassAttributeAssignment");
		when(modelService.getAttributeValue(itemModel, QUALIFIER_TYPE)).thenReturn(ClassificationAttributeTypeEnum.STRING);
	}

	@Test
	public void shouldPopulateToMap()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat((Map)map.get(QUALIFIER_TYPE)).containsOnly(entry("enumCodex", "string"));
	}

	@Test
	public void shouldNotPopulate_whenReturnIntegrationObjectIsNull()
	{
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(null);
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).isEmpty();
	}

	private ItemToMapConversionContext giveConversionContext()
	{
		final ItemToMapConversionContext context = new ItemToMapConversionContext();
		context.setItemModel(itemModel);
		context.setIntegrationObjectItemModel(integrationObjectItemModel);
		return context;
	}
}
