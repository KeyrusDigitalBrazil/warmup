/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.common.service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.function.Function;


/**
 * Service to convert {@link SearchResult} with results of type <code>Model</code> to results of type <code>Data</code>
 */
public interface SearchResultConverter
{
	/**
	 * Convert {@link SearchResult} of type <code>Model</code> to <code>Data</code>
	 *
	 * @param modelSearchResult
	 *           the search result containing model objects to be converted
	 * @param convertFunction
	 *           the function used to convert the model object into the data object
	 * @return the search result containing data objects; empty result list if <code>modelSearchResult</code> is
	 *         <tt>null</tt>; never <tt>null</tt>
	 */
	<S extends ItemModel, T> SearchResult<T> convert(final SearchResult<S> modelSearchResult,
			final Function<S, T> convertFunction);
}
