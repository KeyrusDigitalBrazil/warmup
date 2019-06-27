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
package de.hybris.platform.odata2services.odata.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ODataFeedBuilder;
import de.hybris.platform.odata2services.odata.persistence.exception.PropertyNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.utils.ODataEntryBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.core.uri.NavigationSegmentImpl;
import org.junit.Before;
import org.junit.Test;


public class DefaultNavigationSegmentExplorerUnitTest
{
	private static final String PRODUCTS = "Products";
	private static final String LOCALIZED_ATTRIBUTES = "localizedAttributes";

	private final DefaultNavigationSegmentExplorer explorer = new DefaultNavigationSegmentExplorer();

	private final EdmEntitySet startEntitySet = mock(EdmEntitySet.class);
	private final EdmEntityType entityType = mock(EdmEntityType.class);

	@Before
	public void setUp() throws EdmException
	{
		when(startEntitySet.getEntityType()).thenReturn(entityType);
		when(entityType.getName()).thenReturn("Products");
	}

	@Test
	public void testExpandForSingleEntity_ExpandedEntitySuccess() throws EdmException
	{
		final ODataEntry entry = mock(ODataEntry.class);
		final ODataEntry expectedODataEntry = ODataEntryBuilder.oDataEntryBuilder().build();
		when(entry.getProperties()).thenReturn(Collections.singletonMap("existingProperty", expectedODataEntry));

		final ItemLookupRequest itemLookupRequest = givenLookupRequest(entry, createNavigationSegmentWithNavigationProperty("existingProperty"));

		final ExpandedEntity expandedEntity = explorer.expandForSingleEntity(itemLookupRequest, entry);
		assertThat(expandedEntity).isNotNull()
				.hasFieldOrPropertyWithValue("oDataEntry", expectedODataEntry)
				.hasFieldOrPropertyWithValue("oDataFeed", null)
				.hasFieldOrPropertyWithValue("edmEntitySet", startEntitySet);
	}

	@Test
	public void testExpandForSingleEntity_NavigationSegment_NotFound() throws EdmException
	{
		final ODataEntry entry = mock(ODataEntry.class);
		when(entry.getProperties()).thenReturn(Collections.emptyMap());

		final ItemLookupRequest itemLookupRequest = givenLookupRequest(entry, createNavigationSegmentWithNavigationProperty("notFoundProperty"));

		assertThatThrownBy(() -> explorer.expandForSingleEntity(itemLookupRequest, entry))
				.isInstanceOf(PropertyNotFoundException.class)
				.hasMessage("Property with path [/notFoundProperty] from [Products] with integration key [Staged|Default|ProductCode] was not found.");
	}

	@Test
	public void testExpandForEntityList_FeedAndEntryExisting() throws EdmException
	{
		final ODataEntry entry = mock(ODataEntry.class);
		final ODataEntry expectedODataEntry = mock(ODataEntry.class);
		final ODataFeed expectedODataFeed = ODataFeedBuilder.oDataFeedBuilder().build();
		when(entry.getProperties()).thenReturn(Collections.singletonMap("property1", expectedODataEntry));
		when(expectedODataEntry.getProperties()).thenReturn(Collections.singletonMap("property2", expectedODataFeed));

		final ItemLookupRequest itemLookupRequest = givenLookupRequest(entry,
				createNavigationSegmentWithNavigationProperty("property1"),
				createNavigationSegmentWithNavigationProperty("property2"));

		final ExpandedEntity expandedEntity = explorer.expandForEntityList(itemLookupRequest, entry);
		assertThat(expandedEntity).isNotNull()
				.hasFieldOrPropertyWithValue("oDataEntry", expectedODataEntry)
				.hasFieldOrPropertyWithValue("oDataFeed", expectedODataFeed)
				.hasFieldOrPropertyWithValue("edmEntitySet", startEntitySet);
	}

	@Test
	public void testExpandForEntityList_NotExistingFeed() throws EdmException
	{
		final ODataEntry entry = mock(ODataEntry.class);
		when(entry.getProperties()).thenReturn(Collections.emptyMap());

		final ItemLookupRequest itemLookupRequest = givenLookupRequest(entry, createNavigationSegmentWithNavigationProperty("notFoundProperty"));

		assertThatThrownBy(() -> explorer.expandForEntityList(itemLookupRequest, entry))
				.isInstanceOf(PropertyNotFoundException.class)
				.hasMessage("Property with path [/notFoundProperty] from [Products] with integration key [Staged|Default|ProductCode] was not found.");
	}

	@Test
	public void testLocalizedAttributesNavPropertyIsNotExpandedForEntityList() throws EdmException
	{
		final ODataEntry entry = mock(ODataEntry.class);
		final ODataEntry expectedODataEntry = mock(ODataEntry.class);
		when(entry.getProperties()).thenReturn(Collections.singletonMap(LOCALIZED_ATTRIBUTES, expectedODataEntry));

		final ItemLookupRequest itemLookupRequest = givenLookupRequest(entry,
				createNavigationSegmentWithNavigationProperty(LOCALIZED_ATTRIBUTES));

		assertThatThrownBy(() -> explorer.expandForEntityList(itemLookupRequest, entry))
				.isInstanceOf(PropertyNotFoundException.class)
				.hasMessageContaining("/localizedAttributes");
	}

	private NavigationSegment createNavigationSegmentWithNavigationProperty(final String navigationPropertyName) throws EdmException
	{
		final NavigationSegment navigationSegment = new NavigationSegmentImpl();
		((NavigationSegmentImpl) navigationSegment).setNavigationProperty(mockNavigationProperty(navigationPropertyName));
		((NavigationSegmentImpl) navigationSegment).setEntitySet(startEntitySet);
		return navigationSegment;
	}

	private ItemLookupRequest givenLookupRequest(final ODataEntry oDataEntry, final NavigationSegment... navigationSegments)
	{
		final ItemLookupRequest request = mock(ItemLookupRequest.class);
		when(request.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		when(request.getIntegrationObjectCode()).thenReturn(PRODUCTS);
		when(request.getODataEntry()).thenReturn(oDataEntry);
		when(request.getEntitySet()).thenReturn(startEntitySet);
		when(request.getIntegrationKey()).thenReturn("Staged|Default|ProductCode");
		when(request.getNavigationSegments()).thenReturn(Arrays.asList(navigationSegments));
		return request;
	}

	private EdmNavigationProperty mockNavigationProperty(final String propertyName) throws EdmException
	{
		final EdmNavigationProperty navigationProperty = mock(EdmNavigationProperty.class);
		doReturn(propertyName).when(navigationProperty).getName();
		return navigationProperty;
	}
}