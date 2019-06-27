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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.WebDataBinder;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.servicelayer.exceptions.BusinessException;


@UnitTest
public class AbstractConfigurationOverviewControllerTest extends AbstractProductConfigControllerBaseTest
{

	public static final String ORDER_CODE = "ORDER_CODE";
	public static final int ORDER_ENTRY_NUMBER = 1;
	public static final String CART_ENTRY_KEY = "1";
	public static final String ITEM_PK = "1234567";

	private AbstractConfigurationOverviewController classUnderTest;
	private UiStatus uiStatus;
	private OverviewUiData overviewUiData;
	private ConfigurationOverviewData configOverviewData;
	@Mock
	private QuoteFacade quoteFacade;
	@Mock
	private SaveCartFacade saveCartFacade;
	@Mock
	private OrderFacade orderFacade;
	@Mock
	private CommerceSaveCartResultData commerceSaveCartResultData;

	@Mock
	private QuoteData quoteData;
	@Mock
	private OrderData orderData;
	@Mock
	private CartData cartData;
	@Mock
	private WebDataBinder binder;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new AbstractConfigurationOverviewController();
		classUnderTest.setCartFacade(cartFacadeMock);
		classUnderTest.setAbstractOrderEntryLinkStrategy(abstractOrderEntryLinkStrategy);
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setOrderFacade(orderFacade);
		classUnderTest.setSaveCartFacade(saveCartFacade);
		classUnderTest.setQuoteFacade(quoteFacade);

		overviewUiData = new OverviewUiData();
		configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setId(CONFIG_ID);
		configOverviewData.setGroups(Collections.emptyList());
		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);

		uiStatus = new UiStatus();
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(uiStatus);

		given(quoteFacade.getQuoteForCode(ORDER_CODE)).willReturn(quoteData);
		given(quoteData.getEntries()).willReturn(null);
		given(orderFacade.getOrderDetailsForCodeWithoutUser(ORDER_CODE)).willReturn(orderData);
		given(orderData.getEntries()).willReturn(null);
		given(cartData.getEntries()).willReturn(null);
		given(saveCartFacade.getCartForCodeAndCurrentUser(any())).willReturn(commerceSaveCartResultData);
		given(commerceSaveCartResultData.getSavedCartData()).willReturn(cartData);
	}

	@Test
	public void testGetErrorCountForUi_nonZero()
	{
		final Object errorCountForUi = classUnderTest.getErrorCountForUi(1);
		assertEquals("1", errorCountForUi.toString());
	}

	@Test
	public void testGetErrorCountForUi_zero()
	{
		final Object errorCountForUi = classUnderTest.getErrorCountForUi(0);
		assertEquals(" ", errorCountForUi.toString());
	}

	@Test
	public void testNeedConfigurationDetails()
	{
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		assertTrue(classUnderTest.needConfigurationDetails(overviewUiData));
	}

	@Test
	public void testNeedConfigurationDetailsIsVariant()
	{
		overviewUiData.setOverviewMode(OverviewMode.VARIANT_OVERVIEW);
		assertFalse(classUnderTest.needConfigurationDetails(overviewUiData));
	}

	@Test
	public void testPrepareOverviewUiDataMapsIdAndCode() throws BusinessException
	{
		classUnderTest.initializeFilterListsInUiStatus(configOverviewData, uiStatus);
		classUnderTest.prepareOverviewUiData(uiStatus, overviewUiData, configOverviewData, kbKey);
		assertEquals(CONFIG_ID, overviewUiData.getConfigId());
		assertEquals(PRODUCT_CODE, overviewUiData.getProductCode());
	}


	@Test
	public void testGetQuantityUiStatusNull()
	{
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(null);
		assertEquals(1, classUnderTest.getQuantity(PRODUCT_CODE));
	}

	@Test
	public void testGetQuantity()
	{
		uiStatus.setQuantity(2);
		assertEquals(2, classUnderTest.getQuantity(PRODUCT_CODE));
	}


	@Test(expected = BusinessException.class)
	public void testSetUiStatusForOverviewInSessionQuoteNoEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, PRODUCT_CODE, overviewUiData);
	}

	@Test(expected = BusinessException.class)
	public void testSetUiStatusForOverviewInSessionOrderNoEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.ORDER_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, PRODUCT_CODE, overviewUiData);
	}

	@Test(expected = BusinessException.class)
	public void testSetUiStatusForOverviewInSessionSavedCartNoEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.SAVED_CART_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	private void fillOverviewUIData(final OverviewMode overviewMode)
	{
		overviewUiData.setAbstractOrderEntryNumber(1);
		overviewUiData.setAbstractOrderCode(ORDER_CODE);
		overviewUiData.setOverviewMode(overviewMode);
	}


	@Test
	public void testSetUiStatusForOverviewInSessionOthers() throws BusinessException
	{
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
		verify(sessionAccessFacade).setUiStatusForCartEntry(ITEM_PK, uiStatus);
	}

	@Test
	public void testSetUiStatusForOverviewInSessionQuoteWithEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		given(quoteData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	@Test
	public void testSetUiStatusForOverviewInSessionOrderWithEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.ORDER_OVERVIEW);
		given(orderData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	@Test
	public void testSetUiStatusForOverviewInSessionSavedCartWithEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.SAVED_CART_OVERVIEW);
		given(cartData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	private List<OrderEntryData> createOrderEntries(final String itemPk, final int entryNumber)
	{

		List<OrderEntryData> entries = new ArrayList<OrderEntryData>();
		OrderEntryData entry = new OrderEntryData();
		entry.setItemPK(itemPk);
		entry.setEntryNumber(entryNumber);
		entries.add(entry);
		return entries;

	}

	@Test
	public void testGetUiStatusForOverviewOthers() throws BusinessException
	{
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(CART_ENTRY_KEY);
	}

	@Test(expected = BusinessException.class)
	public void testGetUiStatusForOverviewNoEntriesQuote() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
	}


	@Test
	public void testGetUiStatusForOverviewQuote() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		given(quoteData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));

		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(ITEM_PK);
	}

	@Test
	public void testGetUiStatusForOverviewOrder() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.ORDER_OVERVIEW);
		given(orderData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));

		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(ITEM_PK);
	}

	@Test
	public void testGetUiStatusForOverviewSavedCart() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.SAVED_CART_OVERVIEW);
		given(cartData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));

		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(ITEM_PK);
	}

	@Test
	public void testSetAllowedFields()
	{
		classUnderTest.initBinderConfigOverviewUiData(binder);
		verify(binder).setAllowedFields(AbstractConfigurationOverviewController.ALLOWED_FIELDS_OVERVIEWUIDATA);
	}

	@Test
	public void testPrepareOverviewUiData()
	{
		productData.setCode(PRODUCT_CODE);
		productData.setBaseProduct(PRODUCT_CODE);
		PricingData pricing = new PricingData();
		PriceData basePrice = new PriceData();
		basePrice.setValue(new BigDecimal("100.00"));
		pricing.setBasePrice(basePrice);
		PriceData currentTotal = new PriceData();
		currentTotal.setValue(new BigDecimal("150.00"));
		pricing.setCurrentTotal(currentTotal);
		PriceData selectedOptions = new PriceData();
		selectedOptions.setValue(new BigDecimal("50.00"));
		pricing.setSelectedOptions(selectedOptions);
		configOverviewData.setPricing(pricing);
		List<CharacteristicGroup> groups = createCharacteristicGroups();
		configOverviewData.setGroups(groups);

		classUnderTest.prepareOverviewUiData(overviewUiData, configOverviewData, productData);
		assertEquals(overviewUiData.getProductCode(), productData.getCode());
		assertEquals(overviewUiData.getQuantity(), classUnderTest.getQuantity(productData.getBaseProduct()));
		assertEquals(overviewUiData.getGroups().size(), configOverviewData.getGroups().size());
		assertEquals(overviewUiData.getGroups(), configOverviewData.getGroups());
		assertEquals(overviewUiData.getPricing(), configOverviewData.getPricing());
		assertEquals(overviewUiData.getPricing().getBasePrice().getValue(),
				configOverviewData.getPricing().getBasePrice().getValue());
		assertEquals(overviewUiData.getPricing().getCurrentTotal().getValue(),
				configOverviewData.getPricing().getCurrentTotal().getValue());
		assertEquals(overviewUiData.getPricing().getSelectedOptions().getValue(),
				configOverviewData.getPricing().getSelectedOptions().getValue());
	}

	protected List<CharacteristicGroup> createCharacteristicGroups()
	{
		List<CharacteristicGroup> groups = new ArrayList<>();
		for (int i = 1; i <= 4; i++)
		{
			CharacteristicGroup group = new CharacteristicGroup();
			group.setId("group_" + i);
			groups.add(group);
		}
		return groups;
	}
}
