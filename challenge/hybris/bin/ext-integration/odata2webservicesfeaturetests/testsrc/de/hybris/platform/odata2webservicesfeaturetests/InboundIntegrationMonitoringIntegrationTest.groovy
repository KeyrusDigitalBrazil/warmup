/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2webservicesfeaturetests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel
import de.hybris.platform.inboundservices.model.InboundRequestModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2webservices.odata.ODataFacade
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext

@IntegrationTest
class InboundIntegrationMonitoringIntegrationTest extends ServicelayerSpockSpecification {
    private static final String SERVICE = 'InboundIntegrationMonitoring'

    @Resource(name = "oDataWebMonitoringFacade")
    private ODataFacade facade

    def setupSpec() {
        importCsv '/impex/essentialdata-inboundservices.impex', 'UTF-8'
        IntegrationTestUtil.importImpEx(
                'INSERT InboundRequest; &reqID; status(code); type; integrationKey',
                '; req1 ; ERROR   ; Category; test-error',
                '; req2 ; SUCCESS ; Catalog ; test-success;',
                'INSERT InboundRequestError; code; message; inboundRequest(&reqID); owner(&reqID)',
                '; missing_required_field ; Some field is missing; req1; req1'
        )
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll(InboundRequestModel.class)
    }

    @Test
    @Unroll
    def "can GET /InboundIntegrationMonitoring/#feed"() {
        when:
        def response = facade.handleGetEntity request(feed)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def json = asJson response
        def content = json.getCollectionOfObjects path
        content.size() == result.size()
        content.containsAll result

        where:
        feed                         | path                | result
        ''                           | 'd.EntitySets'      | ['InboundRequestErrors', 'IntegrationRequestStatuses', 'InboundRequests']
        'IntegrationRequestStatuses' | 'd.results[*].code' | ['SUCCESS', 'ERROR']
        'InboundRequests'            | 'd.results[*].type' | ['Category', 'Catalog']
        'InboundRequestErrors'       | 'd.results[*].code' | ['missing_required_field']
    }

    @Test
    def "can GET /InboundIntegrationMonitoring/IntegrationRequestStatuses('key')"() {
        when:
        def response = facade.handleGetEntity request('IntegrationRequestStatuses', 'SUCCESS')

        then:
        response.getStatus() == HttpStatusCodes.OK
        asJson(response).getString('d.code') == 'SUCCESS'
    }

    @Test
    @Unroll
    def "can GET /InboundIntegrationMonitoring/InboundRequests('key')"() {
        setup:
        String key = IntegrationTestUtil.findAny(InboundRequestModel.class, {'Catalog' == it.getProperty('type')})
                .map({integrationKeyFor(it)})
                .orElse('')

        when:
        def response = facade.handleGetEntity request('InboundRequests', key)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def entity = asJson(response)
        entity.getString('d.type') == 'Catalog'
        entity.exists('d.status.__deferred')
        entity.getString('d.integrationKey').contains 'test-success'
    }

    @Test
    @Unroll
    def "can GET /InboundIntegrationMonitoring/InboundRequestErrors('key')"() {
        setup:
        String key = IntegrationTestUtil.findAny(InboundRequestErrorModel.class, { it -> true})
                .map({integrationKeyFor(it)})
                .orElse('')

        when:
        def response = facade.handleGetEntity request('InboundRequestErrors', key)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def entity = asJson(response)
        entity.getString('d.code') == 'missing_required_field'
        entity.getString('d.message') == 'Some field is missing'
    }

    ODataContext request(String entitySet) {
        createContext ODataRequestBuilder.oDataGetRequest()
                .withAccepts('application/json')
                .withPathInfo(PathInfoBuilder.pathInfo()
                    .withServiceName(SERVICE)
                    .withEntitySet(entitySet))
    }

    ODataContext request(String entitySet, String key) {
        createContext ODataRequestBuilder.oDataGetRequest()
                .withAccepts('application/json')
                .withPathInfo(PathInfoBuilder.pathInfo()
                    .withServiceName(SERVICE)
                    .withEntitySet(entitySet)
                    .withEntityKeys(key))
    }

    JsonObject asJson(ODataResponse response) {
        JsonObject.createFrom response.getEntityAsStream()
    }

    String integrationKeyFor(final InboundRequestModel request) {
        "${request.creationtime.time}|null|${request.getProperty('integrationKey')}"
    }

    String integrationKeyFor(final InboundRequestErrorModel error) {
        final InboundRequestModel request = error.getProperty('inboundRequest')
        "${error.creationtime.time}|${integrationKeyFor(request)}"
    }
}
