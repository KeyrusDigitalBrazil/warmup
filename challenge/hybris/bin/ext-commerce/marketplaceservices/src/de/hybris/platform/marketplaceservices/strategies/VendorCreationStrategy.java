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
package de.hybris.platform.marketplaceservices.strategies;

import de.hybris.platform.ordersplitting.model.VendorModel;


/**
 * Strategy for creating a vendor in backoffice.
 */
public interface VendorCreationStrategy
{

	/**
	 * populate the specific vendor model and save it.
	 *
	 * @param vendor
	 *           the vendor to save
	 * @param useCustomPage
	 *           if true will assign the vendor a landing page
	 */
	void createVendor(VendorModel vendor, boolean useCustomPage);
}
