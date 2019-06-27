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
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

import javax.annotation.Resource

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataServiceDocumentIntegrationTest extends ServicelayerTransactionalSpockSpecification {
	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = 'defaultODataFacade')
	private ODataFacade facade

	def setup() {
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; ServiceDocumentTest ; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; ServiceDocumentTest ; Catalog         ; Catalog',
				'; ServiceDocumentTest ; CatalogVersion  ; CatalogVersion',
				'; ServiceDocumentTest ; Category        ; Category',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; ServiceDocumentTest:Catalog        ; id              ; Catalog:id              ;',
				'; ServiceDocumentTest:Category       ; code            ; Category:code           ;',
				'; ServiceDocumentTest:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; ServiceDocumentTest:Catalog',
				'; ServiceDocumentTest:CatalogVersion ; version         ; CatalogVersion:version  ;')
	}
	
	@Test
	def "exposes integration object items for existing integration object"() {
		when:
		ODataResponse response = facade.handleGetEntity(oDataContext('ServiceDocumentTest'))

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection('d.EntitySets').size() == 3
		json.getCollectionOfObjects('d.EntitySets[*]').containsAll('CatalogVersions', 'Catalogs', 'Categories')
	}

	@Test
	def "returns empty collection for integration object with no items"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; OtherIntegrationObj ; INBOUND')

		when:
		ODataResponse response = facade.handleGetEntity(oDataContext('OtherIntegrationObj'))

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.EntitySets").size() == 0
	}

	ODataContext oDataContext(final String integrationObj) {
		def request = ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withPathInfo(PathInfoBuilder.pathInfo()
				.withServiceName(integrationObj))
				.build()

		contextGenerator.generate request
	}
}
