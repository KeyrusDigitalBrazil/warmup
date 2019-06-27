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
package de.hybris.platform.acceleratorfacades.csv;

import de.hybris.platform.commercefacades.order.data.CartData;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Facade for generating CSV content.
 */
public interface CsvFacade
{
	/**
	 * Generate CSV content from CartData
	 *
	 * @param headers
	 *           : list of text which is used as csv header; e.g Code,Name,Price
	 * @param includeHeader
	 *           : flag to indicate whether header should be generated
	 * @param cartData
	 * @param writer
	 *           : CSV content generated is written to the (buffered) writer
	 * @throws IOException
	 */
	void generateCsvFromCart(final List<String> headers, final boolean includeHeader, final CartData cartData, final Writer writer)
			throws IOException;
}
