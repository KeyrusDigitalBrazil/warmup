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
package de.hybris.platform.odata2services.odata.processor.reader;

import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder;

import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.processor.ExpandedEntity;
import de.hybris.platform.odata2services.odata.processor.NavigationSegmentExplorer;

import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * The ExpandedEntityReader reads from the commerce suite a navigation property that is a collection
 */
public class ExpandedEntityReader extends AbstractEntityReader
{
	private NavigationSegmentExplorer navigationSegmentExplorer;

	@Override
	public boolean isApplicable(final UriInfo uriInfo)
	{
		final List<KeyPredicate> keyPredicates = uriInfo.getKeyPredicates();
		final List<NavigationSegment> navigationSegments = uriInfo.getNavigationSegments();

		Preconditions.checkArgument(keyPredicates != null, "Key predicates can't be null when reading entity");
		Preconditions.checkArgument(navigationSegments != null, "Navigation segments can't be null when reading entity");

		return !keyPredicates.isEmpty() &&
				!navigationSegments.isEmpty() &&
				isManyMultiplicity(navigationSegments);
	}

	@Override
	public ODataResponse read(final ItemLookupRequest itemLookupRequest) throws ODataException
	{
		final ConversionOptions options = conversionOptionsBuilder()
				.withIncludeCollections(true)
				.withNavigationSegments(itemLookupRequest.getNavigationSegments())
				.build();
		final ODataEntry entry = getPersistenceService().getEntityData(itemLookupRequest, options);
		final ExpandedEntity expandedEntity = navigationSegmentExplorer.expandForEntityList(itemLookupRequest, entry);
		final ItemLookupResult<ODataEntry> itemLookupResult = ItemLookupResult.createFrom(expandedEntity.getODataFeed().getEntries());
		return getResponseWriter().write(itemLookupRequest, expandedEntity.getEdmEntitySet(), itemLookupResult);
	}

	private boolean isManyMultiplicity(final List<NavigationSegment> navigationSegments)
	{
		try
		{
			final EdmMultiplicity multiplicity = navigationSegments.get(0)
					.getNavigationProperty()
					.getMultiplicity();
			return multiplicity.equals(EdmMultiplicity.MANY);
		}
		catch (final EdmException e)
		{
			return handleAssociationMultiplicityRetrievalError(e);
		}
	}

	protected NavigationSegmentExplorer getNavigationSegmentExplorer()
	{
		return navigationSegmentExplorer;
	}

	@Required
	public void setNavigationSegmentExplorer(final NavigationSegmentExplorer navigationSegmentExplorer)
	{
		this.navigationSegmentExplorer = navigationSegmentExplorer;
	}
}
