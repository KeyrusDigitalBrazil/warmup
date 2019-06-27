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
package de.hybris.platform.cms2.version.service.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionHelperTest
{
	@InjectMocks
	private DefaultCMSVersionHelper cmsVersionHelper;
	@Mock
	private TypeService typeService;


	@Mock
	private ItemModel itemModel;

	@Mock
	private ComposedTypeModel superComposedType;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private AttributeDescriptorModel itemTypeAttribute;
	@Mock
	private TypeModel itemTypeAttributeTypeModel;
	@Mock
	private AttributeDescriptorModel pkAttribute;
	@Mock
	private TypeModel pkAttributeTypeModel;
	@Mock
	private AttributeDescriptorModel writableAttribute1;
	@Mock
	private TypeModel writableAttributeTypeModel1;
	@Mock
	private AttributeDescriptorModel writableAttribute2;
	@Mock
	private TypeModel writableAttributeTypeModel2;
	@Mock
	private RelationDescriptorModel relationAttribute;

	@Mock
	private AttributeDescriptorModel readOnlyAttribute;
	@Mock
	private TypeModel readOnlyAttributeTypeModel;

	@Before
	public void setup()
	{
		when(typeService.getComposedTypeForClass(any())).thenReturn(composedType);

		when(itemTypeAttribute.getAttributeType()).thenReturn(itemTypeAttributeTypeModel);
		when(itemTypeAttributeTypeModel.getCode()).thenReturn(ComposedTypeModel._TYPECODE);
		when(typeService.isAssignableFrom(TypeModel._TYPECODE, ComposedTypeModel._TYPECODE)).thenReturn(true);
		when(itemTypeAttribute.getWritable()).thenReturn(true);
		
		when(pkAttribute.getQualifier()).thenReturn(ItemModel.PK);
		when(pkAttribute.getAttributeType()).thenReturn(pkAttributeTypeModel);
		when(pkAttributeTypeModel.getCode()).thenReturn(PK.class.getName());
		when(typeService.isAssignableFrom(TypeModel._TYPECODE, PK.class.getName())).thenReturn(false);
		when(pkAttribute.getWritable()).thenReturn(true);

		when(writableAttribute1.getAttributeType()).thenReturn(writableAttributeTypeModel1);
		when(writableAttributeTypeModel1.getCode()).thenReturn(Boolean.class.getName());
		when(typeService.isAssignableFrom(TypeModel._TYPECODE, Boolean.class.getName())).thenReturn(false);
		when(writableAttribute1.getWritable()).thenReturn(true);

		when(writableAttribute2.getAttributeType()).thenReturn(writableAttributeTypeModel2);
		when(writableAttributeTypeModel2.getCode()).thenReturn(String.class.getName());
		when(typeService.isAssignableFrom(TypeModel._TYPECODE, String.class.getName())).thenReturn(false);
		when(writableAttribute2.getWritable()).thenReturn(true);

		when(readOnlyAttribute.getAttributeType()).thenReturn(readOnlyAttributeTypeModel);
		when(readOnlyAttributeTypeModel.getCode()).thenReturn(Map.class.getName());
		when(typeService.isAssignableFrom(TypeModel._TYPECODE, Map.class.getName())).thenReturn(false);
		when(readOnlyAttribute.getWritable()).thenReturn(false);
	}

	@Test
	public void shouldReturnWritableAndRelationAttributesFromItemAndItsParent()
	{
		// GIVEN
		when(composedType.getAllSuperTypes()).thenReturn(Collections.singletonList(superComposedType));
		when(superComposedType.getDeclaredattributedescriptors()).thenReturn(Arrays.asList(writableAttribute1, itemTypeAttribute));
		when(composedType.getDeclaredattributedescriptors()).thenReturn(Arrays.asList(writableAttribute2, pkAttribute, relationAttribute));

		// WHEN
		final List<AttributeDescriptorModel> attributeDescriptorModels = cmsVersionHelper
				.getSerializableAttributes(itemModel);

		// THEN
		assertEquals(3, attributeDescriptorModels.size());
		assertThat(attributeDescriptorModels, containsInAnyOrder(writableAttribute1, writableAttribute2, relationAttribute));
	}

	@Test
	public void shouldReturnEmptyListIfItemContainsNoWritableAndNoRelationAttributes()
	{
		// GIVEN
		when(composedType.getAllSuperTypes()).thenReturn(Collections.emptyList());
		when(composedType.getDeclaredattributedescriptors()).thenReturn(Arrays.asList(pkAttribute, itemTypeAttribute, readOnlyAttribute));

		// WHEN
		final List<AttributeDescriptorModel> attributeDescriptorModels = cmsVersionHelper
				.getSerializableAttributes(itemModel);

		// THEN
		assertThat(attributeDescriptorModels, emptyIterable());
	}

	@Test
	public void shouldReturnOnlyRelationAttributeIfItemHasNoWritableAttributeAndHasRelationAttribute()
	{
		// GIVEN
		when(composedType.getAllSuperTypes()).thenReturn(Collections.emptyList());
		when(composedType.getDeclaredattributedescriptors()).thenReturn(Arrays.asList(pkAttribute, itemTypeAttribute, relationAttribute));

		// WHEN
		final List<AttributeDescriptorModel> attributeDescriptorModels = cmsVersionHelper
				.getSerializableAttributes(itemModel);

		// THEN
		assertEquals(1, attributeDescriptorModels.size());
		assertThat(attributeDescriptorModels, containsInAnyOrder(relationAttribute));
	}

	@Test
	public void shouldReturnOnlyWritableAttributesIfItemHasWritableAndNoRelationAttributes()
	{
		// GIVEN
		when(composedType.getAllSuperTypes()).thenReturn(Collections.emptyList());
		when(composedType.getDeclaredattributedescriptors()).thenReturn(Arrays.asList(pkAttribute, itemTypeAttribute, writableAttribute2));

		// WHEN
		final List<AttributeDescriptorModel> attributeDescriptorModels = cmsVersionHelper
				.getSerializableAttributes(itemModel);

		// THEN
		assertEquals(1, attributeDescriptorModels.size());
		assertThat(attributeDescriptorModels, containsInAnyOrder(writableAttribute2));
	}
}
