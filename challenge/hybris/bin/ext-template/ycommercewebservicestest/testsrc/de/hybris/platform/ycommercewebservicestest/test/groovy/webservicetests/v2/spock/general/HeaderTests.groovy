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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.general

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockTest
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.HttpHeaders
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

/**
 *
 */
@Unroll
@ManualTest
class HeaderTests extends AbstractSpockTest {
	
	def "Checking Secured Headers : #format"() {

		when: "user search for products"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'canon'],
				requestContentType: URLENC
		)

		then: "Cache-Control header is in place "
		with(response) {
			status == SC_OK
			response.containsHeader('X-FRAME-Options')
			response.getFirstHeader('X-FRAME-Options').getValue().equals('SAMEORIGIN')
			response.containsHeader('X-XSS-Protection')
			response.getFirstHeader('X-XSS-Protection').getValue().equals('1; mode=block')
			response.containsHeader('X-Content-Type-Options')
			response.getFirstHeader('X-Content-Type-Options').getValue().equals('nosniff')
			response.containsHeader('Strict-Transport-Security')
			response.getFirstHeader('Strict-Transport-Security').getValue().equals('max-age=16070400 ; includeSubDomains')
		}

		where:
		format << [XML, JSON]
	}	

}
