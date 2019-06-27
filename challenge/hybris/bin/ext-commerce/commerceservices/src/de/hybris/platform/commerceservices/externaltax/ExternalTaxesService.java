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
package de.hybris.platform.commerceservices.externaltax;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Abstraction for service to calculate 3rd party taxes
 */
public interface ExternalTaxesService
{
	/**
	 * Calculate the taxes for order via an external service
	 * @param abstractOrder A Hybris cart or order
	 * @return True if calculation was successful and false otherwise
	 */
	boolean calculateExternalTaxes(final AbstractOrderModel abstractOrder);

	/**
	 * Removes tax document from session if present
	 */
	void clearSessionTaxDocument();
}
