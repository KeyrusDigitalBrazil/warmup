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
 * Represents the ConnectedDocument object<br>
 * 
 */
public interface ConnectedDocument extends ConnectedObject {

    /**
     * Application Type Order.
     */
    String ORDER = "";
    /**
     * Application Type Billing.
     */
    String BILL = "BILL";
    /**
     * Application Type Delivery.
     */
    String DLVY = "DLVY";

    /**
     * Returns the application type of the document(e.g. one order document,
     * billing document, etc.).<br>
     * 
     * @return Document application type
     */
    String getAppTyp();

    /**
     * Sets the application type of the document(e.g. one order document,
     * billing document, etc.).<br>
     * 
     * @param appTyp Document application type
     */
    void setAppTyp(String appTyp);

}
