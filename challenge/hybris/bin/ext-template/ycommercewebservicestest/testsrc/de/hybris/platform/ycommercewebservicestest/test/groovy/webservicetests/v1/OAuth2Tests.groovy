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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1

import de.hybris.bootstrap.annotations.ManualTest

import org.junit.Test

import groovy.json.JsonSlurper

@ManualTest
class OAuth2Tests extends BaseWSTest {

	@Test
	void testGetAccessToken() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUserJSON()
		assert testUtil.getAccessToken(uid, CustomerTests.password) : 'Unable to obtain access_token!'
	}

	@Test
	void testGetRefreshToken() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUserJSON()
		def tokenMap = testUtil.getAccessTokenMap(uid, CustomerTests.password)
		assert testUtil.refreshToken(tokenMap.refresh_token) : 'Could not obtian new access token'
	}

	@Test
	void testGetClientCredentialsToken() {
		assert testUtil.getClientCredentialsToken('mobile_android', 'secret')
	}

	@Test
	void testGetWrongClientIdClientCredentialsToken() {
		testUtil.fakeSecurity()
		def con = testUtil.getSecureConnection("${config.OAUTH2_TOKEN_ENDPOINT_URI}?client_id=WRONG&client_secret=secret&grant_type=client_credentials", 'POST', 'JSON', HttpURLConnection.HTTP_UNAUTHORIZED)
		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error);
		assert response.error == "invalid_client"
	}

	@Test
	void testGetWrongGrantTypeToken() {
		testUtil.fakeSecurity()
		def con = testUtil.getSecureConnection("${config.OAUTH2_TOKEN_ENDPOINT_URI}?client_id=mobile_android&client_secret=secret&grant_type=WRONG", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error);
		assert response.error == "unsupported_grant_type"
	}
}