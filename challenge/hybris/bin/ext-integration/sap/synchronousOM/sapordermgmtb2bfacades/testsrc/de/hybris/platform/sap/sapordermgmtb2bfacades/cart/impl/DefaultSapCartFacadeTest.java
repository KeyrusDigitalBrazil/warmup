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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
//import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.DefaultSapCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSapCartFacadeTest
{

	private static final String CART = "cart";
	private static final String PRODUCT_CODE = "375205";
	private static final String ITTEM_KEY = "itemKey";
	private static final long QUANTITY = 1;
	private static final int ENTRY_NUMBER = 1;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private AbstractPopulatingConverter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private CartService sapCartService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private AbstractPopulatingConverter<CartModel, CartData> cartConverter;
	@Mock
	private AbstractPopulatingConverter<CartModel, CartData> miniCartConverter;
	@Mock
	private ProductService productService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private DeliveryService deliveryService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BackendAvailabilityService backendAvailabilityService;
	@Mock
	private CartRestorationFacade cartRestorationFacade;
	@Mock
	private de.hybris.platform.b2b.services.B2BCartService b2bCartService;

	private DefaultSapCartFacade sapCartFacade;

	private CartModificationData cartModificationData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sapCartFacade = new DefaultSapCartFacade()
		{
			@Override
			protected void mergeOrderEntryWithModelConfiguration(final OrderEntryData orderEntry)
			{
				//need to disable functionality
			}

		};

		sapCartFacade.setSapCartService(sapCartService);
		sapCartFacade.setCartService(b2bCartService);
		sapCartFacade.setCartConverter(cartConverter);
		sapCartFacade.setMiniCartConverter(miniCartConverter);
		sapCartFacade.setProductService(productService);
		sapCartFacade.setCommerceCartService(commerceCartService);
		sapCartFacade.setCartModificationConverter(cartModificationConverter);
		sapCartFacade.setDeliveryService(deliveryService);
		sapCartFacade.setBaseSiteService(baseSiteService);
		sapCartFacade.setUserService(userService);
		sapCartFacade.setBaseStoreService(baseStoreService);
		sapCartFacade.setBackendAvailabilityService(backendAvailabilityService);
		sapCartFacade.setCartRestorationFacade(cartRestorationFacade);

		final ProductImageHelper productImageHelper = Mockito.mock(ProductImageHelper.class);
		sapCartFacade.setProductImageHelper(productImageHelper);

		final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
		sapConfigurationModel.setSapordermgmt_enabled(true);

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setSAPConfiguration(sapConfigurationModel);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);

		setInitData();

	}

	private void setInitData()
	{
		given(Boolean.valueOf(sapCartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(backendAvailabilityService.isBackendDown())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(b2bCartService.hasSessionCart())).willReturn(Boolean.TRUE);

		final CartData cartData = new CartData();
		cartData.setCode(CART);
		final CartModel cartModel = new CartModel();
		cartModel.setCode(CART);
		cartModel.setGuid(CART);

		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setEntryNumber(Integer.valueOf(ENTRY_NUMBER));
		orderEntryData.setQuantity(Long.valueOf(QUANTITY));
		orderEntryData.setItemPK(ITTEM_KEY);
		orderEntryData.setItemPK(ITTEM_KEY);
		final ProductData product = new ProductData();
		product.setCode(PRODUCT_CODE);
		orderEntryData.setProduct(product);
		cartData.setEntries(Arrays.asList(orderEntryData));

		given(cartConverter.convert(cartModel)).willReturn(cartData);
		given(sapCartService.getSessionCart()).willReturn(cartData);
		given(sapCartService.getSessionCart(true)).willReturn(cartData);
		given(b2bCartService.getSessionCart()).willReturn(cartModel);

		final BaseSiteModel mockBaseSite = new BaseSiteModel();
		final UserModel mockUser = new UserModel();
		given(baseSiteService.getCurrentBaseSite()).willReturn(mockBaseSite);
		given(userService.getCurrentUser()).willReturn(mockUser);

		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("EUR");
		curr.setSymbol("$");
		curr.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");
		given(commonI18NService.getCurrency(anyString())).willReturn(curr);
		given(commonI18NService.getCurrentCurrency()).willReturn(curr);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);

		cartModificationData = new CartModificationData();
		cartModificationData.setEntry(orderEntryData);
		given(sapCartService.updateCartEntry(ENTRY_NUMBER, QUANTITY)).willReturn(cartModificationData);
		given(sapCartService.updateCartEntry(ENTRY_NUMBER, QUANTITY)).willReturn(cartModificationData);
		given(sapCartService.addToCart("1", QUANTITY)).willReturn(cartModificationData);

	}





	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#addToCart(java.lang.String, long)}
	 * .
	 *
	 * @throws CommerceCartModificationException
	 *
	 */
	@SuppressWarnings("javadoc")
	@Test
	public void testAddToCartStringLong() throws CommerceCartModificationException
	{
		given(sapCartService.addToCart(PRODUCT_CODE, QUANTITY)).willReturn(cartModificationData);
		final CartModificationData modificationData = sapCartFacade.addToCart(PRODUCT_CODE, QUANTITY);
		assertEquals(PRODUCT_CODE, modificationData.getEntry().getProduct().getCode());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#addToCart(java.lang.String, long, java.lang.String)}
	 * .
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartStringLongString() throws CommerceCartModificationException
	{
		given(sapCartService.addToCart(PRODUCT_CODE, QUANTITY)).willReturn(cartModificationData);
		final CartModificationData modificationData = sapCartFacade.addToCart(PRODUCT_CODE, QUANTITY, "StoreId");
		assertEquals(PRODUCT_CODE, modificationData.getEntry().getProduct().getCode());

	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#estimateExternalTaxes(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testEstimateExternalTaxes()
	{
		sapCartFacade.estimateExternalTaxes("deliveryZipCode", "countryIsoCode");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getBackendAvailabilityService()}
	 * .
	 */
	@Test
	public void testGetBackendAvailabilityService()
	{
		assertEquals(backendAvailabilityService, sapCartFacade.getBackendAvailabilityService());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getBaseStoreService()}.
	 */
	@Test
	public void testGetBaseStoreService()
	{
		assertEquals(baseStoreService, sapCartFacade.getBaseStoreService());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getCartRestorationFacade()}
	 * .
	 */
	@Test
	public void testGetCartRestorationFacade()
	{
		assertEquals(cartRestorationFacade, sapCartFacade.getCartRestorationFacade());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getCartsForCurrentUser()}
	 * .
	 */
	@Test
	public void testGetCartsForCurrentUser()
	{
		assertEquals(Arrays.asList(sapCartService.getSessionCart()), sapCartFacade.getCartsForCurrentUser());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getDeliveryCountries()}.
	 */
	@Test
	public void testGetDeliveryCountries()
	{
		assertEquals(Collections.emptyList(), sapCartFacade.getDeliveryCountries());
	}



	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getMiniCart()}.
	 */
	@Test
	public void testGetMiniCart()
	{
		final CartData cart = sapCartFacade.getSessionCart();
		Assert.assertEquals(CART, cart.getCode());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getProductImageHelper()}.
	 */
	@Test
	public void testGetProductImageHelper()
	{
		assertNotNull(sapCartFacade.getProductImageHelper());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getSapCartService()}.
	 */
	@Test
	public void testGetSapCartService()
	{
		assertEquals(sapCartService, sapCartFacade.getSapCartService());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getSessionCart()}.
	 */
	@Test
	public void testGetSessionCart()
	{
		final CartData cart = sapCartFacade.getSessionCart();
		assertEquals(CART, cart.getCode());

	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#getSessionCartWithEntryOrdering(boolean)}
	 * .
	 */
	@Test
	public void testGetSessionCartWithEntryOrdering()
	{
		final CartData cart = sapCartFacade.getSessionCartWithEntryOrdering(true);
		assertEquals(CART, cart.getCode());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#hasEntries()}.
	 */
	@Test
	public void testHasEntries()
	{
		assertEquals(Boolean.valueOf(true), Boolean.valueOf(sapCartFacade.hasEntries()));
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#hasSessionCart()}.
	 */
	@Test
	public void testHasSessionCart()
	{
		assertEquals(Boolean.valueOf(true), Boolean.valueOf(sapCartFacade.hasSessionCart()));
	}



	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#removeSessionCart()}.
	 */
	@Test
	public void testRemoveSessionCart()
	{
		sapCartFacade.removeSessionCart();
		verify(sapCartService, times(1)).removeSessionCart();
		verify(cartRestorationFacade, times(1)).removeSavedCart();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#removeStaleCarts()}.
	 */
	@Test
	public void testRemoveStaleCarts()
	{
		sapCartFacade.removeStaleCarts();
		verify(commerceCartService, times(0)).removeStaleCarts(null);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#restoreAnonymousCartAndMerge(java.lang.String, java.lang.String)}
	 * .
	 *
	 * @throws CommerceCartRestorationException
	 * @throws CommerceCartMergingException
	 */
	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testRestoreAnonymousCartAndMerge() throws CommerceCartMergingException, CommerceCartRestorationException
	{
		sapCartFacade.restoreAnonymousCartAndMerge("fromAnonumousCartGuid", "toUserCartGuid");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#restoreAnonymousCartAndTakeOwnership(java.lang.String)}
	 * .
	 *
	 * @throws CommerceCartRestorationException
	 */
	@Test
	public void testRestoreAnonymousCartAndTakeOwnership() throws CommerceCartRestorationException
	{
		assertEquals(null, sapCartFacade.restoreAnonymousCartAndTakeOwnership(null));
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#restoreCartAndMerge(java.lang.String, java.lang.String)}
	 * .
	 *
	 * @throws CommerceCartMergingException
	 * @throws CommerceCartRestorationException
	 */
	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testRestoreCartAndMerge() throws CommerceCartRestorationException, CommerceCartMergingException
	{
		sapCartFacade.restoreCartAndMerge("fromUserCartGuid", "toUserCartGuid");

	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#restoreSavedCart(java.lang.String)}
	 * .
	 *
	 * @throws CommerceCartRestorationException
	 */
	@Test
	public void testRestoreSavedCart() throws CommerceCartRestorationException
	{
		final CartRestorationData cartRestorationData = new CartRestorationData();
		given(cartRestorationFacade.restoreSavedCart("CartGuid", userService.getCurrentUser())).willReturn(cartRestorationData);
		final CartRestorationData actualCartRestorationData = sapCartFacade.restoreSavedCart("CartGuid");
		assertEquals(cartRestorationData, actualCartRestorationData);


	}



	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#updateCartEntry(long, long)}
	 * .
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testUpdateCartEntryLongLong() throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData = sapCartFacade.updateCartEntry(ENTRY_NUMBER, QUANTITY);
		assertEquals(PRODUCT_CODE, cartModificationData.getEntry().getProduct().getCode());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#updateCartEntry(long, java.lang.String)}
	 * .
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateCartEntryLongString() throws CommerceCartModificationException
	{
		sapCartFacade.updateCartEntry(ENTRY_NUMBER, "storeId");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#updateOrderEntry(de.hybris.platform.commercefacades.order.data.OrderEntryData)}
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testUpdateOrderEntry() throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData = sapCartFacade
				.updateCartEntry(sapCartService.getSessionCart().getEntries().get(0));
		assertEquals(PRODUCT_CODE, cartModificationData.getEntry().getProduct().getCode());
	}


	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#validateCartData()}.
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testValidateCartData() throws CommerceCartModificationException
	{
		sapCartFacade.validateCartData();
		verify(sapCartService, times(1)).validateCartData();

	}

}
