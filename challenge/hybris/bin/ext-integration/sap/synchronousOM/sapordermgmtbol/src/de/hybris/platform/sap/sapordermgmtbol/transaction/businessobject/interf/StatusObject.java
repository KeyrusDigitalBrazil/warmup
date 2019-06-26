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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the Status object. <br>
 * 
 */
public interface StatusObject
{

	/**
	 * Set the shipping status for this object. <br>
	 * 
	 * @param state
	 *           Shipping Status
	 */
	void setShippingStatus(ShippingStatus state);

	/**
	 * Set the billing status for this object. <br>
	 * 
	 * @param state
	 *           Billing Status
	 */
	void setBillingStatus(BillingStatus state);

	/**
	 * Set the overall status for this object. <br>
	 * 
	 * @param state
	 *           Overall Status
	 */
	void setOverallStatus(OverallStatus state);

}
