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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
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
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SapOrdermgmtB2BCartFacadeTest
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


	@Mock
	private de.hybris.platform.commercefacades.order.CartFacade sapCartFacade;

	private SapOrdermgmtB2BCartFacade sapOrdermgmtB2BCartFacade;

	private CartModificationData cartModificationData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		/**
		 * private CartService sapCartService; private CartRestorationFacade cartRestorationFacade; private
		 * BackendAvailabilityService backendAvailabilityService; private ProductConfigurationService
		 * productConfigurationService; private ConfigurationProviderFactory configurationProviderFactory; private
		 * BaseStoreService baseStoreService; private CartFacade sapCartFacade;
		 */

		sapOrdermgmtB2BCartFacade = new SapOrdermgmtB2BCartFacade()
		{

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#isSyncOrdermgmtEnabled()
			 */
			@Override
			protected boolean isSyncOrdermgmtEnabled()
			{

				return true;
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCartFacade#isValidEntry(de.hybris.platform
			 * .commercefacades.order.data.OrderEntryData)
			 */
			@Override
			protected boolean isValidEntry(final OrderEntryData cartEntry)
			{
				// YTODO Auto-generated method stub
				return true;
			}

		};
		sapOrdermgmtB2BCartFacade.setSapCartService(sapCartService);
		sapOrdermgmtB2BCartFacade.setCartService(b2bCartService);

		sapOrdermgmtB2BCartFacade.setBackendAvailabilityService(backendAvailabilityService);
		sapOrdermgmtB2BCartFacade.setCartRestorationFacade(cartRestorationFacade);
		sapOrdermgmtB2BCartFacade.setSapCartFacade(sapCartFacade);


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


		given(sapCartFacade.getSessionCart()).willReturn(cartData);


	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#addOrderEntry(de.hybris.platform.commercefacades.order.data.OrderEntryData)}
	 * .
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddOrderEntry() throws CommerceCartModificationException
	{

		final OrderEntryData cartEntry = OrderEntryDataMockBuilder.create().withStandardQuantity(Long.valueOf(1))
				.withStandardProductCode(PRODUCT_CODE).build();

		given(sapCartFacade.addToCart(PRODUCT_CODE, QUANTITY)).willReturn(cartModificationData);

		final CartModificationData modificationData = sapOrdermgmtB2BCartFacade.addOrderEntry(cartEntry);

		assertEquals(PRODUCT_CODE, modificationData.getEntry().getProduct().getCode());

	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacade#addOrderEntryList(java.util.List)}
	 * .
	 */
	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testAddOrderEntryList()
	{
		sapOrdermgmtB2BCartFacade.addOrderEntryList(null);
	}

}
