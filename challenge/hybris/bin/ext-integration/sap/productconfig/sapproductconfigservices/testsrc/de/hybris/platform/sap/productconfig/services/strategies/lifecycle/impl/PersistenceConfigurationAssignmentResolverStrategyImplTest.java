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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PersistenceConfigurationAssignmentResolverStrategyImplTest
{

	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "PRODUCT_CODE";
	private static final String ORDER_ENTRY_PRODUCT_CODE = "ORDER_ENTRY_PRODUCT_CODE";
	private static final String CONFIG_PRODUCT_CODE = "CONFIG_PRODUCT_CODE";
	private static final String BASE_PRODUCT = "BASE_PRODUCT";

	private final OrderModel order = new OrderModel();
	private final QuoteModel quote = new QuoteModel();

	private final OrderEntryModel orderEntry = new OrderEntryModel();
	private final OrderEntryModel orderEntryToBeFiltered = new OrderEntryModel();
	private final QuoteEntryModel quoteEntry = new QuoteEntryModel();

	private final CartEntryModel cartEntry = new CartEntryModel();
	private final CartEntryModel savedCartEntry = new CartEntryModel();

	private final CartModel cart = new CartModel();
	private final CartModel savedCart = new CartModel();
	private final ProductConfigurationModel productConfigModel = new ProductConfigurationModel();
	private final Collection<ProductModel> products = new ArrayList<>();
	private final ConfigModel configModel = new ConfigModelImpl();

	@Mock
	private CartService cartService;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;
	@Mock
	private ConfigurationModelCacheStrategy configModelCache;
	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;

	@InjectMocks
	private PersistenceConfigurationAssignmentResolverStrategyImpl classUnderTest = new PersistenceConfigurationAssignmentResolverStrategyImpl();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cart.setCode("111");
		savedCart.setCode("222");
		cartEntry.setOrder(cart);
		savedCartEntry.setOrder(savedCart);
		orderEntry.setOrder(order);
		order.setCreationtime(new Date());
		order.setCode("333");
		quote.setCreationtime(new Date());
		quote.setCode("444");


		final OrderModel orderWithVersionID = new OrderModel();
		orderWithVersionID.setCreationtime(new Date());
		orderWithVersionID.setCode("333");
		orderWithVersionID.setVersionID("xxxx");

		orderEntryToBeFiltered.setOrder(orderWithVersionID);
		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		productConfigModel.setProduct(products);
		given(cartService.getSessionCart()).willReturn(cart);

		given(configurationVariantUtil.isCPQNotChangeableVariantProduct(any())).willReturn(Boolean.FALSE);
		configModel.setId(CONFIG_ID);
		configModel.setKbKey(new KBKeyImpl(CONFIG_PRODUCT_CODE));
	}


	@Test
	public void testRetrieveRelatedObjectTypeOrderEntry()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(orderEntry));
		assertEquals(ProductConfigurationRelatedObjectType.ORDER_ENTRY, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeQuoteEntry()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(quoteEntry));
		assertEquals(ProductConfigurationRelatedObjectType.QUOTE_ENTRY, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeCartEntry()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(cartEntry));
		assertEquals(ProductConfigurationRelatedObjectType.CART_ENTRY, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeSavedCartEntry()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(savedCartEntry));
		assertEquals(ProductConfigurationRelatedObjectType.SAVEDCART_ENTRY, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeOrder()
	{
		assertEquals(ProductConfigurationRelatedObjectType.ORDER_ENTRY, classUnderTest.retrieveRelatedObjectType(order));
	}

	@Test
	public void testRetrieveRelatedObjectTypeQuote()
	{
		assertEquals(ProductConfigurationRelatedObjectType.QUOTE_ENTRY, classUnderTest.retrieveRelatedObjectType(quote));
	}

	@Test
	public void testRetrieveRelatedObjectTypeCart()
	{
		assertEquals(ProductConfigurationRelatedObjectType.CART_ENTRY, classUnderTest.retrieveRelatedObjectType(cart));
	}

	@Test
	public void testRetrieveRelatedObjectTypeSavedCart()
	{
		assertEquals(ProductConfigurationRelatedObjectType.CART_ENTRY, classUnderTest.retrieveRelatedObjectType(savedCart));
	}

	@Test
	public void testRetrieveRelatedObjectTypeNull()
	{
		final AbstractOrderModel orderNull = null;
		assertEquals(ProductConfigurationRelatedObjectType.UNKNOWN, classUnderTest.retrieveRelatedObjectType(orderNull));
	}


	@Test
	public void testRetrieveRelatedObjectTypeProduct()
	{
		products.add(new ProductModel());
		assertEquals(ProductConfigurationRelatedObjectType.PRODUCT, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeUnknownNoRElation()
	{
		assertEquals(ProductConfigurationRelatedObjectType.UNKNOWN, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeUnknown()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID))
				.willReturn(Collections.singletonList(new AbstractOrderEntryModel()));
		assertEquals(ProductConfigurationRelatedObjectType.UNKNOWN, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testIsSessionCartEntryTrue()
	{
		assertTrue(classUnderTest.isSessionCartEntry(cartEntry));
	}

	@Test
	public void testIsSessionCartEntryFalse()
	{
		assertFalse(classUnderTest.isSessionCartEntry(savedCartEntry));
	}

	@Test
	public void testIsSessionCartTrue()
	{
		assertTrue(classUnderTest.isSessionCart(cart));
	}

	@Test
	public void testIsSessionCartFalse()
	{
		assertFalse(classUnderTest.isSessionCart(savedCart));
	}

	@Test
	public void testDoesCartCodeBelongToSessionCartTrue()
	{
		assertTrue(classUnderTest.doesCartCodeBelongsToSessionCart(cart.getCode()));
	}

	@Test
	public void testDoesCartCodeBelongToSessionCartFalse()
	{
		assertFalse(classUnderTest.doesCartCodeBelongsToSessionCart(savedCart.getCode()));
	}

	@Test
	public void testRetrieveCreationDateForRelatedEntry()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(orderEntry));
		final Date result = classUnderTest.retrieveCreationDateForRelatedEntry(CONFIG_ID);
		assertNotNull(result);
		assertEquals(order.getCreationtime(), result);
	}

	@Test
	public void testRetrieveCreationDateForRelatedEntryNull()
	{
		assertNull(classUnderTest.retrieveCreationDateForRelatedEntry(CONFIG_ID));
	}

	@Test
	public void testRetrieveOrderEntryNone()
	{
		final AbstractOrderEntryModel retrieveOrderEntry = classUnderTest.retrieveOrderEntry("xxx");
		assertNull(retrieveOrderEntry);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void testRetrieveOrderEntryToMany()
	{
		final List<AbstractOrderEntryModel> list = new ArrayList<>();
		list.add(cartEntry);
		list.add(orderEntry);
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(list);
		classUnderTest.retrieveOrderEntry(CONFIG_ID);
	}

	@Test
	public void testRetrieveOrderFilterOrder()
	{
		final List<AbstractOrderEntryModel> list = new ArrayList<>();
		list.add(orderEntry);
		list.add(orderEntryToBeFiltered);
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(list);
		final AbstractOrderEntryModel retrieveOrderEntry = classUnderTest.retrieveOrderEntry(CONFIG_ID);
		assertSame(orderEntry, retrieveOrderEntry);
	}


	@Test
	public void testRetrieveRelatedProductCodeOrderEntry()
	{
		final ProductModel product = new ProductModel();
		product.setCode(ORDER_ENTRY_PRODUCT_CODE);
		orderEntry.setProduct(product);
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(orderEntry));
		assertEquals(ORDER_ENTRY_PRODUCT_CODE, classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}


	@Test
	public void testRetrieveRelatedProductCodeProduct()
	{
		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		products.add(product);
		assertEquals(PRODUCT_CODE, classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedProductCodeVariantProduct()
	{
		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		orderEntry.setProduct(product);
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(orderEntry));
		given(configurationVariantUtil.isCPQNotChangeableVariantProduct(product)).willReturn(Boolean.TRUE);
		given(configurationVariantUtil.getBaseProductCode(product)).willReturn(BASE_PRODUCT);
		assertEquals(BASE_PRODUCT, classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}


	@Test
	public void testRetrieveRelatedProductFromConfigModel()
	{
		given(configModelCache.getConfigurationModelEngineState(CONFIG_ID)).willReturn(configModel);
		assertEquals(CONFIG_PRODUCT_CODE, classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveRelatedProductCodeNull()
	{
		classUnderTest.retrieveRelatedProductCode(CONFIG_ID);
	}
}
