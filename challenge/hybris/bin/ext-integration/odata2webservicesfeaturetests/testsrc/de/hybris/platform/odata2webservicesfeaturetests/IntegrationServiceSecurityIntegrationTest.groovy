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
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import spock.lang.Unroll

import javax.ws.rs.client.Entity

@NeedsEmbeddedServer(webExtensions = [Odata2webservicesConstants.EXTENSIONNAME])
@IntegrationTest
class IntegrationServiceSecurityIntegrationTest extends ServicelayerSpockSpecification {
    private static final String SERVICE_NAME = "IntegrationService"
    private static final String PASSWORD = 'password'
    private static final String USER_ADMIN = 'integrationAdmin'
    private static final String USER_CREATE = 'integrationCreate'
    private static final String USER_VIEW = 'integrationView'

    def setupSpec() {
        importCsv '/impex/essentialdata-odata2services.impex', 'UTF-8'
        importCsv '/impex/essentialdata-integrationservices.impex', 'UTF-8'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; UID[unique = true]; description; name; groups(uid)',
                "; $USER_ADMIN  ; Test Admin User  ; Integration Test Admin  ; integrationadmingroup",
                "; $USER_CREATE ; Test Create User ; Integration Test Create ; integrationcreategroup",
                "; $USER_VIEW   ; Test View User   ; Integration Test View   ; integrationviewgroup",
                '# password for users',
                'INSERT_UPDATE Employee; uid[unique = true]; @password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                "; $USER_ADMIN  ; *:$PASSWORD",
                "; $USER_CREATE ; *:$PASSWORD",
                "; $USER_VIEW   ; *:$PASSWORD")
    }

    @Test
    def "Users must be authenticated in order to GET /IntegrationService"() {
        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .build()
                .get()

        then:
        response.status == HttpStatusCodes.UNAUTHORIZED.statusCode
    }

    @Test
    @Unroll
    def "Users must be authenticated in order to GET /IntegrationService/#feed"() {
        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .path(feed)
                .build()
                .get()

        then:
        response.status == HttpStatusCodes.UNAUTHORIZED.statusCode

        where:
        feed << ['IntegrationObjects', 'IntegrationObjectItems', 'IntegrationObjectItemAttributes']
    }

    @Test
    @Unroll
    def "Users must be authenticated in order to POST to /IntegrationService/#feed"() {
        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .path(feed)
                .build()
                .post(null)

        then:
        response.status == HttpStatusCodes.UNAUTHORIZED.statusCode

        where:
        feed << ['IntegrationObjects', 'IntegrationObjectItems', 'IntegrationObjectItemAttributes']
    }

    @Test
    @Unroll
    def "Users of integration#user group are #status to access GET /IntegrationService"() {
        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .credentials(user, PASSWORD)
                .build()
                .get()

        then:
        response.status == status.statusCode

        where:
        user        | status
        USER_ADMIN  | HttpStatusCodes.OK
        USER_CREATE | HttpStatusCodes.FORBIDDEN
        USER_VIEW   | HttpStatusCodes.FORBIDDEN
    }

    @Test
    @Unroll
    def "Users of integration#user group are #status to access GET /IntegrationService/#feed"() {
        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .path(feed)
                .credentials(user, PASSWORD)
                .build()
                .get()

        then:
        response.status == status.statusCode

        where:
        feed                              | user        | status
        'IntegrationObjects'              | USER_ADMIN  | HttpStatusCodes.OK
        'IntegrationObjects'              | USER_CREATE | HttpStatusCodes.FORBIDDEN
        'IntegrationObjects'              | USER_VIEW   | HttpStatusCodes.FORBIDDEN
        'IntegrationObjectItems'          | USER_ADMIN  | HttpStatusCodes.OK
        'IntegrationObjectItems'          | USER_CREATE | HttpStatusCodes.FORBIDDEN
        'IntegrationObjectItems'          | USER_VIEW   | HttpStatusCodes.FORBIDDEN
        'IntegrationObjectItemAttributes' | USER_ADMIN  | HttpStatusCodes.OK
        'IntegrationObjectItemAttributes' | USER_CREATE | HttpStatusCodes.FORBIDDEN
        'IntegrationObjectItemAttributes' | USER_VIEW   | HttpStatusCodes.FORBIDDEN
    }

    @Test
    @Unroll
    def "Users of integration#user group are #status to POST to /IntegrationService"() {
        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .credentials(user, PASSWORD)
                .build()
                .post(Entity.json('{}'))

        then:
        response.status == HttpStatusCodes.FORBIDDEN.statusCode

        where:
        user << [USER_ADMIN, USER_CREATE, USER_VIEW]
    }

    @Test
    @Unroll
    def "POST to /IntegrationService/#feed is #status for users of integration#user group" () {
        setup:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                '; Order',
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                '; Order ; Order      ; Order',
                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
                '; Order:Order      ; code     ; Order:code          ;                  ; true')

        when:
        def response = basicAuthRequest()
                .path(SERVICE_NAME)
                .path(feed)
                .credentials(user, PASSWORD)
                .build()
                .post(Entity.json(json))

        then:
        response.status == status.statusCode

        where:
        feed                              | user        | status                    | json
        'IntegrationObjects'              | USER_ADMIN  | HttpStatusCodes.CREATED   | '{"code": "Order"}'
        'IntegrationObjects'              | USER_CREATE | HttpStatusCodes.FORBIDDEN | '{"code": "Order"}'
        'IntegrationObjects'              | USER_VIEW   | HttpStatusCodes.FORBIDDEN | '{"code": "Order"}'
        'IntegrationObjectItems'          | USER_ADMIN  | HttpStatusCodes.CREATED   | '{"code": "Order", "integrationObject": {"code": "Order"}}'
        'IntegrationObjectItems'          | USER_CREATE | HttpStatusCodes.FORBIDDEN | '{"code": "Order", "integrationObject": {"code": "Order"}}'
        'IntegrationObjectItems'          | USER_VIEW   | HttpStatusCodes.FORBIDDEN | '{"code": "Order", "integrationObject": {"code": "Order"}}'
        'IntegrationObjectItemAttributes' | USER_ADMIN  | HttpStatusCodes.CREATED   | '{"attributeName": "code", "attributeDescriptor": {"qualifier": "code", "enclosingType": {"code": "Order"}}, "integrationObjectItem": {"code": "Order", "integrationObject": {"code": "Order"}}}'
        'IntegrationObjectItemAttributes' | USER_CREATE | HttpStatusCodes.FORBIDDEN | '{"attributeName": "code", "attributeDescriptor": {"qualifier": "code", "enclosingType": {"code": "Order"}}}'
        'IntegrationObjectItemAttributes' | USER_VIEW   | HttpStatusCodes.FORBIDDEN | '{"attributeName": "code", "attributeDescriptor": {"qualifier": "code", "enclosingType": {"code": "Order"}}}'
    }

    def basicAuthRequest() {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
                .accept('application/json')
    }
}
