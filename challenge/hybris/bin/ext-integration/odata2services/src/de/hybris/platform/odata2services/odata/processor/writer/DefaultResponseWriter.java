/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor.writer;

import static org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder;
import static org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties.fromProperties;
import static org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties.serviceRoot;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the {@link ResponseWriter}
 */
public class DefaultResponseWriter implements ResponseWriter
{
	private ResponseWriterPropertyPopulatorRegistry populatorRegistry;

	@Override
	public ODataResponse write(final ItemLookupRequest itemLookupRequest, final EdmEntitySet entitySet, final Map<String, Object> data) throws ODataException
	{
		ODataEntityProviderPropertiesBuilder writeProperties = getWriteProperties(itemLookupRequest);
		writeProperties = populateWriteProperties(writeProperties, itemLookupRequest, null);
		return write(itemLookupRequest.getContentType(), entitySet, data, writeProperties.build());
	}

	@Override
	public ODataResponse write(final ItemLookupRequest request, final EdmEntitySet entitySet, final ItemLookupResult<ODataEntry> result) throws ODataException
	{
		final List<Map<String, Object>> feeds = result.getEntries().stream()
				.map(ODataEntry::getProperties)
				.collect(Collectors.toList());

		ODataEntityProviderPropertiesBuilder writePropertiesBuilder = getWriteProperties(request);

		if (!isNavigationProperty(request))
		{
			writePropertiesBuilder = populateWriteProperties(writePropertiesBuilder, request, result);
		}
		return write(request.getContentType(), entitySet, feeds, writePropertiesBuilder.build());
	}

	protected ODataResponse write(
			final String contentType,
			final EdmEntitySet entitySet,
			final Map<String, Object> data,
			final EntityProviderWriteProperties writeProperties) throws EntityProviderException
	{
		return EntityProvider.writeEntry(contentType, entitySet, data, writeProperties);
	}

	protected ODataResponse write(
			final String contentType,
			final EdmEntitySet entitySet,
			final List<Map<String, Object>> feeds,
			final EntityProviderWriteProperties properties) throws EntityProviderException
	{
		return EntityProvider.writeFeed(contentType, entitySet, feeds, properties);
	}

	private static boolean isNavigationProperty(final ItemLookupRequest itemLookupRequest)
	{
		return itemLookupRequest.getODataEntry() != null;
	}

	private ODataEntityProviderPropertiesBuilder getWriteProperties(final ItemLookupRequest itemLookupRequest)
	{
		return serviceRoot(itemLookupRequest.getServiceRoot()).isDataBasedPropertySerialization(true);
	}

	private ODataEntityProviderPropertiesBuilder populateWriteProperties(
			final ODataEntityProviderPropertiesBuilder builder,
			final ItemLookupRequest req,
			final ItemLookupResult result)
	{

		ODataEntityProviderPropertiesBuilder propertiesBuilder = fromProperties(builder.build());
		for (final ResponseWriterPropertyPopulator populator : getPopulatorRegistry().getPopulators(req))
		{
			propertiesBuilder = populator.populate(propertiesBuilder.build(), req, result);
		}
		return propertiesBuilder;
	}

	protected ResponseWriterPropertyPopulatorRegistry getPopulatorRegistry()
	{
		return populatorRegistry;
	}

	@Required
	public void setPopulatorRegistry(final ResponseWriterPropertyPopulatorRegistry populatorRegistry)
	{
		this.populatorRegistry = populatorRegistry;
	}
}
