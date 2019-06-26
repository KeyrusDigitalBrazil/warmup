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
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf;

/**
 * Interface provides the mapping information for the sales organisation and division. <br>
 *
 * @version 1.0
 */
public interface DivisionMapping {

    /**
     * @return alternative division for the customer data
     */
    String getDivisionForCustomers();

    /**
     * @param divisionForCustomers alternative division for the customer data
     */
    void setDivisionForCustomers(String divisionForCustomers);

    /**
     * @return division for the condition technique (pricing for instance)
     */
    String getDivisionForConditions();

    /**
     * @param divisionForConditions division for the condition technique (pricing for instance)
     */
    void setDivisionForConditions(String divisionForConditions);

    /**
     * @return division for the document management
     */
    String getDivisionForDocumentTypes();

    /**
     * @param divisionForDocumentTypes division for the document management
     */
    void setDivisionForDocumentTypes(String divisionForDocumentTypes);

}
