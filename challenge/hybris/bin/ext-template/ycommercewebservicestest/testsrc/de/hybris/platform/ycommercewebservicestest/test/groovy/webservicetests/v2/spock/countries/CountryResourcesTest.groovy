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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.countries

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK


@Unroll
@ManualTest
class CountryResourcesTest extends AbstractUserTest {

	def "Client retrieves available shipping countries: #format"() {

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/countries', contentType: format, query: ['type': "shipping"])
		
		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			isNotEmpty(data.countries)
			data.countries.size() == 59
			data.countries[0].isocode == 'AL'
			data.countries[0].name == 'Albania'
			data.countries[30].isocode == 'KP'
			data.countries[30].name == 'Korea, Democratic People\'s Republic of'
		}

		where:
		format << [JSON, XML]
	}
	
	def "Client retrieves available billing countries: #format"() {

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/countries', contentType: format, query: ['type': "billing"])
		
		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			isNotEmpty(data.countries)
			data.countries.size() == 58
			data.countries[0].isocode == 'AL'
			data.countries[0].name == 'Albania'
			data.countries[30].isocode == 'KR'
			data.countries[30].name == 'Korea, Republic of'
		}

		where:
		format << [JSON, XML]
	}
	
	def "Client retrieves all countries: #format"() {

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/countries', contentType: format)
		
		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			isNotEmpty(data.countries)
			data.countries.size() == 248
			data.countries[0].isocode == 'AF'
			data.countries[0].name == 'Afghanistan'
		}

		where:
		format << [JSON, XML]
	}
	
	def "Client retrieves countries with invalid type: #format"() {

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/countries', contentType: format, query: ['type': "xxx"])
		
		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_BAD_REQUEST
			isEmpty(data.countries)
			data.errors[0].type == 'IllegalStateError'
			data.errors[0].message == "The value of country type : [xxx] is invalid"
		}

		where:
		format << [JSON, XML]
	}

    def "Fetch the list of country's regions: #format"(){

        when: "anonymous (not logged in) user wants to fetch the list of country's regions"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/countries/CA/regions',
                contentType: format,
                requestContentType: URLENC
        )

        then: "he retrieves the list of regions"
        with(response){
            status == SC_OK
            data.regions.size()==13
            data.regions[0].isocode == 'CA-AB'
            data.regions[0].name == 'Alberta'

            data.regions[12].isocode == 'CA-YT'
            data.regions[12].name == 'Yukon Territory'
        }

        where:
        format <<[XML, JSON]
    }

    def "Fetch the list of regions for a country that doesn't have any: #format"(){

        when: "anonymous (not logged in) user wants to fetch the list of regions for a country that doesn't have any"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/countries/RS/regions',
                contentType: format,
                requestContentType: URLENC
        )


        then: "he receives an empty response"
        with(response){
            status == SC_OK
            isEmpty(data.regions)
        }

        where:
        format <<[XML, JSON]
    }

    def "Fetch the list of country's regions for unknown ISO code: #format"(){

        when: "anonymous (not logged in) user wants to fetch the list of country's regions for unknown ISO code"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/countries/XXX/regions',
                contentType: format,
                requestContentType: URLENC
        )

        then: "he gets an error"
        with(response){
            status == SC_BAD_REQUEST
            data.errors.size()==1
            data.errors[0].type == 'UnknownIdentifierError'
        }

        where:
        format <<[XML, JSON]
    }

}
