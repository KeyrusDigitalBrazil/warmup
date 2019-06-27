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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.model.ModelCloningContext;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;


@RunWith(MockitoJUnitRunner.class)
public class ProductCloneStrategyTest
{
	@InjectMocks
	private ProductCloneStrategy productCloneStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private ProductModel mockProduct;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private DataType dataType;

	@Before
	public void setUp() throws TypeNotFoundException
	{
		when(dataType.isSingleton()).thenReturn(false);
		when(typeFacade.getType(any())).thenReturn("type");
		when(typeFacade.load(any())).thenReturn(dataType);
		when(modelService.clone(any())).thenReturn(mockProduct);
		when(objectFacade.isNew(any())).thenReturn(false);
	}

	@Test
	public void shouldNotHandleNotProductModel() throws Exception
	{
		//given - setUp
		//when
		final boolean canHandle = productCloneStrategy.canHandle(new Object());
		//then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldHandleProductModel() throws Exception
	{
		//given - setUp
		//when
		final boolean canHandle = productCloneStrategy.canHandle(new ProductModel());
		//then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldCloneProduct() throws Exception
	{
		//given - setUp
		//when
		final ProductModel clonedProduct = productCloneStrategy.clone(new ProductModel());
		//then
		verify(modelService).clone(any(), any(ModelCloningContext.class));
		verify(modelService).setAttributeValue(clonedProduct, ProductModel.CODE, null);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCloneNewProduct() throws Exception
	{
		//given
		when(objectFacade.isNew(any())).thenReturn(true);
		//when
		productCloneStrategy.clone(new ProductModel());
		//then
		//exception
	}

	@Test
	public void shouldNotHandleSingleton() throws Exception
	{
		//given - setUp
		when(dataType.isSingleton()).thenReturn(true);
		//when
		final boolean canHandle = productCloneStrategy.canHandle(new ProductModel());
		//then
		assertThat(canHandle).isFalse();
	}
}
