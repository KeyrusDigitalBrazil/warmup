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
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.TestConstants
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.ODataSchema
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataIntegrationServiceIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	def SERVICE_NAME = "IntegrationService"

	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade
	@Resource(name = "oDataSchemaGenerator")
	private SchemaGenerator generator

	def setup() {
		importCsv("/impex/essentialdata-integrationservices.impex", "UTF-8")

		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; UnitOne; INBOUND',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; UnitOne  ; Unit         ; Unit',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; UnitOne:Unit        ; code              ; Unit:code  	;',
				'; UnitOne:Unit        ; name              ; Unit:name  	;',
				'; UnitOne:Unit        ; unitType          ; Unit:unitType  ;')
	}

	@Test
	def "GET IntegrationObjects returns all integration objects"() {
		given:
		def context = oDataGetContext("IntegrationObjects")

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 2
		json.getCollectionOfObjects('d.results[*].code').containsAll('IntegrationService', 'UnitOne')
	}

	@Test
	def "GET IntegrationObjectItems with no parameters returns all integration object items"() {
		given:
		def context = oDataGetContext("IntegrationObjectItems")

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 7
		json.getCollectionOfObjects('d.results[*].code').contains('Unit')
	}

	@Test
	def "GET IntegrationObjectItemAttributes returns all integration objects"() {
		given:
		def params = ['\$top': '10', '\$skip': '10']
		def context = oDataGetContext("IntegrationObjectItemAttributes", params)

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 10
		json.getCollectionOfObjects('d.results[*].attributeName').containsAll('code', 'name', 'unitType')
	}

	@Test
	def "POST IntegrationObjects, IntegrationObjectItem and IntegrationObjectItemAttribute creates new service"() {
		given:
		categoryIntegrationObjectIsCreated("CategoryOne")

		when:
		def schema = new ODataSchema(generator.generateSchema(getIntegrationObjectItemModelDefinitions()))

		then:
		with(schema)
				{
					getEntityTypeNames().containsAll("Category", TestConstants.LOCALIZED_ENTITY_PREFIX + "Category")
					getEntityType("Category").getPropertyNames().containsAll("code", "name")

					getEntityType("Category").getAnnotatableProperty("code").getAnnotationNames() == ["s:IsUnique", "Nullable"]
					getEntityType("Category").getAnnotatableProperty("name").getAnnotationNames() == ["s:IsLanguageDependent", "Nullable"]
					getEntityType("Category").getNavigationPropertyNames() == ["localizedAttributes"]
				}

	}

	@Test
	def "POST multiple IntegrationObjects including duplicated Attribute name/descriptor creates different Integration Objects"() {
		given:
		categoryIntegrationObjectIsCreated("CategoryOne")
		categoryIntegrationObjectIsCreated("CategoryTwo")

		when:
		ODataResponse response = facade.handleGetEntity(oDataGetContext("IntegrationObjects"))

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects('d.results[*].code').containsAll('CategoryOne', 'CategoryTwo')

	}

	def categoryIntegrationObjectIsCreated(String integrationObjectCode) {
		def categoryOneIntegrationObjectContext = oDataPostContext("IntegrationObjects",
				categoryIntegrationObjectBody(integrationObjectCode))
		facade.handlePost(categoryOneIntegrationObjectContext)
	}

	ODataContext oDataGetContext(String entitySetName) {
		oDataGetContext(entitySetName, [:])
	}

	ODataContext oDataGetContext(String entitySetName, Map params) {
		def request = ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withPathInfo(PathInfoBuilder.pathInfo()
				.withServiceName(SERVICE_NAME)
				.withEntitySet(entitySetName))
				.withParameters(params)
				.build()

		contextGenerator.generate request
	}

	ODataContext oDataPostContext(String entitySetName, String content) {
		def request = ODataFacadeTestUtils
				.oDataPostRequest(SERVICE_NAME, entitySetName, content, APPLICATION_JSON_VALUE)

		contextGenerator.generate request
	}

	def categoryIntegrationObjectBody(String integrationObjectCode) {
		json()
				.withCode(integrationObjectCode)
				.withField("integrationType", json().withCode("INBOUND"))
				.withFieldValues("items", categoryIntegrationObjectItemBody(integrationObjectCode)).build()
	}

	def categoryIntegrationObjectItemBody(String integrationObjectCode) {
		json()
				.withCode("Category")
				.withField("type", json().withCode("Category"))
				.withField("integrationObject", json().withCode(integrationObjectCode))
				.withFieldValues("attributes",
				categoryCodeAttribute(integrationObjectCode),
				categoryNameAttribute(integrationObjectCode)).build()
	}

	def categoryCodeAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "code")
				.withField("attributeDescriptor", attributeDescriptor("code", "Category"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode)).build()
	}

	def categoryNameAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "name")
				.withField("attributeDescriptor", attributeDescriptor("name", "Category"))
				.withField("unique", false)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode)).build()
	}

	private JsonBuilder attributeIntegrationObjectItem(String integrationObjectCode) {
		json().withCode("Category")
				.withField("type", json().withCode("Category"))
				.withField("integrationObject", json().withCode(integrationObjectCode))
	}

	private String attributeDescriptor(String attributeName, String enclosingType) {
		json().withField("qualifier", attributeName)
				.withField("enclosingType", json().withCode(enclosingType)).build()
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

	def getIntegrationObjectItemModelDefinitions() {
		IntegrationTestUtil.findAll(IntegrationObjectItemModel.class)
	}
}
