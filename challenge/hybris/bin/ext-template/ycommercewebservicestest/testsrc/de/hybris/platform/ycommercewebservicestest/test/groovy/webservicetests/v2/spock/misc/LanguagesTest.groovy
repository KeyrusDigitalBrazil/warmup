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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator

@ManualTest
@Unroll
/**
 *
 *
 */
class LanguagesTest extends AbstractSpockFlowTest {

	private languageList = ['ja', 'en', 'de', 'zh']


	def "Client retrieves supported languages: #format"() {

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/languages', contentType: format)

		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			isNotEmpty(data.languages)
			data.languages.size() == languageList.size()
			data.languages.findAll { language ->
				language.isocode in languageList
			}.size() == languageList.size()
		}

		where:
		format << [JSON, XML]
	}
}
