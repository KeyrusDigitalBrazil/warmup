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
class ODataExpandEntityIntegrationTest extends ServicelayerTransactionalSpockSpecification {
	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade

	def setup() {
		IntegrationTestUtil.importImpEx(
				'$catalog = Default',
				'$version = Staged',
				'$catalogVersion = $catalog:$version',
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; ExpandTest; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; ExpandTest  ; Product         ; Product',
				'; ExpandTest  ; Catalog         ; Catalog',
				'; ExpandTest  ; CatalogVersion  ; CatalogVersion',
				'; ExpandTest  ; Category        ; Category',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; ExpandTest:Catalog        ; id              ; Catalog:id              ;',
				'; ExpandTest:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; ExpandTest:Catalog',
				'; ExpandTest:CatalogVersion ; version         ; CatalogVersion:version  ;',
				'; ExpandTest:Product        ; code            ; Product:code            ;',
				'; ExpandTest:Product        ; catalogVersion  ; Product:catalogVersion  ; ExpandTest:CatalogVersion',
				'; ExpandTest:Product        ; supercategories ; Product:supercategories ; ExpandTest:Category',
				'; ExpandTest:Category       ; code            ; Category:code           ;',
				'; ExpandTest:Category       ; products        ; Category:products       ; ExpandTest:Product',
				'; ExpandTest:Category       ; catalogVersion  ; Category:catalogVersion ; ExpandTest:CatalogVersion',
				'INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;',
				'; $catalog ; $catalog ; true',
				'INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;',
				'; $catalog ; $version ; true'
		)
	}

	@Test
	def "Expands nested navigation properties"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)',
				'; prod1 ; $catalogVersion '
		)
		def context = oDataContext("Products", ['$expand': 'catalogVersion/catalog'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.results").size() == 1
		json.getString("d.results[0].catalogVersion.version") == "Staged"
		json.getString("d.results[0].catalogVersion.catalog.id") == "Default"
	}

	@Test
	def "Expands multiple navigation properties at a time"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test'
		)
		def context = oDataContext("Products", ['$expand': 'catalogVersion,supercategories'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.results").size() == 1
		json.getString("d.results[0].catalogVersion.version") == "Staged"
		!json.exists("d.results[0].supercategories.results[?(@.code == 'test')].catalogVersion.version")
		json.exists "d.results[0].supercategories.results[?(@.code == 'test')].catalogVersion.__deferred"
	}

	@Test
	def "Expanded property is empty when no referenced items exist"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; someCategory ; $catalogVersion'
		)
		def context = oDataContext("Categories", ['$expand': 'products'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.results").size() == 1
		json.getCollection("d.results[0].products.results").isEmpty()
	}

	@Test
	def "Expand collection navigation property for GET for integration object by integration key"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test',
				'; pr-2 ; $catalogVersion; test')
		def context = oDataContext("Categories('Staged%7CDefault%7Ctest')", ['$expand': 'products'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getString("d.integrationKey") == "Staged|Default|test"
		json.getCollection("d.products.results").size() == 2
	}

	@Test
	def "Expand single navigation property for GET for integration object by integration key"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test')
		def context = oDataContext("Categories('Staged%7CDefault%7Ctest')", ['$expand': 'catalogVersion'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getString("d.integrationKey") == "Staged|Default|test"
		json.getString("d.catalogVersion.version") == "Staged"
	}

	@Test
	def "Expand nested single navigation property for GET for integration object by integration key"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test')
		def context = oDataContext("Categories('Staged%7CDefault%7Ctest')", ['$expand': 'catalogVersion/catalog'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getString("d.integrationKey") == "Staged|Default|test"
		json.getString("d.catalogVersion.version") == "Staged"
		json.getString("d.catalogVersion.catalog.id") == "Default"
	}

	@Test
	def "Expand nested collection navigation property for GET for integration object by integration key"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test')
		def context = oDataContext("Categories('Staged%7CDefault%7Ctest')", ['$expand': 'products/catalogVersion'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getString("d.integrationKey") == "Staged|Default|test"
		json.getCollection("d.products.results").size() == 1
		json.getString("d.products.results[0].catalogVersion.version") == "Staged"
	}

	@Test
	def "Recursive access to properties for GET for integration object by integration key"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test')
		def context = oDataContext("Categories('Staged%7CDefault%7Ctest')", ['$expand': 'products/supercategories'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getString("d.integrationKey") == "Staged|Default|test"
		json.getCollection("d.products.results").size() == 1
		json.getCollection("d.products.results[0].supercategories.results").isEmpty()
	}

	@Test
	def "Recursive access to properties for GET for multiple Integration Objects"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
				'; test ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
				'; pr-1 ; $catalogVersion; test')
		def context = oDataContext("Categories", ['$expand': 'products/supercategories'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.results").size() == 1
		json.getCollection("d.results[0].products.results").size() == 1
		json.getCollection("d.results[0].products.results[0].supercategories.results").isEmpty()
	}

	ODataContext oDataContext(String entitySetName, Map params) {
		def request = ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withParameters(params)
				.withPathInfo(PathInfoBuilder.pathInfo()
				.withServiceName("ExpandTest")
				.withEntitySet(entitySetName))
				.build()

		contextGenerator.generate request
	}
}
