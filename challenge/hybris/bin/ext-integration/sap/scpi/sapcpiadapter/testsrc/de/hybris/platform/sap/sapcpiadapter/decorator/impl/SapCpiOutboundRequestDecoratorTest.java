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

import static de.hybris.platform.outboundservices.decorator.DecoratorContext.decoratorContextBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class SapCpiOutboundRequestDecoratorTest {

    private static final String X_CSRF_TOKEN = "X-CSRF-Token";

    @InjectMocks
    private SapCpiOutboundRequestDecorator sapCpiOutboundRequestDecorator;

    @Mock
    private DestinationService destinationService;
    @Mock
    private IntegrationRestTemplateFactory integrationRestTemplateFactory;
    @Mock
    private DecoratorExecution execution;

    @Captor
    private ArgumentCaptor<HttpEntity<Map>> httpEntityCaptor;

    @Test
    public void testDecorate() {

        final ItemModel itemModel = mock(ItemModel.class);
        final ConsumedDestinationModel consumedDestination = mock(ConsumedDestinationModel.class);
        final EndpointModel endpoint = mock(EndpointModel.class);

        when(consumedDestination.getUrl()).thenReturn("https://cd1n01-iflmap.hcisb.int.sap.hana.ondemand.com/gw/odata/SAP/REPLICATE-B2B-CUSTOMER-FROM-SAP-COMMERCE-CLOUD-TO-ERP;v=1/SAPCpiOutboundB2BCustomers");
        when(endpoint.getId()).thenReturn("scpiB2BCustomerEndpoint");
        when(destinationService.getDestinationById(eq("scpiCustomerDestination"))).thenReturn(consumedDestination);
        when(consumedDestination.getEndpoint()).thenReturn(endpoint);

        final RestOperations restOperation = mock(RestOperations.class);
        when(integrationRestTemplateFactory.create(eq(consumedDestination))).thenReturn(restOperation);

        final Map<String, Object> map = new HashMap<>();

        final ResponseEntity<Map> csrfResponse = mock(ResponseEntity.class);
        when(restOperation.exchange(eq("https://cd1n01-iflmap.hcisb.int.sap.hana.ondemand.com/gw/odata/SAP/REPLICATE-B2B-CUSTOMER-FROM-SAP-COMMERCE-CLOUD-TO-ERP;v=1/"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(csrfResponse);
        final HttpHeaders headers = mock(HttpHeaders.class);

        when(csrfResponse.getHeaders()).thenReturn(headers);
        when(headers.getFirst(X_CSRF_TOKEN)).thenReturn("322704B0EB3E980C8E752CB5748F754A");

        final DecoratorContext context = decoratorContextBuilder().withIntegrationObjectCode("integrationObjectCode")
                .withItemModel(itemModel)
                .withDestinationModel(consumedDestination)
                .withIntegrationObjectItemCode(null)
                .build();
        sapCpiOutboundRequestDecorator.decorate(headers, map, context, execution);

        verify(restOperation).exchange(eq("https://cd1n01-iflmap.hcisb.int.sap.hana.ondemand.com/gw/odata/SAP/REPLICATE-B2B-CUSTOMER-FROM-SAP-COMMERCE-CLOUD-TO-ERP;v=1/"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Map.class));
        verify(headers).set(X_CSRF_TOKEN, "322704B0EB3E980C8E752CB5748F754A");
        verify(execution).createHttpEntity(headers, map, context);

        assertThat(httpEntityCaptor.getValue().getHeaders())
                .contains(
                        entry(X_CSRF_TOKEN, Collections.singletonList("Fetch")),
                        entry(ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE)));
    }
}