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

package de.hybris.platform.odata2webservices.odata;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.integrationservices.util.JsonBuilder.json;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.assertBadRequestWithErrorCode;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.handleRequest;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.productModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.inboundservices.util.InboundMonitoringRule;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for entity persistence feature scenarios.
 */
@IntegrationTest
public class ODataPersistenceFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String ENTITYSET = "Products";
	private static final String PRODUCT_CODE = "Test";
	private static final String SERVICE_NAME = "MyProduct";
	private static final String PRODUCT_NAME_ENGLISH = "EnglishProduct";
	private static final String PRODUCT_NAME_GERMAN = "GermanProduct";
	private static final String CATALOG_ID = "Default";
	private static final String CATALOG_INVALID_ID = "Invalid";
	private static final String CATALOG_VERSION = "Staged";
	private static final String INTEGRATION_KEY = CATALOG_VERSION + "|" + CATALOG_ID + "|" + PRODUCT_CODE;
	private static final ProductModel productModel = productModel(PRODUCT_CODE);

	@Rule
	public InboundMonitoringRule inboundMonitoring = InboundMonitoringRule.disabled();
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		// create product metadata
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)",
				"; MyProduct ; INBOUND",
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct ; Product        ; Product",
				"; MyProduct ; Catalog        ; Catalog",
				"; MyProduct ; CatalogVersion ; CatalogVersion",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Product        ; code           ; Product:code           ;",
				"; MyProduct:Product        ; catalogVersion ; Product:catalogVersion ; MyProduct:CatalogVersion",
				"; MyProduct:Product        ; name           ; Product:name           ;",
				"; MyProduct:Product        ; description    ; Product:description    ;",
				"; MyProduct:Catalog        ; id             ; Catalog:id             ;",
				"; MyProduct:CatalogVersion ; catalog        ; CatalogVersion:catalog ; MyProduct:Catalog",
				"; MyProduct:CatalogVersion ; version        ; CatalogVersion:version ;",
				"INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;",
				"; Default ; Default ; true",
				"INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;",
				"; Default ; Staged ; true");
	}

	@Test
	public void testPersistEntityWithInvalidNavProperty()
	{
		final String content = json()
				.withCode(PRODUCT_CODE)
				.withField("name", PRODUCT_NAME_ENGLISH)
				.withField("catalogVersion", json()
						.withField("version", "Staged")
						.withField("catalog", json().withId(CATALOG_INVALID_ID)))
				.build();

		final ODataResponse response = handleRequest(facade, Locale.ENGLISH, content);

		assertBadRequestWithErrorCode("missing_nav_property", response);
		assertModelDoesNotExist(productModel);
	}

	@Test
	public void testPersistEntity()
	{
		assertModelDoesNotExist(productModel);

		final String content = product().withField("name", PRODUCT_NAME_ENGLISH).build();
		final ODataResponse response = handleRequest(facade, Locale.ENGLISH, content);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.name", PRODUCT_NAME_ENGLISH);

		final ProductModel persistedModel = assertModelExists(productModel);
		assertThat(persistedModel.getName(Locale.ENGLISH)).isEqualTo(PRODUCT_NAME_ENGLISH);
	}

	@Test
	public void testPersistEntityWithIntegrationKey()
	{
		assertModelDoesNotExist(productModel);

		final String content = 	product().withField("integrationKey", INTEGRATION_KEY).build();
		final ODataResponse response = handleRequest(facade, oDataPostRequest(SERVICE_NAME, ENTITYSET, content, APPLICATION_JSON_VALUE));

		ODataAssertions.assertThat(response).hasStatus(HttpStatusCodes.CREATED);
		assertModelExists(productModel);
	}

	@Test
	public void testUpdateEntity() throws ImpExException
	{
		final String productName = "Name From Request";
		final String productDesc = "Stored in the database";
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Product; code[unique = true]; description",
				"; " + PRODUCT_CODE + "; " + productDesc);

		final ODataRequest request = postRequest()
				.withBody(product().withField("name", productName))
				.build();
		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.name", productName)
				.hasPathWithValue("d.description", productDesc);

		final ProductModel persistedModel = assertModelExists(productModel);
		assertThat(persistedModel.getName()).isEqualTo(productName);
		assertThat(persistedModel.getDescription()).isEqualTo(productDesc);
	}

	@Test
	public void testUpdateEntityWithIntegrationKey() throws ImpExException
	{
		final String updatedName = "New Name";
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Product; code[unique = true]; name; description",
				"; " + PRODUCT_CODE + "; Old Name; product description");

		final ODataRequest request = postRequest()
				.withBody(product()
						.withField("integrationKey", INTEGRATION_KEY)
						.withField("name", updatedName))
				.build();
		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.name", updatedName)
				.hasPathWithValue("d.description", "product description");

		final ProductModel persistedModel = assertModelExists(productModel);
		assertThat(persistedModel.getName()).isEqualTo(updatedName);
	}

	@Test
	public void testInsertPrimitiveTypeNotPossible() throws  ImpExException
	{
		IntegrationTestUtil.importImpEx("INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; PrimitiveTypesIntegrationObject",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; PrimitiveTypesIntegrationObject ; Order      ; Order",
				"; PrimitiveTypesIntegrationObject ; OrderEntry ; OrderEntry",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; PrimitiveTypesIntegrationObject:Order      ; code              ; Order:code                   ;",

				"; PrimitiveTypesIntegrationObject:OrderEntry ; order             ; OrderEntry:order             ; PrimitiveTypesIntegrationObject:Order",
				"; PrimitiveTypesIntegrationObject:OrderEntry ; entryGroupNumbers ; OrderEntry:entryGroupNumbers ;");
		final ODataRequest request = postRequestBuilder("PrimitiveTypesIntegrationObject","Integer", APPLICATION_JSON_VALUE)
				.withBody(json().withField("value", 3))
				.build();

		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response).hasStatus(HttpStatusCodes.NOT_FOUND);
	}

	@Test
	public void testPersistEntityWhenAcceptLanguageIsDifferentFromContentLanguage() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Product; code[unique = true]; name[lang = 'en']",
				"; " + PRODUCT_CODE + "; " + PRODUCT_NAME_ENGLISH);

		final ODataRequest request = postRequest()
				.withContentLanguage(Locale.GERMAN)
				.withAcceptLanguage(Locale.ENGLISH)
				.withBody(product().withField("name", PRODUCT_NAME_GERMAN))
				.build();
		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response).jsonBody()
				.hasPathWithValue("d.name", PRODUCT_NAME_ENGLISH);
	}

	@Test
	public void testPersistEntityWhithContentLanguageOnly() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Product; code[unique = true]; name[lang = 'en']",
				"; " + PRODUCT_CODE + "; " + PRODUCT_NAME_ENGLISH);

		final ODataRequest request = postRequest()
				.withContentLanguage(Locale.GERMAN)
				.withBody(product().withField("name", PRODUCT_NAME_GERMAN))
				.build();
		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response).jsonBody()
				.hasPathWithValue("d.name", PRODUCT_NAME_GERMAN);
	}

	@Test
	public void testPersistEntityWithAcceptLanguageOnly() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Product; code[unique = true]; name[lang = 'en']",
				"; " + PRODUCT_CODE + "; " + PRODUCT_NAME_ENGLISH);

		final ODataRequest request = postRequest()
				.withAcceptLanguage(Locale.ENGLISH)
				.withBody(product().withField("name", PRODUCT_NAME_GERMAN))
				.build();
		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response).jsonBody()
				.hasPathWithValue("d.name", PRODUCT_NAME_ENGLISH);
	}

	@Test
	public void testUnsupportedContentLanguageHeaderValuePreventsPersistence()
	{
		final Locale unsupportedLocale = Locale.KOREA;
		final ODataRequest request = postRequest()
				.withContentLanguage(unsupportedLocale)
				.withBody(product().withField("name", "Name in Korean"))
				.build();

		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue("error.code", "invalid_language")
				.hasPathWithValueContaining("error.message", unsupportedLocale.getLanguage());
		assertModelDoesNotExist(productModel);
	}

	@Test
	public void testUnsupportedAcceptLanguageHeaderValuePreventsPersistence()
	{
		final Locale unsupportedLocale = new Locale("ru");
		final ODataRequest request = postRequest()
				.withAcceptLanguage(unsupportedLocale)
				.withBody(product().withField("name", "Name in Russian"))
				.build();

		final ODataResponse response = handleRequest(facade, request);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue("error.code", "invalid_language")
				.hasPathWithValueContaining("error.message", unsupportedLocale.getLanguage());
		assertModelDoesNotExist(productModel);
	}

	@Test
	public void testCyclicReferencePersistence() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; InboundCategory",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; InboundCategory ; Category    ; Category",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; InboundCategory:Category      ; code              ; Category:code              ;",
				"; InboundCategory:Category      ; supercategories   ; Category:supercategories   ; InboundCategory:Category",
				"; InboundCategory:Category      ; categories        ; Category:categories        ; InboundCategory:Category",

				"INSERT_UPDATE Category; code[unique = true];",
				"; test_category1");


		final ODataRequest request = postRequestBuilder("InboundCategory", "Categories", APPLICATION_JSON_VALUE)
				.withBody(json()
						.withCode("test_category2")
						.withFieldValues("supercategories", json().withCode("test_category1"))
						.build())
				.build();

		final ODataResponse response = handleRequest(facade, request);
		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.code", "test_category2")
				.hasPathWithValueContaining("d.supercategories", "__deferred")
				.hasPathWithValueContaining("d.categories", "__deferred");

		final CategoryModel category1 = getPersistedCategory("test_category1");
		final CategoryModel category2 = getPersistedCategory("test_category2");

		assertThat(category2.getSupercategories()).containsExactly(category1);
		assertThat(category1.getCategories()).containsExactly(category2);
	}

	private CategoryModel getPersistedCategory(final String code)
	{
		final CategoryModel category = new CategoryModel();
		category.setCode(code);
		return assertModelExists(category);
	}

	private JsonBuilder product()
	{
		return json()
				.withCode(PRODUCT_CODE)
				.withField("catalogVersion", json()
						.withField("version", CATALOG_VERSION)
						.withField("catalog", json().withId(CATALOG_ID)));
	}

	private ODataRequestBuilder postRequest()
	{
		return postRequestBuilder(SERVICE_NAME, ENTITYSET, APPLICATION_JSON_VALUE);
	}
}
