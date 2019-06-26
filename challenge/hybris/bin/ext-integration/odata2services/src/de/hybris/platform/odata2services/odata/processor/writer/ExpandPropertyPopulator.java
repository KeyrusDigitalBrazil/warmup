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

import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;

import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Populates the $expand system property
 */
public class ExpandPropertyPopulator implements ResponseWriterPropertyPopulator
{
	private static final Logger LOG = LoggerFactory.getLogger(ExpandPropertyPopulator.class);

	@Override
	public boolean isApplicable(final ItemLookupRequest itemLookupRequest)
	{
		return itemLookupRequest.getExpand() != null && !itemLookupRequest.getExpand().isEmpty();
	}

	@Override
	public EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder populate(final EntityProviderWriteProperties properties, final ItemLookupRequest itemLookupRequest, final ItemLookupResult result)
	{
		final EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder builder = EntityProviderWriteProperties.fromProperties(properties);
		try
		{
			// We do not support the $select system property which is why null is passed as the argument
			final ExpandSelectTreeNode expandSelectTree = UriParser.createExpandSelectTree(null, itemLookupRequest.getExpand());
			final Map<String, ODataCallback> callbacks = populateCallbacks(expandSelectTree);
			builder.expandSelectTree(expandSelectTree).callbacks(callbacks);
		}
		catch (final EdmException e)
		{
			LOG.error("Cannot set $expand properties due to exception.", e);
			throw new InternalProcessingException("Problem while trying to set $expand system property.");
		}
		return builder;
	}

	private Map<String, ODataCallback> populateCallbacks(final ExpandSelectTreeNode expandSelectTree)
	{
		final Map<String, ODataCallback> callbacks = new HashMap<>();
		expandSelectTree.getLinks().forEach((propName, expandTreeNode) -> callbacks.put(propName, new CallbackWriter()));
		return callbacks;
	}
}
