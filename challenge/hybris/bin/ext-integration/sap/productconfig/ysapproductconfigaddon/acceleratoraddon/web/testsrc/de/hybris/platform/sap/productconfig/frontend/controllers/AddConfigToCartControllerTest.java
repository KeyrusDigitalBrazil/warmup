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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@UnitTest
public class AddConfigToCartControllerTest extends AbstractProductConfigControllerBaseTest
{

	private static final String CART_ITEM_KEY = "123";

	private AddConfigToCartController classUnderTest;
	private OrderEntryData cartItem;

	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	protected BindingResult bindingResults;
	@Mock
	private CartFacade cartFacade;



	@Before
	public void setUp()
	{
		classUnderTest = new AddConfigToCartController();
		MockitoAnnotations.initMocks(this);
		super.injectMocks(classUnderTest);
		classUnderTest.setCartFacade(cartFacade);

		kbKey = createKbKey();
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();
		given(configurationProductLinkStrategy.getConfigIdForProduct(kbKey.getProductCode())).willReturn(CONFIG_ID);

		cartItem = new OrderEntryData();
		cartItem.setItemPK(CART_ITEM_KEY);
		final CartData cart = new CartData();
		cart.setEntries(Collections.singletonList(cartItem));
		given(cartFacade.getSessionCart()).willReturn(cart);
	}

	@Test
	public void testAddConfigToCartIsCorrect() throws Exception
	{
		initializeFirstCall();
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(configCartIntegrationFacade.addConfigurationToCart(configData)).willReturn(CART_ITEM_KEY);

		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(1)).addConfigurationToCart(any(ConfigurationData.class));
		verify(redirectAttributes, times(1)).addFlashAttribute("addedToCart", Boolean.TRUE);
	}

	@Test
	public void testAddConfigToCartProblemWithProduct() throws Exception
	{
		initializeFirstCall();
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(configCartIntegrationFacade.addConfigurationToCart(configData))
				.willThrow(new CommerceCartModificationException(CART_ITEM_KEY));

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(1)).addConfigurationToCart(any(ConfigurationData.class));

		verify(model, times(1)).addAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), any(Collection.class));

	}

	@Test
	public void testAddConfigToCartFailed() throws Exception
	{
		initializeFirstCall();

		final List<FieldError> fieldErrors = new ArrayList<>();
		final FieldError error = new FieldError("config", "cstic[0].value", "a", true, null, null, null);
		fieldErrors.add(error);

		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(Integer.valueOf(bindingResults.getErrorCount())).willReturn(Integer.valueOf(1));
		given(bindingResults.getFieldErrors()).willReturn(fieldErrors);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.TRUE);

		final String targetUrl = classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model,
				redirectAttributes, request);
		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(0)).addConfigurationToCart(any(ConfigurationData.class));
		verify(redirectAttributes, times(0)).addFlashAttribute("addedToCart", Boolean.TRUE);

		final String expectedTargetUrl = "redirect:/" + kbKey.getProductCode() + "/configuratorPage/CPQCONFIGURATOR";
		assertEquals(expectedTargetUrl, targetUrl);
	}

	@Test
	public void testAddConfigToCartTwice() throws Exception
	{
		initializeFirstCall();
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(configCartIntegrationFacade.addConfigurationToCart(configData)).willReturn(CART_ITEM_KEY);

		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(1)).addConfigurationToCart(any(ConfigurationData.class));
		verify(redirectAttributes, times(1)).addFlashAttribute("addedToCart", Boolean.TRUE);

		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("4711");

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(redirectAttributes, times(1)).addFlashAttribute("addedToCart", Boolean.FALSE);
	}

	@Test
	public void testReset() throws Exception
	{
		initializeFirstCall();

		final UiStatus uiStatus = new UiStatus();
		given(sessionAccessFacade.getUiStatusForProduct(kbKey.getProductCode())).willReturn(uiStatus);

		final String targetUrl = classUnderTest.resetConfiguration(kbKey.getProductCode(), "/");
		verify(sessionAccessFacade, times(1)).removeUiStatusForProduct(kbKey.getProductCode());
		verify(configCartIntegrationFacade, times(1)).removeConfigurationLink(kbKey.getProductCode());

		assertEquals("redirect:/", targetUrl);
	}


	@Override
	@SuppressWarnings("unchecked")
	protected void initializeFirstCall() throws Exception
	{
		given(productData.getCode()).willReturn(PRODUCT_CODE);
		given(productModel.getCode()).willReturn(PRODUCT_CODE);

		given(sessionService.getCurrentSession()).willReturn(hybriSession);
		given(hybriSession.getSessionId()).willReturn("1");

		given(configFacade.getConfiguration(any(KBKeyData.class))).willReturn(configData);
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel);

		given(storeSessionFacade.getCurrentCurrency()).willReturn(createCurrencyData());
		given(cmsPageService.getPageForProduct(any(ProductModel.class))).willReturn(new ProductPageModel());
		given(pageTitleResolver.resolveProductPageTitle(any(ProductModel.class))).willReturn("TEST");
	}

	@Test
	public void testGetCartEntryNumber() throws BusinessException
	{
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("123");

		final Integer expectedEntryNumber = Integer.valueOf(20);
		cartItem.setEntryNumber(expectedEntryNumber);
		final Integer cartEntryNumber = classUnderTest.getCartEntryNumber(PRODUCT_CODE);
		assertEquals(expectedEntryNumber, cartEntryNumber);
	}

	@Test
	public void testGetCartEntryNumber_noItem() throws BusinessException
	{

		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(null);


		final Integer cartEntryNumber = classUnderTest.getCartEntryNumber(PRODUCT_CODE);
		assertNull("null cart entry number expected", cartEntryNumber);
	}
}
