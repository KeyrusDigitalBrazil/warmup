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
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.consent.services.ConsentService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.ConsentWithdrawnEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT;

/**
 * Event listener for Consent Withdrawn Event.
 */
public class ConsentWithdrawnEventListener extends AbstractSiteEventListener<ConsentWithdrawnEvent> {

    private static final Logger LOG = Logger.getLogger(ConsentWithdrawnEventListener.class);
    private ConsentService consentService;

    @Override
    protected void onSiteEvent(ConsentWithdrawnEvent event) {

        try {

            final ConsentModel consent = event.getConsent();

            if (consent == null) {
                LOG.warn("Consent is null. Unable to send consent revoked to Profile");
            } else {
                LOG.debug("Consent Withdrawn Event Received Successfully!!!!!");
                getConsentService().deleteConsentReferenceInConsentServiceAndInUserModel(consent.getCustomer(), consent.getConsentTemplate().getBaseSite().getUid());
            }
        } catch (Exception e) {
            LOG.error("Error sending Consent Withdrawn event: " + e.getMessage());
            LOG.debug("Error sending Consent Withdrawn event: ", e);
        }

    }


    @Override
    protected boolean shouldHandleEvent(ConsentWithdrawnEvent event) {
        final ConsentModel consent = event.getConsent();
        ServicesUtil.validateParameterNotNullStandardMessage("event.consent", consent);
        final BaseSiteModel site = consent.getConsentTemplate().getBaseSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.consent.consentTemplate.site", site);

        return SiteChannel.B2C.equals(site.getChannel()) && isProfileConsentWithdrawn(event);
    }

    protected boolean isProfileConsentWithdrawn(ConsentWithdrawnEvent event){

        return event.getConsent().getConsentTemplate().getId().equals(PROFILE_CONSENT);
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    @Required
    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }
}
