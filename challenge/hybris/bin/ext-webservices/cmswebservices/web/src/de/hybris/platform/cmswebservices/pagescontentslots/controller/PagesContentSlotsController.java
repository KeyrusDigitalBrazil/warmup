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
package de.hybris.platform.cmswebservices.pagescontentslots.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.pagescontentslots.PageContentSlotFacade;
import de.hybris.platform.cmswebservices.data.PageContentSlotData;
import de.hybris.platform.cmswebservices.data.PageContentSlotListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.Collections;
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
 * Controller that provides an API to retrieve all pages where a given content slot is present.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslots")
public class PagesContentSlotsController
{
	@Resource
	private PageContentSlotFacade pageContentSlotFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET, params =
	{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get content slots by page", notes = "Retrieve all content slots defined on the page specified by the page id.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of PageContentSlotListData, never null.", response = PageContentSlotListData.class)
	})
	public @ResponseBody PageContentSlotListData getContentSlotsByPage(
			@ApiParam(value = "Identifier of the page", required = true) @RequestParam("pageId") final String pageId)
	{
		final PageContentSlotListData pageContentSlotList = new PageContentSlotListData();

		try
		{
			final List<PageContentSlotData> convertedSlots = getDataMapper()
					.mapAsList(getPageContentSlotFacade().getContentSlotsByPage(pageId), PageContentSlotData.class, null);
			pageContentSlotList.setPageContentSlotList(convertedSlots);
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotList.setPageContentSlotList(Collections.emptyList());
		}
		return pageContentSlotList;
	}

	protected PageContentSlotFacade getPageContentSlotFacade()
	{
		return pageContentSlotFacade;
	}

	public void setPageContentSlotFacade(final PageContentSlotFacade pageContentSlotFacade)
	{
		this.pageContentSlotFacade = pageContentSlotFacade;
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
