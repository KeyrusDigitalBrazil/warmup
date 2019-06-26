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

import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.PRODUCTS_ENTITYSET;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createGetEntitySetPathInfo;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataGetRequest;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.odata2services.odata.ODataContextGenerator;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder;
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;

@IntegrationTest
public class ODataReadEntitySetIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String SERVICE_GROUP1 = "GetIntegrationTestGroup1";
	private static final String CATEGORY_CODE1_ENCODED = "testCategory%7Cwith%7Cpipes";
	private static final String PRODUCT_CODE_ENCODED = "testProduct001%7Cwith%7Cpipes";
	private static final String PRODUCT_INTEGRATION_KEY = "Staged|Default|" + PRODUCT_CODE_ENCODED;
	private static final String COUNT_PATH = "d.__count";
	private static final String NEXT_PAGE_LINK_PATH = "d.__next";
	private static final String PRODUCTS_ENTITY_SET_SIZE = "11";
	private static final String INLINECOUNT = "$inlinecount";
	private static final String TOP = "$top";
	private static final String SKIP = "$skip";
	private static final String INTEGRATIONKEY = "integrationKey";
	private static final String CODE = "code";
	private static final String SKIPTOKEN = "$skiptoken";
	private static final String COUNT = "$count";
	private static final int MAX_PAGE_SIZE = 5;
	private static final int DEFAULT_PAGE_SIZE = 2;
	private static final String ALL_RESULTS_PATH = "$['d']['results'][*]";
	private static final String SOME_RESULTS_PATH = "$['d']['results'][?]";
	private static final String ALLPAGES = "allpages";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private ODataContextGenerator oDataContextGenerator;
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;
	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Before
	public void setUp() throws Exception
	{
		configurationService.getConfiguration().setProperty("odata2services.page.size.max", "5");
		configurationService.getConfiguration().setProperty("odata2services.page.size.default", "2");
		createCoreData();
		createDefaultCatalog();

		importCsv("/test/get-entity-integrationtest-odata2webservices.impex", "UTF-8");
	}

	@Test
	public void testGetEntityWhenNoEntitySetWithNameExists()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithEntitySetAndQueryParams("NonExistingEntitySetName", new HashMap<>());

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.NOT_FOUND)
				.jsonBody()
				.hasPathWithValue("$.error.message.value", "Could not find an entity set or function import for 'NonExistingEntitySetName'.");
	}

	@Test
	public void testGetCartsWhenNoCartItemsExist() throws ImpExException
	{
		IntegrationTestUtil.importImpEx("INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; GetIntegrationTestGroup1 ; Cart ; Cart",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; GetIntegrationTestGroup1:Cart ; code ; Cart:code");

		final ODataResponse oDataResponse = getProductsODataResponseWithEntitySetAndQueryParams("Carts", new HashMap<>());

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, 0);
	}

	@Test
	public void testReadEntitySetNoPagingQueryParametersShouldReturnDefaultSizedPagedProductsWithNoCount()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(new HashMap<>());

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, DEFAULT_PAGE_SIZE)
				.doesNotHavePath(COUNT_PATH)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + DEFAULT_PAGE_SIZE);
	}

	@Test
	public void testGetAllProductsWithTop1ReturnsTheFirstProductAndNextLink()
	{
		final int requestedTop = 1;
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(TOP, Integer.toString(requestedTop)));

		final List<String> products = getProductNamesSubList(0, requestedTop);
		final Filter product1Filter = Filter.filter(Criteria.where(CODE).eq(products.get(0)));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product1Filter)
				.pathHasSize(ALL_RESULTS_PATH, requestedTop)
				.doesNotHavePath(COUNT_PATH)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + requestedTop);
	}

	@Test
	public void testGetProductsWithTopExceedingMaxPageSizeReturnsMaxPageSizeItems()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(TOP, "6"));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, MAX_PAGE_SIZE)
				.doesNotHavePath(COUNT_PATH)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + MAX_PAGE_SIZE);
	}

	@Test
	public void testGetAllProductsWithNoneInlineCountAndTop2ReturnsFirst2ProductsAndNextLink()
	{
		final int requestedTop = 2;
		final ODataRequestBuilder oDataRequest = requestBuilder(PRODUCTS_ENTITYSET)
				.withParameter(INLINECOUNT, "none")
				.withParameter(TOP, requestedTop);

		final ODataResponse oDataResponse = handleRequest(oDataRequest);

		final List<String> products = getProductNamesSubList(0, requestedTop);
		final Filter product1Filter = Filter.filter(Criteria.where(CODE).eq(products.get(0)));
		final Filter product2Filter = Filter.filter(Criteria.where(CODE).eq(products.get(1)));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product1Filter)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product2Filter)
				.pathHasSize(ALL_RESULTS_PATH, requestedTop)
				.doesNotHavePath(COUNT_PATH)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + requestedTop);
	}

	@Test
	public void testGetAllProductsWithAllPagesInlineCountAndTop1ReturnsFirstProductWithCountAndNextLink()
	{
		final int requestedTop = 1;
		final ODataRequestBuilder oDataRequest = requestBuilder(PRODUCTS_ENTITYSET)
				.withParameter(INLINECOUNT, ALLPAGES)
				.withParameter(TOP, requestedTop);

		final ODataResponse oDataResponse = handleRequest(oDataRequest);

		final List<String> products = getProductNamesSubList(0, requestedTop);

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria.where(CODE).eq(products.get(0))))
				.pathHasSize(ALL_RESULTS_PATH, requestedTop)
				.hasPathWithValue(COUNT_PATH, PRODUCTS_ENTITY_SET_SIZE)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + requestedTop);
	}

	@Test
	public void testGetProductsWithTop2AndSkip1Returns2ndThrough3rdProductWithNextLink()
	{
		final int requestedTop = 2;
		final int requestedSkip = 1;
		final ODataRequestBuilder oDataRequest = requestBuilder(PRODUCTS_ENTITYSET)
				.withParameter(SKIP, requestedSkip)
				.withParameter(TOP, requestedTop);

		final ODataResponse oDataResponse = handleRequest(oDataRequest);

		final List<String> products = getProductNamesSubList(requestedSkip, requestedSkip + requestedTop);
		final Filter product1Filter = Filter.filter(Criteria.where(CODE).eq(products.get(0)));
		final Filter product2Filter = Filter.filter(Criteria.where(CODE).eq(products.get(1)));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, requestedTop)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product1Filter)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product2Filter)
				.doesNotHavePath(COUNT_PATH)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + (requestedTop + requestedSkip));
	}

	@Test
	public void testGetProductsWithTop1AndSkip2Returns3rdProductWithNextLink()
	{
		final int requestedTop = 1;
		final int requestedSkip = 2;
		final ODataRequestBuilder oDataRequest = requestBuilder(PRODUCTS_ENTITYSET)
				.withParameter(SKIP, requestedSkip)
				.withParameter(TOP, requestedTop);

		final ODataResponse oDataResponse = handleRequest(oDataRequest);

		final List<String> products = getProductNamesSubList(requestedSkip, requestedSkip + requestedTop);
		final Filter product1Filter = Filter.filter(Criteria.where(CODE).eq(products.get(0)));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, requestedTop)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product1Filter)
				.doesNotHavePath(COUNT_PATH)
				.hasPathWithValueContaining(NEXT_PAGE_LINK_PATH, SKIPTOKEN + "=" + (requestedTop + requestedSkip));
	}

	@Test
	public void testGetProductsWithAllPagesInlineCountTop2AndSkip10ReturnsLastPageWith1ProductCountAndNoNextLink()
	{
		final Integer requestedSkip = 10;
		final ODataRequestBuilder oDataRequest = requestBuilder(PRODUCTS_ENTITYSET)
				.withParameter(INLINECOUNT, ALLPAGES)
				.withParameter(SKIP, requestedSkip)
				.withParameter(TOP, 2);

		final ODataResponse oDataResponse = handleRequest(oDataRequest);

		final List<String> products = getProductNamesSubList(requestedSkip, requestedSkip + 1);
		final Filter product1Filter = Filter.filter(Criteria.where(CODE).eq(products.get(0)));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, 1)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, product1Filter)
				.doesNotHavePath(NEXT_PAGE_LINK_PATH)
				.hasPathWithValue(COUNT_PATH, PRODUCTS_ENTITY_SET_SIZE);
	}

	@Test
	public void testGetProductsWithSkipExceedingSizeOfProducts()
	{
		// productsSize = 11
		final ODataRequestBuilder oDataRequest = requestBuilder(PRODUCTS_ENTITYSET)
				.withParameter(INLINECOUNT, ALLPAGES)
				.withParameter(SKIP, 12);

		final ODataResponse oDataResponse = handleRequest(oDataRequest);

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, 0)
				.hasPathWithValue(COUNT_PATH, PRODUCTS_ENTITY_SET_SIZE)
				.doesNotHavePath(NEXT_PAGE_LINK_PATH);
	}

	@Test
	public void testGetProductsCount()
	{
		final ODataRequest oDataRequest = createGetEntitySetPathInfo(SERVICE_GROUP1, PRODUCTS_ENTITYSET, new HashMap<>(), Locale.ENGLISH, COUNT);
		final ODataContext context = oDataContextGenerator.generate(oDataRequest);
		final ODataResponse oDataResponse = facade.handleGetEntity(context);

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.hasBody(PRODUCTS_ENTITY_SET_SIZE);
	}

	@Test
	public void testGetProductsWithInvalidTop()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(TOP, "invalidTop"));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testGetProductsWithInvalidInlineCount()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(INLINECOUNT, "invalidInlineCount"));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testGetProductsWithInvalidSkip()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(SKIP, "invalidSkip"));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testGetProductsWithInvalidCount()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(COUNT, "invalidValue"));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testGetProductsWithInvalidCountAndAnotherValidQueryParamIsInvalid()
	{
		final ODataResponse oDataResponse = getProductsODataResponseWithQueryParams(ImmutableMap.of(COUNT, "invalidValue", TOP, "1"));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testGetNavigationSegmentItemsShouldReturnItemsInCollectionAttribute()
	{
		final ODataRequest oDataRequest = oDataGetRequest(SERVICE_GROUP1, PRODUCTS_ENTITYSET, Locale.FRANCE, "supercategories", PRODUCT_INTEGRATION_KEY);

		final ODataResponse oDataResponse = facade.handleGetEntity(oDataContextGenerator.generate(oDataRequest));

		final Filter category1Filter = Filter.filter(Criteria.where(CODE).eq("testCategory|with|pipes").and(INTEGRATIONKEY).eq(CATEGORY_CODE1_ENCODED));
		final Filter category2Filter = Filter.filter(Criteria.where(CODE).eq("testCategory2").and(INTEGRATIONKEY).eq("testCategory2"));
		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.OK)
				.jsonBody()
				.pathHasSize(ALL_RESULTS_PATH, 2)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, category1Filter)
				.pathContainsMatchingElement(SOME_RESULTS_PATH, category2Filter);
	}

	private List<String> getProductNamesSubList(final Integer startIndex, final Integer endIndex)
	{
		final SearchResult<ProductModel> products = flexibleSearchService.search("SELECT {PK} FROM {Product}");
		return products.getResult().subList(startIndex, endIndex).stream().map(ProductModel::getCode).collect(Collectors.toList());
	}

	private ODataResponse getProductsODataResponseWithQueryParams(final Map<String, String> queryParams)
	{
		return getProductsODataResponseWithEntitySetAndQueryParams(PRODUCTS_ENTITYSET, queryParams);
	}

	private ODataResponse getProductsODataResponseWithEntitySetAndQueryParams(final String entitySetName, final Map<String, String> queryParams)
	{
		final ODataRequestBuilder oDataRequest = requestBuilder(entitySetName)
				.withParameters(queryParams);
		return handleRequest(oDataRequest);
	}

	private ODataRequestBuilder requestBuilder(final String entitySetName)
	{
		return ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(SERVICE_GROUP1)
						.withEntitySet(entitySetName));
	}

	private ODataResponse handleRequest(final ODataRequestBuilder builder)
	{
		final ODataContext context = oDataContextGenerator.generate(builder.build());
		return facade.handleGetEntity(context);
	}
}
