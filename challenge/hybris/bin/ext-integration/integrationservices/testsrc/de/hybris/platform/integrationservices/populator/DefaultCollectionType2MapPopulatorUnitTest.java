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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCollectionType2MapPopulatorUnitTest
{
	private static final String QUALIFIER_SUPER_CATEGORIES = "superCategories";
	private static final String ITEM_TYPE_CODE = "Product";
	@InjectMocks
	private DefaultCollectionType2MapPopulator populator;
	@Mock
	private Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter;
	@Mock
	private IntegrationObjectItemModel productIntegrationObjectItemModel;
	@Mock
	private ItemModel productModel;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private IntegrationObjectItemAttributeModel integrationObjectItemAttributeModel;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ModelService modelService;
	@Mock
	private Map<String, Object> mockedCategoryMap;

	@Before
	public void setup()
	{
		when(productModel.getItemtype()).thenReturn(ITEM_TYPE_CODE);
		when(productIntegrationObjectItemModel.getType()).thenReturn(composedTypeModel);
		when(composedTypeModel.getCode()).thenReturn(ITEM_TYPE_CODE);
		when(productIntegrationObjectItemModel.getAttributes()).thenReturn(Collections.singleton(integrationObjectItemAttributeModel));
		when(integrationObjectItemAttributeModel.getAttributeDescriptor()).thenReturn(attributeDescriptor);
		when(attributeDescriptor.getQualifier()).thenReturn(QUALIFIER_SUPER_CATEGORIES);
		when(integrationObjectItemAttributeModel.getAttributeName()).thenReturn(QUALIFIER_SUPER_CATEGORIES);
	}

	@Test
	public void testPopulateToMap_ForItemModelCollection()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final CategoryModel supercategorie = mock(CategoryModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(new CollectionTypeModel());
		when(modelService.getAttributeValue(productModel, QUALIFIER_SUPER_CATEGORIES)).thenReturn(Arrays.asList(supercategorie,supercategorie));

		when(itemToIntegrationObjectMapConverter.convert(any(ItemToMapConversionContext.class))).thenReturn(ImmutableMap.of("key",supercategorie));

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat((ArrayList) map.get(QUALIFIER_SUPER_CATEGORIES)).extracting("key").containsExactlyInAnyOrder(supercategorie,
				supercategorie);
	}

	@Test
	public void testPopulateToMap_ForPrimitiveCollection()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getAttributeType()).thenReturn(new CollectionTypeModel());
		when(modelService.getAttributeValue(productModel, QUALIFIER_SUPER_CATEGORIES)).thenReturn(Arrays.asList("value1","value2"));

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		verify(itemToIntegrationObjectMapConverter, never()).convert(any());
		assertThat((ArrayList) map.get(QUALIFIER_SUPER_CATEGORIES)).containsExactlyInAnyOrder("value1","value2");
	}

	@Test
	public void testPopulateToMap_ForParentContext()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final CategoryModel supercategorie = mock(CategoryModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(new CollectionTypeModel());
		when(modelService.getAttributeValue(productModel, QUALIFIER_SUPER_CATEGORIES)).thenReturn(Arrays.asList(supercategorie,supercategorie));
		when(itemToIntegrationObjectMapConverter.convert(any(ItemToMapConversionContext.class))).thenReturn(ImmutableMap.of("key",supercategorie));

		final IntegrationObjectItemModel categoryIntegrationObjectItemModel = mock(IntegrationObjectItemModel.class);
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(categoryIntegrationObjectItemModel);
		when(integrationObjectItemAttributeModel.getReturnIntegrationObjectItem()).thenReturn(categoryIntegrationObjectItemModel);
		when(categoryIntegrationObjectItemModel.getType()).thenReturn(composedTypeModel);

		final ItemToMapConversionContext mockConversionContext = mock(ItemToMapConversionContext.class);
		when(mockConversionContext.getParentContext()).thenReturn(itemToMapConversionContext);
		when(mockConversionContext.getItemModel()).thenReturn(productIntegrationObjectItemModel);
		itemToMapConversionContext.setParentContext(mockConversionContext);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat((ArrayList) map.get(QUALIFIER_SUPER_CATEGORIES)).extracting("key").containsExactlyInAnyOrder(supercategorie,
				supercategorie);
	}

	@Test
	public void shouldNotPopulate_whenItemModelsIsEmpty()
	{
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getAttributeType()).thenReturn(new CollectionTypeModel());
		when(modelService.getAttributeValue(productModel, QUALIFIER_SUPER_CATEGORIES)).thenReturn(Collections.emptyList());

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).isEmpty();
	}

	private ItemToMapConversionContext giveConversionContext()
	{
		final ItemToMapConversionContext context = new ItemToMapConversionContext();
		context.setItemModel(productModel);
		context.setIntegrationObjectItemModel(productIntegrationObjectItemModel);
		return context;
	}
}
