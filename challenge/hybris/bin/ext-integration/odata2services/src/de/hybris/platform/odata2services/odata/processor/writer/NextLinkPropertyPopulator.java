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

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.processor.ODataNextLink;

import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;

public class NextLinkPropertyPopulator implements ResponseWriterPropertyPopulator
{
	@Override
	public boolean isApplicable(final ItemLookupRequest itemLookupRequest)
	{
		return itemLookupRequest.getODataEntry() == null;
	}

	@Override
	public EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder populate(
			final EntityProviderWriteProperties properties,
			final ItemLookupRequest itemLookupRequest,
			final ItemLookupResult result)
	{
		final String nextLink = ODataNextLink.Builder.nextLink()
				.withLookupRequest(itemLookupRequest)
				.withTotalCount(result.getTotalCount())
				.build();
		return EntityProviderWriteProperties.fromProperties(properties)
				.nextLink(nextLink);
	}
}
