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
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf;

/**
 * BO representation of a county (relevant for countries where tax jurisdiction
 * code determination based on county is necessary)
 * 
 */
public interface County extends Cloneable {

    /**
     * @return county description
     */
    String getCountyText();

    /**
     * @param countyText county description
     */
    void setCountyText(String countyText);

    /**
     * @return tax jurisdiction code. Will be determined in ERP or CRM backend
     */
    String getTaxJurCode();

    /**
     * @param taxJurCode tax jurisdiction code. Will be determined in ERP or CRM
     *            backend
     */
    void setTaxJurCode(String taxJurCode);

    /**
     * @return a clone of the county
     */
    @SuppressWarnings("squid:S1161")
    County clone();

}
