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

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cmssmarteditwebservices.dto.CategoryWsDTO;
import de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller to retrieve Product Categories by its item composed key.
 * For more details about how to generate the item composed key,
 * refer to the documentation about the {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}.
 */
@Controller
@RequestMapping(API_VERSION + "/sites/{siteId}/categories")
public class CategoryController
{

	@Resource
	private ProductSearchFacade cmsSeProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get category by code", notes = "Endpoint to retrieve a category that matches the given product category code.")
	@ApiResponses({
			@ApiResponse(code = 400, message = "When the item has not been found (CMSItemNotFoundException) or when there was problem during conversion (ConversionException)."),
			@ApiResponse(code = 200, message = "Category data", response = CategoryWsDTO.class)
	})
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	public CategoryWsDTO getCategoryByCode(
			@ApiParam(value = "Category code", required = true) @PathVariable final String code)
	{
		return getDataMapper().map(getCmsSeProductSearchFacade().getProductCategoryByUid(code), CategoryWsDTO.class);
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
