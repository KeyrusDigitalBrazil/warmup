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

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ATTRIBUTE_NAME;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.exception.PropertyNotFoundException;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.uri.NavigationSegment;

public class DefaultNavigationSegmentExplorer implements NavigationSegmentExplorer
{
	@Override
	public ExpandedEntity expandForSingleEntity(
			final ItemLookupRequest lookupRequest,
			final ODataEntry entry) throws EdmException
	{
		final StringBuilder propertyNamePath = new StringBuilder();
		EdmEntitySet entitySet = lookupRequest.getEntitySet();
		ODataEntry navigationEntry = entry;
		for (final NavigationSegment navigationSegment : lookupRequest.getNavigationSegments())
		{
			entitySet = navigationSegment.getEntitySet();
			final String propertyName = navigationSegment.getNavigationProperty().getName();
			propertyNamePath.append("/").append(propertyName);
			navigationEntry = (ODataEntry) navigationEntry.getProperties().get(propertyName);
			if (navigationEntry == null)
			{
				handlePropertyNotFound(lookupRequest, propertyNamePath);
			}
		}

		return ExpandedEntity.expandedEntityBuilder()
				.withEdmEntitySet(entitySet)
				.withODataEntry(navigationEntry).build();
	}

	@Override
	public ExpandedEntity expandForEntityList(
			final ItemLookupRequest lookupRequest,
			final ODataEntry entry) throws EdmException
	{
		ODataFeed dataFeed = null;
		final StringBuilder propertyNamePath = new StringBuilder();
		EdmEntitySet entitySet = lookupRequest.getEntitySet();
		ODataEntry navigationEntry = entry;
		for (final NavigationSegment navigationSegment : lookupRequest.getNavigationSegments())
		{
			entitySet = navigationSegment.getEntitySet();
			final String propertyName = navigationSegment.getNavigationProperty().getName();
			propertyNamePath.append("/").append(propertyName);
			if (navigationEntry.getProperties().get(propertyName) instanceof ODataEntry && isSupportedNavigationProperty(propertyName))
			{
				navigationEntry = (ODataEntry) navigationEntry.getProperties().get(propertyName);
			}
			else if (navigationEntry.getProperties().get(propertyName) instanceof ODataFeed)
			{
				dataFeed = (ODataFeed) navigationEntry.getProperties().get(propertyName);
				break;
			}
		}

		if (dataFeed == null)
		{
			return handlePropertyNotFound(lookupRequest, propertyNamePath);
		}

		return ExpandedEntity.expandedEntityBuilder()
				.withEdmEntitySet(entitySet)
				.withODataEntry(navigationEntry)
				.withODataFeed(dataFeed).build();
	}

	private boolean isSupportedNavigationProperty(final String propertyName)
	{
		return !LOCALIZED_ATTRIBUTE_NAME.equals(propertyName);
	}

	private ExpandedEntity handlePropertyNotFound(final ItemLookupRequest lookupRequest, final StringBuilder propertyNamePath) throws EdmException
	{
		throw new PropertyNotFoundException(lookupRequest.getEntitySet().getEntityType().getName(),
				propertyNamePath.toString(), lookupRequest.getIntegrationKey());
	}
}
