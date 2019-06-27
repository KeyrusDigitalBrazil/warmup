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
import de.hybris.platform.commerceservices.event.DeletedAddressEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class DeletedAddressEventListener extends AbstractSiteEventListener<DeletedAddressEvent> {

    private static final Logger LOG = Logger.getLogger(DeletedAddressEventListener.class);

    private ProfileTransactionService profileTransactionService;

    @Override
    protected void onSiteEvent(DeletedAddressEvent event) {

        try {
            final String consentReference = event.getCustomer().getConsentReference();
            final String baseSiteId = event.getBaseStore().getUid();
            this.getProfileTransactionService().sendAddressDeletedEvent(event.getCustomer(), baseSiteId, consentReference);
        } catch (Exception e) {
            LOG.error("Error sending Deleted Address event: " + e.getMessage());
            LOG.debug("Error sending Deleted Address event: ", e);
        }
    }

    @Override
    protected boolean shouldHandleEvent(DeletedAddressEvent event) {
        return event.getCustomer() != null && event.getCustomer().getConsentReference() != null;
    }

    @Required
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    private ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }
}
