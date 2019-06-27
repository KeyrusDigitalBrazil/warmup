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
package de.hybris.platform.sap.saporderexchangeoms.outbound.impl;

import de.hybris.platform.sap.saporderexchangeoms.constants.SapOmsOrderExchangeConstants;
import de.hybris.platform.sap.saporderexchangeoms.outbound.SapVendorService;

/**
 * Concrete implementation to provide business logic for {@link de.hybris.platform.sap.saporderexchangeoms.outbound.SapVendorService}
 */
public class SapOmsVendorService implements
		SapVendorService {

	@Override
	public boolean isVendorExternal(String vendorCode) {
        
		return !vendorCode.contentEquals(SapOmsOrderExchangeConstants.INTERNAl_VENDOR);

	}

	@Override
	public String getVendorItemCategory() {

		return SapOmsOrderExchangeConstants.VENDOR_ITEM_CATEGORY;
		
	}
	
	
}
