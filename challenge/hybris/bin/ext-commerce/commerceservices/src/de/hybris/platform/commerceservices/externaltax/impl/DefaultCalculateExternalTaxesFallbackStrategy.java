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
package de.hybris.platform.commerceservices.externaltax.impl;

import de.hybris.platform.commerceservices.externaltax.CalculateExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;

/**
 * An default implementation for fallback strategy to CalculateExternalTaxesStrategy
 */
public class DefaultCalculateExternalTaxesFallbackStrategy implements CalculateExternalTaxesStrategy
{

	@Override
	public ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		return new ExternalTaxDocument();
	}
}
