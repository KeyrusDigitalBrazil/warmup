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


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;


/**
 * Service for calculating taxes using CIS tax service.
 */
public interface CisTaxCalculationService
{
	/**
	 * Calculate taxes for an order.
	 * 
	 * @param abstractOrder
	 *           order to calculate taxes for
	 * @return an ExternalTaxDocument that represents the taxes
	 */
	ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder);

}
