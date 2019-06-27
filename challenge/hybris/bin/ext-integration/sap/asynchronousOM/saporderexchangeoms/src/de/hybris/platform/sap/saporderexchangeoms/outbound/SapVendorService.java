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
package de.hybris.platform.sap.saporderexchangeoms.outbound;

/**
 * Interface to provide access to Vendor and Item information for OMS configurations
 */
public interface SapVendorService {

	/**
	 * Returns whether or not the Vendor is external given a vendor code
	 * @param vendorCode
	 * 			Code for the vendor
	 * @return Boolean value
	 */
	public boolean isVendorExternal(String vendorCode);

	/**
	 * Returns the String representation of the VendorItemCategory of the desired instance
	 * @return VendorItemCategory String
	 */
	public String getVendorItemCategory();
	
}
