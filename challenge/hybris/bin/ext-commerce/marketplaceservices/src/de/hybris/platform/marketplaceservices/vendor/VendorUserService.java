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
package de.hybris.platform.marketplaceservices.vendor;

import de.hybris.platform.marketplaceservices.model.VendorUserModel;


/**
 * Service with VendorUser related methods
 */
public interface VendorUserService
{
	/**
	 * Deactivate a specific vendorUser
	 *
	 * @param vendorUser
	 *           the specific vendorUser
	 */
	void deactivateUser(VendorUserModel vendorUser);

	/**
	 * Activate a specific vendorUser
	 *
	 * @param vendorUser
	 *           the specific vendorUser
	 */
	void activateUser(VendorUserModel vendorUser);
}
