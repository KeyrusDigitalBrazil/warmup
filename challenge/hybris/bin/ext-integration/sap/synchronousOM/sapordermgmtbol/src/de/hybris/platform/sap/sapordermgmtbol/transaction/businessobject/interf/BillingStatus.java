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
 * Represents the BillingStatus object. <br>
 * 
 */
public interface BillingStatus extends BusinessStatus {

    /**
     * Initializes the BillingStatus object.<br>
     * 
     * @param dlvStatus Delivery Status
     * @param ordInvoiceStatus Order Invoice Status
     * @param dlvInvoiceStatus Delivery Invoice Status
     * @param rjStatus Rejection Status
     */
    void init(EStatus dlvStatus,
                     EStatus ordInvoiceStatus,
                     EStatus dlvInvoiceStatus,
                     EStatus rjStatus);

}
