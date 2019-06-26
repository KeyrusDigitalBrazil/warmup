/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.util;

import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Utility class for commerce facades.
 */
public final class CommerceUtils
{


	/**
	 * Converts a {@link SearchPageData} of type {@literal <}S{@literal >} into one of type {@literal <}T{@literal >}
	 * using the converter provided.
	 *
	 * @param source
	 * @param converter
	 * @param <S>
	 *           The source type.
	 * @param <T>
	 *           The target type.
	 * @return The new {@link SearchPageData}.
	 */
	public static <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}
}
