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
package de.hybris.platform.cmsfacades.common.populator.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will take parameters of {@NamedQueryData} and add it to the {@NamedQuery} parameters map.
 */
public abstract class AbstractNamedQueryDataPopulator implements Populator<NamedQueryData, NamedQuery>
{
	protected static final String COMMA = ",";
	protected static final String COLON = ":";
	protected static final String PERCENT = "%";

	private CMSAdminSiteService cmsAdminSiteService;

	/**
	 * Converts the parameters from the request String into a Map. The input, if not empty, should be already validated
	 * with the following format: {param}:{value} Multiple parameters are also allowed separated by comma
	 *
	 * @param params
	 *           - the parameters received from the request
	 * @return a Map of parameters
	 */
	abstract public Map<String, ? extends Object> convertParameters(final String params);

	@Override
	public void populate(final NamedQueryData namedQueryData, final NamedQuery namedQuery) throws ConversionException
	{
		namedQuery.setParameters(convertParameters(namedQueryData.getParams()));
	}

	/**
	 * Using the parameter String, convert it to a Map<String, String> for future processing.
	 *
	 * @param params
	 *           - the String parameter received from the request
	 * @return a Map<String, String> representing the requested parameter String
	 */
	public Map<String, String> buildParameterStringMap(final String params)
	{
		final String[] paramBlocks = params.split(COMMA);
		try (Stream<String> stream = Arrays.stream(paramBlocks))
		{
			return stream.map(paramBlock -> paramBlock.trim().split(COLON)) //
					.filter(paramBlock -> paramBlock.length == 2)
					.collect(Collectors.toMap(paramPair -> paramPair[0].trim(), paramPair -> paramPair[1].trim()));
		}
		catch (final IllegalStateException e)
		{
			throw new ConversionException("Error while parsing parameter map.", e);
		}
	}

	public CatalogVersionModel getActiveCatalogVersion()
	{
		return getCmsAdminSiteService().getActiveCatalogVersion();
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
