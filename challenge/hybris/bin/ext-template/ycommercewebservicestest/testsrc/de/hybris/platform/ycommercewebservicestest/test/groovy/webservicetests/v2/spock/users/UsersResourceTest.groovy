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
/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*

import de.hybris.bootstrap.annotations.ManualTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator

/**
 *
 * This class focuses on tests more related to resource like registering users
 *
 */
@ManualTest
@Unroll
class UsersResourceTest extends AbstractUserTest {

	def "Register user with all required data when request: #requestFormat and response: #responseFormat"() {
		given: "authenticated client"
		authorizeClient(restClient)

		when: "user attempts to register"
		def response = restClient.post(
				path: getBasePathWithSite() + '/users',
				body: postBody,
				contentType: responseFormat,
				requestContentType: requestFormat)

		then: "account is created"
		with(response) {
			if (isNotEmpty(data)) println data
			status == SC_CREATED
			1 == responseBase.original.headergroup.headers.findAll {"${it}".contains("Location: ") && "${it}".contains(getBasePathWithSite() + '/users/' + data.uid)}.size()
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC        | JSON           | ['login': System.currentTimeMillis() + '@urlenc2json.pl', 'password': CUSTOMER_PASSWORD_STRONG, 'firstName': 'Jan', 'lastName': 'Kowalski', 'titleCode': 'mr']
		URLENC        | XML            | ['login': System.currentTimeMillis() + '@urlenc2xml.pl', 'password': CUSTOMER_PASSWORD_STRONG, 'firstName': 'Jan', 'lastName': 'Kowalski', 'titleCode': 'mr']
		JSON          | JSON           | '{"uid": "' + System.currentTimeMillis() + '@json2json.pl", "password": "'+CUSTOMER_PASSWORD_STRONG+'", "firstName": "Jan", "lastName": "Kowalski", "titleCode": "mr"}'
		XML           | XML            | "<user><uid>${System.currentTimeMillis()}@xml2xml.pl</uid><password>${CUSTOMER_PASSWORD_STRONG}</password><firstName>Jan</firstName><lastName>Kowalski</lastName><titleCode>mr</titleCode></user>"
	}

	def "Register user using HTTP"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)
		// YTODO : find a better way to disable automatic redirects
		restClient.setUri(getDefaultHttpUri())

		when: "user attempts to register using HTTP"
		def randomUID = System.currentTimeMillis()
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/',
				body: [
					'login': randomUID + '@test.v2.com',
					'password': CUSTOMER_PASSWORD_STRONG,
					'firstName': CUSTOMER_FIRST_NAME,
					'lastName': CUSTOMER_LAST_NAME,
					'titleCode': CUSTOMER_TITLE_CODE
				],
				requestContentType: URLENC)

		then: "he is not allowed to do so"
		with(response) { status == SC_MOVED_TEMPORARILY }

	}

	def "Register user with duplicate ID : #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "user registers account"
		def randomUID = System.currentTimeMillis()
		with(restClient.post(
				path: getBasePathWithSite() + '/users/',
				body: [
					'login': randomUID + '@test.v2.com',
					'password': CUSTOMER_PASSWORD_STRONG,
					'firstName': CUSTOMER_FIRST_NAME,
					'lastName': CUSTOMER_LAST_NAME,
					'titleCode': CUSTOMER_TITLE_CODE
				],
				contentType: format,
				requestContentType: URLENC)) {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_CREATED
				}

		and: "tries to register account with the same login"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/',
				body: [
					'login': randomUID + '@test.v2.com',
					'password': CUSTOMER_PASSWORD_STRONG,
					'firstName': CUSTOMER_FIRST_NAME,
					'lastName': CUSTOMER_LAST_NAME,
					'titleCode': CUSTOMER_TITLE_CODE
				],
				contentType: format,
				requestContentType: URLENC)

		then: "it is not allowed to register same login twice"
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'DuplicateUidError'
		}

		where:
		format << [XML, JSON]
	}

	def "Login a non-existing user : #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "a non-existing customer tries to log in"
		HttpResponseDecorator response = restClient.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: [
					'grant_type': 'password',
					'client_id': clientId,
					'client_secret': clientSecret,
					'username': 'nonExistingUser@hybris.com',
					'password': 'password'
				],
				contentType: JSON,
				requestContentType: URLENC)

		then: "he gets an error"
		with(response) {
			status == SC_BAD_REQUEST
			data.error == 'invalid_grant'
			data.error_description == 'Bad credentials'
		}
		where:
		format << [XML, JSON]
	}

	def "Login with wrong password : #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "a non-existing customer tries to log in"
		HttpResponseDecorator response = restClient.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: [
					'grant_type': 'password',
					'client_id': clientId,
					'client_secret': clientSecret,
					'username': CUSTOMER_USERNAME,
					'password': 'this_is_wrong'
				],
				contentType: JSON,
				requestContentType: URLENC)

		then: "he gets an error"
		with(response) {
			status == SC_BAD_REQUEST
			data.error == 'invalid_grant'
			data.error_description == 'Bad credentials'
		}
		where:
		format << [XML, JSON]
	}


	def "Deactivate given user: #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		def randomUID = System.currentTimeMillis() + '-deact@test.v2.com'
		when: "user registers account"
		with(restClient.post(
				path: getBasePathWithSite() + '/users/',
				body: [
					'login': randomUID,
					'password': CUSTOMER_PASSWORD_STRONG,
					'firstName': CUSTOMER_FIRST_NAME,
					'lastName': CUSTOMER_LAST_NAME,
					'titleCode': CUSTOMER_TITLE_CODE
				],
				contentType: format,
				requestContentType: URLENC)) {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_CREATED
				}

		and: "deactivates new user"
		def response = restClient.delete(
				path: getBasePathWithSite() + '/users/' + randomUID ,
				contentType: format,
				requestContentType: URLENC
				)
		with(response){ status == SC_OK }

		then: "user is deactivated"
		def errorData = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), randomUID, CUSTOMER_PASSWORD_STRONG, false);
		errorData.error == 'invalid_grant'
		errorData.error_description == 'User is disabled'

		where:
		format <<[XML, JSON]
	}

}