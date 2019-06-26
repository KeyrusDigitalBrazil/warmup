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
package de.hybris.platform.cmswebservices.pages.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.data.UidListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
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
 * Controller to deal with AbstractPageModel objects
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages")
public class PageController
{
	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private PageFacade cmsPageFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params =
	{ "uids" })
	@ResponseBody
	@ApiOperation( //
			value = "Find pages by ids", notes = "Find specific pages. Deprecated since 6.6, please use "
					+ "GET /v1/sites/{siteId}/cmsitems{?uuids} instead.")
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 200, message = "List of AbstractPageData", response = PageListData.class) })
	public PageListData findPagesByIds(
			@ApiParam(value = "List of identifier of the pages that we are looking for", required = true)
			@RequestParam("uids")
			final List<String> uids)
	{
		final List<de.hybris.platform.cmsfacades.data.AbstractPageData> pages = getCmsPageFacade().findAllPages().stream()
				.filter(page -> uids.contains(page.getUid())).collect(Collectors.toList());

		final PageListData pageListData = new PageListData();
		pageListData.setPages(getDataMapper().mapAsList(pages, AbstractPageData.class, null));
		return pageListData;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{pageId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation( //
			value = "Get page by uid", notes = "Get the page that matches the given page uid. Deprecated since 6.6, please use "
					+ "GET /v1/sites/{siteId}/cmsitems/{uuid} instead.")
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the page cannot be found (CMSItemNotFoundException)."),
			@ApiResponse(code = 200, message = "Page data object", response = AbstractPageData.class) })
	public AbstractPageData getPageByUid(@ApiParam(value = "Page identifier", required = true)
	@PathVariable
	final String pageId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getCmsPageFacade().getPageByUid(pageId), AbstractPageData.class);
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params =
	{ "typeCode", "defaultPage" })
	@ResponseBody
	@ApiOperation( //
			value = "Find pages by type", notes = "Get all default or variation pages that matches the given page type. "
					+ "Deprecated since 6.6, please use "
					+ "GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 200, message = "All default or variation pages for a given page type", response = PageListData.class) })
	public PageListData findPagesByType(@ApiParam(value = "The type code of a page", required = true)
	@RequestParam("typeCode")
	final String typeCode,
			@ApiParam(value = "Setting this to true will find all default pages; otherwise find all variation pages", required = true)
			@RequestParam("defaultPage")
			final Boolean isDefaultPage)
	{
		final List<de.hybris.platform.cmsfacades.data.AbstractPageData> pages = getCmsPageFacade().findPagesByType(typeCode,
				isDefaultPage);

		final PageListData pageListData = new PageListData();
		pageListData.setPages(getDataMapper().mapAsList(pages, AbstractPageData.class, null));
		return pageListData;
	}

	@RequestMapping(value = "/{pageId}/variations", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find variation pages", notes = "Get all variation pages uid for a given page.")
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the pageId is invalid (CMSItemNotFoundException)."),
			@ApiResponse(code = 200, message = "All variation pages uid for a given page; empty if the given page is already a variation page; never null.", response = UidListData.class) })
	public UidListData findVariationPages(@ApiParam(value = "The page identifier", required = true)
	@PathVariable
	final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findVariationPages(pageId));
	}

	@RequestMapping(value = "/{pageId}/fallbacks", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find fallback pages", notes = "Get all fallback pages uid for a given page.")
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the pageId is invalid (CMSItemNotFoundException)."),
			@ApiResponse(code = 200, message = "All fallback pages uid for a given page; empty if the given page is already a fallback page; never null", response = UidListData.class) })
	public UidListData findFallbackPages(@ApiParam(value = "The page identifier", required = true)
	@PathVariable
	final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findFallbackPages(pageId));
	}

	protected UidListData convertToUidListData(final List<String> pageIds)
	{
		final UidListData pageData = new UidListData();
		pageData.setUids(pageIds);
		return pageData;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected PageFacade getCmsPageFacade()
	{
		return cmsPageFacade;
	}

	public void setCmsPageFacade(final PageFacade pageFacade)
	{
		this.cmsPageFacade = pageFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}
}