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

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static de.hybris.platform.cmsfacades.util.models.ProductModelMother.*;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmsfacades.util.models.ProductModelMother;
import de.hybris.platform.cmswebservices.dto.ProductDataListWsDTO;
import de.hybris.platform.cmswebservices.dto.ProductWsDTO;
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
public class ProductControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String PAGE_ENDPOINT = "/v1/productcatalogs/{catalogId}/versions/{versionId}/products";

	@Resource
	private ProductModelMother productModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private SiteModelMother siteModelMother;

	@Before
	public void setup()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		final CatalogVersionModel catalogVersion = catalogVersionModelMother
				.createAppleStagedCatalogVersionModel();
		productModelMother.createMouseProduct(catalogVersion);
		productModelMother.createCarProduct(catalogVersion);
	}

	@Test
	public void shouldReturnProductByCode()
	{
		//execute
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>()))
				.path(MOUSE)
				.build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//assert
		assertResponse(Response.Status.OK, response);

		final ProductWsDTO entity = response.readEntity(ProductWsDTO.class);
		assertOnProductData(entity, MOUSE);
	}


	@Test
	public void shouldFindProductByText()
	{
		//execute
		Response response = getResponse("text", "");

		//assert
		assertResponse(Response.Status.OK, response);
		final ProductDataListWsDTO entity = response.readEntity(ProductDataListWsDTO.class);
		assertOnProductList(entity, 1);
		assertOnProductData(entity.getProducts().get(0), MOUSE);
	}

	@Test
	public void shouldFindProductByMask()
	{
		//execute
		Response response = getResponse("", "mask");

		//assert
		assertResponse(Response.Status.OK, response);
		final ProductDataListWsDTO entity = response.readEntity(ProductDataListWsDTO.class);
		assertOnProductList(entity, 1);
		assertOnProductData(entity.getProducts().get(0), CAR);
	}

	@Test
	public void shouldFailWhenProvidedBothTextAndMask()
	{
		//execute
		Response response = getResponse("text", "mask");

		//assert
		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldReturnAllProducts()
	{
		//execute
		Response response = getResponse("", "");

		//assert
		assertResponse(Response.Status.OK, response);
		final ProductDataListWsDTO entity = response.readEntity(ProductDataListWsDTO.class);
		assertOnProductList(entity, 2);
		assertOnProductData(entity.getProducts().get(0), CAR);
		assertOnProductData(entity.getProducts().get(1), MOUSE);
	}

	protected void assertOnProductList(final ProductDataListWsDTO entity, final int expectedNumberOfElements)
	{
		assertThat("ProductControllerWebServiceTest product list should not be null", entity.getProducts(), notNullValue());
		assertThat("ProductControllerWebServiceTest product list should contain " + expectedNumberOfElements + " elements",
				entity.getProducts().size(), is(expectedNumberOfElements));
	}

	protected void assertOnProductData(final ProductWsDTO productWsDTO, final String modelName)
	{
		assertThat(
				"ProductControllerWebServiceTest product DTO should contain proper attributes: name, code, catalogId, catalogVersion",
				productWsDTO,
				allOf(hasProperty("name", is(ImmutableMap.<String, String> builder().put("en", modelName).build())),
						hasProperty("code", is(modelName)), //
						hasProperty("catalogId", is(ID_APPLE.name())), //
						hasProperty("catalogVersion", is(STAGED.getVersion()))));
	}

	private Response getResponse(final String text, final String mask)
	{
		return getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>())) //
				.queryParam(text, MOUSE) //
				.queryParam(mask, CAR) //
				.queryParam("pageSize", 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
	}
}
