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
package de.hybris.platform.integration.cis.tax.service;

import com.hybris.cis.client.tax.models.CisTaxLine;
import com.hybris.cis.client.tax.models.CisTaxValue;
import de.hybris.platform.util.TaxValue;
import java.util.List;


/**
 * Interface to create the taxValues from CisTaxLines.
 */
public interface TaxValueConversionService
{
	List<TaxValue> getShippingTaxes(final List<CisTaxLine> taxLines, final String currencyCode, final boolean shippingIncluded);

	List<TaxValue> getLineTaxValues(final List<CisTaxValue> taxLines, final String currencyCode);
}
