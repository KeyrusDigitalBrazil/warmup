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
package de.hybris.platform.campaigns.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.campaigns.dao.CampaignDao;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.servicelayer.time.TimeService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCampaignServiceTest
{
	private static final String CAMPAIGN_CODE = "CAMPAIGN_CODE";
	@InjectMocks
	private DefaultCampaignService campaignService;
	@Mock
	private CampaignDao campaignDao;
	@Mock
	private TimeService timeService;
	@Mock
	private CampaignModel campaignModel;
	@Mock
	private Date currentDateTime;

	@Test
	public void shouldRaiseExceptionWhenGettingCampaignWithNullCode() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> campaignService.getCampaignByCode(null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Parameter code can not be null");
	}

	@Test
	public void shouldGetCampaignByCode() throws Exception
	{
		//given
		given(campaignDao.findCampaignByCode(CAMPAIGN_CODE)).willReturn(campaignModel);
		//when
		final CampaignModel campaignByCode = campaignService.getCampaignByCode(CAMPAIGN_CODE);
		//then
		assertThat(campaignByCode).isEqualTo(campaignModel);
	}

	@Test
	public void shouldGetAllCampaigns() throws Exception
	{
		//given
		final List<CampaignModel> campaigns = newArrayList(campaignModel);
		given(campaignDao.findAllCampaigns()).willReturn(campaigns);
		//when
		final List<CampaignModel> allCampaigns = campaignService.getAllCampaigns();
		//then
		assertThat(allCampaigns).containsExactly(campaignModel);
	}

	@Test
	public void shouldGetAllActiveCampaignsForSpecifiedDate() throws Exception
	{
		//given
		final List<CampaignModel> campaigns = newArrayList(campaignModel);
		given(timeService.getCurrentTime()).willReturn(currentDateTime);
		given(campaignDao.findActiveCampaigns(currentDateTime)).willReturn(campaigns);
		//when
		final List<CampaignModel> activeCampaigns = campaignService.getActiveCampaigns();
		//then
		assertThat(activeCampaigns).containsExactly(campaignModel);
		verify(campaignDao).findActiveCampaigns(currentDateTime);
	}
}
