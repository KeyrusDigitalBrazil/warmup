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

package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import org.springframework.http.MediaType
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder

@IntegrationTest
class ODataCircularDependencyIntegrationTest extends ServicelayerTransactionalSpockSpecification {
	private static final String CODE = "001"
	private static final String OTHER_CODE = '002'
	private static final String INTEGRATION_OBJECT_NAME = "CircularDependencyTest"
	private static final String ORDERS = "Orders"
	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = 'defaultODataFacade')
	private ODataFacade facade

	def setup() {
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; CircularDependencyTest ; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; CircularDependencyTest ; Order          ; Order',
				'; CircularDependencyTest ; OrderEntry     ; OrderEntry',
				'; CircularDependencyTest ; Currency       ; Currency',
				'; CircularDependencyTest ; User           ; User',
				'; CircularDependencyTest ; Product        ; Product',
				'; CircularDependencyTest ; CatalogVersion ; CatalogVersion',
				'; CircularDependencyTest ; Catalog        ; Catalog',
				'; CircularDependencyTest ; Unit           ; Unit',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autocreate',
				'; CircularDependencyTest:Order          ; code              ; Order:code              ;',
				'; CircularDependencyTest:Order          ; entries           ; Order:entries           ; CircularDependencyTest:OrderEntry',
				'; CircularDependencyTest:OrderEntry     ; order             ; OrderEntry:order        ; CircularDependencyTest:Order       ; true',
				// required attributes on Order and OrderEntry but not relevant for test
				'; CircularDependencyTest:OrderEntry     ; product           ; OrderEntry:product      ; CircularDependencyTest:Product     ;       ; true',
				'; CircularDependencyTest:OrderEntry     ; unit              ; OrderEntry:unit         ; CircularDependencyTest:Unit        ;       ; true',
				'; CircularDependencyTest:OrderEntry     ; quantity          ; OrderEntry:quantity     ; ',
				'; CircularDependencyTest:Order          ; user              ; Order:user              ; CircularDependencyTest:User        ;       ; true',
				'; CircularDependencyTest:Order          ; currency          ; Order:currency          ; CircularDependencyTest:Currency    ;       ; true',
				'; CircularDependencyTest:Order          ; date              ; Order:date              ; ',
				'; CircularDependencyTest:Currency       ; isocode           ; Currency:isocode        ;                                    ; true',
				'; CircularDependencyTest:User           ; uid               ; User:uid                ;                                    ; true',
				'; CircularDependencyTest:Catalog        ; id                ; Catalog:id              ;                                    ; true',
				'; CircularDependencyTest:CatalogVersion ; catalog           ; CatalogVersion:catalog  ; CircularDependencyTest:Catalog     ; true  ; true',
				'; CircularDependencyTest:CatalogVersion ; version           ; CatalogVersion:version  ;                                    ; true',
				'; CircularDependencyTest:Unit           ; code              ; Unit:code               ;                                    ; true',
				'; CircularDependencyTest:Unit           ; unitType          ; Unit:unitType           ;                                    ; true',
				'; CircularDependencyTest:Product        ; code              ; Product:code            ;                                    ; true',
				'; CircularDependencyTest:Product        ; catalogVersion    ; Product:catalogVersion  ; CircularDependencyTest:CatalogVersion ; true; true',
		)
	}

	@Unroll
	@Test
	def "can persist an order with order #entryType that refer back to the same order"() {
		when:
		def response = facade.handlePost(createContext(request))

		then:
		response.getStatus() == HttpStatusCodes.CREATED

		where:
		request                                  | entryType
		orderWith(entry())                       | "entry"
		orderWith(entry(), entry())              | "entries"
		orderWith(entryWithAllOrderAttributes()) | "entry with all order attributes"
	}

	@Unroll
	@Test
	def "persist an order with #entryType that refers to a different existing order fails"() {
		given: 'order 002 exists and the OrderEntry included in Order 001 refers to order 002'
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Currency; isocode[unique = true];',
				'; EUR',
				'INSERT_UPDATE User; uid[unique = true];',
				'; bob',
				'INSERT_UPDATE Order; code[unique = true]; user(uid); currency(isocode); date[dateformat = yyyymmdd]',
				"; ${OTHER_CODE} ; bob ; EUR ; 20181101")

		when:
		def response = facade.handlePost(createContext(request))

		then:
		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.INTERNAL_SERVER_ERROR)
				.jsonBody()
				.hasPathWithValueContaining("error.message.value", "since it already belongs to")

		where:
		request << [orderWith(entry(OTHER_CODE)), orderWith(entry(), entry(OTHER_CODE))]
	}

	@Unroll
	@Test
	def "persist an order with entry that refers to a different non-existing order"() {
		when:
		def response = facade.handlePost(createContext(request))

		then:
		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValueContaining("error.code", "missing_nav_property")

		where:
		request << [orderWith(entryWithAllOrderAttributes('non-existing-order')), orderWith(entry(), entryWithAllOrderAttributes('non-existing-order'))]
	}

	private ODataRequestBuilder orderWith(JsonBuilder... entries) {
		request().withBody(
				order().withFieldValues("entries", entries)
		)
	}

	private ODataRequestBuilder request() {
		postRequestBuilder(INTEGRATION_OBJECT_NAME, ORDERS, MediaType.APPLICATION_JSON_VALUE)
	}

	private JsonBuilder order() {
		json()
				.withCode(CODE)
				.withField("date", "/Date(1540469763000)/")
				.withField("user", json().withField("uid", "John"))
				.withField("currency", json().withField("isocode", "USD"))
	}

	private JsonBuilder entry(String code = CODE) {
		json()
				.withField("order", json()
				.withCode(code)
		)
				.withField("quantity", "7")
				.withField("unit", json()
				.withCode("weight")
				.withField("unitType", "kilogram"))
				.withField("product", json()
				.withCode("black pants")
				.withField('catalogVersion', json()
				.withField('version', 'Demo')
				.withField('catalog', json()
				.withField('id', 'Clothing')))
		)
	}

	private JsonBuilder entryWithAllOrderAttributes(String code = CODE) {
		entry(code)
				.withField("order", json()
				.withCode(code)
				.withField("date", "/Date(1540469763000)/")
				.withField("user", json().withField("uid", "John"))
				.withField("currency", json().withField("isocode", "USD"))
		)
	}
}
