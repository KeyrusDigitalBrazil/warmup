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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Event listener for order submit event.
 */
public class NewOrderEventListener extends AbstractSiteEventListener<SubmitOrderEvent>
{
    private static final Logger LOG = Logger.getLogger(NewOrderEventListener.class);
    private ProfileTransactionService profileTransactionService;

    @Override
    protected void onSiteEvent(final SubmitOrderEvent event) {
        try {
            final OrderModel order = event.getOrder();
            ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);

            if (order == null) {
                LOG.warn("Order is null. Unable to send order to yProfile");
            } else {
                getProfileTransactionService().sendSubmitOrderEvent(order);
            }
        } catch (Exception e) {
            LOG.error("Error sending New Order event: " + e.getMessage());
            LOG.debug("Error sending New Order event: ", e);
        }
    }

    @Override
    protected boolean shouldHandleEvent(final SubmitOrderEvent event) {
        final OrderModel order = event.getOrder();
        ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
        final BaseSiteModel site = order.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
        return SiteChannel.B2C.equals(site.getChannel());
    }


    protected ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    @Required
    public void setProfileTransactionService(final ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }
}
