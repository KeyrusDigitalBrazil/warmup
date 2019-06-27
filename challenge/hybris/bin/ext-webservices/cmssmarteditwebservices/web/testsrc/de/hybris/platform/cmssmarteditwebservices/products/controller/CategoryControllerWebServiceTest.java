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
package de.hybris.platform.cmssmarteditwebservices.products.controller;

import static de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother.CARS;
import static de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother.ELECTRONICS;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.cmssmarteditwebservices.dto.CategorySearchResultWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.CategoryWsDTO;
import de.hybris.platform.cmssmarteditwebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Comparator;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

@NeedsEmbeddedServer(webExtensions =
{ CmssmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CategoryControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String MASK = "mask";
	private static final String TEXT = "text";
	private static final String PAGE_SIZE = "pageSize";

	private static final String ENDPOINT_SEARCH = "/v1/productcatalogs/{catalogId}/versions/{versionId}/categories";
	private static final String ENDPOINT_GET_BY_ID = "/v1/sites/{siteId}/categories";

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
		catalogVersion = catalogVersionModelMother
				.createAppleStagedCatalogVersionModel();
		productCategoryModelMother.createDefaultCategory(catalogVersion);
		productCategoryModelMother.createCarsCategory(catalogVersion);
	}

	@Test
	public void testGetCategoryByCode()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_GET_BY_ID, new HashMap<>()))
				.path(buildUid(ELECTRONICS))
				.build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategoryWsDTO entity = response.readEntity(CategoryWsDTO.class);
		assertOnCategoryData(entity);
	}


	protected String buildUid(final String uid)
	{
		final EncodedItemComposedKey encodedItemComposedKey = new EncodedItemComposedKey();
		encodedItemComposedKey.setItemId(uid);
		encodedItemComposedKey.setCatalogId(catalogVersion.getCatalog().getId());
		encodedItemComposedKey.setCatalogVersion(catalogVersion.getVersion());
		return encodedItemComposedKey.toEncoded();
	}


	@Test
	public void testFindCategoryByText()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_SEARCH, new HashMap<>())) //
				.queryParam(TEXT, ELECTRONICS) //
				.queryParam(PAGE_SIZE, 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategorySearchResultWsDTO entity = response.readEntity(CategorySearchResultWsDTO.class);
		assertThat(entity.getProductCategories(), notNullValue());
		assertThat(entity.getProductCategories(), hasSize(1));
		assertOnCategoryData(entity.getProductCategories().get(0));
	}

	@Test
	public void testFindCategoryByMask()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_SEARCH, new HashMap<>())) //
				.queryParam(MASK, ELECTRONICS) //
				.queryParam(PAGE_SIZE, 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategorySearchResultWsDTO entity = response.readEntity(CategorySearchResultWsDTO.class);
		assertThat(entity.getProductCategories(), notNullValue());
		assertThat(entity.getProductCategories(), hasSize(1));
		assertOnCategoryData(entity.getProductCategories().get(0));
	}

	@Test
	public void testFindCategoryByTextAndMask_shouldReturnOneElectronicsCategory()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_SEARCH, new HashMap<>())) //
				.queryParam(TEXT, ELECTRONICS) //
				.queryParam(MASK, CARS) //
				.queryParam(PAGE_SIZE, 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategorySearchResultWsDTO entity = response.readEntity(CategorySearchResultWsDTO.class);
		assertThat(entity.getProductCategories(), notNullValue());
		assertThat(entity.getProductCategories(), hasSize(1));
		assertOnCategoryData(entity.getProductCategories().get(0));
	}

	@Test
	public void testFindCategoryByTextOrMaskWhenTextAndMaskAreNull_shouldReturnAllCategories()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_SEARCH, new HashMap<>())) //
				.queryParam(PAGE_SIZE, 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategorySearchResultWsDTO entity = response.readEntity(CategorySearchResultWsDTO.class);
		assertThat(entity.getProductCategories(), notNullValue());
		assertThat(entity.getProductCategories(), hasSize(2));

		entity.getProductCategories().sort(getCategoryWsDTOComparator());
		assertOnCategoryData(entity.getProductCategories().get(1));
	}

	protected Comparator<CategoryWsDTO> getCategoryWsDTOComparator()
	{
		return new Comparator<CategoryWsDTO>()
		{
			@Override
			public int compare(final CategoryWsDTO category1, final CategoryWsDTO category2)
			{
				return category1.getUid().compareTo(category2.getUid());
			}
		};
	}

	protected void assertOnCategoryData(final CategoryWsDTO categoryWsDTO)
	{
		assertThat(
				categoryWsDTO,
				allOf(hasProperty("name", is(ImmutableMap.<String, String>builder().put("en", ELECTRONICS).build())),
						hasProperty("code", is(ELECTRONICS)),
						hasProperty("catalogId", is(catalogVersion.getCatalog().getId())),
						hasProperty("catalogVersion", is(catalogVersion.getVersion()))
						));
	}
}
