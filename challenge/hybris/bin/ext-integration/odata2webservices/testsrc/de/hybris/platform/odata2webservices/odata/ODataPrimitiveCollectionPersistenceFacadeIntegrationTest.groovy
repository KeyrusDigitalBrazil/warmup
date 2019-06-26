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
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import org.springframework.http.MediaType

import javax.annotation.Resource

import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataGetRequest
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest

@IntegrationTest
class ODataPrimitiveCollectionPersistenceFacadeIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	def SERVICE_NAME = "PrimitiveCollection"
	def URL1 = "url1"
	def URL2 = "url2"
	def URL_PATTERNS = "urlPatterns"

	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade

	@Test
	def "can persist and get a String collection"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; PrimitiveCollection ; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; PrimitiveCollection ; Catalog ; Catalog',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; PrimitiveCollection:Catalog ; id          ; Catalog:id          ;',
				'; PrimitiveCollection:Catalog ; urlPatterns ; Catalog:urlPatterns ;'
		)

		def body = integrationObjectWithStringCollection()
		def postContext = ODataFacadeTestUtils.createContext(
				oDataPostRequest(SERVICE_NAME, "Catalogs", body, Locale.ENGLISH, MediaType.APPLICATION_JSON_VALUE))
		def getContext = ODataFacadeTestUtils.createContext(
				oDataGetRequest(SERVICE_NAME, "Catalogs", Locale.ENGLISH, "urlPatterns", "Default"))

		when:
		def postResponse = facade.handlePost(postContext)
		def getResponse = facade.handleGetEntity(getContext)
		def getBody = extractBody(getResponse)

		then:
		postResponse.getStatus() == HttpStatusCodes.CREATED
		getBody.getCollection("\$.d.results").size() == 2
		getBody.getCollectionOfObjects("d.results[*].value") == [URL1, URL2]
	}

	@Test
	def "can persist and get an Integer collection"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; PrimitiveCollection ; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; PrimitiveCollection ; Order          ; Order',
				'; PrimitiveCollection ; OrderEntry     ; OrderEntry',
				'; PrimitiveCollection ; CatalogVersion ; CatalogVersion',
				'; PrimitiveCollection ; Catalog        ; Catalog',
				'; PrimitiveCollection ; Product        ; Product',
				'; PrimitiveCollection ; Unit           ; Unit',
				'; PrimitiveCollection ; Currency       ; Currency',
				'; PrimitiveCollection ; User           ; User',

				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]',
				'; PrimitiveCollection:Order          ; code              ; Order:code                   ;',
				'; PrimitiveCollection:Order          ; currency          ; Order:currency               ; PrimitiveCollection:Currency',
				'; PrimitiveCollection:Order          ; user              ; Order:user                   ; PrimitiveCollection:User    ;',
				'; PrimitiveCollection:Order          ; date              ; Order:date                   ;',

				'; PrimitiveCollection:Currency       ; isocode           ; Currency:isocode             ;',

				'; PrimitiveCollection:User           ; uid               ; User:uid                     ;                        ;',

				'; PrimitiveCollection:Catalog        ; id                ; Catalog:id                   ;',

				'; PrimitiveCollection:CatalogVersion ; catalog           ; CatalogVersion:catalog       ; PrimitiveCollection:Catalog ',
				'; PrimitiveCollection:CatalogVersion ; version           ; CatalogVersion:version       ;',

				'; PrimitiveCollection:Product        ; code              ; Product:code                 ;',
				'; PrimitiveCollection:Product        ; catalogVersion    ; Product:catalogVersion       ; PrimitiveCollection:CatalogVersion',
				'; PrimitiveCollection:Product        ; name              ; Product:name                 ;',

				'; PrimitiveCollection:Unit           ; code              ; Unit:code                    ;',
				'; PrimitiveCollection:Unit           ; name              ; Unit:name                    ;',
				'; PrimitiveCollection:Unit           ; unitType          ; Unit:unitType                ;',

				'; PrimitiveCollection:OrderEntry     ; entryGroupNumbers ; OrderEntry:entryGroupNumbers ;',
				'; PrimitiveCollection:OrderEntry     ; entryNumber       ; OrderEntry:entryNumber       ;',
				'; PrimitiveCollection:OrderEntry     ; quantity          ; OrderEntry:quantity          ;',
				'; PrimitiveCollection:OrderEntry     ; order             ; OrderEntry:order             ; PrimitiveCollection:Order   ;',
				'; PrimitiveCollection:OrderEntry     ; product           ; OrderEntry:product           ; PrimitiveCollection:Product ;',
				'; PrimitiveCollection:OrderEntry     ; unit              ; OrderEntry:unit              ; PrimitiveCollection:Unit    ;')

		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Unit; code[unique = true]; name[lang = en]; unitType;',
				'; pieces ; pieces ; pieces',
				'INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;',
				'; Default ; Default ; true',
				'INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;',
				'; Default ; Staged ; true',
				'INSERT_UPDATE Currency; isocode[unique = true]',
				'; EUR',
				'INSERT_UPDATE User; uid[unique = true]',
				'; anonymous',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); name',
				'; prod1 ; $catalogVersion ; product1',
				'INSERT_UPDATE Order; code[unique = true]; currency(isocode); user(uid); date[dateformat=MM:dd:yyyy]',
				'; order1 ; EUR ; anonymous ; 08:27:1984')

		def body = integrationObjectWithIntegerCollection()

		def postContext = ODataFacadeTestUtils.createContext(
				oDataPostRequest(SERVICE_NAME, "OrderEntries", body, Locale.ENGLISH, MediaType.APPLICATION_JSON_VALUE))
		def getContext = ODataFacadeTestUtils.createContext(
				oDataGetRequest(SERVICE_NAME, "OrderEntries", Locale.ENGLISH, "entryGroupNumbers", "237|order1"))

		when:
		def postResponse = facade.handlePost(postContext)
		def getResponse = facade.handleGetEntity(getContext)
		def getBody = extractBody(getResponse)

		then:
		postResponse.getStatus() == HttpStatusCodes.CREATED
		getBody.getCollection("\$.d.results").size() == 5
		getBody.getCollectionOfObjects("d.results[*].value").containsAll(11, 22, 33, 44, 55)
	}


	def integrationObjectWithIntegerCollection() {
		JsonBuilder.json()
				.withField("order", JsonBuilder.json().withCode("order1"))
				.withField("entryNumber", 237)
				.withField("product", JsonBuilder.json().withCode("prod1")
				.withField("catalogVersion", JsonBuilder.json()
				.withField("catalog", JsonBuilder.json().withId("Default"))
				.withField("version", "Staged")))
				.withField("unit", JsonBuilder.json()
				.withCode("pieces")
				.withField("name", "pieces")
				.withField("unitType", "pieces"))
				.withField("quantity", "25")
				.withFieldValues("entryGroupNumbers",
				JsonBuilder.json().withField("value", 11),
				JsonBuilder.json().withField("value", 22),
				JsonBuilder.json().withField("value", 33),
				JsonBuilder.json().withField("value", 44),
				JsonBuilder.json().withField("value", 55))
				.build()
	}

	def integrationObjectWithStringCollection() {
		JsonBuilder.json()
				.withId("Default")
				.withFieldValues(URL_PATTERNS,
				JsonBuilder.json().withField("value", URL1),
				JsonBuilder.json().withField("value", URL2))
				.build()
	}

	JsonObject extractBody(final IntegrationODataResponse response) {
		JsonObject.createFrom(response.getEntityAsStream())
	}
}
