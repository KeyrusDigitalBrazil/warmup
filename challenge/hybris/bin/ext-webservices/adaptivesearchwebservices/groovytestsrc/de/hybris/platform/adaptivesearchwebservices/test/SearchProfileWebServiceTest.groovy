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
package de.hybris.platform.adaptivesearchwebservices.test

import static javax.ws.rs.core.MediaType.APPLICATION_JSON
import static javax.ws.rs.core.MediaType.APPLICATION_XML
import static javax.ws.rs.core.Response.Status.OK

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.adaptivesearchwebservices.constants.AdaptivesearchwebservicesConstants
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer

import org.junit.Before
import org.junit.Test

@IntegrationTest
@NeedsEmbeddedServer(webExtensions = [AdaptivesearchwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME])
class SearchProfileWebServiceTest extends AbstractWebServiceTest {

	private static final String URI = "v1/searchprofiles"

	protected static final String ADMIN_USERNAME_PROP = "adaptivesearchwebservices.test.admin.username"
	protected static final String ADMIN_PASSWORD_PROP = "adaptivesearchwebservices.test.admin.password"
	protected static final String TEST_SEARCH_MANAGER_USERNAME_PROP = "adaptivesearchwebservices.test.searchmanager.username"
	protected static final String TEST_SEARCH_MANAGER_PASSWORD_PROP = "adaptivesearchwebservices.test.searchmanager.password"
	protected static final String TEST_CUSTOMER_USERNAME_PROP = "adaptivesearchwebservices.test.testuser.username"
	protected static final String TEST_CUSTOMER_PASSWORD_PROP = "adaptivesearchwebservices.test.testuser.password"

	@Before
	void setUp() {
        super.setUp();
		importCsv("/adaptivesearchwebservices/test/webcontext_testdata.impex", "utf-8")
	}

	@Test
	void getSearchProfilesWithUserAdminCredentialsWithXMLFormat() {
		def adminUserName = getConfigurationProperty(ADMIN_USERNAME_PROP)
		def adminPassword = getConfigurationProperty(ADMIN_PASSWORD_PROP)
		"get search profiles should return profiles for"(getWsSecuredRequestBuilder(adminUserName, adminPassword), APPLICATION_XML)
	}

	@Test
	void getSearchProfilesWithUserAdminCredentialsWithJsonFormat() {
		def adminUserName = getConfigurationProperty(ADMIN_USERNAME_PROP)
		def adminPassword = getConfigurationProperty(ADMIN_PASSWORD_PROP)
		"get search profiles should return profiles for"(getWsSecuredRequestBuilder(adminUserName, adminPassword), APPLICATION_JSON)
	}

	@Test
	void getSearchProfilesWithTestSearchManagerCredentials() {
		def searchManagerUserName = getConfigurationProperty(TEST_SEARCH_MANAGER_USERNAME_PROP)
		def searchManagerPassword = getConfigurationProperty(TEST_SEARCH_MANAGER_PASSWORD_PROP)
		"get search profiles should return profiles for"(getWsSecuredRequestBuilder(searchManagerUserName, searchManagerPassword))
	}

	@Test
	void getSearchProfilesWithTestCustomerCredentials() {
		def customerUserName = getConfigurationProperty(TEST_CUSTOMER_USERNAME_PROP)
		def customerPassword = getConfigurationProperty(TEST_CUSTOMER_PASSWORD_PROP)
		"get search profiles should not return profiles for"(getWsSecuredRequestBuilder(customerUserName, customerPassword))
	}

	def 'get search profiles should return profiles for'(requestBuilder, format = APPLICATION_JSON) {
		given:
		def request = requestBuilder
				.path(URI)
				.queryParam("indexTypes", "testIndex")
				.queryParam("catalogVersions", "hwcatalog:online")
				.build()
				.accept(format)

		when:
		def response = request.get()

		then:
		assert response.getStatus() == OK.getStatusCode()

		def data = parseResponse(response, format);
		assert data.searchProfiles[0].code == "simpleProfile"
		assert data.searchProfiles[0].name == "Simple search profile"
		assert data.searchProfiles[0].indexType == "testIndex"
		assert data.searchProfiles[0].catalogVersion == "hwcatalog:online"
	}

	def "get search profiles should not return profiles for"(requestBuilder, format = APPLICATION_JSON) {
		given:
		def request = requestBuilder
				.path(URI)
				.queryParam("catalogVersions", "hwcatalog:online")
				.build()
				.accept(format)

		when:
		def response = request.get()

		then:
		assert response.getStatus() == OK.getStatusCode()

		def data = parseResponse(response, format)
		assert !data.searchProfiles
	}

}
