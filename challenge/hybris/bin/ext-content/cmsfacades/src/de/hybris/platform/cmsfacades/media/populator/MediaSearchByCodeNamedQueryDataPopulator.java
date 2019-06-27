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
package de.hybris.platform.cmsfacades.media.populator;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.common.populator.impl.AbstractNamedQueryDataPopulator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableMap;


/**
 * This populator will convert parameters for the Media Search by code Named Query. This converter uses the catalogId
 * and catalogVersionId parameters to fetch the {@link CatalogVersionModel} and add it to the NamedQuery parameter's
 * Map.
 */
public class MediaSearchByCodeNamedQueryDataPopulator extends AbstractNamedQueryDataPopulator
{
	private static final String EMPTY = "";

	private static final String NAMED_QUERY_PARAM_CODE = MediaModel.CODE;
	private static final String NAMED_QUERY_PARAM_CATALOG_VERSION = MediaModel.CATALOGVERSION;

	public static final String PARAM_CODE = "code";
	public static final String PARAM_CATALOG_ID = "catalogId";
	public static final String PARAM_CATALOG_VERSION = "catalogVersion";

	private CatalogVersionService catalogVersionService;

	@Override
	public Map<String, ? extends Object> convertParameters(final String params)
	{
		if (StringUtils.isEmpty(params))
		{
			throw new ConversionException("Media Search by Code query parameters should never be empty.");
		}

		final Map<String, String> paramMap = buildParameterStringMap(params);

		final String catalogId = paramMap.get(PARAM_CATALOG_ID);
		final String catalogVersionId = paramMap.get(PARAM_CATALOG_VERSION);
		final String code = paramMap.get(PARAM_CODE);

		validateInputParameters(catalogId, catalogVersionId, code);

		try
		{
			final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(catalogId, catalogVersionId);

			final Map<String, Object> namedQueryParameterMap = new HashMap<>();
			namedQueryParameterMap.put(NAMED_QUERY_PARAM_CODE, PERCENT + code + PERCENT);
			namedQueryParameterMap.put(NAMED_QUERY_PARAM_CATALOG_VERSION, catalogVersion);

			return namedQueryParameterMap;
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException | IllegalArgumentException e)
		{
			throw new ConversionException("Error while getting the CatalogVersionModel from the parameters.", e);
		}
	}

	/**
	 * Validate if the input parameters are empty, and if they are, throws a ConversionException
	 *
	 * @param catalogId
	 *           - the catalog id extracted from the request
	 * @param catalogVersionId
	 *           - the catalog version id extracted from the request
	 * @throws ConversionException
	 */
	protected void validateInputParameters(final String catalogId, final String catalogVersionId, final String code)
	{
		if (StringUtils.isEmpty(catalogId))
		{
			throw new ConversionException("A [catalogId] parameter is required.");
		}
		if (StringUtils.isEmpty(catalogVersionId))
		{
			throw new ConversionException("A [catalogVersionId] parameter is required.");
		}
	}

	/**
	 * Using the parameter String, convert it to a Map<String, String> for future processing.
	 * 
	 * Suppress sonar warning (squid:S2095 | Java's garbage collection cannot be relied on to clean up everything. 
	 * 	Specifically, connections, streams, files and other classes that implement the Closeable interface or it's 
	 * 	super-interface) : in this case Stream is not opening any connection or IO stream. 
	 * 
	 * @param params
	 *           - the String parameter received from the request
	 * @return a Map<String, String> representing the requested parameter String
	 */
	@SuppressWarnings("squid:S2095")
	@Override
	public Map<String, String> buildParameterStringMap(final String params)
	{
		final String[] paramBlocks = params.split(COMMA);
		try (final Stream<String> stream = Arrays.stream(paramBlocks))
		{

			Map<String, String> parameterStringMap = new HashMap<String, String>();

			parameterStringMap = stream.map(paramBlock -> paramBlock.trim().split(COLON)) //
					.filter(paramBlock -> (paramBlock.length == 2))
					.collect(Collectors.toMap(paramPair -> paramPair[0].trim(), paramPair -> paramPair[1].trim()));

			return Stream
					.concat(ImmutableMap.of(NAMED_QUERY_PARAM_CODE, EMPTY).entrySet().stream(), parameterStringMap.entrySet().stream())
					.collect(Collectors.toMap(entry -> entry.getKey(), //
							entry -> entry.getValue(), //
							(entry1, entry2) -> entry1 + entry2));

		}
		catch (final IllegalStateException e)
		{
			throw new ConversionException("Error while parsing parameter map.", e);
		}

	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}
}
