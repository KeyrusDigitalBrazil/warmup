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

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.ProductData;
import de.hybris.platform.cmsfacades.products.ProductSearchFacade;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.dto.ProductDataListWsDTO;
import de.hybris.platform.cmswebservices.dto.ProductWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller to retrieve and search Products within a Product Catalog Version.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/productcatalogs/{catalogId}/versions/{versionId}/products")
public class ProductController
{

	@Resource
	private ProductSearchFacade cmsProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get product by code", notes = "Get a product that matches the given product code.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{
			@ApiResponse(code = 400, message = "When the item has not been found (CMSItemNotFoundException) or when there was problem during conversion (ConversionException)."),
			@ApiResponse(code = 200, message = "ProductData", response = ProductWsDTO.class)
	})
	public ProductWsDTO getProductByCode(
			@ApiParam(value = "Product code", required = true) @PathVariable final String code) throws CMSItemNotFoundException,
			ConversionException
	{
		return getDataMapper().map(getCmsProductSearchFacade().getProductByCode(code), ProductWsDTO.class);
	}

	@RequestMapping(method = RequestMethod.GET, params =
	{ "pageSize" })
	@ResponseBody
	@ApiOperation(value = "Find products by text", notes = "Find products using a free text search field.")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ProductData, never null.", response = ProductDataListWsDTO.class)
	})
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "The maximum number of elements in the result list.", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "currentPage", value = "The requested page number", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "The string field the results will be sorted with", required = false, dataType = "string", paramType = "query") })
	public ProductDataListWsDTO findProductsByText(
			@ApiParam(value = "The string value on which products will be filtered deprecated, use mask instead", required = false) @RequestParam(required = false) final String text,
			@ApiParam(value = "The string value on which products will be filtered", required = false) @RequestParam(required = false) final String mask,
			@ApiParam(value = "Defines the pageSize, currentPage and sorting order", required = true) @ModelAttribute final PageableWsDTO pageableDto)
	{
		if (Objects.nonNull(text) && Objects.nonNull(mask))
		{
			throw new IllegalArgumentException("Invalid query parameters. text and mask cannot be used together, use mask instead.");
		}
		final String filterAttribute = Optional.ofNullable(text).orElse(mask);

		final SearchResult<ProductData> productSearchResult = getCmsProductSearchFacade()
				.findProducts(filterAttribute, Optional.of(pageableDto)
						.map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class)).get());

		final ProductDataListWsDTO productList = new ProductDataListWsDTO();
		productList.setProducts(productSearchResult //
				.getResult() //
				.stream() //
				.map(productData -> getDataMapper().map(productData, ProductWsDTO.class)) //
				.collect(Collectors.toList()));
		productList.setPagination(getWebPaginationUtils().buildPagination(productSearchResult));
		return productList;
	}

	protected ProductSearchFacade getCmsProductSearchFacade()
	{
		return cmsProductSearchFacade;
	}

	public void setCmsProductSearchFacade(final ProductSearchFacade productSearchFacade)
	{
		this.cmsProductSearchFacade = productSearchFacade;
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
