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
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;


import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SapRevenueCloudSubscriptionClient;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Configuration Service Subscription client.
 *
 */
public class DefaultSapRevenueCloudSubscriptionConfigurationService implements SapRevenueCloudSubscriptionConfigurationService
{

	private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionConfigurationService.class);

    private BaseSiteService baseSiteService;
    private YaasServiceFactory yaasServiceFactory;
    private YaasConfigurationService yaasConfigurationService;

    @Override
    public SapRevenueCloudSubscriptionClient getSapSubscriptionClient() {

        final String siteId = getSiteId();
        if (isYaaSConfigPresentForBaseSite(siteId)) {

            try {
                return getYaasServiceFactory().lookupService(SapRevenueCloudSubscriptionClient.class);
            } catch (final Exception e) {
                LOG.error(String.format("Cannot retrieve YaaS Configuration for service: [%s] and base site: [%s]", SapRevenueCloudSubscriptionClient.class.getSimpleName(), siteId));
                LOG.error(e);
                return null;
            }
        }

        LOG.error(String.format("YaaS Configuration not found for REST client [%s]!", SapRevenueCloudSubscriptionClient.class.getName()));
        return null;

    }

    @Override
    public boolean isYaaSConfigPresentForBaseSite(final String siteId) {

        SapRevenueCloudSubscriptionClient sapRevenueCloudSubscriptionClient;

        if (!getCurrentBaseSiteModel().isPresent()) {

            if (getBaseSiteForUID(siteId).isPresent()) {
                getBaseSiteService().setCurrentBaseSite(siteId, true);
            } else {
                LOG.error(String.format("Failed to load base site: [%s]", siteId));
                return false;
            }
        }

        try {

            sapRevenueCloudSubscriptionClient = getYaasServiceFactory().lookupService(SapRevenueCloudSubscriptionClient.class);

        } catch (final Exception e) {
            LOG.error(String.format("Cannot retrieve YaaS Configuration for service: [%s] and base site: [%s]", SapRevenueCloudSubscriptionClient.class.getSimpleName(), siteId));
            LOG.error(e);
            return false;
        }

        return sapRevenueCloudSubscriptionClient != null;

    }

    @Override
    public String getYaaSTenantForBaseSite(final String siteId) {
        return getYaasProject(siteId).isPresent() ? getYaasProject(siteId).get().getIdentifier() : StringUtils.EMPTY;
    }

    protected Optional<YaasProjectModel> getYaasProject(final String siteId) {

    		return ofNullable(getYaasConfigurationService().getBaseSiteServiceMappingForId(siteId, getYaasServiceModel().get()).getYaasClientCredential().getYaasProject());
    }

    protected Optional<YaasServiceModel> getYaasServiceModel() {
        try {
            return ofNullable(getYaasConfigurationService().getYaasServiceForId(SapRevenueCloudSubscriptionClient.class.getSimpleName()));
        } catch (final Exception e) {
        	LOG.error(e);
            LOG.warn(String.format("Cannot retrieve YaaS Configuration for the REST client [%s]!", SapRevenueCloudSubscriptionClient.class.getSimpleName()));
        }

        return empty();
    }

    protected String getSiteId() {
        return getCurrentBaseSiteModel().isPresent() ? getCurrentBaseSiteModel().get().getUid() : StringUtils.EMPTY;
    }

    protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
        return ofNullable(getBaseSiteService().getCurrentBaseSite());
    }

    protected Optional<BaseSiteModel> getBaseSiteForUID(final String siteId) {
        return ofNullable(getBaseSiteService().getBaseSiteForUID(siteId));
    }


    protected YaasConfigurationService getYaasConfigurationService() {
        return yaasConfigurationService;
    }

    @Required
    public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService) {
        this.yaasConfigurationService = yaasConfigurationService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }


    protected YaasServiceFactory getYaasServiceFactory() {
        return yaasServiceFactory;
    }

    @Required
    public void setYaasServiceFactory(final YaasServiceFactory yaasServiceFactory) {
        this.yaasServiceFactory = yaasServiceFactory;
    }
}
