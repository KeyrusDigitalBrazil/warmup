/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2b.occ.v2.controllers;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/categories")
@ApiVersion("v2")
@Api(tags = "B2B Categories")
public class B2BCategoriesController
{
	private static final String DEFAULT_PAGE_VALUE = "0";

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> solrProductSearchFacade;

	@Resource(name = "fieldSetBuilder")
	private FieldSetBuilder fieldSetBuilder;

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;


	@RequestMapping(value = "/{categoryId}/products", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Gets a list of products under a specific category", notes = "Returns a list of products in the specified category.")
	@ApiBaseSiteIdParam
	public ProductSearchPageWsDTO searchProducts(
			@ApiParam(value = "The category ID", required = true) @PathVariable final String categoryId,
			@ApiParam(value = "Serialized query in format: freeTextSearch: sort: facetKey1: facetValue1: facetKey2: facetValue2") @RequestParam(required = false) final String query,
			@ApiParam(value = "The current result page requested.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(required = false, defaultValue = "20") final int pageSize,
			@ApiParam(value = "Sorting method applied to the display search results.") @RequestParam(required = false) final String sort,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{

		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(query);

		final SearchStateData searchState = new SearchStateData();
		searchState.setQuery(searchQueryData);

		final PageableData pageable = new PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);

		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = solrProductSearchFacade.categorySearch(categoryId,
				searchState, pageable);

		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			return dataMapper.map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}

		return dataMapper.map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

}
