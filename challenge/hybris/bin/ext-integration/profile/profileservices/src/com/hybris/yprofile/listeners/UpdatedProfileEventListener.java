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

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.UpdatedProfileEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class UpdatedProfileEventListener extends AbstractSiteEventListener<UpdatedProfileEvent> {

    private static final Logger LOG = Logger.getLogger(UpdatedProfileEventListener.class);

    private ProfileTransactionService profileTransactionService;

    @Override
    protected void onSiteEvent(UpdatedProfileEvent event) {

        try {
            final String consentReference = event.getCustomer().getConsentReference();
            final String baseSiteId = event.getBaseStore().getUid();
            this.getProfileTransactionService().sendPersonalDetailsChangedEvent(event.getCustomer(), baseSiteId, consentReference);
        } catch (Exception e) {
            LOG.error("Error sending Updated Address event: " + e.getMessage());
            LOG.debug("Error sending Updated Address event: ", e);
        }
    }

    @Override
    protected boolean shouldHandleEvent(UpdatedProfileEvent event) {
        return event.getCustomer() != null && event.getCustomer().getConsentReference() != null ;
    }

    @Required
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    private ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }
}
