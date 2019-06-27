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
package com.hybris.ymkt.recommendationwebservices.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import com.hybris.ymkt.recommendation.model.CMSSAPRecommendationComponentModel;
import com.hybris.ymkt.recommendationwebservices.facades.RecommendationPopulatorFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.OptionData;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class RecommendationScenariosControllerTest
{
	@Mock
	private RecommendationPopulatorFacade recommendationPopulatorFacade;
	
	private RecommendationScenariosController recommendationScenariosController = new RecommendationScenariosController();
	
	private static final String RECO_TYPE_JSON = "{\"options\":[{\"id\":\"111111\",\"label\":\"recotype\"}]}";
	private static final String LEADING_ITEM_TYPE_JSON = "{\"options\":[{\"id\":\"111111\",\"label\":\"leadingitemtype\"}]}";
	private static final String LEADING_ITEM_DS_TYPE_JSON = "{\"options\":[{\"id\":\"111111\",\"label\":\"leadingitemdstype\"}]}";
	private static final String CART_ITEM_DS_TYPE_JSON = "{\"options\":[{\"id\":\"111111\",\"label\":\"cartitemdstype\"}]}";
	
	@Before
	public void setup() throws IOException
	{
		MockitoAnnotations.initMocks(this);
		recommendationScenariosController.setRecommendationPopulatorFacade(recommendationPopulatorFacade);
	}

	@Test
	public void testPopulateDropdown_RecoType() throws IOException
	{
		final String DROPDOWN_TYPE = CMSSAPRecommendationComponentModel.RECOTYPE;
		
		OptionData optionData = new OptionData();
		optionData.setId("111111");
		optionData.setLabel(DROPDOWN_TYPE);
		
		given(recommendationPopulatorFacade.populateDropDown(DROPDOWN_TYPE)).willReturn(Arrays.asList(optionData));
		
		final String options = recommendationScenariosController.populateDropdown(DROPDOWN_TYPE);
		assertEquals(RECO_TYPE_JSON, options);
	}
	
	@Test
	public void testPopulateDropdown_LeadingItemType() throws IOException
	{
		final String DROPDOWN_TYPE = CMSSAPRecommendationComponentModel.LEADINGITEMTYPE;
		
		OptionData optionData = new OptionData();
		optionData.setId("111111");
		optionData.setLabel(DROPDOWN_TYPE);
		
		given(recommendationPopulatorFacade.populateDropDown(DROPDOWN_TYPE)).willReturn(Arrays.asList(optionData));
		
		final String options = recommendationScenariosController.populateDropdown(DROPDOWN_TYPE);
		assertEquals(LEADING_ITEM_TYPE_JSON, options);
	}
	
	@Test
	public void testPopulateDropdown_LeadingItemDataSourceType() throws IOException
	{
		final String DROPDOWN_TYPE = CMSSAPRecommendationComponentModel.LEADINGITEMDSTYPE;
		
		OptionData optionData = new OptionData();
		optionData.setId("111111");
		optionData.setLabel(DROPDOWN_TYPE);
		
		given(recommendationPopulatorFacade.populateDropDown(DROPDOWN_TYPE)).willReturn(Arrays.asList(optionData));
		
		final String options = recommendationScenariosController.populateDropdown(DROPDOWN_TYPE);
		assertEquals(LEADING_ITEM_DS_TYPE_JSON, options);
	}
	
	@Test
	public void testPopulateDropdown_CartItemDataSourceType() throws IOException
	{
		final String DROPDOWN_TYPE = CMSSAPRecommendationComponentModel.CARTITEMDSTYPE;
		
		OptionData optionData = new OptionData();
		optionData.setId("111111");
		optionData.setLabel(DROPDOWN_TYPE);
		
		given(recommendationPopulatorFacade.populateDropDown(DROPDOWN_TYPE)).willReturn(Arrays.asList(optionData));
		
		final String options = recommendationScenariosController.populateDropdown(DROPDOWN_TYPE);
		assertEquals(CART_ITEM_DS_TYPE_JSON, options);
	}
}
