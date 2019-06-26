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
package de.hybris.platform.campaigns.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.campaigns.dao.CampaignDao;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;


@IntegrationTest
public class DefaultCampaignDaoIT extends ServicelayerTransactionalTest
{
	@Resource
	private CampaignDao campaignDao;
	@Resource
	private ModelService modelService;
	@Resource
	private TimeService timeService;
	private List<CampaignModel> allCampaigns;
	private List<CampaignModel> activeCampaigns;
	private Date nowDate;

	@Before
	public void setUp() throws Exception
	{

		final Instant instant = Instant.ofEpochMilli(getTimeService().getCurrentTime().getTime());
		final LocalDateTime now = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

		nowDate = Date.from(now.toInstant(ZoneOffset.UTC));
		final Date pastDate = Date.from(now.minusDays(2).toInstant(ZoneOffset.UTC));
		final Date futureDate = Date.from(now.plusDays(2).toInstant(ZoneOffset.UTC));

		final CampaignModel activeCampaign = createCampaign("active campaign", pastDate, futureDate, true);
		final CampaignModel rollingActiveCampaign = createCampaign("rolling active campaign", pastDate, null, true);

		activeCampaigns = Lists.newArrayList();
		activeCampaigns.add(activeCampaign);
		activeCampaigns.add(rollingActiveCampaign);

		allCampaigns = Lists.newArrayList();
		allCampaigns.add(activeCampaign);
		allCampaigns.add(rollingActiveCampaign);
		allCampaigns.add(createCampaign("disabled campaign", pastDate, futureDate, false));
		allCampaigns.add(createCampaign("past campaign", pastDate, pastDate, false));
		allCampaigns.add(createCampaign("rolling disabled campaign", pastDate, null, false));
		allCampaigns.add(createCampaign("forthcoming campaign", futureDate, null, true));
		allCampaigns.add(createCampaign("since ever active campaign", null, futureDate, true));
	}

	@Test
	public void shouldProvideAllCampaigns() throws Exception
	{
		//when
		final List<CampaignModel> activeCampaigns = getCampaignDao().findAllCampaigns();
		//then
		assertThat(activeCampaigns).containsExactlyElementsOf(allCampaigns);
	}

	@Test
	public void shouldProvideCampaignByCode() throws Exception
	{
		//given
		final String code = allCampaigns.iterator().next().getCode();
		//when
		final CampaignModel matchingCampaign = getCampaignDao().findCampaignByCode(code);
		//then
		assertThat(matchingCampaign.getCode()).isEqualTo(code);
	}

	@Test
	public void shouldRaiseExceptionIfNoCampaignWithGivenCode() throws Exception
	{
		//given
		final String code = "you will never find me";
		assertThat(allCampaigns.stream().map(CampaignModel::getCode).anyMatch(code::equals)).isFalse();
		//when
		final Throwable throwable = catchThrowable(() -> getCampaignDao().findCampaignByCode(code));
		//then
		assertThat(throwable).isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldProvideOnlyActiveCampaigns() throws Exception
	{
		//when
		final List<CampaignModel> activeCampaigns = getCampaignDao().findActiveCampaigns(nowDate);
		//then
		assertThat(activeCampaigns).containsExactlyElementsOf(activeCampaigns);
	}


	protected CampaignModel createCampaign(final String code, final Date pastDate, final Date futureDate, final boolean enabled)
	{
		final CampaignModel campaign = getModelService().create(CampaignModel.class);
		campaign.setCode(code);
		campaign.setStartDate(pastDate);
		campaign.setEndDate(futureDate);
		campaign.setEnabled(Boolean.valueOf(enabled));
		getModelService().save(campaign);
		return campaign;
	}

	protected CampaignDao getCampaignDao()
	{
		return campaignDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}
}
