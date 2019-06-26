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
package com.hybris.ymkt.segmentation.facades;

import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.util.localization.Localization;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.segmentation.dto.SAPInitiative;
import com.hybris.ymkt.segmentation.services.InitiativeService;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery.TileFilterCategory;


/**
 * Facade that provides functionality to retrieve campaigns from back end
 */
public class CampaignRestrictionPopulatorFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignRestrictionPopulatorFacade.class);

	protected InitiativeService initiativeService;

	protected OptionData createOptionData(final SAPInitiative initiative)
	{
		final OptionData opData = new OptionData();
		opData.setId(initiative.getId());
		opData.setLabel(initiative.getFormattedLabel());
		return opData;
	}

	/**
	 * Get and return one campaign
	 * 
	 * @param campaignId
	 *           The campaignId string
	 * @return campaign {@link OptionData} (dropdown value) with label "campaignId + campaignName + (memberCount)" and
	 *         value "campaignId".
	 */
	public OptionData getCampaignById(final String campaignId)
	{
		try
		{
			final InitiativeQuery query = new InitiativeQuery.Builder() //
					.setTileFilterCategories(TileFilterCategory.ACTIVE, TileFilterCategory.PLANNED) //
					.setId(campaignId) //
					.build();

			final List<OptionData> temp = initiativeService.getInitiatives(query).stream() //
					.map(this::createOptionData) //
					.collect(Collectors.toList());

			if (temp.isEmpty())
			{
				throw new UnknownIdentifierException(Localization.getLocalizedString("segmentation.campaignIdError.description"));
			}

			return temp.get(0);
		}
		catch (IOException e)
		{
			LOGGER.error("Error retrieving Campaign Initiative", e);
			return null;
		}
	}

	/**
	 * Get and return campaigns from backend
	 * 
	 * @param searchTerm
	 *           value entered by user
	 * @param top
	 *           number of campaigns to show per page
	 * @param skip
	 *           number of campaigns to skip
	 * @return campaigns
	 */
	public List<OptionData> getCampaigns(final String searchTerm, final String skip, final String top)
	{
		final String actualSkip = Integer.toString(Integer.parseInt(skip) * Integer.parseInt(top));

		try
		{
			final InitiativeQuery query = new InitiativeQuery.Builder() //
					.setTileFilterCategories(TileFilterCategory.ACTIVE, TileFilterCategory.PLANNED) //
					.setSearchTerms(searchTerm) //
					.build();

			return this.initiativeService.getInitiatives(query, actualSkip, top).stream() //
					.map(this::createOptionData) //
					.collect(Collectors.toList());
		}
		catch (IOException e)
		{
			LOGGER.error("Error retrieving Campaign Initiatives", e);
			return Collections.emptyList();
		}

	}

	@Required
	public void setInitiativeService(final InitiativeService initiativeService)
	{
		this.initiativeService = initiativeService;
	}
}
