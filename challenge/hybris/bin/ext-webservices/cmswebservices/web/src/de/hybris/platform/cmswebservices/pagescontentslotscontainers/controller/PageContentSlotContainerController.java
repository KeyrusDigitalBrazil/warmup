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
package de.hybris.platform.cmswebservices.pagescontentslotscontainers.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;
import static java.util.Collections.emptyList;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.pagescontentslotscontainers.PageContentSlotContainerFacade;
import de.hybris.platform.cmswebservices.dto.PageContentSlotContainerListWsDTO;
import de.hybris.platform.cmswebservices.dto.PageContentSlotContainerWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller that provides an API to retrieve information about containers in content slots in a page.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscontainers")
public class PageContentSlotContainerController
{
	@Resource
	private PageContentSlotContainerFacade pageContentSlotContainerFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET, params =
	{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get containers by page", notes = "Fetches all containers on a given page.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "The list of page content slot containers", response = PageContentSlotContainerListWsDTO.class)
	})
	public @ResponseBody PageContentSlotContainerListWsDTO getContainersByPage(
			@ApiParam(value = "Identifier of the page", required = true) @RequestParam("pageId") final String pageId)
	{
		final PageContentSlotContainerListWsDTO pageContentSlotContainerList = new PageContentSlotContainerListWsDTO();

		try
		{
			final List<de.hybris.platform.cmsfacades.data.PageContentSlotContainerData> pageSlotContainerList = getPageContentSlotContainerFacade()
					.getPageContentSlotContainersByPageId(pageId);

			final List<PageContentSlotContainerWsDTO> convertedList = getDataMapper().mapAsList(pageSlotContainerList,
					PageContentSlotContainerWsDTO.class, null);
			pageContentSlotContainerList.setPageContentSlotContainerList(convertedList);
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotContainerList.setPageContentSlotContainerList(emptyList());
		}
		return pageContentSlotContainerList;
	}

	protected PageContentSlotContainerFacade getPageContentSlotContainerFacade()
	{
		return pageContentSlotContainerFacade;
	}

	public void setPageContentSlotContainerFacade(final PageContentSlotContainerFacade containerSlotFacade)
	{
		this.pageContentSlotContainerFacade = containerSlotFacade;
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
