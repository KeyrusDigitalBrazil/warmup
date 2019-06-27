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
package de.hybris.platform.permissionswebservices.controllers


/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 *
 */
;


import static javax.ws.rs.core.MediaType.*
import static javax.ws.rs.core.Response.Status.*

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.permissionswebservices.constants.PermissionswebservicesConstants
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import org.junit.Before
import org.junit.Test

import groovy.json.JsonSlurper


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
[ PermissionswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME ])
public class PermissionsWebServicesCompatibilityTest extends AbstractPermissionsWebServicesTest {
	static final String SUBGROUP2 = "subgroup2"

	WsSecuredRequestBuilder wsSecuredRequestBuilder

	@Before
	void setup() {
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
				.extensionName(PermissionswebservicesConstants.EXTENSIONNAME)
				.path("v1")
				.client("mobile_android", "secret");
		importData(new ClasspathImpExResource("/permissionswebservices/test/testpermissions.impex", "UTF-8"));
		insertGlobalPermission(SUBGROUP2, "globalpermission1");
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 */
	@Test
	public void testJSONCompatibility_6_1(){
		"get permissions"("json", APPLICATION_JSON)
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 */
	@Test
	public void testXMLCompatibility_6_1(){
		"get permissions"("xml", APPLICATION_XML)
	}


	def "get permissions"(ext, format) {
		given: "predefined response"
		def expected = loadObject("/permissionswebservices/test/wstests/permissions-response."+ext, format )

		when: "actual request is made"
		def response = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path("admin")//
				.path("types")//
				.queryParam("types", "User,Order")//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.resourceOwner("admin", "nimda")//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(format)//
				.get();

		def actual = parse(response, format)

		then: "request was made "
		assert response.status == OK.statusCode

		then: "actual response is the same as expected"
		assert actual == expected

	}

	def loadText(name) {
		this.getClass().getResource(name).text
	}

	def loadObject(name, format) {
		stringParse( loadText(name), format )
	}

	def parse(response, format) {
		def text = response.readEntity(String.class)
		stringParse(text, format)
	}

	def stringParse(text, format) {
		switch(format) {
			case APPLICATION_JSON:
				return new JsonSlurper().parseText(text);
			case APPLICATION_XML:
				return new XmlSlurper().parseText(text);
			default:
				return null;
		}
	}
}
