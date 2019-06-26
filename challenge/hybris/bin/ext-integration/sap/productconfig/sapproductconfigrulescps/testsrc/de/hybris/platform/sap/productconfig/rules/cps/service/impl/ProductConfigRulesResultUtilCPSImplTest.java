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
package de.hybris.platform.sap.productconfig.rules.cps.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.cps.handler.CharacteristicValueRulesResultHandler;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRulesResultUtilCPSImplTest
{
	private static final String MESSAGE_TEXT = "messageText";
	private static final String CONFIG_ID = "configId";
	private static final String CSTIC_ID = "csticId";
	private static final String VALUE_ID = "valueId";
	private static final BigDecimal DISCOUNT = new BigDecimal(0.8);
	private static final String CSTIC_ID2 = "csticId2";
	private static final String VALUE_ID2 = "valueId2";
	private static final BigDecimal DISCOUNT2 = new BigDecimal(0.5);
	private Date currentDate;

	private ProductConfigRulesResultUtilCPSImpl classUnderTest;
	private List<CharacteristicValueRulesResultModel> rulesResults;

	@Mock
	private CharacteristicValueRulesResultHandler rulesResultHandler;
	private DiscountMessageRulesResultModel discountMessage;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigRulesResultUtilCPSImpl();
		classUnderTest.setRulesResultHandler(rulesResultHandler);
		rulesResults = new ArrayList<>();
		rulesResults.add(prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT));
		rulesResults.add(prepareRulesResult(CSTIC_ID2, VALUE_ID2, DISCOUNT2));
		given(rulesResultHandler.getRulesResultsByConfigId(CONFIG_ID)).willReturn(rulesResults);
		currentDate = new Date();

		classUnderTest.setConfigModelFactory(new ConfigModelFactoryImpl());
		discountMessage = new DiscountMessageRulesResultModel();
		discountMessage.setMessage(MESSAGE_TEXT);
		discountMessage.setEndDate(currentDate);
	}

	@Test
	public void testRetrieveRulesBasedVariantConditionModifications()
	{
		final List<ProductConfigurationDiscount> result = classUnderTest.retrieveRulesBasedVariantConditionModifications(CONFIG_ID);
		verify(rulesResultHandler).getRulesResultsByConfigId(CONFIG_ID);
		assertEquals(2, result.size());
		assertEquals(CSTIC_ID, result.get(0).getCsticName());
		assertEquals(VALUE_ID, result.get(0).getCsticValueName());
		assertEquals(0, result.get(0).getDiscount().compareTo(DISCOUNT));
		assertEquals(CSTIC_ID2, result.get(1).getCsticName());
		assertEquals(VALUE_ID2, result.get(1).getCsticValueName());
		assertEquals(0, result.get(1).getDiscount().compareTo(DISCOUNT2));
	}

	@Test
	public void testRetrieveRulesBasedVariantConditionModificationsNoRulesResults()
	{
		given(rulesResultHandler.getRulesResultsByConfigId(CONFIG_ID)).willReturn(null);
		final List<ProductConfigurationDiscount> result = classUnderTest.retrieveRulesBasedVariantConditionModifications(CONFIG_ID);
		verify(rulesResultHandler).getRulesResultsByConfigId(CONFIG_ID);
		assertTrue(result.isEmpty());
	}


	@Test
	public void testDeleteRulesResultsByConfigId()
	{
		classUnderTest.deleteRulesResultsByConfigId(CONFIG_ID);
		verify(rulesResultHandler).deleteRulesResultsByConfigId(CONFIG_ID);
	}

	protected CharacteristicValueRulesResultModel prepareRulesResult(final String cstic, final String value,
			final BigDecimal discount)
	{
		final CharacteristicValueRulesResultModel rulesResult = new CharacteristicValueRulesResultModel();
		rulesResult.setCharacteristic(cstic);
		rulesResult.setValue(value);
		rulesResult.setDiscountValue(discount);
		final DiscountMessageRulesResultModel message = new DiscountMessageRulesResultModel();
		message.setMessage("dummy text");
		rulesResult.setMessageRulesResults(Collections.singletonList(message));
		return rulesResult;
	}

	@Test
	public void testMapRulesResultToVariantConditionModfication()
	{
		final ProductConfigurationDiscount varCondModification = classUnderTest
				.mapRulesResultToVariantConditionModfication(prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT));
		assertEquals(CSTIC_ID, varCondModification.getCsticName());
		assertEquals(VALUE_ID, varCondModification.getCsticValueName());
		assertEquals(DISCOUNT, varCondModification.getDiscount());
	}

	@Test
	public void testRetrieveDiscountMessagesEmpty()
	{
		given(rulesResultHandler.getRulesResultsByConfigId(CONFIG_ID)).willReturn(null);
		final Map<String, Map<String, List<ProductConfigMessage>>> messages = classUnderTest.retrieveDiscountMessages(CONFIG_ID);
		assertTrue(messages.isEmpty());
	}

	@Test
	public void testRetrieveDiscountMessages()
	{
		rulesResults.add(prepareRulesResult(CSTIC_ID, VALUE_ID2, DISCOUNT));
		final Map<String, Map<String, List<ProductConfigMessage>>> messages = classUnderTest.retrieveDiscountMessages(CONFIG_ID);
		assertEquals(2, messages.size());
		assertEquals(2, messages.get(CSTIC_ID).size());
		assertEquals(1, messages.get(CSTIC_ID).get(VALUE_ID).size());
		assertEquals(1, messages.get(CSTIC_ID).get(VALUE_ID2).size());
		assertEquals(1, messages.get(CSTIC_ID2).size());
		assertEquals(1, messages.get(CSTIC_ID2).get(VALUE_ID2).size());
	}

	@Test
	public void testGetOrCreateValueMapEmpty()
	{
		final Map<String, Map<String, List<ProductConfigMessage>>> csticMap = new HashMap();
		final Map<String, List<ProductConfigMessage>> valueMap = classUnderTest.getOrCreateValueMap(csticMap, CSTIC_ID);
		assertNotNull(valueMap);
		assertSame(valueMap, csticMap.get(CSTIC_ID));
	}

	@Test
	public void testGetOrCreateValueMapExisting()
	{
		final Map<String, Map<String, List<ProductConfigMessage>>> csticMap = new HashMap();
		final Map<String, List<ProductConfigMessage>> expectedValueMap = new HashMap();
		csticMap.put(CSTIC_ID, expectedValueMap);
		final Map<String, List<ProductConfigMessage>> actualValueMap = classUnderTest.getOrCreateValueMap(csticMap, CSTIC_ID);
		assertSame(expectedValueMap, actualValueMap);
	}

	@Test
	public void testConvertDiscountMessage()
	{
		final ProductConfigMessage configMessage = classUnderTest.convertDiscountMessage(discountMessage);
		assertEquals(MESSAGE_TEXT, configMessage.getMessage());
		assertEquals(currentDate, configMessage.getEndDate());
		assertEquals(ProductConfigMessageSource.RULE, configMessage.getSource());
		assertEquals(ProductConfigMessageSeverity.INFO, configMessage.getSeverity());
		assertEquals(ProductConfigMessageSourceSubType.DISPLAY_PROMO_MESSAGE, configMessage.getSourceSubType());
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED, configMessage.getPromoType());
	}

	@Test
	public void testConvertDiscountMessageList()
	{
		final List<ProductConfigMessage> configMessageList = classUnderTest
				.convertDiscountMessageList(Collections.singletonList(discountMessage));
		assertEquals(1, configMessageList.size());
	}

	@Test
	public void testConvertDiscountMessageListEmpty()
	{
		final List<ProductConfigMessage> configMessageList = classUnderTest.convertDiscountMessageList(Collections.emptyList());
		assertTrue(configMessageList.isEmpty());
	}

}
