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
package de.hybris.platform.cmsfacades.types.service.impl;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSAttributeTypeServiceTest
{
	@InjectMocks
	private DefaultCMSAttributeTypeService cmsAttributeTypeService;
	@Mock
	private TypeService typeService;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private AtomicTypeModel atomicType;
	@Mock
	private CollectionTypeModel collectionType;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private MapTypeModel mapType;
	@Mock
	private ComposedTypeModel attributeComposedType;

	@Before
	public void setUp()
	{
		when(atomicType.getItemtype()).thenReturn(AtomicTypeModel._TYPECODE);
		when(collectionType.getItemtype()).thenReturn(CollectionTypeModel._TYPECODE);
		when(collectionType.getElementType()).thenReturn(atomicType);
		when(mapType.getItemtype()).thenReturn(MapTypeModel._TYPECODE);
		when(composedType.getCode()).thenReturn(CMSItemModel._TYPECODE);
	}

	@Test
	public void shouldGetAttributeContainedTypeForSingleAttribute()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(atomicType);

		final TypeModel attributeType = cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor);

		assertThat(attributeType, equalTo(atomicType));
	}

	@Test
	public void shouldGetAttributeContainedTypeForCollectionAttribute()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(collectionType);

		final TypeModel attributeType = cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor);

		assertThat(attributeType, equalTo(atomicType));
	}

	@Test
	public void shouldGetAttributeContainedTypeForLocalizedAttribute()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(mapType);
		when(attributeDescriptor.getLocalized()).thenReturn(TRUE);
		when(mapType.getReturntype()).thenReturn(atomicType);

		final TypeModel attributeType = cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor);

		assertThat(attributeType, equalTo(atomicType));
	}

	@Test
	public void shouldGetAttributeContainedTypeForLocalizedCollectionAttribute()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(mapType);
		when(attributeDescriptor.getLocalized()).thenReturn(TRUE);
		when(mapType.getReturntype()).thenReturn(collectionType);

		final TypeModel attributeType = cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor);

		assertThat(attributeType, equalTo(atomicType));
	}
}
