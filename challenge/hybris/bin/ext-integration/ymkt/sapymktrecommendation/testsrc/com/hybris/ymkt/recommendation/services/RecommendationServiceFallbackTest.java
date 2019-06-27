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
package com.hybris.ymkt.recommendation.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceFallbackTest {

	@Mock
	private ModelService modelService;

	@Mock
	private RecommendationBufferService recoBufferService;

	@InjectMocks
	private final RecommendationService recoService = new RecommendationService();

	final String USER_ID = "userId1";
	final String HASH_ID = "hashId1";
	final String SCENARIO_ID = "scenarioId1";
	final boolean IS_ANONYMOUS = false;
	final String RECOMMENDATION_TYPE = RecommendationService.PERSONALIZED_RECOMMENDATION;
	boolean isFirstCacheCallTested = false;

	final List<String> LEADING_ITEMS_1 = Arrays.asList("3", "2", "4", "1");
	final List<String> LEADING_ITEMS_2 = Arrays.asList("3", "1", "2");
	final List<String> LEADING_ITEMS_3 = Arrays.asList("4", "1");
	final List<String> LEADING_ITEMS_4 = Arrays.asList("1");
	final List<String> LEADING_ITEMS_5 = Arrays.asList("");
	final List<String> LEADING_ITEMS_6 = Arrays.asList("3");
	final List<String> LEADING_ITEMS_7 = Arrays.asList("10", "12", "23", "42", "11");
	final List<String> LEADING_ITEMS_8 = Arrays.asList("10", "12", "23", "3", "42", "11");
	final List<String> LEADING_ITEMS_9 = Arrays.asList("10", "15", "3", "2", "1");

	SAPRecommendationBufferModel buffer1 = generateBufferModel(LEADING_ITEMS_1, "bufferRecoList1");
	SAPRecommendationBufferModel buffer2 = generateBufferModel(LEADING_ITEMS_2, "bufferRecoList2");
	SAPRecommendationBufferModel buffer3 = generateBufferModel(LEADING_ITEMS_3, "bufferRecoList3");
	SAPRecommendationBufferModel buffer4 = generateBufferModel(LEADING_ITEMS_4, "bufferRecoList4");
	SAPRecommendationBufferModel buffer5 = generateBufferModel(LEADING_ITEMS_5, "bufferRecoList5");
	SAPRecommendationBufferModel buffer6 = generateBufferModel(LEADING_ITEMS_6, "bufferRecoList6");

	@Before
	public void setUp() throws Exception {

		saveRecommendationInBuffer(LEADING_ITEMS_1);
		saveRecommendationInBuffer(LEADING_ITEMS_2);
		saveRecommendationInBuffer(LEADING_ITEMS_3);
		saveRecommendationInBuffer(LEADING_ITEMS_4);
		saveRecommendationInBuffer(LEADING_ITEMS_5);
		saveRecommendationInBuffer(LEADING_ITEMS_6);

		Mockito.when(recoBufferService.getPersonalizedRecommendation(USER_ID, SCENARIO_ID,
				convertListToString(LEADING_ITEMS_1))).thenReturn(buffer1);
		Mockito.when(recoBufferService.getPersonalizedRecommendation(USER_ID, SCENARIO_ID,
				convertListToString(LEADING_ITEMS_2))).thenReturn(buffer2);
		Mockito.when(recoBufferService.getPersonalizedRecommendation(USER_ID, SCENARIO_ID,
				convertListToString(LEADING_ITEMS_3))).thenReturn(buffer3);
		Mockito.when(recoBufferService.getPersonalizedRecommendation(USER_ID, SCENARIO_ID,
				convertListToString(LEADING_ITEMS_4))).thenReturn(buffer4);
		Mockito.when(recoBufferService.getPersonalizedRecommendation(USER_ID, SCENARIO_ID,
				convertListToString(LEADING_ITEMS_5))).thenReturn(buffer5);
		Mockito.when(recoBufferService.getPersonalizedRecommendation(USER_ID, SCENARIO_ID,
				convertListToString(LEADING_ITEMS_6))).thenReturn(buffer6);

	}

	/**
	 * input : leading items not in buffer, but "" is. output : ""
	 */
	@Test
	public void testWithLeadingItemsNotFoundWillReturnEmptyString() {
		SAPRecommendationBufferModel result = new SAPRecommendationBufferModel();
		result = recoService.getFallbackRecommendationByType(USER_ID, SCENARIO_ID, LEADING_ITEMS_7, RECOMMENDATION_TYPE);
		assertEquals("bufferRecoList5", result.getRecoList());
	}

	/**
	 * Input : leading items not in buffer except 1 individual product output :
	 * individual product
	 */
	@Test
	public void testWithOnlyOneLeadingItemInListIsPresentInBuffer() {
		SAPRecommendationBufferModel result = new SAPRecommendationBufferModel();
		result = recoService.getFallbackRecommendationByType(USER_ID, SCENARIO_ID, LEADING_ITEMS_8, RECOMMENDATION_TYPE);
		assertEquals("bufferRecoList6", result.getRecoList());
	}

	@Test
	public void testWithSequenceOfLeadingItemsPresentInBuffer() {
		SAPRecommendationBufferModel result = new SAPRecommendationBufferModel();
		result = recoService.getFallbackRecommendationByType(USER_ID, SCENARIO_ID, LEADING_ITEMS_9, RECOMMENDATION_TYPE);
		assertEquals("bufferRecoList2", result.getRecoList());
	}

	/**
	 * Tests the null case, when nothing is found in the buffer
	 */
	@Test
	public void testGetFallbackSolutionResults_NullCase() {

		Mockito.reset(recoBufferService);
		Mockito.when(recoBufferService.getHashIdsForUser(USER_ID, SCENARIO_ID)).thenReturn(HASH_ID);
		SAPRecommendationBufferModel result = new SAPRecommendationBufferModel();
		result = recoService.getFallbackRecommendationByType(USER_ID, SCENARIO_ID, LEADING_ITEMS_7, RECOMMENDATION_TYPE);
		assertNull(result);
	}

	private String convertListToString(final List<String> list) {

		return list.stream().sorted() //
				.map(s -> s.toString()) //
				.collect(Collectors.joining(","));
	}

	private SAPRecommendationBufferModel generateBufferModel(final List<String> leadingItemsList, final String recoList) {
		final SAPRecommendationBufferModel buffer = new SAPRecommendationBufferModel();

		buffer.setRecoList(recoList);
		buffer.setHashId(HASH_ID);
		buffer.setScenarioId(SCENARIO_ID);
		buffer.setLeadingItems(convertListToString(leadingItemsList));

		return buffer;
	}

	private void saveRecommendationInBuffer(final List<String> leadingItemsList) {
		recoBufferService.saveRecommendation(USER_ID, SCENARIO_ID, HASH_ID, "1,2,3,4",
				convertListToString(leadingItemsList), "recoType", new Date());

	}

}
