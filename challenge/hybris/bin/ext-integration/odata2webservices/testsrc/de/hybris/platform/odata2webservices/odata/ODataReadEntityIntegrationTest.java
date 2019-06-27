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

import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.ERROR_CODE;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.PRODUCTS_ENTITYSET;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataGetRequest;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.odata2services.odata.ODataContextGenerator;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ODataReadEntityIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CATEGORY_ENTITY_SET = "Categories";
	private static final String TRIGGER_ENTITY_SET = "Triggers";
	private static final String SERVICE_GROUP1 = "GetIntegrationTestGroup1";
	private static final String SERVICE_GROUP2 = "GetIntegrationTestGroup2";

	private static final String PRODUCT_CODE_ENCODED = "testProduct001%7Cwith%7Cpipes";
	private static final String PRODUCT_CODE = "testProduct001|with|pipes";
	private static final String PRODUCT_INTEGRATION_KEY = "Staged|Default|" + PRODUCT_CODE_ENCODED;

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private ODataContextGenerator oDataContextGenerator;
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		importCsv("/test/get-entity-integrationtest-odata2webservices.impex", "UTF-8");
	}

	@Test
	public void testGetEntity_shouldReturnProduct_whenExists()
	{
		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, PRODUCT_INTEGRATION_KEY, Locale.FRANCE);
		final ODataContext context = oDataContext(oDataRequest);

		final ODataResponse oDataResponse = facade.handleGetEntity(context);

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.code", PRODUCT_CODE)
				.hasPathWithValue("d.name", "fr name for testProduct001")
				.hasPathWithValue("d.integrationKey", PRODUCT_INTEGRATION_KEY);
	}

	@Test
	public void testGetEntity_withNavigationSegment_shouldReturnCatalogVersion_whenExists()
	{
		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, Locale.FRANCE, "catalogVersion", PRODUCT_INTEGRATION_KEY);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.version", "Staged")
				.hasPathWithValue("d.integrationKey", "Staged|Default");

		final ODataRequest oDataRequest2 = oDataGetRequest(SERVICE_GROUP1, "CatalogVersions", Locale.FRANCE, "catalog", "Staged|Default");
		final ODataResponse oDataResponse2 = facade.handleGetEntity(oDataContext(oDataRequest2));

		ODataAssertions.assertThat(oDataResponse2)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.id", "Default")
				.hasPathWithValue("d.integrationKey", "Default");
	}

	@Test
	public void testGetEntity_withNavigationSegment_shouldReturnError_whenPropertyDoesNotExist()
	{
		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, Locale.FRANCE, "unit", PRODUCT_INTEGRATION_KEY);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.NOT_FOUND)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "not_found");
	}

	@Test
	public void testGetEntity_shouldReturnCategory_whenExists()
	{
		final String testModelCode = "testCategory|with|pipes";
		final String testModelCodeEncoded = "testCategory%7Cwith%7Cpipes";

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, CATEGORY_ENTITY_SET, testModelCodeEncoded, Locale.ENGLISH);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.code", testModelCode)
				.hasPathWithValue("d.name", "en name for testCategory")
				.hasPathWithValue("d.integrationKey", testModelCodeEncoded);
	}

	@Test
	public void testGetEntity_shouldReturnTrigger_whenExistsAndHasOnlyNavigationPropertyKey()
	{
		final String integrationKey = "A-Test-ImpExImportCronJob";

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, TRIGGER_ENTITY_SET, integrationKey, Locale.ENGLISH);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.integrationKey", integrationKey);
	}

	@Test
	public void testGetEntity_shouldReturnProduct_WhenHasTwoNavigationPropertyKey()
	{
		final String testModelCode = PRODUCT_CODE_ENCODED;
		final String integrationKey = "Staged|Default|" + testModelCode + "|pieces";

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP2, PRODUCTS_ENTITYSET, integrationKey, Locale.FRANCE);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.hasPathWithValue("d.code", PRODUCT_CODE)
				.hasPathWithValue("d.name", "fr name for testProduct001")
				.hasPathWithValue("d.integrationKey", integrationKey);
	}

	@Test
	public void testGetEntity_shouldReturnInvalidKey()
	{
		final String integrationKey = "''";

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, integrationKey, Locale.FRANCE);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "invalid_key");
	}

	@Test
	public void testGetEntity_shouldReturnInvalidKey_whenKeyMalformed()
	{
		final String integrationKey = "Staged||Default|" + PRODUCT_CODE_ENCODED;

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, integrationKey, Locale.FRANCE);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "invalid_key");
	}

	@Test
	public void testGetEntity_shouldReturnInvalidKey_whenKeyEmpty()
	{
		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, "", Locale.FRANCE);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "invalid_key");
	}

	@Test
	public void testGetEntity_shouldReturnInvalidKey_whenMoreThenOneKey()
	{
		final String keyPredicate = "integrationKey='abc',keyTwo='def'";

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, Locale.FRANCE, "", "integrationKey=abc", "keyTwo=def");
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue("error.message.value", "Invalid key predicate: '" + keyPredicate + "'.");
	}

	@Test
	public void testGetEntity_shouldNotFound_whenIntegrationObjectNotExists()
	{
		final String testModelCode = "testProduct001_DOES_NOT_EXIST";
		final String integrationKey = "Staged|Default|" + testModelCode;

		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, integrationKey, Locale.FRANCE);
		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContext(oDataRequest));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.NOT_FOUND)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "not_found");
	}

	@Test
	public void testRoundTripForProduct() throws UnsupportedEncodingException
	{
		final String productCode = "testProduct201|with|pipes";
		final String productCodeEncoded = URLEncoder.encode(productCode, "UTF-8");
		final String name = "myTest";
		final String categoryCode = "testCategory|with|pipes";
		final String expectedIntegrationKey = "Staged|Default|" + productCodeEncoded;

		final String productContent = getProduct(productCode, name, categoryCode);

		final ODataRequest postRequest = oDataPostRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, productContent, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataPostResponse = facade.handlePost(oDataContext(postRequest));

		ODataAssertions.assertThat(oDataPostResponse)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.code", productCode)
				.hasPathWithValue("d.integrationKey", expectedIntegrationKey);

		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		final ProductModel persistedProductModel = flexibleSearchService.getModelByExample(productModel);
		assertThat(persistedProductModel.getName(Locale.ENGLISH)).isEqualTo(name);

		final ODataRequest getRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, expectedIntegrationKey, Locale.ENGLISH);
		final ODataResponse oDataGetResponse = facade.handleGetEntity(oDataContext(getRequest));

		ODataAssertions.assertThat(oDataGetResponse)
				.isSuccessful()
				.jsonBody()
				.hasPathWithValue("d.code", productCode)
				.hasPathWithValue("d.name", name)
				.hasPathWithValue("d.integrationKey", expectedIntegrationKey);
	}

	@Test
	public void testRoundTripForItemWithEnumTypeAttribute() throws UnsupportedEncodingException
	{

		final String productCode = "testProduct201|with|pipes";
		final String expectedIntegrationKey = "Staged|Default|" + URLEncoder.encode(productCode, "UTF-8");

		final String productContent = productWithEnumTypeAttribute("check");

		final ODataRequest postRequest = oDataPostRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, productContent, Locale.FRANCE, APPLICATION_JSON_VALUE);
		final ODataResponse oDataPostResponse = facade.handlePost(oDataContext(postRequest));

		ODataAssertions.assertThat(oDataPostResponse)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPath("d.approvalStatus");

		final ODataRequest getRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, Locale.FRANCE, "approvalStatus", expectedIntegrationKey);
		final ODataResponse oDataGetResponse = facade.handleGetEntity(oDataContext(getRequest));

		ODataAssertions.assertThat(oDataGetResponse)
				.isSuccessful()
				.jsonBody()
				.hasPathWithValue("d.code", "check")
				.hasPathWithValue("d.integrationKey", "check");
	}

	@Test
	public void testInvalidEnumTypeValueThrowsError()
	{
		final String productContent = productWithEnumTypeAttribute("nonExistantEnumValue");

		final ODataRequest postRequest = oDataPostRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, productContent, Locale.FRANCE, APPLICATION_JSON_VALUE);
		final ODataResponse oDataPostResponse = facade.handlePost(oDataContext(postRequest));

		ODataAssertions.assertThat(oDataPostResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "missing_nav_property");
	}

	private ODataContext oDataContext(final ODataRequest oDataRequest)
	{
		return oDataContextGenerator.generate(oDataRequest);
	}

	private String getProduct(final String code, final String name, final String superCategory)
	{
		return "{"
				+ " \"code\": \"" + code + "\","
				+ " \"name\": \"" + name + "\","
				+ " \"catalogVersion\": {"
				+ "  \"catalog\": {"
				+ "   \"id\": \"Default\""
				+ "  },"
				+ "  \"version\": \"Staged\""
				+ " },"
				+ " \"supercategories\": [{"
				+ "  \"code\": \"" + superCategory + "\""
				+ " }]"
				+ "}";
	}

	private String productWithEnumTypeAttribute(final String code)
	{
		return "{"
				+ " \"code\": \"testProduct201|with|pipes\","
				+ " \"catalogVersion\": {"
				+ "  \"catalog\": {"
				+ "   \"id\": \"Default\""
				+ "  },"
				+ "  \"version\": \"Staged\""
				+ " },"
				+ " \"approvalStatus\": {"
				+ "   \"code\": \"" + code + "\""
				+ "	}"
				+ "}";
	}
}
