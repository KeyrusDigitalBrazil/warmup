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
/**
 * 
 */
package com.hybris.ymkt.segmentation.facades;

import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.testframework.Assert;
import de.hybris.platform.util.localization.Localization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.segmentation.dto.SAPInitiative;
import com.hybris.ymkt.segmentation.services.InitiativeService;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CampaignRestrictionPopulatorFacadeTest
{
	private static CampaignRestrictionPopulatorFacade populator = new CampaignRestrictionPopulatorFacade();
	private static OptionData optionData = new OptionData();
	private static SAPInitiative initiative = new SAPInitiative();

	@Mock
	InitiativeService initiativeService;

	@Before
	public void setUp() throws Exception
	{

		initiative.setId("initiativeId");
		initiative.setMemberCount("1");
		initiative.setName("initiativeName");

		optionData = populator.createOptionData(initiative);

		populator.setInitiativeService(initiativeService);
	}

	@Test
	public void createOptionDataTest()
	{
		optionData = populator.createOptionData(initiative);

		Assert.assertEquals("initiativeId", optionData.getId());
		Assert.assertEquals(initiative.getFormattedLabel(), optionData.getLabel());
	}

	@Test
	public void getCampaignByIdTest() throws IOException
	{

		List<SAPInitiative> initiativeList = new ArrayList<>();
		initiativeList.add(initiative);
		List<OptionData> optionDataList = new ArrayList<>();
		optionDataList.add(optionData);

		Mockito.doReturn(initiativeList).when(initiativeService).getInitiatives(any());

		Assert.assertEquals(optionData.getId(), populator.getCampaignById(anyString()).getId());
		Assert.assertEquals(optionData.getLabel(), populator.getCampaignById(anyString()).getLabel());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCampaignByIdTest_emptyResultsFromRequest() throws IOException
	{
		List<SAPInitiative> initiativeList = new ArrayList<>();
		initiativeList.add(initiative);
		List<OptionData> optionDataList = new ArrayList<>();
		optionDataList.add(optionData);

		Mockito.doReturn(Collections.emptyList()).when(initiativeService).getInitiatives(any());
		populator.getCampaignById("");

	}

	@Test
	public void getCampaignByIdTest_throwException() throws IOException
	{
		given(initiativeService.getInitiatives(any())).willThrow(IOException.class);
		assertNull(populator.getCampaignById(""));
	}

	@Test
	public void getCampaignsTest() throws IOException
	{
		List<SAPInitiative> initiativeList = new ArrayList<>();
		initiativeList.add(initiative);
		List<OptionData> optionDataList = new ArrayList<>();
		optionDataList.add(optionData);

		Mockito.doReturn(initiativeList).when(initiativeService).getInitiatives(any(), anyString(), anyString());

		Assert.assertEquals(optionDataList.get(0).getId(), populator.getCampaigns("searchTerms", "1", "10").get(0).getId());
		Assert.assertEquals(optionDataList.get(0).getLabel(), populator.getCampaigns("searchTerms", "1", "10").get(0).getLabel());
	}

	@Test
	public void getCampaignsTest_throwException() throws IOException
	{
		given(initiativeService.getInitiatives(any(), anyString(), anyString())).willThrow(IOException.class);
		Assert.assertCollection(Collections.emptyList(), populator.getCampaigns("searchTerms", "1", "10"));
	}

}
