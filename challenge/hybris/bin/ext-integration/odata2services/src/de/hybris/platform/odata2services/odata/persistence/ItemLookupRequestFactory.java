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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.UriInfo;

/**
 * Factory to create an {@link ItemLookupRequest}
 */
public interface ItemLookupRequestFactory
{
	/**
	 * Creates an {@link ItemLookupRequest} from the given parameters
	 * @param uriInfo Used to create the ItemLookupRequest
	 * @param context Used to create the ItemLookupRequest
	 * @param contentType Used to create the ItemLookupRequest
	 * @return ItemLookupRequest instance
	 */
	ItemLookupRequest create(UriInfo uriInfo, ODataContext context, String contentType);

	/**
	 * Creates an {@link ItemLookupRequest} that can be used to filter
	 * items matching the integration key
	 *
	 * @param context Used to create the ItemLookupRequest
	 * @param edmEntitySet Used to create the ItemLookupRequest
	 * @param oDataEntry Used to create the ItemLookupRequest
	 * @param integrationKey Used to create the ItemLookupRequest
	 * @return ItemLookupRequest instance
	 * @throws EdmException if OData problem
	 */
	ItemLookupRequest create(ODataContext context, EdmEntitySet edmEntitySet, ODataEntry oDataEntry, String integrationKey) throws EdmException;

	/**
	 * Creates an {@link ItemLookupRequest} that can be used to filter
	 * items matching the given attribute (e.g. version = 'Staged')
	 *
	 * @param context Used to create the ItemLookupRequest
	 * @param edmEntitySet Used to create the ItemLookupRequest
	 * @param attribute Used to create the ItemLookupRequest
	 * @return ItemLookupRequest instance
	 * @throws EdmException if OData problem
	 */
	ItemLookupRequest create(ODataContext context, EdmEntitySet edmEntitySet, Pair<String, String> attribute) throws EdmException;

	/**
	 * Creates an {@link ItemLookupRequest} from the given parameters
	 * @param request base request to create new request from
	 * @param entitySet entity set of the new lookup request
	 * @param oDataEntry the new ODataEntry for the lookup request
	 * @return ItemLookupRequest instance
	 * @throws EdmException if OData problem
	 */
	ItemLookupRequest createFrom(ItemLookupRequest request, final EdmEntitySet entitySet, final ODataEntry oDataEntry) throws EdmException;
}
