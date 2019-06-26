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
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.orderprocessing.events.ConsignmentProcessingEvent;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Event listener for create return event.
 */
public class ReturnOrderEventListener extends AbstractSiteEventListener<CreateReturnEvent> {

    private static final Logger LOG = Logger.getLogger(ReturnOrderEventListener.class);
    private ProfileTransactionService profileTransactionService;
    private BaseSiteService baseSiteService;

    @Override
    protected void onSiteEvent(CreateReturnEvent event) {

        try {

            final ReturnRequestModel returnRequest = event.getReturnRequest();
            ServicesUtil.validateParameterNotNullStandardMessage("event.returnRequest", returnRequest);


            if (returnRequest == null) {
                LOG.warn("ReturnRequest is null. Unable to send return order to yProfile");
            } else {
                setCurrentBaseSite(event);
                getProfileTransactionService().sendReturnOrderEvent(returnRequest);
            }
        } catch (Exception e) {
            LOG.error("Error sending Return Order event: " + e.getMessage());
            LOG.debug("Error sending Return Order event: ", e);
        }

    }

    protected void setCurrentBaseSite(CreateReturnEvent event) {
        getBaseSiteService().setCurrentBaseSite(event.getReturnRequest().getOrder().getSite(), true);
    }

    @Override
    protected boolean shouldHandleEvent(final CreateReturnEvent event) {
        final ReturnRequestModel returnRequest = event.getReturnRequest();
        ServicesUtil.validateParameterNotNullStandardMessage("event.return", returnRequest);
        final BaseSiteModel site = returnRequest.getOrder().getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.return.site", site);
        return SiteChannel.B2C.equals(site.getChannel());
    }


    protected ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    @Required
    public void setProfileTransactionService(final ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
