/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.yprofile.services.impl;

import com.hybris.yprofile.constants.ProfileservicesConstants;
import com.hybris.yprofile.exceptions.ProfileException;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;


/**
 * Default implementation for the {@link ProfileConfigurationService}.
 */
public class DefaultProfileConfigurationService implements ProfileConfigurationService {

    private static final Logger LOG = Logger.getLogger(DefaultProfileConfigurationService.class);

    private static final String PROFILE_TAG_DEBUG_SESSION_ATTR_KEY = "profile-tag-debug";

    private DestinationService<AbstractDestinationModel> destinationService;
    private RetrieveRestClientStrategy retrieveRestClientStrategy;
    private ConsumedDestinationLocatorStrategy baseSiteConsumedDestinationLocatorStrategy;

    private SessionService sessionService;

    private boolean isProfileTrackingPaused;

    @Override
    public boolean isProfileTrackingPaused() {
        try {

            if (getSessionService().getAttribute(ProfileservicesConstants.PROFILE_TRACKING_PAUSE) == null) {

                //default value
                setProfileTrackingPauseValue(isProfileTrackingPaused);
            }

            return (Boolean) getSessionService().getAttribute(ProfileservicesConstants.PROFILE_TRACKING_PAUSE);

        } catch (Exception e) {
            LOG.warn("Error getting "+ ProfileservicesConstants.PROFILE_TRACKING_PAUSE+" from session", e);
        }

        return isProfileTrackingPaused;
    }

    @Override
    public void setProfileTrackingPauseValue(final boolean isProfileTrackingPaused) {

        try {
            getSessionService().setAttribute(ProfileservicesConstants.PROFILE_TRACKING_PAUSE, isProfileTrackingPaused);
        } catch (Exception e) {
            LOG.warn("Error setting "+ ProfileservicesConstants.PROFILE_TRACKING_PAUSE+" in session with value " + isProfileTrackingPaused, e);
        }
    }

    @Override
    public boolean isConfigurationPresent(){

        try {
            getRetrieveRestClientStrategy().getProfileRestClient();
        } catch (ProfileException e){
            LOG.debug("Api registry configuration not found: ", e);
            return false;
        }

        return true;
    }

    @Override
    public String getTenant(final String siteId) {
        final Optional<AbstractDestinationModel> destinationModel = getDestinationModel(ProfileClient.class.getSimpleName());

        if (destinationModel.isPresent()){
            return destinationModel.get().getDestinationTarget().getId();
        }

        return StringUtils.EMPTY;
    }

    @Override
    public String getClientIdForProfileTag(final String siteId) {
        final Optional<AbstractDestinationModel> destinationModel = getDestinationModel(ProfileservicesConstants.PROFILE_TAG_URL);

        if (destinationModel.isPresent()){

            final ConsumedOAuthCredentialModel credential = (ConsumedOAuthCredentialModel) destinationModel.get().getCredential();

            return credential.getClientId();
        }


        return StringUtils.EMPTY;
    }

    protected Optional<AbstractDestinationModel> getDestinationModel(final String className) {
        return Optional.ofNullable(baseSiteConsumedDestinationLocatorStrategy.lookup(className));
    }

    @Override
    public String getProfileTagUrl() {
        final Optional<AbstractDestinationModel> destination = getDestinationModel(ProfileservicesConstants.PROFILE_TAG_URL);

        return destination.isPresent() ? destination.get().getUrl() : StringUtils.EMPTY;
    }

    @Override
    public String getProfileTagConfigUrl() {

        final Optional<AbstractDestinationModel> destination = getDestinationModel(ProfileservicesConstants.PROFILE_TAG_CONFIG_URL);

        return destination.isPresent() ? destination.get().getUrl() : StringUtils.EMPTY;
    }


    public void setProfileTagDebugFlagInSession(final Boolean debug){
        getSessionService().setAttribute(PROFILE_TAG_DEBUG_SESSION_ATTR_KEY, debug);
    }

    public Boolean isProfileTagDebugEnabledInSession(){
        return getSessionService().getAttribute(PROFILE_TAG_DEBUG_SESSION_ATTR_KEY);
    }


    public DestinationService<AbstractDestinationModel> getDestinationService() {
        return destinationService;
    }

    @Required
    public void setDestinationService(DestinationService<AbstractDestinationModel> destinationService) {
        this.destinationService = destinationService;
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setProfileTrackingPaused(boolean profileTrackingPaused) {
        this.isProfileTrackingPaused = profileTrackingPaused;
    }

    public ConsumedDestinationLocatorStrategy getBaseSiteConsumedDestinationLocatorStrategy() {
        return baseSiteConsumedDestinationLocatorStrategy;
    }

    @Required
    public void setBaseSiteConsumedDestinationLocatorStrategy(ConsumedDestinationLocatorStrategy baseSiteConsumedDestinationLocatorStrategy) {
        this.baseSiteConsumedDestinationLocatorStrategy = baseSiteConsumedDestinationLocatorStrategy;
    }
}
