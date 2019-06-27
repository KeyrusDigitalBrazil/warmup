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

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.cmssmarteditwebservices.dto.PageableWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.ProductSearchResultWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.ProductWsDTO;
import de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;


/**
 * Controller to search Products within a Product Catalog Version.
 */
@Controller
@RequestMapping(API_VERSION + "/productcatalogs/{catalogId}/versions/{versionId}/products")
public class ProductSearchController
{
	@Resource
	private ProductSearchFacade cmsSeProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET, params = { "pageSize" })
	@ResponseBody
	@ApiOperation(value = "Find products by text or mask", notes = "Endpoint to retrieve products using a free text search field.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ProductData, never null", response = ProductSearchResultWsDTO.class)
	})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "The maximum number of elements in the result list.", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "currentPage", value = "The requested page number",  dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "The string field the results will be sorted with", dataType = "string", paramType = "query")
	})
	public ProductSearchResultWsDTO findProductsByTextOrMask(
			@ApiParam(value = "The string value on which products will be filtered") @RequestParam(required = false) final String text,
			@ApiParam(value = "The string value on which products will be filtered if no text value is provided") @RequestParam(required = false) final String mask,
			@ApiParam(value = "Pageable DTO", required = true) @ModelAttribute final PageableWsDTO pageableDto)
	{
		final String searchText = Strings.isNullOrEmpty(text) ? mask : text;
		final PageableData pageableData = Optional.of(pageableDto) //
				.map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class)) //
				.orElse(null);
		final SearchResult<ProductData> productSearchResult = getCmsSeProductSearchFacade().findProducts(searchText, pageableData);

		final ProductSearchResultWsDTO productList = new ProductSearchResultWsDTO();
		productList.setProducts(productSearchResult //
				.getResult() //
				.stream() //
				.map(productData -> getDataMapper().map(productData, ProductWsDTO.class)) //
				.collect(Collectors.toList()));
		productList.setPagination(getWebPaginationUtils().buildPagination(productSearchResult));
		return productList;
	}

	protected ProductSearchFacade getCmsSeProductSearchFacade()
	{
		return cmsSeProductSearchFacade;
	}

	public void setCmsSeProductSearchFacade(final ProductSearchFacade cmsSeProductSearchFacade)
	{
		this.cmsSeProductSearchFacade = cmsSeProductSearchFacade;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
