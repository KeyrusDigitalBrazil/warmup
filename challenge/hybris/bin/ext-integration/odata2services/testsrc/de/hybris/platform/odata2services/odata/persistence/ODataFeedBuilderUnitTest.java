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

import static de.hybris.platform.odata2services.odata.persistence.ODataFeedBuilder.oDataFeedBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.utils.ODataEntryBuilder;

@UnitTest
public class ODataFeedBuilderUnitTest
{
	@Test
	public void testEmptyFeedBuilder()
	{
		ODataFeed feed = oDataFeedBuilder().build();
		assertThat(feed).hasFieldOrPropertyWithValue("entries", Collections.emptyList());
	}

	@Test
	public void testFeedBuilderSetsMetadataCount()
	{
		final List<ODataEntry> entries = Collections.singletonList(new ODataEntryBuilder().build());
		ODataFeed feed = oDataFeedBuilder().withEntries(entries).build();
		assertThat(feed).hasFieldOrPropertyWithValue("entries", entries);
		assertThat(feed.getFeedMetadata()).hasFieldOrPropertyWithValue("inlineCount", 1);
	}

	@Test
	public void testUnsetEntriesIsNotAllowed()
	{
		assertThatThrownBy(()-> oDataFeedBuilder().withEntries(null).build())
				.isInstanceOf(IllegalArgumentException.class);
	}
}