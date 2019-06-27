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
package com.hybris.yprofile.services.customer;

import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * yProfile implementation for the {@link CustomerFacade} to send login events.
 */
public class ProfileCustomerFacade extends DefaultCustomerFacade {

    private static final Logger LOG = Logger.getLogger(ProfileCustomerFacade.class);

    private ProfileTransactionService profileTransactionService;
    private ConsentService consentService;
    private DefaultSessionTokenService defaultSessionTokenService;
    private ProfileConfigurationService profileConfigurationService;

    @Override
    public void loginSuccess(){

        super.loginSuccess();

        final UserModel currentUser = getUserService().getCurrentUser();

        try {
            if (!getProfileConfigurationService().isProfileTrackingPaused()) {
                final String consentReferenceId = getConsentService().getConsentReferenceFromSession();
                final String sessionId = getDefaultSessionTokenService().getOrCreateSessionToken();
                final Boolean profileTagDebugSession = profileConfigurationService.isProfileTagDebugEnabledInSession();

                if (consentReferenceId != null) {
                    setDebugFlag(currentUser, profileTagDebugSession);
                    getProfileTransactionService().sendLoginEvent(currentUser, consentReferenceId, sessionId, getSiteId());
                }
            }
        } catch (Exception e){
            LOG.error("Error sending login event to profile", e);
        }
    }

    private void setDebugFlag(final UserModel currentUser, final Boolean profileTagDebugSession) {
        if (Boolean.TRUE.equals(profileTagDebugSession)) {
            currentUser.setProfileTagDebug(profileTagDebugSession);
        }
    }

    protected String getSiteId() {
        return getBaseSiteService().getCurrentBaseSite().getUid();
    }

    public ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    @Required
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    @Required
    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }

    public DefaultSessionTokenService getDefaultSessionTokenService() {
        return defaultSessionTokenService;
    }

    @Required
    public void setDefaultSessionTokenService(DefaultSessionTokenService defaultSessionTokenService) {
        this.defaultSessionTokenService = defaultSessionTokenService;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }
}