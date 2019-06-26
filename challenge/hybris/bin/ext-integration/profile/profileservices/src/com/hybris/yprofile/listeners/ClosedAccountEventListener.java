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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.ClosedAccountEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT;

/**
 * Event listener for Closed Account Event.
 */
public class ClosedAccountEventListener extends AbstractSiteEventListener<ClosedAccountEvent> {

    private static final Logger LOG = Logger.getLogger(ClosedAccountEventListener.class);
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(ClosedAccountEvent event) {

        try {

            final CustomerModel customer = event.getCustomer();

            if (customer == null) {
                LOG.warn("Customer is null. Unable to send ClosedAccount event to Profile");
            } else {
                LOG.debug("Closed Account Event Received Successfully!!!!!");

                final ConsentTemplateModel consentTemplate = getCommerceConsentService().getLatestConsentTemplate(PROFILE_CONSENT, event.getSite());
                final ConsentModel activeConsent = getCommerceConsentService().getActiveConsent(customer, consentTemplate);
                //this one triggers the deletion of the consent reference
                getCommerceConsentService().withdrawConsent(activeConsent);
            }
        } catch (Exception e) {
            LOG.error("Error sending Closed Account event: " + e.getMessage());
            LOG.debug("Error sending Closed Account event: ", e);
        }

    }

    @Override
    protected boolean shouldHandleEvent(ClosedAccountEvent event) {
        final CustomerModel customer = event.getCustomer();
        ServicesUtil.validateParameterNotNullStandardMessage("event.customer", customer);
        final BaseSiteModel site = event.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
        return SiteChannel.B2C.equals(site.getChannel());
    }

    public CommerceConsentService getCommerceConsentService() {
        return commerceConsentService;
    }

    @Required
    public void setCommerceConsentService(CommerceConsentService commerceConsentService) {
        this.commerceConsentService = commerceConsentService;
    }
}
