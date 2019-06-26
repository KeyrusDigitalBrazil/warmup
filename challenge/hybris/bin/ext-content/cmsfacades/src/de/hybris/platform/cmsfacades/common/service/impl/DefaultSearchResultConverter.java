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
package de.hybris.platform.cmsfacades.common.service.impl;

import de.hybris.platform.cmsfacades.common.service.SearchResultConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Default implementation of {@link SearchResultConverter}
 */
public class DefaultSearchResultConverter implements SearchResultConverter
{

	@Override
	public <S extends ItemModel, T> SearchResult<T> convert(final SearchResult<S> modelSearchResult,
			final Function<S, T> convertFunction)
	{
		SearchResult<T> dataSearchResult;

		if (Objects.nonNull(modelSearchResult))
		{
			final List<T> dataList = modelSearchResult.getResult().stream().map(model -> convertFunction.apply(model))
					.collect(Collectors.toList());
			dataSearchResult = new SearchResultImpl<>(dataList, modelSearchResult.getTotalCount(),
					modelSearchResult.getRequestedCount(), modelSearchResult.getRequestedStart());
		}
		else
		{
			dataSearchResult = new SearchResultImpl<>(Collections.emptyList(), 0, 0, 0);
		}
		return dataSearchResult;
	}

}
