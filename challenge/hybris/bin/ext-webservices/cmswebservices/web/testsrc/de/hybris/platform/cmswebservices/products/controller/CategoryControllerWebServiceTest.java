/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmswebservices.products.controller;

import static de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother.CARS;
import static de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother.ELECTRONICS;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CategoryDataListWsDTO;
import de.hybris.platform.cmswebservices.dto.CategoryWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CategoryControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String PAGE_ENDPOINT = "/v1/productcatalogs/{catalogId}/versions/{versionId}/categories";

	@Resource
	private ProductCategoryModelMother productCategoryModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private SiteModelMother siteModelMother;

	private CatalogVersionModel catalogVersion;
	@Before
	public void setup()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		productCategoryModelMother.createDefaultCategory(catalogVersion);
		productCategoryModelMother.createCarsCategory(catalogVersion);
	}

	@Test
	public void shouldReturnCategoryByCode()
	{
		//execute
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>()))
				.path(ELECTRONICS)
				.build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//assert
		assertResponse(Response.Status.OK, response);
		final CategoryWsDTO entity = response.readEntity(CategoryWsDTO.class);
		assertOnCategoryData(entity, ELECTRONICS);
	}


	@Test
	public void shouldFindCategoryByText()
	{
		//execute
		final Response response = getResponse("text", "");

		//assert
		assertResponse(Response.Status.OK, response);
		final CategoryDataListWsDTO entity = response.readEntity(CategoryDataListWsDTO.class);
		assertOnCategoryList(entity, 1);
		assertOnCategoryData(entity.getProductCategories().get(0), ELECTRONICS);
	}

	@Test
	public void shouldFindCategoryByMask()
	{
		//execute
		final Response response = getResponse("", "mask");

		//assert
		assertResponse(Response.Status.OK, response);
		final CategoryDataListWsDTO entity = response.readEntity(CategoryDataListWsDTO.class);
		assertOnCategoryList(entity, 1);
		assertOnCategoryData(entity.getProductCategories().get(0), CARS);
	}

	@Test
	public void shouldFailWhenProvidedBothTextAndMask()
	{
		//execute
		final Response response = getResponse("text", "mask");

		//assert
		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldReturnAllCategories()
	{
		//execute
		final Response response = getResponse("", "");

		//assert
		assertResponse(Response.Status.OK, response);
		final CategoryDataListWsDTO entity = response.readEntity(CategoryDataListWsDTO.class);
		assertOnCategoryList(entity, 2);
		assertOnCategoryData(entity.getProductCategories().get(0), CARS);
		assertOnCategoryData(entity.getProductCategories().get(1), ELECTRONICS);
	}

	protected void assertOnCategoryList(final CategoryDataListWsDTO entity, final int expectedNumberOfElements)
	{
		assertThat("CategoryControllerWebServiceTest product categories list should not be null", entity.getProductCategories(), notNullValue());
		assertThat("CategoryControllerWebServiceTest product categories list should contain " + expectedNumberOfElements + " elements",
				entity.getProductCategories().size(), is(expectedNumberOfElements));
	}

	protected void assertOnCategoryData(final CategoryWsDTO categoryWsDTO, final String modelName)
	{
		assertThat("CategoryControllerWebServiceTest product category DTO should contain proper attributes: name, code, catalogId, catalogVersion",
				categoryWsDTO,
				allOf(hasProperty("name", is(ImmutableMap.<String, String>builder().put("en", modelName).build())),
						hasProperty("code", is(modelName)),
						hasProperty("catalogId", is(catalogVersion.getCatalog().getId())),
						hasProperty("catalogVersion", is(catalogVersion.getVersion()))
						));
	}

	private Response getResponse(final String text, final String mask)
	{
		return getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>())) //
				.queryParam(text, ELECTRONICS) //
				.queryParam(mask, CARS) //
				.queryParam("pageSize", 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
	}
}
