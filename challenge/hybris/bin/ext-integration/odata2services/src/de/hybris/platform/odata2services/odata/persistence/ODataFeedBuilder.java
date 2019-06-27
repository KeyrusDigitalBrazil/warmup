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
package de.hybris.platform.odata2services.odata.persistence;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.core.ep.feed.FeedMetadataImpl;
import org.apache.olingo.odata2.core.ep.feed.ODataDeltaFeedImpl;

import com.google.common.base.Preconditions;

/**
 * Builder to create Valid ODataFeeds incl Metadata from a collection of ODataEntries
 */
public class ODataFeedBuilder
{
	private List<ODataEntry> entries = Collections.emptyList();

	private ODataFeedBuilder()
	{
		// not instantiable
	}

	public static ODataFeedBuilder oDataFeedBuilder()
	{
		return new ODataFeedBuilder();
	}

	public ODataFeedBuilder withEntries(final List<ODataEntry> entries)
	{
		this.entries = entries;
		return this;
	}

	public ODataFeed build()
	{
		Preconditions.checkArgument(entries != null, "List<ODataEntry> must be provided through withEntries(...) method");

		final FeedMetadataImpl metadata = new FeedMetadataImpl();
		metadata.setInlineCount(entries.size());
		return new ODataDeltaFeedImpl(entries, metadata);
	}
}
