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
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import org.springframework.http.MediaType

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest

@IntegrationTest
class ODataNullableIntegrationKeyPropertiesPersistenceFacadeIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	def SERVICE_NAME = "OrderEntryExample"

	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade

	def setup() {
		importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; OrderEntryExample ; INBOUND',

				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; OrderEntryExample ; Order          ; Order',
				'; OrderEntryExample ; OrderEntry     ; OrderEntry',
				'; OrderEntryExample ; CatalogVersion ; CatalogVersion',
				'; OrderEntryExample ; Catalog        ; Catalog',
				'; OrderEntryExample ; Product        ; Product',
				'; OrderEntryExample ; Unit           ; Unit',
				'; OrderEntryExample ; Currency       ; Currency',
				'; OrderEntryExample ; User           ; User',

				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]',
				'; OrderEntryExample:Order          ; code              ; Order:code                   ;',
				'; OrderEntryExample:Order          ; currency          ; Order:currency               ; OrderEntryExample:Currency',
				'; OrderEntryExample:Order          ; user              ; Order:user                   ; OrderEntryExample:User    ;',
				'; OrderEntryExample:Order          ; date              ; Order:date                   ;',

				'; OrderEntryExample:Currency       ; isocode           ; Currency:isocode             ;',

				'; OrderEntryExample:User           ; uid               ; User:uid                     ;                           ;',

				'; OrderEntryExample:Catalog        ; id                ; Catalog:id                   ;',

				'; OrderEntryExample:CatalogVersion ; catalog           ; CatalogVersion:catalog       ; OrderEntryExample:Catalog',
				'; OrderEntryExample:CatalogVersion ; version           ; CatalogVersion:version       ;',

				'; OrderEntryExample:Product        ; code              ; Product:code                 ;',
				'; OrderEntryExample:Product        ; catalogVersion    ; Product:catalogVersion       ; OrderEntryExample:CatalogVersion',
				'; OrderEntryExample:Product        ; name              ; Product:name                 ;',

				'; OrderEntryExample:Unit           ; code              ; Unit:code                    ;',
				'; OrderEntryExample:Unit           ; name              ; Unit:name                    ;',
				'; OrderEntryExample:Unit           ; unitType          ; Unit:unitType                ;',

				'; OrderEntryExample:OrderEntry     ; entryGroupNumbers ; OrderEntry:entryGroupNumbers ;',
				'; OrderEntryExample:OrderEntry     ; entryNumber       ; OrderEntry:entryNumber       ;',
				'; OrderEntryExample:OrderEntry     ; quantity          ; OrderEntry:quantity          ;',
				'; OrderEntryExample:OrderEntry     ; order             ; OrderEntry:order             ; OrderEntryExample:Order   ; ; true',
				'; OrderEntryExample:OrderEntry     ; product           ; OrderEntry:product           ; OrderEntryExample:Product ; ; true',
				'; OrderEntryExample:OrderEntry     ; unit              ; OrderEntry:unit              ; OrderEntryExample:Unit    ;')

		importImpEx(
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
				'; anonymous')

	}

	@Test
	def "can persist without providing nullable integration key properties"() {
		given:
		def body = orderEntryWithoutEntryNumberJson()
		def postContext = ODataFacadeTestUtils.createContext(
				oDataPostRequest(SERVICE_NAME, "OrderEntries", body, Locale.ENGLISH, MediaType.APPLICATION_JSON_VALUE))
		when:
		def postResponse = facade.handlePost(postContext)

		then:
		postResponse.getStatus() == HttpStatusCodes.CREATED
	}


	def orderEntryWithoutEntryNumberJson() {
		JsonBuilder.json()
				.withField("order", orderJson())
				.withField("product", productJson())
				.withField("unit", unitJson())
				.withField("quantity", "25").build()
	}

	def orderJson() {
		JsonBuilder.json().withCode("code1")
				.withField("currency", currencyJson())
				.withField("user", userJson())
				.withField("date", "2019-04-02T08:59:04").build()
	}

	def userJson() {
		JsonBuilder.json()
				.withField("uid", "anonymous").build()
	}

	def currencyJson() {
		JsonBuilder.json()
				.withField("isocode", "EUR").build()
	}

	def productJson() {
		JsonBuilder.json()
				.withCode("test_article1")
				.withField("catalogVersion", catalogVersionJson()).build()
	}

	def catalogVersionJson() {
		JsonBuilder.json()
				.withField("catalog", catalogJson())
				.withField("version", "Staged").build()
	}

	def catalogJson() {
		JsonBuilder.json().withId("Default")
	}

	def unitJson() {
		JsonBuilder.json().withCode("pieces")
				.withField("name", "pieces")
				.withField("unitType", "pieces").build()
	}
}
