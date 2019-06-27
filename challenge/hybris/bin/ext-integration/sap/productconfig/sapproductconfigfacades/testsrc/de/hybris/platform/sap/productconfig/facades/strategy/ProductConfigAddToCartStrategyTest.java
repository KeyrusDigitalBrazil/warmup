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
package de.hybris.platform.sap.productconfig.facades.strategy;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigAddToCartStrategyTest
{
	private static final String CONFIG_ID = "config id";
	private ProductConfigAddToCartStrategy classUnderTest;
	private CommerceCartParameter params;
	private ProductModel product;
	private CartModel cart;
	@Mock
	private CartEntryModel cartEntry;
	private final CommerceCartModification commerceCartModification = new CommerceCartModification();

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private ProductService productService;

	@Mock
	private CartService cartService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CommerceStockService commerceStockService;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigAddToCartStrategy();
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setProductService(productService);
		classUnderTest.setCartService(cartService);
		classUnderTest.setBaseStoreService(baseStoreService);
		classUnderTest.setCommerceStockService(commerceStockService);
		classUnderTest.setModelService(modelService);

		createValidEntity();
		createCartEntry();
		Mockito.when(cartService.addNewEntry(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.any(UnitModel.class),
				Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(cartEntry);

	}

	private void createCartEntry()
	{
		Mockito.when(cartEntry.getPk()).thenReturn(PK.fromLong(1));
	}

	private void createValidEntity()
	{
		params = new CommerceCartParameter();
		cart = new CartModel();
		params.setCart(cart);
		product = new ProductModel();
		params.setProduct(product);
		params.setQuantity(1);
		params.setConfigId(CONFIG_ID);

		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(true);
	}

	@Test
	public void testValidate_OK() throws CommerceCartModificationException
	{
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_cartNull() throws CommerceCartModificationException
	{
		params.setCart(null);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_productNull() throws CommerceCartModificationException
	{
		params.setProduct(null);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidate_quantityZero() throws CommerceCartModificationException
	{
		params.setQuantity(0);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidate_hybrisBaseProduct() throws CommerceCartModificationException
	{
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(false);
		product.setVariantType(new VariantTypeModel());
		classUnderTest.validateAddToCart(params);
	}

	public void testValidate_cpqBaseProductOK() throws CommerceCartModificationException
	{
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(true);
		product.setVariantType(new VariantTypeModel());
		classUnderTest.validateAddToCart(params);
	}

	@Test
	public void testDoAddToCart() throws CommerceCartModificationException
	{
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy)
				.setConfigIdForCartEntry(Mockito.eq(cartEntry.getPk().toString()), Mockito.eq(CONFIG_ID));
	}

	@Test
	public void testDoAddToCartNoConfigurableProduct() throws CommerceCartModificationException
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(false);
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy, Mockito.times(0)).setConfigIdForCartEntry(Mockito.any(),
				Mockito.any());
	}

	@Test
	public void testDoAddToCartChangeableVariant() throws CommerceCartModificationException
	{
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(false);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(any())).thenReturn(true);
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy)
				.setConfigIdForCartEntry(Mockito.eq(cartEntry.getPk().toString()), Mockito.eq(CONFIG_ID));
	}
}
