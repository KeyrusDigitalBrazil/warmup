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

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultComposedType2MapPopulatorUnitTest
{
	private static final String QUALIFIER_CATALOG_VERSION = "catalogVersion";
	private static final String ATTRIBUTE_VERSION = "Version";
	private static final String ITEM_TYPE_CODE = "Product";
	private static final String FIELD_ITEM_MODEL = "itemModel";
	private static final String FIELD_IO_ITEM_MODEL = "integrationObjectItemModel";
	@InjectMocks
	private DefaultComposedType2MapPopulator populator;
	@Mock
	private ItemModel productModel;
	@Mock
	private ItemModel catalogVersionModel;
	@Mock
	private IntegrationObjectItemModel productIntegrationObjectItemModel;
	@Mock
	private IntegrationObjectItemModel catalogVersionIntegrationObjectItemModel;
	@Mock
	private IntegrationObjectItemAttributeModel integrationObjectItemAttributeModel;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter;
	@Mock
	private Map<String, Object> mockedCatalogVersionMap;
	@Mock
	private ModelService modelService;

	@Before
	public void setup()
	{
		when(productIntegrationObjectItemModel.getAttributes()).thenReturn(Collections.singleton(integrationObjectItemAttributeModel));
		when(productIntegrationObjectItemModel.getType()).thenReturn(composedTypeModel);
		when(integrationObjectItemAttributeModel.getAttributeDescriptor()).thenReturn(attributeDescriptor);
		when(integrationObjectItemAttributeModel.getAttributeName()).thenReturn(ATTRIBUTE_VERSION);
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(catalogVersionIntegrationObjectItemModel);
		when(composedTypeModel.getCode()).thenReturn(ITEM_TYPE_CODE);
		when(attributeDescriptor.getQualifier()).thenReturn(QUALIFIER_CATALOG_VERSION);
		when(attributeDescriptor.getAttributeType()).thenReturn(new ComposedTypeModel());
		when(productModel.getItemtype()).thenReturn(ITEM_TYPE_CODE);


		when(itemToIntegrationObjectMapConverter.convert(any(ItemToMapConversionContext.class))).thenReturn(mockedCatalogVersionMap);
	}

	@Test
	public void shouldPopulateToMap()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(modelService.getAttributeValue(productModel, QUALIFIER_CATALOG_VERSION)).thenReturn(catalogVersionModel);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);
		verify(itemToIntegrationObjectMapConverter).convert(
				refEq(itemToMapConversionContext, FIELD_ITEM_MODEL, FIELD_IO_ITEM_MODEL));
		assertThat(map).containsOnly(entry(ATTRIBUTE_VERSION, mockedCatalogVersionMap));
	}

	@Test
	public void shouldNotPopulateToMap_whenItemIsNull()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(modelService.getAttributeValue(productModel, QUALIFIER_CATALOG_VERSION)).thenReturn(null);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);
		verify(itemToIntegrationObjectMapConverter, never()).convert(
				refEq(itemToMapConversionContext, FIELD_ITEM_MODEL, FIELD_IO_ITEM_MODEL));
		assertThat(map).isEmpty();
	}

	@Test
	public void shouldNotPopulateToMap_whenReturnItemIsNull()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(null);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);
		verify(itemToIntegrationObjectMapConverter, never()).convert(
				refEq(itemToMapConversionContext, FIELD_ITEM_MODEL, FIELD_IO_ITEM_MODEL));
		assertThat(map).isEmpty();
	}

	@Test
	public void shouldNotPopulateToMap_whenItemAndReturnItemBothIsNull()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(modelService.getAttributeValue(productModel, QUALIFIER_CATALOG_VERSION)).thenReturn(null);
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(null);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);
		verify(itemToIntegrationObjectMapConverter, never()).convert(
				refEq(itemToMapConversionContext, FIELD_ITEM_MODEL, FIELD_IO_ITEM_MODEL));
		assertThat(map).isEmpty();
	}

	@Test
	public void shouldNotApplicableForEnumerationMetaType()
	{
		final AttributeDescriptorModel attributeDescriptorModel = attributeDescriptorModelWithType(EnumerationMetaTypeModel.class);
		assertThat(populator.isApplicable(attributeDescriptorModel)).isFalse();
	}

	@Test
	public void shouldNotApplicableForAtomicType()
	{
		final AttributeDescriptorModel attributeDescriptorModel = attributeDescriptorModelWithType(AtomicTypeModel.class);
		assertThat(populator.isApplicable(attributeDescriptorModel)).isFalse();
	}

	private ItemToMapConversionContext giveConversionContext()
	{
		final ItemToMapConversionContext context = new ItemToMapConversionContext();
		context.setItemModel(productModel);
		context.setIntegrationObjectItemModel(productIntegrationObjectItemModel);
		return context;
	}

	private <T extends TypeModel> AttributeDescriptorModel attributeDescriptorModelWithType(final Class<T> clazz)
	{
		return attributeDescriptor().withType(clazz).build();
	}
}
