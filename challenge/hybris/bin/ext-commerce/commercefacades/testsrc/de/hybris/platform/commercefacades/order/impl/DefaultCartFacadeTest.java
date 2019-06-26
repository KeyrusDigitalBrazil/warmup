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
package de.hybris.platform.commercefacades.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.strategies.merge.ProductConfigurationMergeStrategy;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;


@UnitTest
public class DefaultCartFacadeTest
{


	private DefaultCartFacade defaultCartFacade;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private AbstractPopulatingConverter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private CartService cartService;
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
	private Converter<CountryModel, CountryData> countryConverter;
	@Mock
	private ProductConfigurationMergeStrategy productConfigurationMergeStrategy;
	@Mock
	private ProductFacade productFacade;
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter;

	private CartModel cartModel;
	private CartModel cartModel1;

	private static final String PRODUCT_CODE = "prodCode";
	private static final String SESSION_CART_GUID = "SESSION_CART_GUID";
	private static final String CART = "cart";
	private static final String CART1 = "cart1";
	private static final ConfiguratorType TEXTFIELD_CONFIGURATOR_TYPE = ConfiguratorType.valueOf("TEXTFIELD");
	private static final ConfiguratorType RADIOBUTTON_CONFIGURATOR_TYPE = ConfiguratorType.valueOf("RADIOBUTTON");

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultCartFacade = new DefaultCartFacade();
		defaultCartFacade.setCartService(cartService);
		defaultCartFacade.setCartConverter(cartConverter);
		defaultCartFacade.setMiniCartConverter(miniCartConverter);
		defaultCartFacade.setProductService(productService);
		defaultCartFacade.setCommerceCartService(commerceCartService);
		defaultCartFacade.setCartModificationConverter(cartModificationConverter);
		defaultCartFacade.setCountryConverter(countryConverter);
		defaultCartFacade.setDeliveryService(deliveryService);
		defaultCartFacade.setBaseSiteService(baseSiteService);
		defaultCartFacade.setUserService(userService);
		defaultCartFacade.setProductFacade(productFacade);
		defaultCartFacade.setModelService(modelService);
		defaultCartFacade.setCartRestorationConverter(cartRestorationConverter);
		defaultCartFacade.setProductConfigurationMergeStrategies(ImmutableMap.of(TEXTFIELD_CONFIGURATOR_TYPE,
				productConfigurationMergeStrategy, RADIOBUTTON_CONFIGURATOR_TYPE, productConfigurationMergeStrategy));
		defaultCartFacade.setCommerceCartParameterConverter(commerceCartParameterConverter);
		defaultCartFacade.setOrderEntryConverter(orderEntryConverter);
		cartModel = new CartModel();
		cartModel.setCode(CART);
		cartModel.setGuid(CART);
		cartModel.setEntries(Collections.emptyList());
		cartModel1 = new CartModel();
		cartModel1.setCode(CART1);
		cartModel1.setGuid(CART1);
		cartModel1.setEntries(Collections.emptyList());
		final CartData cartData = new CartData();
		cartData.setCode(CART);
		final CartData cartData1 = new CartData();
		cartData1.setCode(CART1);

		given(cartConverter.convert(cartModel)).willReturn(cartData);
		given(cartConverter.convert(cartModel1)).willReturn(cartData1);
		given(miniCartConverter.convert(cartModel)).willReturn(cartData);
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willReturn(new CommerceCartParameter());
	}

	@Test
	public void testGetSessionCart()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);

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

		final CartData cart = defaultCartFacade.getSessionCart();
		Assert.assertEquals(CART, cart.getCode());
	}

	@Test
	public void testGetSessionCartNull()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);

		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("EUR");
		curr.setSymbol("$");
		curr.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");
		final CartData emptyCart = new CartData();

		given(miniCartConverter.convert(null)).willReturn(emptyCart);
		given(commonI18NService.getCurrency(anyString())).willReturn(curr);
		given(commonI18NService.getCurrentCurrency()).willReturn(curr);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);


		final CartData cart = defaultCartFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertEquals(emptyCart, cart);
	}

	@Test
	public void testHasSessionCartFalse()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);
		final boolean hasCart = defaultCartFacade.hasSessionCart();
		Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(hasCart));
	}

	@Test
	public void testHasSessionCartTrue()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		final boolean hasCart = defaultCartFacade.hasSessionCart();
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(hasCart));
	}

	@Test
	public void testGetMiniCart()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		final CartData cart = defaultCartFacade.getMiniCart();
		Assert.assertEquals(CART, cart.getCode());
	}

	@Test
	public void testGetMiniCartEmpty()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);

		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("EUR");
		curr.setSymbol("$");
		curr.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");
		final CartData emptyCart = new CartData();

		given(miniCartConverter.convert(null)).willReturn(emptyCart);
		given(commonI18NService.getCurrency(anyString())).willReturn(curr);
		given(commonI18NService.getCurrentCurrency()).willReturn(curr);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);

		final CartData cart = defaultCartFacade.getMiniCart();
		Assert.assertNotNull(cart);
		Assert.assertEquals(emptyCart, cart);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willAnswer(invocationOnMock -> {
			final AddToCartParams source = (AddToCartParams) invocationOnMock.getArguments()[0];
			final CommerceCartParameter result = new CommerceCartParameter();
			final ProductModel product = mock(ProductModel.class);
			given(product.getCode()).willReturn(source.getProductCode());
			result.setQuantity(source.getQuantity());
			result.setProduct(product);
			return result;
		});
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		defaultCartFacade.addToCart(PRODUCT_CODE, 1);

		verify(commerceCartService).addToCart(captor.capture());
		Assert.assertEquals(1L, captor.getValue().getQuantity());
		Assert.assertEquals(PRODUCT_CODE, captor.getValue().getProduct().getCode());
	}

	@Test
	public void addToCartShouldPassPointOfService() throws CommerceCartModificationException
	{
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willAnswer(invocationOnMock -> {
			final AddToCartParams source = (AddToCartParams) invocationOnMock.getArguments()[0];
			final CommerceCartParameter result = new CommerceCartParameter();
			if (source.getStoreId() != null)
			{
				final PointOfServiceModel pos = mock(PointOfServiceModel.class);
				given(pos.getName()).willReturn(source.getStoreId());
				result.setPointOfService(pos);
			}
			return result;
		});
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);

		defaultCartFacade.addToCart(PRODUCT_CODE, 2, "storeId");

		verify(commerceCartService).addToCart(captor.capture());
		Assert.assertEquals("storeId", captor.getValue().getPointOfService().getName());
	}

	@Test
	public void testUpdateCartEntry() throws CommerceCartModificationException
	{
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willAnswer(invocationOnMock -> {
			final AddToCartParams source = (AddToCartParams) invocationOnMock.getArguments()[0];
			final CommerceCartParameter result = new CommerceCartParameter();
			result.setQuantity(source.getQuantity());
			return result;
		});
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		defaultCartFacade.updateCartEntry(3, 1);

		verify(commerceCartService).updateQuantityForCartEntry(captor.capture());
		Assert.assertEquals(1L, captor.getValue().getQuantity());
		Assert.assertEquals(3, captor.getValue().getEntryNumber());
	}

	@Test
	public void updateCartEntryShouldPassPOS() throws CommerceCartModificationException
	{
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willAnswer(invocationOnMock -> {
			final AddToCartParams source = (AddToCartParams) invocationOnMock.getArguments()[0];
			final CommerceCartParameter result = new CommerceCartParameter();
			result.setQuantity(source.getQuantity());
			final PointOfServiceModel pos = mock(PointOfServiceModel.class);
			given(pos.getName()).willReturn(source.getStoreId());
			result.setPointOfService(pos);
			return result;
		});
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);

		defaultCartFacade.updateCartEntry(3, "storeId");

		verify(commerceCartService).updatePointOfServiceForCartEntry(captor.capture());
		Assert.assertEquals(3, captor.getValue().getEntryNumber());
		Assert.assertEquals("storeId", captor.getValue().getPointOfService().getName());
	}

	@Test
	public void testGetDeliveryCountries()
	{
		final CountryModel country = mock(CountryModel.class);
		final List<CountryModel> deliveryCountries = new ArrayList<>();
		deliveryCountries.add(country);
		deliveryCountries.add(country);
		given(deliveryService.getDeliveryCountriesForOrder(null)).willReturn(deliveryCountries);
		given(country.getName()).willReturn("PL");
		given(countryConverter.convertAll(deliveryCountries)).willReturn(Arrays.asList(new CountryData(), new CountryData()));

		final List<CountryData> results = defaultCartFacade.getDeliveryCountries();
		verify(deliveryService).getDeliveryCountriesForOrder(null);
		verify(countryConverter, Mockito.times(2)).convert(country);
		Assert.assertEquals(results.size(), 2);
	}

	@Test
	public void testGetMostRecentCartForUser()
	{
		final BaseSiteModel mockBaseSite = mock(BaseSiteModel.class);
		final UserModel mockUser = mock(UserModel.class);
		given(baseSiteService.getCurrentBaseSite()).willReturn(mockBaseSite);
		given(userService.getCurrentUser()).willReturn(mockUser);
		final String excludedCartsGuid = SESSION_CART_GUID;

		//when there is only one cart for the user
		given(commerceCartService.getCartsForSiteAndUser(mockBaseSite, mockUser)).willReturn(Collections.EMPTY_LIST);

		final String cartGuid = defaultCartFacade.getMostRecentCartGuidForUser(Arrays.asList(excludedCartsGuid));
		Assert.assertNull(cartGuid);

		//when there is more than one cart for the user, it returns the first cart excluding carts in the list
		given(commerceCartService.getCartsForSiteAndUser(mockBaseSite, mockUser)).willReturn(Arrays.asList(cartModel));

		final String secondMostRecentCartGuid = defaultCartFacade.getMostRecentCartGuidForUser(Arrays.asList(excludedCartsGuid));
		Assert.assertEquals(CART, secondMostRecentCartGuid);

		//when there is more than one cart for the user, but excluding list is empty, it returns the first recently modified cart
		given(commerceCartService.getCartsForSiteAndUser(mockBaseSite, mockUser)).willReturn(Arrays.asList(cartModel1));

		final String firstMostRecentCartGuid = defaultCartFacade.getMostRecentCartGuidForUser(Collections.EMPTY_LIST);
		Assert.assertEquals(CART1, firstMostRecentCartGuid);
	}

	@Test
	public void testUpdateOrderEntryForNonMultiD() throws CommerceCartModificationException
	{
		final UnitModel unit = new UnitModel();
		unit.setCode("unit");

		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		// same product, but different entry number
		final OrderEntryData orderEntryData1 = getOrderEntryData(1, 1, "49042000");
		final OrderEntryData orderEntryData2 = getOrderEntryData(2, 1, "49042000");

		final List<OrderEntryData> orderEntryList = new ArrayList<>();
		orderEntryList.add(orderEntryData1);
		orderEntryList.add(orderEntryData2);

		final CartData cartData = new CartData();
		cartData.setEntries(orderEntryList);

		// update the quantity of the second entry to 11
		final OrderEntryData findEntry = getOrderEntryData(2, 11, "49042000");

		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		commerceCartModification.setQuantity(11);

		final CartModificationData cartModificationData = new CartModificationData();
		cartModificationData.setQuantity(11);
		cartModificationData.setEntry(orderEntryData2);

		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(defaultCartFacade.getSessionCart()).willReturn(cartData);
		given(commerceCartService.updateQuantityForCartEntry(Mockito.anyObject())).willReturn(commerceCartModification);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);
		given(productService.getProductForCode(anyString())).willReturn(product);

		final CartModificationData resultCartModificationData = defaultCartFacade.updateCartEntry(findEntry);
		verify(commerceCartService).updateQuantityForCartEntry(Mockito.anyObject());
		Assert.assertEquals(cartModificationData.getQuantity(), resultCartModificationData.getQuantity());
		Assert.assertEquals(Integer.valueOf(2), resultCartModificationData.getEntry().getEntryNumber());
	}

	@Test
	public void testUpdateOrderEntryForMultiDUpdate() throws CommerceCartModificationException
	{
		final ProductModel product = setUpProductModel();

		// main entry as a base product
		final OrderEntryData orderEntryData1 = getOrderEntryData(1, 1, "49042000");

		final List<OrderEntryData> orderEntryList = new ArrayList<>();
		orderEntryList.add(orderEntryData1);

		// sub entry 1 as a variant product1
		final OrderEntryData subOrderEntryData21 = getOrderEntryData(0, 1, "49042000_1");

		// sub entry 1 as a variant product2
		final OrderEntryData subOrderEntryData22 = getOrderEntryData(1, 1, "49042000_2");

		final List<OrderEntryData> orderSubEntryMultiDList = new ArrayList<>();
		orderSubEntryMultiDList.add(subOrderEntryData21);
		orderSubEntryMultiDList.add(subOrderEntryData22);

		orderEntryData1.setEntries(orderSubEntryMultiDList);

		final CartData cartData = new CartData();
		cartData.setEntries(orderEntryList);

		//-1 represents that it's a multi D entry
		final OrderEntryData findEntry = getOrderEntryData(-1, 11, "49042000_2");
		findEntry.setEntries(null); // should be null to add or update

		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		commerceCartModification.setQuantity(11);

		final CartModificationData cartModificationData = new CartModificationData();
		cartModificationData.setQuantity(11); //

		final CartModificationData resultCartModificationData;

		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(defaultCartFacade.getSessionCart()).willReturn(cartData);
		given(commerceCartService.updateQuantityForCartEntry(Mockito.anyObject())).willReturn(commerceCartModification);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);
		given(productService.getProductForCode(anyString())).willReturn(product);

		resultCartModificationData = defaultCartFacade.updateCartEntry(findEntry);
		verify(commerceCartService).updateQuantityForCartEntry(Mockito.anyObject());
		Assert.assertEquals(cartModificationData.getQuantity(), resultCartModificationData.getQuantity());
	}


	@Test
	public void testUpdateOrderEntryMultiDAdd() throws CommerceCartModificationException
	{
		final ProductModel product = setUpProductModel();

		final OrderEntryData existingEntry = getOrderEntryData(0, 2, "490420001");
		final OrderEntryData addEntry = getOrderEntryData(-1, 4, "490420001_01");


		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		commerceCartModification.setQuantity(4);

		final List<OrderEntryData> orderEntryList = new ArrayList<>();
		orderEntryList.add(existingEntry);

		final CartData cartData = new CartData();
		cartData.setEntries(orderEntryList);

		final CartModificationData cartModificationData = new CartModificationData();
		cartModificationData.setQuantity(4);

		final CartModificationData resultCartModificationData;

		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(defaultCartFacade.getSessionCart()).willReturn(cartData);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);
		given(commerceCartService.addToCart(Mockito.anyObject())).willReturn(commerceCartModification);
		given(productService.getProductForCode(anyString())).willReturn(product);

		resultCartModificationData = defaultCartFacade.updateCartEntry(addEntry);
		verify(commerceCartService).addToCart(Mockito.anyObject());
		Assert.assertEquals(cartModificationData.getQuantity(), resultCartModificationData.getQuantity());
	}

	protected OrderEntryData getOrderEntryData(final int entryNumber, final long qty, final String productCode)
	{
		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setEntryNumber(Integer.valueOf(entryNumber));
		orderEntryData.setQuantity(Long.valueOf(qty));
		orderEntryData.setProduct(new ProductData());
		orderEntryData.getProduct().setCode(productCode);

		return orderEntryData;
	}

	@Test
	public void testConfigureCartEntry() throws CommerceCartModificationException
	{
		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);

		final ProductData productData = new ProductData();
		final OrderEntryData orderEntryData = new OrderEntryData();

		final ConfigurationInfoData configurationInfoData1 = new ConfigurationInfoData();
		configurationInfoData1.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationInfoData1.setConfigurationLabel("Accessories");
		configurationInfoData1.setConfigurationValue("Waterproof Case");
		configurationInfoData1.setStatus(ProductInfoStatus.INFO);

		final ConfigurationInfoData configurationInfoData2 = new ConfigurationInfoData();
		configurationInfoData2.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationInfoData2.setConfigurationLabel("Edition");
		configurationInfoData2.setConfigurationValue("Black Edition");
		configurationInfoData2.setStatus(ProductInfoStatus.INFO);

		productData.setCode(PRODUCT_CODE);
		orderEntryData.setConfigurationInfos(Arrays.asList(configurationInfoData1, configurationInfoData2));
		orderEntryData.setEntryNumber(99);
		orderEntryData.setProduct(productData);
		orderEntryData.setQuantity(1L);
		cartData.setEntries(Arrays.asList(orderEntryData));

		final CommerceCartParameter cartParameter = new CommerceCartParameter();
		cartParameter.setCart(cartModel);
		final ArgumentCaptor<AddToCartParams> addToCartParamsCaptor = ArgumentCaptor.forClass(AddToCartParams.class);
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willReturn(cartParameter);

		defaultCartFacade.updateCartEntry(orderEntryData);

		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		verify(commerceCartService).updateQuantityForCartEntry(any());
		verify(commerceCartService).configureCartEntry(captor.capture());
		verify(commerceCartParameterConverter, times(2)).convert(addToCartParamsCaptor.capture());
		verifyNoMoreInteractions(commerceCartService, commerceCartParameterConverter);

		final CommerceCartParameter capturedParameter = captor.getValue();
		final List<ProductConfigurationItem> configurationItems = new ArrayList<>(capturedParameter.getProductConfiguration());
		Assert.assertEquals(cartModel, capturedParameter.getCart());
		Assert.assertEquals(RADIOBUTTON_CONFIGURATOR_TYPE, capturedParameter.getProductConfiguration().iterator().next()
				.getConfiguratorType());
		Assert.assertEquals(2, configurationItems.size());
		Assert.assertEquals("Accessories", configurationItems.get(0).getKey());
		Assert.assertEquals("Waterproof Case", configurationItems.get(0).getValue());
		Assert.assertEquals(ProductInfoStatus.INFO, configurationItems.get(0).getStatus());
		Assert.assertEquals("Edition", configurationItems.get(1).getKey());
		Assert.assertEquals("Black Edition", configurationItems.get(1).getValue());
		Assert.assertEquals(ProductInfoStatus.INFO, configurationItems.get(1).getStatus());
		Assert.assertNull(defaultCartFacade.updateCartEntry(orderEntryData));
		verifyAddToCartParams(addToCartParamsCaptor);
	}

	@Test
	public void testConfigureCartEntryMultipleConfigurators() throws CommerceCartModificationException
	{
		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);

		final ProductData productData = new ProductData();
		final OrderEntryData orderEntryData = new OrderEntryData();

		final ConfigurationInfoData configurationInfoData1 = new ConfigurationInfoData();
		configurationInfoData1.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationInfoData1.setConfigurationLabel("Accessories");
		configurationInfoData1.setConfigurationValue("Waterproof Case");
		configurationInfoData1.setStatus(ProductInfoStatus.INFO);

		final ConfigurationInfoData configurationInfoData2 = new ConfigurationInfoData();
		configurationInfoData2.setConfiguratorType(TEXTFIELD_CONFIGURATOR_TYPE);
		configurationInfoData2.setConfigurationLabel("Edition");
		configurationInfoData2.setConfigurationValue("Black Edition");
		configurationInfoData2.setStatus(ProductInfoStatus.INFO);

		productData.setCode(PRODUCT_CODE);
		orderEntryData.setConfigurationInfos(Arrays.asList(configurationInfoData1, configurationInfoData2));
		orderEntryData.setEntryNumber(99);
		orderEntryData.setProduct(productData);
		orderEntryData.setQuantity(1L);
		cartData.setEntries(Collections.singletonList(orderEntryData));

		final CommerceCartParameter cartParameter = new CommerceCartParameter();
		cartParameter.setCart(cartModel);
		final ArgumentCaptor<AddToCartParams> addToCartParamsCaptor = ArgumentCaptor.forClass(AddToCartParams.class);
		given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willReturn(cartParameter);

		defaultCartFacade.updateCartEntry(orderEntryData);

		final ArgumentCaptor<CommerceCartParameter> commerceCartParamsCaptor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		verify(commerceCartService).updateQuantityForCartEntry(any());
		verify(commerceCartService, times(1)).configureCartEntry(commerceCartParamsCaptor.capture());
		verify(commerceCartParameterConverter, times(2)).convert(addToCartParamsCaptor.capture());
		verifyNoMoreInteractions(commerceCartService, commerceCartParameterConverter);

		final List<CommerceCartParameter> cartParametersList = commerceCartParamsCaptor.getAllValues();
		Assert.assertEquals(1, cartParametersList.size());
		Assert.assertEquals(cartModel, cartParametersList.get(0).getCart());
		Assert.assertEquals(RADIOBUTTON_CONFIGURATOR_TYPE, cartParametersList.get(0).getProductConfiguration().iterator().next()
				.getConfiguratorType());
		Assert.assertEquals(2, cartParametersList.get(0).getProductConfiguration().size());
		Assert.assertNull(defaultCartFacade.updateCartEntry(orderEntryData));
		verifyAddToCartParams(addToCartParamsCaptor);
	}

	@Test
	public void testMergeProductConfigurations() throws CommerceCartModificationException
	{
		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);

		final ProductData productData = new ProductData();
		final OrderEntryData orderEntryData = new OrderEntryData();

		final ConfigurationInfoData configurationInfoData1 = new ConfigurationInfoData();
		configurationInfoData1.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationInfoData1.setConfigurationLabel("Accessories");
		configurationInfoData1.setConfigurationValue("Waterproof Case");
		configurationInfoData1.setStatus(ProductInfoStatus.SUCCESS);
		final List<ConfigurationInfoData> orderEntryConfiguration = Collections.singletonList(configurationInfoData1);

		productData.setCode("prodCode");
		orderEntryData.setEntryNumber(99);
		orderEntryData.setProduct(productData);
		orderEntryData.setQuantity(1L);
		orderEntryData.setConfigurationInfos(orderEntryConfiguration);
		cartData.setEntries(Collections.singletonList(orderEntryData));

		final ConfigurationInfoData configurationInfoData2 = new ConfigurationInfoData();
		configurationInfoData2.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationInfoData2.setConfigurationLabel("Color");
		configurationInfoData2.setConfigurationValue("Black");
		configurationInfoData2.setStatus(ProductInfoStatus.SUCCESS);
		final List<ConfigurationInfoData> modelConfiguration = Collections.singletonList(configurationInfoData2);

		given(productConfigurationMergeStrategy.merge(anyList(), anyList())).willReturn(Collections.emptyList());
		given(productFacade.getConfiguratorSettingsForCode(anyString())).willReturn(modelConfiguration);

		final CartModificationData result = defaultCartFacade.updateCartEntry(orderEntryData);

		Assert.assertEquals(null, result);
		verify(productConfigurationMergeStrategy).merge(orderEntryConfiguration, modelConfiguration);
		verify(productConfigurationMergeStrategy).merge(Collections.emptyList(), Collections.emptyList());
		verify(productFacade).getConfiguratorSettingsForCode(PRODUCT_CODE);
		verifyNoMoreInteractions(productConfigurationMergeStrategy, productFacade);
	}

	@Test
	public void testMergeWithEmptyOrderEntryConfiguration() throws CommerceCartModificationException
	{
		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);

		final ProductData productData = new ProductData();
		final OrderEntryData orderEntryData = new OrderEntryData();

		final List<ConfigurationInfoData> emptyList = Collections.emptyList();

		productData.setCode(PRODUCT_CODE);
		orderEntryData.setEntryNumber(99);
		orderEntryData.setProduct(productData);
		orderEntryData.setQuantity(1L);
		orderEntryData.setConfigurationInfos(emptyList);
		cartData.setEntries(Collections.singletonList(orderEntryData));

		final ConfigurationInfoData configurationInfoData = new ConfigurationInfoData();
		configurationInfoData.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationInfoData.setConfigurationLabel("Color");
		configurationInfoData.setConfigurationValue("Black");
		configurationInfoData.setStatus(ProductInfoStatus.SUCCESS);
		final List<ConfigurationInfoData> modelConfiguration = Collections.singletonList(configurationInfoData);

		given(productConfigurationMergeStrategy.merge(anyList(), anyList())).willReturn(Collections.emptyList());
		given(productFacade.getConfiguratorSettingsForCode(anyString())).willReturn(modelConfiguration);

		final CartModificationData result = defaultCartFacade.updateCartEntry(orderEntryData);

		Assert.assertEquals(null, result);
		verify(productConfigurationMergeStrategy).merge(emptyList, modelConfiguration);
		verify(productConfigurationMergeStrategy).merge(emptyList, emptyList);
		verify(productFacade).getConfiguratorSettingsForCode(PRODUCT_CODE);
		verifyNoMoreInteractions(productConfigurationMergeStrategy, productFacade);
	}

	@Test
	public void testUpdateCartEntryWithEmptyConfiguration() throws CommerceCartModificationException
	{

		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);

		final ProductData productData = new ProductData();
		final OrderEntryData orderEntryData = new OrderEntryData();

		final List<ConfigurationInfoData> emptyList = Collections.emptyList();

		productData.setCode(PRODUCT_CODE);
		orderEntryData.setEntryNumber(99);
		orderEntryData.setProduct(productData);
		orderEntryData.setQuantity(1L);
		orderEntryData.setConfigurationInfos(emptyList);
		cartData.setEntries(Collections.singletonList(orderEntryData));

		given(productConfigurationMergeStrategy.merge(anyList(), anyList())).willReturn(Collections.emptyList());
		given(productFacade.getConfiguratorSettingsForCode(anyString())).willReturn(emptyList);

		final CartModificationData result = defaultCartFacade.updateCartEntry(orderEntryData);

		Assert.assertEquals(null, result);
		verify(productConfigurationMergeStrategy, times(2)).merge(emptyList, emptyList);
		verify(productFacade).getConfiguratorSettingsForCode(PRODUCT_CODE);
		verifyNoMoreInteractions(productConfigurationMergeStrategy, productFacade);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateCartMetadataNullMetadata()
	{
		defaultCartFacade.updateCartMetadata(null);
	}

	@Test
	public void testUpdateCartMetadata()
	{
		final CommerceCartMetadata metadata = mock(CommerceCartMetadata.class);

		final Optional<String> name = Optional.of("name");
		final Optional<String> description = Optional.of("description");
		final Optional<Date> expirationTime = Optional.of(new Date());

		given(metadata.getName()).willReturn(name);
		given(metadata.getDescription()).willReturn(description);
		given(metadata.getExpirationTime()).willReturn(expirationTime);
		given(metadata.isRemoveExpirationTime()).willReturn(true);
		given(cartService.getSessionCart()).willReturn(cartModel);

		defaultCartFacade.updateCartMetadata(metadata);

		final ArgumentCaptor<CommerceCartMetadataParameter> argument = ArgumentCaptor.forClass(CommerceCartMetadataParameter.class);
		verify(commerceCartService).updateCartMetadata(argument.capture());

		Assert.assertEquals("Name should be the same", name, argument.getValue().getName());
		Assert.assertEquals("Description should be the same", description, argument.getValue().getDescription());
		Assert.assertEquals("Expiration time should be the same", expirationTime, argument.getValue().getExpirationTime());
		Assert.assertTrue("Is remove expiration should be the same", argument.getValue().isRemoveExpirationTime());
		Assert.assertTrue("Enable hooks should be set", argument.getValue().isEnableHooks());
		Assert.assertEquals("Cart should be the same", cartModel, argument.getValue().getCart());
	}

	@Test
	public void testRestoreSavedCartEmpty() throws Exception
	{
		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);

		final CommerceCartRestoration commerceCartRestoration = new CommerceCartRestoration();
		given(commerceCartService.restoreCart(any(CommerceCartParameter.class))).willReturn(commerceCartRestoration);
		final CartRestorationData cartRestorationData = new CartRestorationData();
		given(cartRestorationConverter.convert(any())).willReturn(cartRestorationData);

		final CartRestorationData actualRestorationData = defaultCartFacade.restoreSavedCart("any");

		Assert.assertEquals(cartRestorationData, actualRestorationData);
		verify(cartService).setSessionCart(null);
	}

	@Test
	public void testRestoreSavedCartWithEntries() throws Exception
	{
		final CartModel cartModelWithEntry = new CartModel();
		cartModelWithEntry.setCode(CART);
		cartModelWithEntry.setGuid(CART);
		cartModelWithEntry.setEntries(Collections.singletonList(new AbstractOrderEntryModel()));

		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);
		final CommerceCartRestoration commerceCartRestoration = new CommerceCartRestoration();
		given(commerceCartService.restoreCart(any(CommerceCartParameter.class))).willReturn(commerceCartRestoration);
		final CartRestorationData cartRestorationData = new CartRestorationData();
		given(cartRestorationConverter.convert(any())).willReturn(cartRestorationData);
		given(cartService.getSessionCart()).willReturn(cartModelWithEntry);

		final CartRestorationData actualRestorationData = defaultCartFacade.restoreSavedCart("any");

		Assert.assertEquals(cartRestorationData, actualRestorationData);
		verify(cartService, never()).setSessionCart(null);
	}

	@Test
	public void testRestoreSavedCartWithEntryGroups() throws Exception
	{
		final CartModel cartModelWithEntryGroup = new CartModel();
		cartModelWithEntryGroup.setCode(CART);
		cartModelWithEntryGroup.setGuid(CART);
		cartModelWithEntryGroup.setEntryGroups(Collections.singletonList(new EntryGroup()));

		final CartData cartData = new CartData();
		setUpServiceMocks(cartData);
		final CommerceCartRestoration commerceCartRestoration = new CommerceCartRestoration();
		given(commerceCartService.restoreCart(any(CommerceCartParameter.class))).willReturn(commerceCartRestoration);
		final CartRestorationData cartRestorationData = new CartRestorationData();
		given(cartRestorationConverter.convert(any())).willReturn(cartRestorationData);
		given(cartService.getSessionCart()).willReturn(cartModelWithEntryGroup);

		final CartRestorationData actualRestorationData = defaultCartFacade.restoreSavedCart("any");

		Assert.assertEquals(cartRestorationData, actualRestorationData);
		verify(cartService, never()).setSessionCart(null);
	}

	protected void setUpServiceMocks(final CartData cartData)
	{
		final ProductModel productModel = setUpProductModel();
		given(productService.getProductForCode(anyString())).willReturn(productModel);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartConverter.convert(cartModel)).willReturn(cartData);
	}

	protected ProductModel setUpProductModel()
	{
		final UnitModel unit = new UnitModel();
		unit.setCode("unit");

		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		return product;
	}

	protected void verifyAddToCartParams(final ArgumentCaptor<AddToCartParams> addToCartParamsCaptor)
	{
		final List<AddToCartParams> addToCartParamsList = addToCartParamsCaptor.getAllValues();
		Assert.assertEquals(2, addToCartParamsList.size());
		Assert.assertEquals(1, addToCartParamsList.get(0).getQuantity());
		Assert.assertEquals(null, addToCartParamsList.get(0).getProductCode());
		Assert.assertEquals(0, addToCartParamsList.get(1).getQuantity());
		Assert.assertEquals(PRODUCT_CODE, addToCartParamsList.get(1).getProductCode());
	}
}
