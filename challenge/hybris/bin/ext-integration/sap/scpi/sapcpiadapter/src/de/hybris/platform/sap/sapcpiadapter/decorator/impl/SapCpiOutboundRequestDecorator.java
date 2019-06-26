/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiadapter.decorator.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.google.common.base.Strings;

/**
 * Decorates SAP CPI outbound requests with CSRF tokens.
 */
public class SapCpiOutboundRequestDecorator implements OutboundRequestDecorator {

    private static final String X_CSRF_TOKEN = "X-CSRF-Token";
    private static final String X_CSRF_TOKEN_FETCH = "Fetch";
    private static final Logger LOG = LoggerFactory.getLogger(SapCpiOutboundRequestDecorator.class);

    private IntegrationRestTemplateFactory integrationRestTemplateFactory;

    @Override
    public HttpEntity<Map<String, Object>> decorate(final HttpHeaders httpHeaders, final Map<String, Object> payload,
                                                    final DecoratorContext context, final DecoratorExecution execution) {

        final String csrfToken = fetchCsrfTokenFromSCPI(context.getDestinationModel());
        httpHeaders.set(X_CSRF_TOKEN, csrfToken);

        return execution.createHttpEntity(httpHeaders, payload, context);

    }

    protected String fetchCsrfTokenFromSCPI(final ConsumedDestinationModel destinationModel) {

        final HttpHeaders headers = new HttpHeaders();
        headers.set(X_CSRF_TOKEN, X_CSRF_TOKEN_FETCH);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
        final ResponseEntity<Map> csrfTokenExchange = restOperations.exchange(getBaseUrl(destinationModel), HttpMethod.GET, new HttpEntity(headers), Map.class);
        final String csrfToken = csrfTokenExchange.getHeaders().getFirst(X_CSRF_TOKEN);

        if (!Strings.isNullOrEmpty(csrfToken)) {
            LOG.info("The CSRF token has been fetched from SCPI!");
        } else {
            LOG.error("Failed to fetch the CSRF token from SCPI!");
        }

        return csrfToken;

    }

    protected String getBaseUrl(ConsumedDestinationModel destinationModel) {

        String url = destinationModel.getUrl();
        return url.substring(0, (url.lastIndexOf('/') + 1));

    }

    protected IntegrationRestTemplateFactory getIntegrationRestTemplateFactory() {
        return integrationRestTemplateFactory;
    }

    @Required
    public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory) {
        this.integrationRestTemplateFactory = integrationRestTemplateFactory;
    }

}
