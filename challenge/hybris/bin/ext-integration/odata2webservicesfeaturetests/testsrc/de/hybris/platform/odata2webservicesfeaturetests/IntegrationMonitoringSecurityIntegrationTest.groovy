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
class IntegrationMonitoringSecurityIntegrationTest extends ServicelayerSpockSpecification
{
	private static final String PASSWORD = 'password'
	private static final String USER_ADMIN = 'monitoringTestAdmin'
	private static final String USER_CREATE = 'monitoringTestCreate'
	private static final String USER_VIEW = 'monitoringTestView'

	def setupSpec() {
        importCsv '/impex/essentialdata-odata2services.impex', 'UTF-8'
		importCsv '/impex/essentialdata-inboundservices.impex', 'UTF-8'
		importCsv '/impex/essentialdata-outboundservices.impex', 'UTF-8'
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
	@Unroll
    def "Users must be authenticated in order to GET /#service"() {
        when:
        def response = basicAuthRequest(service)
                .build()
                .get()

        then:
        response.status == HttpStatusCodes.UNAUTHORIZED.statusCode

		where:
		service << ['InboundIntegrationMonitoring', 'OutboundIntegrationMonitoring']
    }

    @Test
    @Unroll
    def "Users must be authenticated in order to GET /#path"() {
        when:
        def response = basicAuthRequest(service)
                .path(feed)
                .build()
                .get()

        then:
        response.status == HttpStatusCodes.UNAUTHORIZED.statusCode

        where:
        service                         | feed                         | path
        'InboundIntegrationMonitoring'  | 'InboundRequests'            | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | "$service/$feed"
    }

  	@Test
    @Unroll
	def "Users of integration#user group are #status to GET /#service"() {
		when:
		def response = basicAuthRequest(service)
				.credentials(user, PASSWORD)
				.build()
				.get()

		then:
		response.status == status.statusCode

		where:
        status                    | user        | service
        HttpStatusCodes.OK        | USER_ADMIN  | 'InboundIntegrationMonitoring'
        HttpStatusCodes.FORBIDDEN | USER_CREATE | 'InboundIntegrationMonitoring'
        HttpStatusCodes.FORBIDDEN | USER_VIEW   | 'InboundIntegrationMonitoring'
        HttpStatusCodes.OK        | USER_ADMIN  | 'OutboundIntegrationMonitoring'
        HttpStatusCodes.FORBIDDEN | USER_CREATE | 'OutboundIntegrationMonitoring'
        HttpStatusCodes.FORBIDDEN | USER_VIEW   | 'OutboundIntegrationMonitoring'
	}

	@Test
	@Unroll
	def "Users of integration#user group are #status to GET /#path"()	{
		when:
		def response = basicAuthRequest(service)
				.path(feed)
				.credentials(user, PASSWORD)
				.build()
				.get()

		then:
		response.status == status.statusCode

		where:
		service                         | feed                         | user        | status                    | path
		'InboundIntegrationMonitoring'  | 'InboundRequests'            | USER_ADMIN  | HttpStatusCodes.OK        | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequests'            | USER_CREATE | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequests'            | USER_VIEW   | HttpStatusCodes.FORBIDDEN | "$service/$feed"
		'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | USER_ADMIN  | HttpStatusCodes.OK        | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | USER_CREATE | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | USER_VIEW   | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | USER_ADMIN  | HttpStatusCodes.OK        | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | USER_CREATE | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | USER_VIEW   | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | USER_ADMIN  | HttpStatusCodes.OK        | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | USER_CREATE | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | USER_VIEW   | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | USER_ADMIN  | HttpStatusCodes.OK        | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | USER_CREATE | HttpStatusCodes.FORBIDDEN | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | USER_VIEW   | HttpStatusCodes.FORBIDDEN | "$service/$feed"
	}

	@Test
    @Unroll
	def "Users of integration#user group are forbidden to POST to /#path"() {
		when:
		def response = basicAuthRequest(service)
				.path(feed)
				.credentials(user, PASSWORD)
				.build()
				.post(Entity.json('{}'))

		then:
		response.status == HttpStatusCodes.FORBIDDEN.statusCode

		where:
        service                         | feed                         | user        | path
        'InboundIntegrationMonitoring'  | 'InboundRequests'            | USER_ADMIN  | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequests'            | USER_CREATE | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequests'            | USER_VIEW   | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | USER_ADMIN  | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | USER_ADMIN  | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'InboundRequestErrors'       | USER_CREATE | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | USER_CREATE | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | USER_VIEW   | "$service/$feed"
        'InboundIntegrationMonitoring'  | 'IntegrationRequestStatuses' | USER_VIEW   | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | USER_ADMIN  | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | USER_CREATE | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'OutboundRequests'           | USER_VIEW   | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | USER_CREATE | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | USER_VIEW   | "$service/$feed"
        'OutboundIntegrationMonitoring' | 'IntegrationRequestStatuses' | USER_VIEW   | "$service/$feed"
	}

    def basicAuthRequest(String path) {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
				.accept("application/json")
				.path(path)
    }
}
