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
package com.hybris.backoffice.cockpitng.util.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.ViewTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
public class PlatformTypeContextStrategyTest
{
	@Mock
	private TypeService typeService;
	@InjectMocks
	@Spy
	private PlatformTypeContextStrategy platformTypeContextStrategy;

	@Test
	public void testAtomicType()
	{
		final AtomicTypeModel stringAtomic = mock(AtomicTypeModel.class);
		final AtomicTypeModel objectAtomic = mock(AtomicTypeModel.class);

		when(objectAtomic.getCode()).thenReturn(Object.class.getName());
		when(stringAtomic.getSuperType()).thenReturn(objectAtomic);
		when(typeService.getTypeForCode(String.class.getName())).thenReturn(stringAtomic);

		final List<String> parentContexts = platformTypeContextStrategy.getParentContexts(String.class.getName());

		assertThat(parentContexts).hasSize(1);
		assertThat(parentContexts.get(0)).isEqualTo(Object.class.getName());
	}

	@Test
	public void testComposedType()
	{
		final ComposedTypeModel productType = mock(ComposedTypeModel.class);
		final ComposedTypeModel itemType = mock(ComposedTypeModel.class);

		when(itemType.getCode()).thenReturn(ItemModel.class.getName());
		when(productType.getSuperType()).thenReturn(itemType);
		when(typeService.getTypeForCode(ProductModel.class.getName())).thenReturn(productType);

		final List<String> parentContexts = platformTypeContextStrategy.getParentContexts(ProductModel.class.getName());

		assertThat(parentContexts).hasSize(1);
		assertThat(parentContexts.get(0)).isEqualTo(ItemModel.class.getName());
	}

	@Test
	public void testComposedTypeWithNoSuperType()
	{
		final ComposedTypeModel itemType = mock(ComposedTypeModel.class);

		when(itemType.getSuperType()).thenReturn(null);
		when(typeService.getTypeForCode(ItemModel.class.getName())).thenReturn(itemType);

		final List<String> parentContexts = platformTypeContextStrategy.getParentContexts(ItemModel.class.getName());

		assertThat(parentContexts).hasSize(0);
	}

	@Test
	public void testViewType()
	{
		final ViewTypeModel viewType = mock(ViewTypeModel.class);

		when(typeService.getTypeForCode("SomeViewType")).thenReturn(viewType);

		final List<String> parentContexts = platformTypeContextStrategy.getParentContexts("SomeViewType");

		assertThat(parentContexts).hasSize(0);
	}

	@Test
	public void testPojoType()
	{
		when(typeService.getTypeForCode(NonPlatformPojo.class.getName())).thenThrow(UnknownIdentifierException.class);

		final List<String> nonPlatformType = platformTypeContextStrategy.getParentContexts(NonPlatformPojo.class.getName());

		assertThat(nonPlatformType).isEqualTo(
				Lists.newArrayList(NonPlatformPojo.class.getSuperclass().getName(), Cloneable.class.getName(), StringUtils.EMPTY));
	}

	@Test
	public void testCollectionType()
	{
		final CollectionTypeModel collectionType = mock(CollectionTypeModel.class);

		when(typeService.getTypeForCode(ArrayList.class.getName())).thenReturn(collectionType);

		final List<String> parentContexts = platformTypeContextStrategy.getParentContexts(ArrayList.class.getName());

		assertThat(parentContexts).isEqualTo(Lists.newArrayList(ArrayList.class.getSuperclass().getName(), List.class.getName(),
				RandomAccess.class.getName(), Cloneable.class.getName(), Serializable.class.getName()));
	}

	@Test
	public void testMapType()
	{
		final MapTypeModel mapType = mock(MapTypeModel.class);

		when(typeService.getTypeForCode(HashMap.class.getName())).thenReturn(mapType);

		final List<String> parentContexts = platformTypeContextStrategy.getParentContexts(HashMap.class.getName());
		assertThat(parentContexts).isEqualTo(Lists.newArrayList(HashMap.class.getSuperclass().getName(), Map.class.getName(),
				Cloneable.class.getName(), Serializable.class.getName()));
	}

	class NonPlatformPojo implements Cloneable
	{
		// empty class used to represent non-existent type
	}
}
