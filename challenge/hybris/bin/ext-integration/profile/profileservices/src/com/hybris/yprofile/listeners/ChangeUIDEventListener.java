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
import de.hybris.platform.commerceservices.event.ChangeUIDEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class ChangeUIDEventListener extends AbstractSiteEventListener<ChangeUIDEvent> {
    private static final Logger LOG = Logger.getLogger(ChangeUIDEventListener.class);

    private ProfileTransactionService profileTransactionService;

    @Override
    protected void onSiteEvent(ChangeUIDEvent event) {
        try {
            final String consentReference = event.getCustomer().getConsentReference();
            this.getProfileTransactionService().sendUidChangedEvent(event, consentReference);
        } catch (Exception e) {
            LOG.error("Error sending Change UID event: " + e.getMessage());
            LOG.debug("Error sending Change UID event: ", e);
        }
    }

    @Override
    protected boolean shouldHandleEvent(final ChangeUIDEvent event) {
        return (eventContainsCustomer(event) && eventContainsUid(event) && eventContainsOriginalUid(event));
    }

    @Required
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }


    private static boolean eventContainsCustomer(ChangeUIDEvent event) {
        final CustomerModel customer = event.getCustomer();
        ServicesUtil.validateParameterNotNullStandardMessage("event.customer", customer);
        return customer != null;
    }

    private static boolean eventContainsUid(ChangeUIDEvent event) {
        final String uid = getUidFromEvent(event);
        ServicesUtil.validateParameterNotNullStandardMessage("event.customer.email", uid);
        return uid != null;
    }

    private static boolean eventContainsOriginalUid(ChangeUIDEvent event) {
        final String originalUid = getOriginalUidFromEvent(event);
        ServicesUtil.validateParameterNotNullStandardMessage("event.customer.email", originalUid);
        return originalUid != null;
    }

    private static String getUidFromEvent(ChangeUIDEvent event) {
        return event.getCustomer().getUid();
    }

    private static String getOriginalUidFromEvent(ChangeUIDEvent event) {
        return event.getCustomer().getOriginalUid();
    }

    private ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }
}
