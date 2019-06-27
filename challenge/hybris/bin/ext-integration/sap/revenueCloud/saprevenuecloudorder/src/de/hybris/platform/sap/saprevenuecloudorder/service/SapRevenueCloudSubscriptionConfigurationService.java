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
package de.hybris.platform.sap.saprevenuecloudorder.service;


import de.hybris.platform.sap.saprevenuecloudorder.clients.SapRevenueCloudSubscriptionClient;

/**
 * Configuragtion Service for Subscription Client. 
 *
 */
public interface SapRevenueCloudSubscriptionConfigurationService {
    /**
     * Check if the base site has YaaS configuration maintained
     * @param siteId Base Site Id
     * @return boolean
     */
    boolean isYaaSConfigPresentForBaseSite(final String siteId);

    /**
     * Get YaaS tenant for the base site
     * @param siteId Base Site Id
     * @return Yaas tenant 
     */
    String getYaaSTenantForBaseSite(final String siteId);

    /**
     * Get SAP subscription REST client
     * @return REST client SapSubscriptionClient
     */
    SapRevenueCloudSubscriptionClient getSapSubscriptionClient();

}
