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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.callback.OnWriteEntryContent;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.ep.callback.WriteCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackResult;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackResult;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallbackWriter implements OnWriteFeedContent, OnWriteEntryContent
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CallbackWriter.class);

	@Override
	public WriteFeedCallbackResult retrieveFeedResult(final WriteFeedCallbackContext context) throws ODataApplicationException
	{
		final WriteFeedCallbackResult result = new WriteFeedCallbackResult();
		result.setInlineProperties(properties(context));
		result.setFeedData(getFeedEntries(context));
		return result;
	}

	@Override
	public WriteEntryCallbackResult retrieveEntryResult(final WriteEntryCallbackContext context) throws ODataApplicationException
	{
		final WriteEntryCallbackResult result = new WriteEntryCallbackResult();
		result.setInlineProperties(properties(context));
		result.setEntryData(getNavPropertyEntityData(context));
		return result;
	}

	private EntityProviderWriteProperties properties(final WriteCallbackContext context)
	{
		return EntityProviderWriteProperties
				.fromProperties(context.getCurrentWriteProperties())
				.expandSelectTree(context.getCurrentExpandSelectTreeNode())
				.callbacks(populateCallbacks(context.getCurrentExpandSelectTreeNode()))
				.build();
	}

	private List<Map<String, Object>> getFeedEntries(final WriteFeedCallbackContext context) throws ODataApplicationException
	{
		try
		{
			final ODataFeed entries = (ODataFeed) context.getEntryData().get(context.getNavigationProperty().getName());
			return entries == null ? Collections.emptyList() : entries.getEntries().stream().map(ODataEntry::getProperties).collect(Collectors.toList());
		}
		catch (final EdmException e)
		{
			LOGGER.error("Encountered error while trying to get navigation property name from {}", context.getNavigationProperty(), e);
			throw new ODataApplicationException("Could not $expand navigation property", Locale.ENGLISH, e);
		}
	}

	private Map<String, ODataCallback> populateCallbacks(final ExpandSelectTreeNode expandSelectTree)
	{
		final Map<String, ODataCallback> callbacks = new HashMap<>();
		expandSelectTree.getLinks().forEach((propName, expandTreeNode) -> callbacks.put(propName, new CallbackWriter()));
		return callbacks;
	}

	private Map<String, Object> getNavPropertyEntityData(final WriteEntryCallbackContext context) throws ODataApplicationException
	{
		try
		{
			final ODataEntry navProperty = (ODataEntry) context.getEntryData().get(context.getNavigationProperty().getName());
			return navProperty != null ? navProperty.getProperties() : null;
		}
		catch (final EdmException e)
		{
			LOGGER.error("Encountered error while trying to get navigation property name from {}", context.getNavigationProperty(), e);
			throw new ODataApplicationException("Could not $expand navigation property.", Locale.ENGLISH, e);
		}
	}
}
