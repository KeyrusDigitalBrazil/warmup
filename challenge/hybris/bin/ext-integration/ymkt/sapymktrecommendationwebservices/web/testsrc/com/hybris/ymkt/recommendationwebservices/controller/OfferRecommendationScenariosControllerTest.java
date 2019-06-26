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

import com.hybris.ymkt.recommendation.model.CMSSAPOfferRecoComponentModel;
import com.hybris.ymkt.recommendationwebservices.facades.OfferRecommendationPopulatorFacade;

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
public class OfferRecommendationScenariosControllerTest
{
	@Mock
	private OfferRecommendationPopulatorFacade offerRecommendationPopulatorFacade;
	
	private OfferRecommendationScenariosController offerRecommendationScenariosController = new OfferRecommendationScenariosController();
	
	private static final String CONTENT_POSITION_JSON = "{\"options\":[{\"id\":\"111111\",\"label\":\"contentposition\"}]}";
	
	@Before
	public void setup() throws IOException
	{
		MockitoAnnotations.initMocks(this);
		offerRecommendationScenariosController.setOfferRecoPopulatorFacade(offerRecommendationPopulatorFacade);
	}

	@Test
	public void testPopulateDropdown_ContentPosition() throws IOException
	{
		final String DROPDOWN_TYPE = CMSSAPOfferRecoComponentModel.CONTENTPOSITION;
		
		OptionData optionData = new OptionData();
		optionData.setId("111111");
		optionData.setLabel(DROPDOWN_TYPE);
		
		given(offerRecommendationPopulatorFacade.populateDropDown(DROPDOWN_TYPE)).willReturn(Arrays.asList(optionData));
		
		final String options = offerRecommendationScenariosController.populateDropdown(DROPDOWN_TYPE);
		assertEquals(CONTENT_POSITION_JSON, options);
	}
}
