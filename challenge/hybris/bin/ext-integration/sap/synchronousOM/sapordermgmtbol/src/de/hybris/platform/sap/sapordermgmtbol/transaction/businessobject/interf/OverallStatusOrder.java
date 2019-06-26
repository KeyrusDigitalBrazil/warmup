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
 * Represents the OverallStatusOrder object. <br>
 * 
 */
public interface OverallStatusOrder extends OverallStatus {

    /**
     * Initializes the OverallStatusOrder object <br>
     * 
     * @param procStatus - Processing Status
     * @param shippingStatus - Shipping Status
     * @param billingStatus - Billing Status
     * @param rjStatus - Rejection Status
     */
    void init(EStatus procStatus,
                     ShippingStatus shippingStatus,
                     BillingStatus billingStatus,
                     EStatus rjStatus);

}
