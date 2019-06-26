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
import spock.lang.Unroll

import javax.annotation.Resource

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
/**
 * WARNING: Don't use GString syntax (e.g. "$str1 $str2") to concatenate Strings in tests,
 * you will get an internal server error. Use + instead (e.g. str1 + str2).
 */
@IntegrationTest
class ODataFilterExpressionIntegrationTest extends ServicelayerTransactionalSpockSpecification {
	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade

	def setup() {
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; FilterTest; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; FilterTest  ; Product         ; Product',
				'; FilterTest  ; Catalog         ; Catalog',
				'; FilterTest  ; CatalogVersion  ; CatalogVersion',
				'; FilterTest  ; Category        ; Category',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; FilterTest:Catalog        ; id              ; Catalog:id              ;',
				'; FilterTest:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; FilterTest:Catalog',
				'; FilterTest:CatalogVersion ; version         ; CatalogVersion:version  ;',
				'; FilterTest:Product        ; code            ; Product:code            ;',
				'; FilterTest:Product        ; name            ; Product:name            ;',
				'; FilterTest:Product        ; catalogVersion  ; Product:catalogVersion  ; FilterTest:CatalogVersion',
				'; FilterTest:Product        ; supercategories ; Product:supercategories ; FilterTest:Category',
				'; FilterTest:Category       ; code            ; Category:code           ;',
				'; FilterTest:Category       ; products        ; Category:products       ; FilterTest:Product',
				'INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;',
				'; Default ; Default ; true',
				'INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;',
				'; Default ; Staged ; true',
				'; Default ; Online ; true')
	}

	@Test
	def "can filter by navigation property integrationKey"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$stagedVersion = Default:Staged',
				'$onlineVersion = Default:Online',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)',
				'; prod1 ; $stagedVersion ',
				'; prod2 ; $onlineVersion ',
				'; prod3 ; $onlineVersion ')
		def context = oDataContext(['$filter': "catalogVersion/integrationKey eq 'Staged|Default'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollection("d.results").size() == 1
		json.getString("d.results[0].code") == "prod1"
	}

	@Test
	def "can filter by localized property"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Language; isocode[unique = true]',
				'; de',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id)[unique = true], version); name[lang=en]',
				'; prod1 ; Default:Staged ; Product One',
				'; prod2 ; Default:Staged ; Product Two',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]; name[lang=de]',
				'; prod1 ; Default:Staged ; Produkt Eins')
		def context = oDataContext(['$filter': "name eq 'Product One'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollection("d.results").size() == 1
		json.getString("d.results[0].name") == 'Product One'
	}

	@Test
	def "can filter by simple property"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]',
				'; prod1 ; Default:Staged ',
				'; prod2 ; Default:Online ',
				'; prod2 ; Default:Staged ')
		def context = oDataContext(['$filter': "code eq 'prod2'", '$expand': 'catalogVersion'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results[*].code") == ["prod2", "prod2"]
		json.getCollectionOfObjects("d.results[*].catalogVersion.version").containsAll(["Online", "Staged"])
	}

	@Test
	def "can filter by nested key entity property"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]',
				'; prod1 ; Default:Staged',
				'; prod2 ; Default:Online')
		def context = oDataContext(['$filter': "catalogVersion/version eq 'Staged'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results[*].code") == ['prod1']
	}

	@Test
	@Unroll
	def "filter expression can contain '#operator' operator"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]',
				'; prod1 ; Default:Staged ',
				'; prod1 ; Default:Online ',
				'; prod2 ; Default:Staged ')
		def context = oDataContext(['$filter': "catalogVersion/integrationKey eq 'Staged|Default' " + operator + " code eq 'prod1'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollection("d.results").size() == result.size()
		json.getCollectionOfObjects("d.results[*].integrationKey").containsAll(result)

		where:
		operator | result
		'and'    | ['Staged|Default|prod1']
		'or'     | ['Staged|Default|prod1', 'Online|Default|prod1', 'Staged|Default|prod2']
	}

	@Test
	def "empty results returned when filter is not satisfied"() {
		given:
		def context = oDataContext(['$filter': "code eq 'some_product'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollection("d.results").isEmpty()
	}

	@Test
	def "filter is ignored when used with get an entity by ID"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]',
				'; pr ; Default:Staged')
		def context = oDataContext('Staged|Default|pr', ['$filter': "code eq 'prod1'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getString("d.code") == 'pr'
	}

	@Test
	@Unroll
	def "reports error when attempting to filter on non-existing property: #filterExpr"() {
		given:
		def context = oDataContext(['$filter': filterExpr])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractErrorFrom response
		! json.exists("error.code")
		json.getString("error.message.value").contains(filterExpr)

		where:
		filterExpr << ["catalogVersion-integrationKey eq 'Staged|Default'",
					   "code eq 'prod1' and catalogVersion-integrationKey eq 'Staged|Default'"]
	}

	@Test
	@Unroll
	def "reports error when filter [#left #operator #right] contains unsupported comparison operator '#operator'"() {
		given:
		def context = oDataContext(['$filter': left + " " + operator + " " + right])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractErrorFrom response
		json.getString("error.code") == "operator_not_supported"
		json.getString("error.message.value").contains("[$operator]")

		where:
		left                                 | operator | right
		"code"                               | "ne"     | "'prod1'"
		"catalogVersion/integrationKey"      | "gt"     | "'Staged|Default'"
		"catalogVersion/version"             | "lt"     | "'Staged'"
	}

	@Test
	def "filtering by integration key is not supported"() {
		given:
		def context = oDataContext(['$filter': "integrationKey eq 'Staged|Default|prod'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractErrorFrom response
		json.getString("error.code") == "integration_key_not_supported"
		json.getString("error.message.value").contains("integration key is not supported")
	}

	@Test
	def "filtering by nested entity properties is not supported"() {
		given:
		def context = oDataContext(['$filter': "catalogVersion/catalog/id eq 'Default'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractErrorFrom response
		json.getString("error.code") == "filter_not_supported"
		json.getString("error.message.value").contains("catalogVersion/catalog")
	}

	@Test
	@Unroll
	def "can filter on property with many-to-many relationship by #property"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Category; code[unique = true]',
				'; cat1',
				'; cat2',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]; supercategories(code)',
				'; prod1 ; Default:Staged ; cat1',
				'; prod2 ; Default:Staged ; cat1, cat2')
		def context = oDataContext(['$filter': "supercategories/" + property + " eq 'cat2'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 1
		json.getCollectionOfObjects("d.results[*].code") == ["prod2"]

		where:
		property << ["integrationKey", "code"]
	}

	@Test
	def "can filter on property with many-to-many relationship, one-to-many relationship, and simple property"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Category; code[unique = true]',
				'; cat1',
				'; cat2',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]; supercategories(code)',
				'; prod1 ; Default:Staged ; cat1',
				'; prod2 ; Default:Staged ; cat2')
		def context = oDataContext(['$filter': "catalogVersion/integrationKey eq 'Staged|Default' and code eq 'prod2' or supercategories/integrationKey eq 'cat1'"])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 2
		json.getCollectionOfObjects("d.results[*].code") == ["prod1", "prod2"]
	}

	@Test
	@Unroll
	def "can filter reverse direction many-to-many relationship by #property"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE Category; code[unique = true]',
				'; cat1',
				'; cat2',
				'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]; supercategories(code)',
				'; prod1 ; Default:Staged ; cat1',
				'; prod2 ; Default:Staged ; cat1, cat2')
		def context = oDataContext(['$filter': "products/" + property + " eq '" + value + "'"],  'Categories')

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 1
		json.getCollectionOfObjects("d.results[*].code") == ["cat1"]

		where:
		property         | value
		"integrationKey" | "Staged|Default|prod1"
		"code"           | "prod1"
	}

	ODataContext oDataContext(Map params, String entitySetName) {
		def request = requestBuilder(params, entitySetName).build()
		contextGenerator.generate request
	}

	ODataContext oDataContext(Map params) {
		def request = requestBuilder(params, 'Products').build()
		contextGenerator.generate request
	}

	ODataContext oDataContext(String key, Map params) {
		def request = requestBuilder(params, 'Products', key).build()
		contextGenerator.generate request
	}

	def requestBuilder(Map params, String entitySetName, String... keys) {
		ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withParameters(params)
				.withPathInfo(pathInfo(entitySetName, keys))
	}

	def pathInfo(String entitySetName, String... keys) {
		def builder = PathInfoBuilder.pathInfo()
				.withServiceName("FilterTest")
				.withEntitySet(entitySetName)
		keys != null && keys.length > 0	? builder.withEntityKeys(keys) : builder
	}

	def extractEntitiesFrom(ODataResponse response) {
		extractBodyWithExpectedStatus(response, HttpStatusCodes.OK)
	}

	def extractErrorFrom(ODataResponse response) {
		extractBodyWithExpectedStatus(response, HttpStatusCodes.BAD_REQUEST)
	}

	def extractBodyWithExpectedStatus(ODataResponse response, HttpStatusCodes expStatus) {
		assert response.getStatus() == expStatus
		JsonObject.createFrom response.getEntity()
	}
}
