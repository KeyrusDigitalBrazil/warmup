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
package de.hybris.platform.sap.sappricing.services.impl;

import de.hybris.platform.commerceservices.externaltax.impl.DefaultExternalTaxesService;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * calculate sap external Taxes
 */
public class SapExternalTaxesService extends DefaultExternalTaxesService {

	@Override
	public boolean calculateExternalTaxes(AbstractOrderModel abstractOrder) {
		// since the taxes are already calculated from ERP backend, no need to process taxes
		return true;
	}
}
