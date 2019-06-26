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
package de.hybris.platform.kymaintegrationservices.event.listeners;

import de.hybris.platform.kymaintegrationservices.event.InvalidateCertificateCredentialsCacheEvent;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Listener, which runs restTemplate connection factory cache invalidation
 */
public class InvalidateCertificateCredentialsCacheEventListener extends AbstractEventListener<InvalidateCertificateCredentialsCacheEvent>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidateCertificateCredentialsCacheEventListener.class);

    private RestTemplateWrapper kymaDestinationRestTemplateWrapper;
    private RestTemplateWrapper kymaEventRestTemplateWrapper;

    @Override
    protected void onEvent(InvalidateCertificateCredentialsCacheEvent event)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("InvalidateCertificateCredentialsCacheEvent received, object: " + event);
        }
        kymaDestinationRestTemplateWrapper.invalidateAndUpdateCache(event.getConsumedCertificateCredential());
        kymaEventRestTemplateWrapper.invalidateAndUpdateCache(event.getConsumedCertificateCredential());
    }

    protected RestTemplateWrapper getKymaEventRestTemplateWrapper()
    {
        return kymaEventRestTemplateWrapper;
    }

    @Required
    public void setKymaEventRestTemplateWrapper(
          RestTemplateWrapper kymaEventRestTemplateWrapper)
    {
        this.kymaEventRestTemplateWrapper = kymaEventRestTemplateWrapper;
    }

    protected RestTemplateWrapper getKymaDestinationRestTemplateWrapper()
    {
        return kymaDestinationRestTemplateWrapper;
    }

    @Required
    public void setKymaDestinationRestTemplateWrapper(
          RestTemplateWrapper kymaDestinationRestTemplateWrapper)
    {
        this.kymaDestinationRestTemplateWrapper = kymaDestinationRestTemplateWrapper;
    }
}
