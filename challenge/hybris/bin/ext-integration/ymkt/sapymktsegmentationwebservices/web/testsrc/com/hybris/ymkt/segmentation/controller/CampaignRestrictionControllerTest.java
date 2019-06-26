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
package com.hybris.ymkt.segmentation.controller;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import com.hybris.ymkt.segmentation.facades.CampaignRestrictionPopulatorFacade;


/**
 *
 */
@UnitTest
public class CampaignRestrictionControllerTest
{
	private static final String CAMPAIGN_JSON = "{\"id\":\"333333\",\"label\":\"Campaign 3\"}";
	private static final String CAMPAIGNS_JSON = "{\"options\":[{\"id\":\"111111\",\"label\":\"Campaign 1\"},{\"id\":\"222222\",\"label\":\"Campaign 2\"}]}";

	private CampaignRestrictionController campaignRestrictionController = new CampaignRestrictionController();
	
	@Mock
	private CampaignRestrictionPopulatorFacade campaignRestrictionPopulatorFacade;

	@Before
	public void setup() throws IOException
	{
		MockitoAnnotations.initMocks(this);
		campaignRestrictionController.setCampaignRestrictionPopulatorFacade(campaignRestrictionPopulatorFacade);
	}

	@Test
	public void testPopulateCampaignDropdown() throws IOException
	{
		OptionData campaignOption1 = new OptionData();
		campaignOption1.setId("111111");
		campaignOption1.setLabel("Campaign 1");

		OptionData campaignOption2 = new OptionData();
		campaignOption2.setId("222222");
		campaignOption2.setLabel("Campaign 2");

		given(campaignRestrictionPopulatorFacade.getCampaigns(any(), any(), any()))
				.willReturn(Arrays.asList(campaignOption1, campaignOption2));

		final String campaigns = campaignRestrictionController.populateCampaignsDropdown("", "1", "5");
		Assert.assertEquals(CAMPAIGNS_JSON, campaigns);
	}

	@Test
	public void testSetExistingCampaignDropdownValue_WithEmpty() throws IOException
	{
		Assert.assertEquals("", campaignRestrictionController.setExistingCampaignDropdownValue(Optional.empty()));
	}

	@Test
	public void testSetExistingCampaignDropdownValue_WithValue() throws IOException
	{
		OptionData campaignOption = new OptionData();
		campaignOption.setId("333333");
		campaignOption.setLabel("Campaign 3");
		given(campaignRestrictionPopulatorFacade.getCampaignById(any())).willReturn(campaignOption);

		final String campaign = campaignRestrictionController.setExistingCampaignDropdownValue(Optional.of("333333"));

		Assert.assertEquals(CAMPAIGN_JSON, campaign);
	}
}
