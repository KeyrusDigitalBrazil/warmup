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
package de.hybris.platform.sap.productconfig.rules.cps.handler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CharacteristicValueRulesResultHandlerImplTest
{

	private static final String TEST_MSG = "test 123";
	private static final String CONFIG_ID = "configId";
	private static final String CSTIC_ID = "csticId";
	private static final String VALUE_ID = "valueId";
	private static final BigDecimal DISCOUNT = new BigDecimal(20);
	private static final String CSTIC_ID2 = "csticId2";
	private static final String VALUE_ID2 = "valueId2";
	private static final BigDecimal DISCOUNT2 = new BigDecimal(50);
	private static final BigDecimal DISCOUNT3 = new BigDecimal(12.5);
	private static final BigDecimal DISCOUNT4 = new BigDecimal(44.4);
	private static final BigDecimal DISCOUNT1AND2 = new BigDecimal(70);
	private static final BigDecimal DISCOUNTMAX = new BigDecimal(100);

	private static final String MESSAGE_STRING = "message";
	private static final Date MESSAGE_END_DATE = new Date();

	private CharacteristicValueRulesResultHandlerImpl classUnderTest;
	private final ProductConfigurationModel productConfigModel = new ProductConfigurationModel();
	private final List<CharacteristicValueRulesResultModel> rulesResults = new ArrayList<>();

	@Mock
	private ModelService modelService;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;

	@Before
	public void setUp() throws ConfigurationEngineException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CharacteristicValueRulesResultHandlerImpl();
		classUnderTest.setModelService(modelService);
		classUnderTest.setPersistenceService(persistenceService);
		productConfigModel.setConfigurationId(CONFIG_ID);
		rulesResults.add(prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT));
		rulesResults.add(prepareRulesResult(CSTIC_ID2, VALUE_ID2, DISCOUNT2));
		productConfigModel.setCharacteristicValueRulesResults(rulesResults);
		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		given(modelService.create(CharacteristicValueRulesResultModel.class)).willReturn(new CharacteristicValueRulesResultModel());
		given(modelService.create(DiscountMessageRulesResultModel.class)).willReturn(new DiscountMessageRulesResultModel());
	}

	protected CharacteristicValueRulesResultModel prepareRulesResult(final String cstic, final String value,
			final BigDecimal discount)
	{
		final CharacteristicValueRulesResultModel rulesResult = new CharacteristicValueRulesResultModel();
		rulesResult.setCharacteristic(cstic);
		rulesResult.setValue(value);
		rulesResult.setDiscountValue(discount);
		rulesResult.setProductConfiguration(productConfigModel);
		return rulesResult;
	}

	@Test
	public void testPersistRulesResult()
	{
		final CharacteristicValueRulesResultModel resultModel = prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT);
		classUnderTest.mergeDiscountAndPersistResults(resultModel, CONFIG_ID);
		verify(modelService).save(productConfigModel);

		assertSame(productConfigModel, resultModel.getProductConfiguration());
		verify(modelService).save(productConfigModel);
	}

	@Test
	public void testGetRulesResultsByConfigId()
	{
		final List<CharacteristicValueRulesResultModel> rulesResults = classUnderTest.getRulesResultsByConfigId(CONFIG_ID);
		assertNotNull(rulesResults);
		assertEquals(2, rulesResults.size());
		assertEquals(CSTIC_ID, rulesResults.get(0).getCharacteristic());
		assertEquals(VALUE_ID, rulesResults.get(0).getValue());
		assertEquals(DISCOUNT, rulesResults.get(0).getDiscountValue());
		assertEquals(CSTIC_ID2, rulesResults.get(1).getCharacteristic());
		assertEquals(VALUE_ID2, rulesResults.get(1).getValue());
		assertEquals(DISCOUNT2, rulesResults.get(1).getDiscountValue());
	}

	@Test
	public void testGetRulesResultsByConfigIdNoResults()
	{
		productConfigModel.setCharacteristicValueRulesResults(null);
		final List<CharacteristicValueRulesResultModel> rulesResults = classUnderTest.getRulesResultsByConfigId(CONFIG_ID);
		assertNotNull(rulesResults);
		assertEquals(0, rulesResults.size());
	}

	@Test
	public void testDeleteRulesResultsByConfigId()
	{
		classUnderTest.deleteRulesResultsByConfigId(CONFIG_ID);
		verify(modelService, times(1)).removeAll(Mockito.anyListOf(CharacteristicValueRulesResultModel.class));
	}

	@Test
	public void testDeleteRulesResultsByConfigIdNoResults()
	{
		productConfigModel.setCharacteristicValueRulesResults(null);
		classUnderTest.deleteRulesResultsByConfigId(CONFIG_ID);
		verify(modelService, times(0)).removeAll(Mockito.anyListOf(CharacteristicValueRulesResultModel.class));
	}

	@Test
	public void testAddDiscountEmptyList()
	{
		rulesResults.clear();
		final CharacteristicValueRulesResultModel rulesResultModel = prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT);
		classUnderTest.addDiscount(rulesResultModel, rulesResults);
		assertEquals(1, rulesResults.size());
		assertEquals(CSTIC_ID, rulesResults.get(0).getCharacteristic());
		assertEquals(VALUE_ID, rulesResults.get(0).getValue());
		assertEquals(DISCOUNT, rulesResults.get(0).getDiscountValue());
	}

	@Test
	public void testAddDiscountListContainsOtherEntry()
	{
		rulesResults.remove(0);
		final CharacteristicValueRulesResultModel rulesResultModel = prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT);
		classUnderTest.addDiscount(rulesResultModel, rulesResults);
		assertEquals(2, rulesResults.size());
		assertEquals(CSTIC_ID2, rulesResults.get(0).getCharacteristic());
		assertEquals(VALUE_ID2, rulesResults.get(0).getValue());
		assertEquals(DISCOUNT2, rulesResults.get(0).getDiscountValue());
		assertEquals(CSTIC_ID, rulesResults.get(1).getCharacteristic());
		assertEquals(VALUE_ID, rulesResults.get(1).getValue());
		assertEquals(DISCOUNT, rulesResults.get(1).getDiscountValue());
	}

	@Test
	public void testAddDiscountListContainsSameEntry()
	{
		final CharacteristicValueRulesResultModel rulesResultModel = prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT3);
		classUnderTest.addDiscount(rulesResultModel, rulesResults);
		assertEquals(2, rulesResults.size());
		assertEquals(CSTIC_ID, rulesResults.get(0).getCharacteristic());
		assertEquals(VALUE_ID, rulesResults.get(0).getValue());
		assertEquals(0, new BigDecimal(32.5).compareTo(rulesResults.get(0).getDiscountValue()));
		assertEquals(CSTIC_ID2, rulesResults.get(1).getCharacteristic());
		assertEquals(VALUE_ID2, rulesResults.get(1).getValue());
		assertEquals(DISCOUNT2, rulesResults.get(1).getDiscountValue());
	}

	@Test
	public void testCalculateDiscount()
	{
		final BigDecimal calculatedDiscount = classUnderTest.calculateDiscount(DISCOUNT, DISCOUNT2);
		assertEquals(0, DISCOUNT1AND2.compareTo(calculatedDiscount));
		final BigDecimal calculatedDiscount2 = classUnderTest.calculateDiscount(calculatedDiscount, DISCOUNT4);
		assertEquals(0, DISCOUNTMAX.compareTo(calculatedDiscount2));
	}

	@Test(expected = NoSuchElementException.class)
	public void testAddMessageToRulesNoResult()
	{
		rulesResults.clear();
		final Date endDate = new Date();
		final DiscountMessageRulesResultModel msgResult = classUnderTest.createMessageInstance();
		msgResult.setMessage(TEST_MSG);
		msgResult.setEndDate(endDate);

		classUnderTest.addMessageToRulesResult(msgResult, CONFIG_ID, CSTIC_ID, VALUE_ID);
	}

	@Test
	public void testAddMessageToRulesResultFirstMessage()
	{
		final CharacteristicValueRulesResultModel resultModel = rulesResults.get(0);
		final Date endDate = new Date();
		final DiscountMessageRulesResultModel msgResult = classUnderTest.createMessageInstance();
		msgResult.setMessage(TEST_MSG);
		msgResult.setEndDate(endDate);

		classUnderTest.addMessageToRulesResult(msgResult, CONFIG_ID, CSTIC_ID, VALUE_ID);

		assertEquals(1, resultModel.getMessageRulesResults().size());
		assertSame(resultModel, msgResult.getCsticValueRulesResult());
		verify(modelService).save(resultModel);
	}

	@Test
	public void testAddMessageToRulesResultCsticLevel()
	{
		given(modelService.create(DiscountMessageRulesResultModel.class)).willReturn(new DiscountMessageRulesResultModel(),
				new DiscountMessageRulesResultModel());
		rulesResults.add(prepareRulesResult(CSTIC_ID, VALUE_ID2, DISCOUNT));
		final CharacteristicValueRulesResultModel resultModel1 = rulesResults.get(0);
		final CharacteristicValueRulesResultModel resultModel2 = rulesResults.get(1);
		final CharacteristicValueRulesResultModel resultModel3 = rulesResults.get(2);
		final Date endDate = new Date();
		final DiscountMessageRulesResultModel msgResult = classUnderTest.createMessageInstance();
		msgResult.setMessage(TEST_MSG);
		msgResult.setEndDate(endDate);

		classUnderTest.addMessageToRulesResult(msgResult, CONFIG_ID, CSTIC_ID, "");

		assertEquals(1, resultModel1.getMessageRulesResults().size());
		assertTrue(CollectionUtils.isEmpty(resultModel2.getMessageRulesResults()));
		assertEquals(1, resultModel3.getMessageRulesResults().size());
		verify(modelService).save(resultModel1);
		verify(modelService).save(resultModel3);
		assertNotSame(resultModel1.getMessageRulesResults().get(0), resultModel3.getMessageRulesResults().get(0));
		assertSame(resultModel1, resultModel1.getMessageRulesResults().get(0).getCsticValueRulesResult());
		assertSame(resultModel3, resultModel3.getMessageRulesResults().get(0).getCsticValueRulesResult());
	}


	@Test
	public void testAddMessageToRulesResultSecondMessage()
	{
		final CharacteristicValueRulesResultModel resultModel = rulesResults.get(1);
		resultModel.setMessageRulesResults(new ArrayList());
		resultModel.getMessageRulesResults().add(new DiscountMessageRulesResultModel());
		final Date endDate = new Date();
		final DiscountMessageRulesResultModel msgResult = classUnderTest.createMessageInstance();
		msgResult.setMessage(TEST_MSG);
		msgResult.setEndDate(endDate);

		classUnderTest.addMessageToRulesResult(msgResult, CONFIG_ID, CSTIC_ID2, VALUE_ID2);

		assertEquals(2, resultModel.getMessageRulesResults().size());
		verify(modelService).save(resultModel);
	}

	@Test
	public void testCloneRulesResultModelList()
	{
		final ProductConfigurationModel targetConfigModel = new ProductConfigurationModel();
		final List<CharacteristicValueRulesResultModel> sourceRulesResultModelList = new ArrayList<>();
		final CharacteristicValueRulesResultModel sourceRulesResultModel1 = prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT);
		addMessagesToRulesResultModel(sourceRulesResultModel1);
		sourceRulesResultModelList.add(sourceRulesResultModel1);

		final List<CharacteristicValueRulesResultModel> targetRulesResultModelList = classUnderTest
				.cloneRulesResultModelList(targetConfigModel, sourceRulesResultModelList);

		assertEquals(1, targetRulesResultModelList.size());
		assertEquals(CSTIC_ID, targetRulesResultModelList.get(0).getCharacteristic());
		assertEquals(VALUE_ID, targetRulesResultModelList.get(0).getValue());
		assertEquals(DISCOUNT, targetRulesResultModelList.get(0).getDiscountValue());
		assertSame(targetConfigModel, targetRulesResultModelList.get(0).getProductConfiguration());

		assertEquals(1, targetRulesResultModelList.get(0).getMessageRulesResults().size());
		final DiscountMessageRulesResultModel messageRulesResultModel = targetRulesResultModelList.get(0).getMessageRulesResults()
				.get(0);
		assertEquals(MESSAGE_STRING, messageRulesResultModel.getMessage());
		assertEquals(MESSAGE_END_DATE, messageRulesResultModel.getEndDate());
		assertSame(targetRulesResultModelList.get(0), messageRulesResultModel.getCsticValueRulesResult());
	}

	protected void addMessagesToRulesResultModel(final CharacteristicValueRulesResultModel sourceRulesResultModel1)
	{
		final List<DiscountMessageRulesResultModel> messageList = new ArrayList<>();
		final DiscountMessageRulesResultModel message = new DiscountMessageRulesResultModel();
		message.setMessage(MESSAGE_STRING);
		message.setEndDate(MESSAGE_END_DATE);
		messageList.add(message);
		sourceRulesResultModel1.setMessageRulesResults(messageList);
	}

	@Test
	public void testCopyAndPersistRuleResults()
	{
		final String sourceConfigId = "123";
		final String targetConfigId = "456";

		final ProductConfigurationModel targetConfigModel = new ProductConfigurationModel();

		final ProductConfigurationModel sourceConfigModel = new ProductConfigurationModel();
		final List<CharacteristicValueRulesResultModel> sourceRulesResultModelList = new ArrayList<>();
		final CharacteristicValueRulesResultModel sourceRulesResultModel1 = prepareRulesResult(CSTIC_ID, VALUE_ID, DISCOUNT);
		addMessagesToRulesResultModel(sourceRulesResultModel1);
		sourceRulesResultModelList.add(sourceRulesResultModel1);
		sourceConfigModel.setCharacteristicValueRulesResults(sourceRulesResultModelList);

		given(persistenceService.getByConfigId(sourceConfigId)).willReturn(sourceConfigModel);
		given(persistenceService.getByConfigId(targetConfigId)).willReturn(targetConfigModel);

		classUnderTest.copyAndPersistRuleResults(sourceConfigId, targetConfigId);

		verify(modelService).save(targetConfigModel);
		assertEquals(1, targetConfigModel.getCharacteristicValueRulesResults().size());
		assertEquals(1, targetConfigModel.getCharacteristicValueRulesResults().get(0).getMessageRulesResults().size());
	}
}
