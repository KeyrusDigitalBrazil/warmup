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
 *Interface provides the mapping information for the sales organisation and distribution channel <br>
 *
 * @version 1.0
 */
public interface DistChannelMapping {


    /**
     * @return alternative distribution channel for the condition technique
     */
    String getDistChannelForConditions();

    /**
     * @param distChannelForConditions mapped distribution channel which is used for the pricing
     */
    void setDistChannelForConditions(String distChannelForConditions);

    /**
     * @return alternative distribution channel for the document management
     */
    String getDistChannelForSalesDocTypes();

    /**
     * @param distChannelForSalesDocTypes alternative distribution channel for the document management
     */
    void setDistChannelForSalesDocTypes(String distChannelForSalesDocTypes);

    /**
     * @return alternative distribution channel for the customer and material data (product data in catalog)
     */
    String getDistChannelForCustomerMatirial();

    /**
     * @param distChannelForCustomerMatirial alternative distribution channel for the customer and material data (product data in catalog)
     */
    void setDistChannelForCustomerMatirial(String distChannelForCustomerMatirial);

    /**
     * @return reference plant
     */
    String getReferencePlant();

    /**
     * @param referencePlant reference plant
     */
    void setReferencePlant(String referencePlant);

    /**
     * @return distribution chain category
     */
    String getDistChainCategory();

    /**
     * @param distChainCategory distribution chain category
     */
    void setDistChainCategory(String distChainCategory);

    /**
     * @return Allowed pricing levels below distribution chain level
     */
    String getAllowedPricingLevel();

    /**
     * @param allowedPricingLevel Allowed pricing levels below distribution chain level
     */
    void setAllowedPricingLevel(String allowedPricingLevel);

}
