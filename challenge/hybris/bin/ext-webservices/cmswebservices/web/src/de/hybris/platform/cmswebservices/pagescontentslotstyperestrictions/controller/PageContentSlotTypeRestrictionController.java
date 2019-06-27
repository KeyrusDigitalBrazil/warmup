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
package de.hybris.platform.cmswebservices.pagescontentslotstyperestrictions.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.ContentSlotTypeRestrictionsData;
import de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions.PageContentSlotTypeRestrictionsFacade;
import de.hybris.platform.cmswebservices.dto.ContentSlotTypeRestrictionsWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller that provides type restrictions for CMS content slots.
 *
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/pages/{pageId}/contentslots/{slotId}/typerestrictions")
public class PageContentSlotTypeRestrictionController
{
	@Resource
	private PageContentSlotTypeRestrictionsFacade pageContentSlotTypeRestrictionsFacade;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get type restrictions for content slot", notes = "Get type restriction for a given page id and content slot id.")
	@ApiResponses(
	{
			@ApiResponse(code = 400, message = "When the page/slot cannot be found (CMSItemNotFoundException)"),
			@ApiResponse(code = 200, message = "DTO providing the mapping", response = ContentSlotTypeRestrictionsWsDTO.class)
	})
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "catalogId", value = "Catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "Catalog version identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "pageId", value = "Page identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "slotId", value = "Slot identifier", required = true, dataType = "string", paramType = "path") })
	public ContentSlotTypeRestrictionsWsDTO getTypeRestrictionsForContentSlot(
			@ApiParam(value = "Page identifier", required = true) final @PathVariable String pageId,
			@ApiParam(value = "Content slot identifier", required = true) final @PathVariable String slotId)
			throws CMSItemNotFoundException
	{
		final ContentSlotTypeRestrictionsData data = getPageContentSlotTypeRestrictionsFacade()
				.getTypeRestrictionsForContentSlotUID(pageId, slotId);

		return getDataMapper().map(data, ContentSlotTypeRestrictionsWsDTO.class);
	}


	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}


	protected PageContentSlotTypeRestrictionsFacade getPageContentSlotTypeRestrictionsFacade()
	{
		return pageContentSlotTypeRestrictionsFacade;
	}

	public void setPageContentSlotTypeRestrictionsFacade(
			final PageContentSlotTypeRestrictionsFacade pageContentSlotTypeRestrictionsFacade)
	{
		this.pageContentSlotTypeRestrictionsFacade = pageContentSlotTypeRestrictionsFacade;
	}
}
