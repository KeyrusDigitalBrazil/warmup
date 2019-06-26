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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

/**
 *
 *
 */
@ManualTest
@Unroll
class ProductResourceTest extends AbstractSpockFlowTest {
	static final PRODUCT_ID_FLEXI_TRIPOD = '3429337'
	static final NUMBER_OF_ALL_PRODUCTS = 42
	static final STORE_NAME = 'WS-Shinbashi'
	//for export reference
	static final PRODUCT_CODE = 2053226
	static final REFERENCES_COUNT = 7


	def "Search for product: #format"() {

		when: "user search for specified product attributes"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['fields': 'products,sorts,pagination'],
				requestContentType: URLENC
				)

		then: "he gets all the requested fields"
		with(response) {
			status == SC_OK
			data.products.size() > 0
			data.sorts.size() > 0
			data.pagination
			data.pagination.pageSize.toString() == '20'
			data.pagination.totalPages.toString() == Math.ceil(NUMBER_OF_ALL_PRODUCTS / 20).toInteger().toString()
			data.pagination.currentPage.toString() == '0'
			data.pagination.totalResults.toString() == NUMBER_OF_ALL_PRODUCTS.toString()
			response.containsHeader(HEADER_TOTAL_COUNT)
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == NUMBER_OF_ALL_PRODUCTS.toString()
		}

		where:
		format << [XML, JSON]
	}

	def "Get number of all products: #format"() {

		when: "user search for any products (empty query)"
		HttpResponseDecorator response = restClient.head(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				requestContentType: URLENC
				)

		then: "he gets proper header"
		with(response) {
			status == SC_OK
			response.containsHeader(HEADER_TOTAL_COUNT)
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == NUMBER_OF_ALL_PRODUCTS.toString()
		}

		where:
		format << [XML, JSON]
	}

	def "Check spelling suggestion: #format"() {

		when: "search input contains a typo"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'somy'],
				requestContentType: URLENC
				)

		then: "correct spelling suggestion will be returned"
		with(response) {
			status == SC_OK;
			data.currentQuery.query.value == 'somy:relevance'
			data.spellingSuggestion
			data.spellingSuggestion.suggestion == 'sony'
			data.spellingSuggestion.query == 'sony:relevance' || data.spellingSuggestion.query == 'sony:topRated'
		}
		where:
		format << [XML, JSON]
	}

	def "Check context default: #format"() {

		when: "search input contains a typo"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': '',
					'fields': 'products, facets',
					'searchQueryContext': 'DEFAULT'],
				requestContentType: URLENC
				)

		then: "results using default search query template"
		with(response) {
			status == SC_OK;
			data.pagination
			data.pagination.pageSize.toString() == '20'
			isNotEmpty(data.facets)
			data.facets.size() == 1
		}

		where:
		format << [XML, JSON]
	}

	def "Check context suggestion: #format"() {

		when: "search input contains a typo"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'sony',
					'fields': 'products, facets',
					'searchQueryContext': 'SUGGESTIONS'],
				requestContentType: URLENC
				)

		then: "results using suggestions search query template"
		with(response) {
			status == SC_OK;
			data.pagination
			data.pagination.pageSize.toString() == '20'
			isEmpty(data.facets)
		}

		where:
		format << [XML, JSON]
	}

	def "Check autosuggestion: #format"() {

		when: "search text is incomplete"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/suggestions',
				contentType: format,
				query: ['term': 'can'],
				requestContentType: URLENC
				)
		then: "set of possible autosuggested fields will be returned"
		with(response) {
			status == SC_OK;
			isNotEmpty(data.suggestions)
			data.suggestions.size() == 2
			data.suggestions.value[0] == 'canon'
			data.suggestions.value[1] == 'canyon'
		}
		where:
		format << [XML, JSON]
	}

	def "Check autosuggestion required parameter: #format"() {

		when: "required search text parameter is missing"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/suggestions',
				contentType: format,
				query: ['noneterm': 'can'],
				requestContentType: URLENC
				)
		then: "error with message 'required parameter missing' should be returned"
		with(response) {
			status == SC_BAD_REQUEST;
			isNotEmpty(data)
			data.toString().contains('Required String parameter')
		}
		where:
		format << [XML, JSON]
	}

	def "Check autosuggest with limit: #format"() {

		when: "search text is incomplete and the expected outcome is limmited"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/suggestions',
				contentType: format,
				query: ['max' : 1,
					'term': 'can'
				],
				requestContentType: URLENC
				)
		then: "limited set of possible autosuggested fields will be returned"
		with(response) {
			status == SC_OK;
			isNotEmpty(data.suggestions)
			data.suggestions.size() == 1
			data.suggestions.value[0] == 'canon'
		}
		where:
		format << [XML, JSON]
	}

	def "Check product sorting: #format"() {

		expect: "all camaras sorted by default sort order"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'camera'],
				requestContentType: URLENC
				)

		with(response) {
			status == SC_OK
			data.pagination.sort == 'relevance'
			isNotEmpty(data.sorts)
			data.sorts.find { it.code == response.data.pagination.sort }.selected == true
		}

		def allSorts = response.data.sorts

		and: "check all possible sort orders"

		for (def sortOrder : allSorts) {
			HttpResponseDecorator sortResponse = restClient.get(
					path: getBasePathWithSite() + '/products/search',
					contentType: format,
					query: ['sort': sortOrder.code],
					requestContentType: URLENC
					)

			with(sortResponse) {
				status == SC_OK
				data.pagination.sort == sortOrder.code
			}
		}

		where:
		format << [XML, JSON]
	}

	def "Check product details: #format"() {

		expect: "all camaras sorted by default sort order"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'camera'],
				requestContentType: URLENC
				)

		with(response) {
			status == SC_OK
			isNotEmpty(data.products)
		}

		def productsList = response.data.products

		and: "check products details for all camaras"

		for (def product : productsList) {
			HttpResponseDecorator productResponse = restClient.get(
					path: getBasePathWithSite() + '/products/' + product.code,
					contentType: format,
					requestContentType: URLENC
					)

			with(productResponse) {
				status == SC_OK
				data.code == product.code
				isNotEmpty(data.name)
				isNotEmpty(data.url)
			}
		}
		where:
		format << [XML, JSON]
	}


	def "Trusted client exports similar product references: #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "client requests an export of similar product references"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_CODE + '/references',
				contentType: format,
				query: ['referenceType': 'SIMILAR'],
				requestContentType: URLENC
				)

		then: "all similar products are returned"
		with(response) {
			if (isNotEmpty(data.errors)) println data.errors
			status == SC_OK
			isNotEmpty(data.references)
			data.references.size() == REFERENCES_COUNT
		}

		where:
		format << [XML, JSON]
	}

	def "Trusted client exports invalid product references: #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "client requests an export of similar product references"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_CODE + '/references',
				contentType: format,
				query: ['referenceType': 'INVALID_REFERENCE_TYPE'],
				requestContentType: URLENC
				)

		then: "he receives proper header"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors.size() > 0
		}
		and: "error is properly wrapped"
		with(response) {
			data.errors[0].type == "IllegalArgumentError"
		}

		where:
		format << [XML, JSON]
	}

	def "Create anonymous review : #format"()
	{
		given: "review data"
		println 'Review data : ' + postBody

		when: "create review request is send"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/products/' + PRODUCT_CODE + '/reviews',
				body: postBody,
				contentType: format,
				requestContentType: format)

		then: "review is created"
		with(response) {
			if (isNotEmpty(data.errors)) println data.errors
			status == SC_CREATED
			data.alias == "krzys"
			data.rating == 4.0
			data.comment == "perfect"
			data.headline == "samplereview"
			data.principal.uid == "anonymous"
		}
		where:
		format | postBody
		JSON   | '{\"alias\": \"krzys\", \"rating\": 4, \"comment\": \"perfect\", \"headline\": \"samplereview\"}'
		XML    | "<review><alias>krzys</alias><rating>4</rating><comment>perfect</comment><headline>samplereview</headline></review>"
	}
}
