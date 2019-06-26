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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.strategies.intf.ProductConfigurationCartEntryValidationStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests
 */
@UnitTest
public class ProductConfigurationCartValidationStrategyImplTest
{

	protected static final String PRODUCT_CODE = "1234";

	@Mock
	protected CartModel cartModel;
	@Mock
	protected CartEntryModel cartEntryModel;
	@Mock
	protected ProductModel productModel;
	@Mock
	protected BaseStoreModel baseStoreModel;

	@Mock
	protected ProductService productService;
	@Mock
	protected CommerceStockService commerceStockService;
	@Mock
	protected BaseStoreService baseStoreService;
	@Mock
	protected CartService cartService;
	@Mock
	protected ModelService modelService;
	@Mock
	protected ProductConfigurationCartEntryValidationStrategy productConfigurationCartEntryValidationStrategy;

	@InjectMocks
	private final ProductConfigurationCartValidationStrategyImpl classUnderTest = new ProductConfigurationCartValidationStrategyImpl();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		final List<CartEntryModel> cartEntries = new ArrayList<>();
		cartEntries.add(cartEntryModel);

		when(cartEntryModel.getProduct()).thenReturn(productModel);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(productService.getProductForCode(anyString())).thenReturn(productModel);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(commerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).thenReturn(100L);
		when(cartService.getEntriesForProduct(cartModel, productModel)).thenReturn(cartEntries);
	}

	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final CartEntryModel cartEntryModel)
	{
		return classUnderTest.validateCartEntry(cartModel, cartEntryModel);
	}

	@Test
	public void testValidateCartEntry()
	{
		final CommerceCartModification cartModification = validateCartEntry(cartModel, cartEntryModel);

		assertEquals(CommerceCartModificationStatus.SUCCESS, cartModification.getStatusCode());
	}

	@Test
	public void testValidateCartEntryConfigModification()
	{
		final CommerceCartModification configCartModificaton = new CommerceCartModification();
		configCartModificaton.setEntry(cartEntryModel);
		configCartModificaton.setStatusCode(ProductConfigurationCartEntryValidationStrategyImpl.PRICING_ERROR);
		when(productConfigurationCartEntryValidationStrategy.validateConfiguration(cartEntryModel)).thenReturn(configCartModificaton);
		final CommerceCartModification cartModification = validateCartEntry(cartModel, cartEntryModel);

		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.PRICING_ERROR, cartModification.getStatusCode());
		assertEquals(configCartModificaton, cartModification);
	}

	@Test
	public void testValidateCartEntryNoStock()
	{
		when(commerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).thenReturn(0L);
		final CommerceCartModification cartModification = validateCartEntry(cartModel, cartEntryModel);

		assertEquals(CommerceCartModificationStatus.NO_STOCK, cartModification.getStatusCode());
	}
}
