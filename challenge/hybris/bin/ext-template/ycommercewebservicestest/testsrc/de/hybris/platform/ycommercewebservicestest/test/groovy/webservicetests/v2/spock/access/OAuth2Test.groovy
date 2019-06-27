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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import spock.lang.Ignore
import spock.lang.Unroll

@ManualTest
@Unroll
class OAuth2Test extends AbstractSpockFlowTest {

	@Ignore //ignore until preventing brute force attack start working again
	def "Test brute force attack"(){
		given: "Registered user"
		authorizeClient(restClient)
		def customer = registerCustomer(restClient);

		when:"try to get token 5 times with wrong password"
		def errorData1 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer.id, 'wrongPassword', false);
		def errorData2 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer.id, 'wrongPassword', false);
		def errorData3 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer.id, 'wrongPassword', false);
		def errorData4 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer.id, 'wrongPassword', false);
		def errorData5 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer.id, 'wrongPassword', false);
		def errorData6 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer.id, customer.password, false);

		then:"user account will be disabled"
		errorData1.error == 'invalid_grant'
		errorData1.error_description == 'Bad credentials'
		errorData2.error == 'invalid_grant'
		errorData2.error_description == 'Bad credentials'
		errorData3.error == 'invalid_grant'
		errorData3.error_description == 'Bad credentials'
		errorData4.error == 'invalid_grant'
		errorData4.error_description == 'Bad credentials'
		errorData5.error == 'invalid_grant'
		errorData5.error_description == 'Bad credentials'
		errorData6.error == 'invalid_grant'
		errorData6.error_description == 'User is disabled'
	}
}
