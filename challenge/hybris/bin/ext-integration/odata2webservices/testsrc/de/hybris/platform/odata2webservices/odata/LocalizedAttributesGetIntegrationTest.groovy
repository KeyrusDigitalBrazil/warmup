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

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.inboundservices.util.LocalizationRule
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.product.ProductService
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Rule
import org.junit.Test

import javax.annotation.Resource

import static org.apache.commons.lang.StringUtils.EMPTY
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class LocalizedAttributesGetIntegrationTest extends ServicelayerTransactionalSpockSpecification {
	private static final String SERVICE_NAME = "MyProduct"
	private static final String PRODUCTS = "Products"
	private static final String PRODUCT_CODE_ENCODED = "testProduct"
	private static final String PRODUCT_INTEGRATION_KEY = "Staged|Default|" + PRODUCT_CODE_ENCODED
	private static final String LANGUAGE = "language"
	private static final String SOME_RESULTS_PATH = "\$['d']['results'][?]"
	private static final String ALL_RESULTS_PATH = "\$['d']['results'][*]"
	private static final String LOCALIZED_ATTRIBUTES = "localizedAttributes"

	@Resource(name = "defaultODataFacade")
	ODataFacade facade
	@Resource
	ModelService modelService
	@Resource
	ODataContextGenerator oDataContextGenerator
	@Resource
	CommonI18NService commonI18NService
	@Resource
	ProductService productService
	@Rule
	LocalizationRule localizationRule = LocalizationRule.initialize()

	Filter frenchProduct
	Filter englishProduct

	def setup() {
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique=true]; code[unique = true]; type(code)",
				"; MyProduct ; Product        	; Product",
				"; MyProduct ; Catalog        	; Catalog",
				"; MyProduct ; CatalogVersion 	; CatalogVersion",
				"; MyProduct ; Category 		; Category",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Product        ; code               	; Product:code                        	;",
				"; MyProduct:Product        ; name               	; Product:name                        	;",
				"; MyProduct:Product        ; description        	; Product:description                 	;",
				"; MyProduct:Product        ; catalogVersion     	; Product:catalogVersion              	; MyProduct:CatalogVersion",
				"; MyProduct:Catalog        ; id                 	; Catalog:id                          	;",
				'; MyProduct:Category      	; code					; Category:code           ;',
				"; MyProduct:CatalogVersion ; catalog            	; CatalogVersion:catalog              	; MyProduct:Catalog",
				"; MyProduct:CatalogVersion ; version            	; CatalogVersion:version              	;",
				"; MyProduct:CatalogVersion ; categorySystemName 	; CatalogVersion:categorySystemName   	;",
				'; MyProduct:Product        ; supercategories 		; Product:supercategories 				; MyProduct:Category',
				"INSERT_UPDATE Catalog;id[unique=true];name[lang=en];defaultCatalog;",
				";Default;Default;true",
				"INSERT_UPDATE CatalogVersion; catalog(id)[unique=true]; version[unique=true];active;",
				";Default;Staged;true",
				"INSERT_UPDATE Language;isocode[unique=true];name[lang=en]",
				";fr;French",
				"INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]; name[lang = fr]; description[lang = en]; description[lang = fr]",
				"; testProduct ; Default:Staged ; en name for testProduct ; fr name for testProduct ; en description for testProduct ; fr description for testProduct",
		)
		frenchProduct = Filter.filter(
				Criteria.where("name").eq("fr name for testProduct")
						.and("description").eq("fr description for testProduct")
						.and(LANGUAGE).eq("fr")
		)

		englishProduct = Filter.filter(
				Criteria.where("name").eq("en name for testProduct")
						.and("description").eq("en description for testProduct")
						.and(LANGUAGE).eq("en")
		)

	}

	@Test
	def "all values for localizedAttributes returned when requesting item's localizedAttributes navigation segment"() {
		given:
		def context = oDataContext(PRODUCT_INTEGRATION_KEY, LOCALIZED_ATTRIBUTES)

		when:
		def oDataResponse = facade.handleGetEntity(context)

		then:
		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, 2)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, frenchProduct)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, englishProduct)
	}

	@Test
	def "localized properties are returned in the platform's default language"() {
		given:
		localizationRule.setSessionLanguage("fr")

		and:
		def context = oDataContext(PRODUCT_INTEGRATION_KEY)

		when:
		def oDataResponse = facade.handleGetEntity(context)

		then:
		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.name", "fr name for testProduct")
				.hasPathWithValue("d.description", "fr description for testProduct")
	}

	@Test
	def "all languages are provided in the response when \$expand=localizedAttributes"() {
		given:
		def context = oDataContext(PRODUCT_INTEGRATION_KEY, ['\$expand': 'localizedAttributes'])

		when:
		def oDataResponse = facade.handleGetEntity(context)

		then:
		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathContainsMatchingElement("\$['d']['localizedAttributes']['results'][*]", frenchProduct)
				.pathContainsMatchingElement("\$['d']['localizedAttributes']['results'][*]", englishProduct)
	}

	@Test
	def "localized attribute with null value is not returned in response body"() {
		given:
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]; name[lang = fr]; description[lang = en]; description[lang = fr]",
				"; null_fields_prod ; Default:Staged ; en name for testProduct ; ; ; fr description for testProduct",
		)

		and:
		def context = oDataContext('Staged|Default|null_fields_prod', LOCALIZED_ATTRIBUTES)

		when:
		def oDataResponse = facade.handleGetEntity(context)

		then:
		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria
							.where('name').exists(false)
							.and('description').eq('fr description for testProduct')))
				.pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria
							.where('name').eq('en name for testProduct')
							.and("description").exists(false)))

	}

	@Test
	def "localized attribute with empty string will be returned in response body as empty"() {
		given:
		def product = productService.getProductForCode('testProduct')
		// can't set empty string with ImpEx so doing it programmatically
		product.setDescription(EMPTY, Locale.ENGLISH)
		modelService.save(product)

		and:
		def context = oDataContext(PRODUCT_INTEGRATION_KEY, LOCALIZED_ATTRIBUTES)

		when:
		def oDataResponse = facade.handleGetEntity(context)

		then:
		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria
							.where('name').eq('en name for testProduct')
							.and('description').eq(EMPTY)))
				.pathContainsMatchingElement(SOME_RESULTS_PATH, frenchProduct)
	}

	@Test
	def "includes deferred localizedAttributes for item with localizedAttributes"() {
		given:
		IntegrationTestUtil.importImpEx(
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; MyProduct:Product        	; name            ; Product:name            		;',
				'INSERT_UPDATE Product; code[unique = true]; name; catalogVersion(catalog(id), version)',
				'; pr-1 ; enProductName  ; $catalogVersion',
		)
		def context = oDataContext(null)

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.results").size() == 2
		json.exists "d.results[0].localizedAttributes.__deferred"
		json.exists "d.results[1].localizedAttributes.__deferred"
	}

	@Test
	def "Expands nested localizedAttributes. "() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; MyProduct:Product		; name		; Product:name            ;',
				'; MyProduct:Category		; name		; Category:name                   ;',
				'$catalogVersion = Default:Staged',
				'INSERT_UPDATE Category; code[unique = true]; name[lang=en]; catalogVersion(catalog(id), version)',
				'; test ; enCategoryName ; $catalogVersion',
				'INSERT_UPDATE Product; code[unique = true]; name[lang=en]; name[lang=fr]; catalogVersion(catalog(id), version); supercategories(code)',
				'; testProduct ; enProductName ; frProductName ; $catalogVersion; test ',
		)
		def context = oDataContext(null, ['$expand': 'localizedAttributes,supercategories/localizedAttributes'])

		when:
		ODataResponse response = facade.handleGetEntity(context)

		then:
		response.getStatus() == HttpStatusCodes.OK
		def json = JsonObject.createFrom response.getEntity()
		json.getCollection("d.results").size() == 1
		json.getCollection("d.results[0].localizedAttributes.results").size() == 2
		json.getCollection("d.results[0].supercategories.results").size() == 1
		json.getCollection("d.results[0].supercategories.results[0].localizedAttributes.results").size() == 1
		json.getString("d.results[0].supercategories.results[0].localizedAttributes.results[0].language") == "en"
		json.getString("d.results[0].supercategories.results[0].localizedAttributes.results[0].name") == "enCategoryName"
	}

	ODataContext oDataContext(final String integrationKey) {
		oDataContext(integrationKey, "", [:], null)
	}

	ODataContext oDataContext(final String integrationKey, final String navigationSegment) {
		oDataContext(integrationKey, navigationSegment, [:], null)
	}

	ODataContext oDataContext(final String integrationKey, Map params) {
		oDataContext(integrationKey, "", params, null)
	}

	ODataContext oDataContext(String integrationKey, String navigationSegment, Map params, Locale locale) {
		def request = ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withAcceptLanguage(locale)
				.withParameters(params)
				.withPathInfo(PathInfoBuilder.pathInfo()
					.withServiceName(SERVICE_NAME)
					.withEntitySet(PRODUCTS)
					.withEntityKeys(integrationKey)
					.withNavigationSegment(navigationSegment))
				.build()

		oDataContextGenerator.generate request
	}
}
