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
package com.hybris.backoffice.cockpitng.dataaccess.facades.clone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;


@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformCloneStrategyTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private TypeService typeService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private ObjectFacade objectFacade;
	@InjectMocks
	private DefaultPlatformCloneStrategy defaultPlatformCloneStrategy;

	@Mock
	private ItemModel mockItem;
	@Mock
	private DataType dataType;

	@Before
	public void setUp() throws ObjectNotFoundException, TypeNotFoundException
	{
		when(dataType.isSingleton()).thenReturn(false);

		when(modelService.clone(any())).thenReturn(mockItem);
		when(modelService.isNew(any())).thenReturn(false);
		when(typeFacade.getType(any())).thenReturn("type");
		when(typeFacade.load(any())).thenReturn(dataType);
		when(objectFacade.isNew(any())).thenReturn(false);
	}

	@Test
	public void shouldNotHandleNotItemModel() throws Exception
	{
		//given - setUp
		//when
		final boolean canHandle = defaultPlatformCloneStrategy.canHandle(new Object());
		//then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldHandleItemModel() throws Exception
	{
		//given - setUp
		//when
		final boolean canHandle = defaultPlatformCloneStrategy.canHandle(new ProductModel());
		//then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleSingleton() throws Exception
	{
		//given - setUp
		when(dataType.isSingleton()).thenReturn(true);
		//when
		final boolean canHandle = defaultPlatformCloneStrategy.canHandle(new ProductModel());
		//then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldCloneItem() throws Exception
	{
		//given - setUp
		when(typeService.getUniqueAttributes(any())).thenReturn(Sets.newSet("attribute"));
		//when
		defaultPlatformCloneStrategy.clone(new ItemModel());
		//then
		verify(modelService).clone(any());

		verify(typeFacade, times(2)).getType(any());
		verify(typeService).getUniqueAttributes(any());
		verify(modelService).setAttributeValue(any(), anyString(), any(Object.class));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCloneNewItem() throws Exception
	{
		//given
		when(objectFacade.isNew(any())).thenReturn(true);
		//when
		defaultPlatformCloneStrategy.clone(new ItemModel());
		//then
		//exception
	}

}
