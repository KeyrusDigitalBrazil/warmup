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

import java.util.Map;

/**
 * The PricingInfo object contains settings for pricing purposes. This interface
 * is used to communicate with IPC.
 * 
 */
public interface PricingInfo {

    /**
     * Sets the property procedureName
     * 
     * @param procedureName - procedure name
     */
    void setProcedureName(String procedureName);

    /**
     * Sets the free good procedure.
     * 
     * @param fGProcedureName key of the free good procedure
     */
    void setFGProcedureName(String fGProcedureName);

    /**
     * Sets the property documentCurrencyUnit
     * 
     * @param documentCurrencyUnit - document currency unit
     */
    void setDocumentCurrencyUnit(String documentCurrencyUnit);

    /**
     * Sets the property localCurrencyUnit
     * 
     * @param localCurrencyUnit - local currency unit
     */
    void setLocalCurrencyUnit(String localCurrencyUnit);

    /**
     * Sets the property salesOrganisation
     * 
     * @param salesOrganisation -sales organization
     */
    void setSalesOrganisation(String salesOrganisation);

    /**
     * Sets the property salesOrganisationCrm
     * 
     * @param salesOrganisationCrm - sales organization CRM
     */
    void setSalesOrganisationCrm(String salesOrganisationCrm);

    /**
     * Sets the property distributionChannel
     * 
     * @param distributionChannel - distribution channel
     */
    void setDistributionChannel(String distributionChannel);

    /**
     * Sets the property distributionChannelOriginal
     * 
     * @param distributionChannelOriginal - distribution channel original
     */
    void setDistributionChannelOriginal(String distributionChannelOriginal);

    /**
     * Sets the property headerAttributes
     * 
     * @param headerAttributes - header attributes
     */
    void setHeaderAttributes(Map<String, String> headerAttributes);

    /**
     * Gets the property headerAttributes
     * 
     * @return HeaderAttributes
     */
    Map<String, String> getHeaderAttributes();

    /**
     * Sets the property itemAttributes
     * 
     * @param itemAttributes - item attributes
     */
    void setItemAttributes(Map<String, Map<String, Map<String, String>>> itemAttributes);

    /**
     * Gets the property itemAttributes
     * 
     * @return ItemAttributes
     */
    Map<String, Map<String, Map<String, String>>> getItemAttributes();

    /**
     * Returns the property procedureName
     * 
     * @return procedureName
     */
    String getProcedureName();

    /**
     * Returns the free good procedure.
     * 
     * @return the free good procedure
     */
    String getFGProcedureName();

    /**
     * Returns the property documentCurrencyUnit
     * 
     * @return documentCurrencyUnit
     */
    String getDocumentCurrencyUnit();

    /**
     * Returns the property localCurrencyUnit
     * 
     * @return localCurrencyUnit
     */
    String getLocalCurrencyUnit();

    /**
     * Returns the property salesOrganisation
     * 
     * @return salesOrganisation
     */
    String getSalesOrganisation();

    /**
     * Returns the property salesOrganisationCrm
     * 
     * @return salesOrganisationCrm
     */
    String getSalesOrganisationCrm();

    /**
     * Returns the property distributionChannel
     * 
     * @return distributionChannel
     */
    String getDistributionChannel();

    /**
     * Returns the property distributionChannelOriginal
     * 
     * @return distributionChannelOriginal
     */
    String getDistributionChannelOriginal();

}
