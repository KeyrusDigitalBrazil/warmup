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
package de.hybris.platform.cmswebservices.pagescontentslotscomponents.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;
import static java.util.Collections.emptyList;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.pagescontentslotscomponents.PageContentSlotComponentFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentData;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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


/*
 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
 * perfectly acceptable not to handle "e" here
 */
@SuppressWarnings("squid:S1166")
/**
 * Controller that provides an API to update components either between slots, or within a single slot.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents")
public class PageContentSlotComponentController
{
	@Resource
	private PageContentSlotComponentFacade pageContentSlotComponentFacade;
	@Resource
	private LocationHeaderResource locationHeaderResource;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET, params =
	{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get components by page", notes = "Fetches all components on a given page.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 200, message = "List of components defined for a given page", response = PageContentSlotComponentListData.class) })
	public @ResponseBody PageContentSlotComponentListData getComponentsByPage(
			@ApiParam(value = "Identifier of the page", required = true) //
			@RequestParam("pageId") final String pageId)
	{
		final PageContentSlotComponentListData pageContentSlotComponentList = new PageContentSlotComponentListData();

		try
		{
			final List<de.hybris.platform.cmsfacades.data.PageContentSlotComponentData> pageSlotComponentList = //
					getPageContentSlotComponentFacade().getPageContentSlotComponentsByPageId(pageId);
			final List<PageContentSlotComponentData> convertedList = getDataMapper().mapAsList(pageSlotComponentList,
					PageContentSlotComponentData.class, null);
			pageContentSlotComponentList.setPageContentSlotComponentList(convertedList);
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotComponentList.setPageContentSlotComponentList(emptyList());
		}
		return pageContentSlotComponentList;
	}

	@RequestMapping(value = "/pages/{pageId}/contentslots/{slotId}/components/{componentId}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Move component", notes = "Move a component to a different content slot or by changing its position within the content slot on a given page.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find the content item (CMSItemNotFoundException) "
					+ "or if there is any validation error (WebserviceValidationException) "
					+ "or if component is not allowed in slot (ComponentNotAllowedInSlotException)."),
			@ApiResponse(code = 200, message = "The updated PageContentSlotComponentData", response = PageContentSlotComponentData.class) })
	public PageContentSlotComponentData moveComponent(//
			@ApiParam(value = "Page identifier", required = true) //
			@PathVariable("pageId") final String pageId, //
			@ApiParam(value = "Content slot identifier", required = true) //
			@PathVariable("slotId") final String slotId, //
			@ApiParam(value = "Component identifier", required = true) //
			@PathVariable("componentId") final String componentId, //
			@ApiParam(value = "PageContentSlotComponentData", required = true) //
			@RequestBody final PageContentSlotComponentData pageContentSlotComponent) throws CMSItemNotFoundException
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.PageContentSlotComponentData convertedData = getDataMapper()
					.map(pageContentSlotComponent, de.hybris.platform.cmsfacades.data.PageContentSlotComponentData.class);
			final de.hybris.platform.cmsfacades.data.PageContentSlotComponentData movedComponent = getPageContentSlotComponentFacade()
					.moveComponent(pageId, componentId, slotId, convertedData);
			return getDataMapper().map(movedComponent, PageContentSlotComponentData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Add component to slot", notes = "Add a component to a slot.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When component item does not exist (CMSItemNotFoundException) "
					+ "or when validation errors are found (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "PageContentSlotComponentData", response = PageContentSlotComponentData.class) })
	public @ResponseBody PageContentSlotComponentData addComponentToSlot( //
			@ApiParam(value = "PageContentSlotComponentData", required = true) //
			@RequestBody final PageContentSlotComponentData pageContentSlotComponentData, //
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.PageContentSlotComponentData convertedData = getDataMapper()
					.map(pageContentSlotComponentData, de.hybris.platform.cmsfacades.data.PageContentSlotComponentData.class);
			final de.hybris.platform.cmsfacades.data.PageContentSlotComponentData resultData = getPageContentSlotComponentFacade()
					.addComponentToContentSlot(convertedData);

			// passing concatenation of pageId, slotId and componentId as identifier to the location header
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION, getLocationHeaderResource().createLocationForChildResource(
					request, resultData.getPageId() + "-" + resultData.getSlotId() + "-" + resultData.getComponentId()));
			return getDataMapper().map(resultData, PageContentSlotComponentData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/contentslots/{slotId}/components/{componentId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Remove component", notes = "Remove a component from a content slot")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the component or slot cannot be found (CMSItemNotFoundException) "
					+ "or when the component slot does not contain the component (ComponentNotFoundInSlotException)") })
	public void removeComponent( //
			@ApiParam(value = "Content slot identifier containing the component to be removed", required = true) //
			@PathVariable("slotId") final String slotId, //
			@ApiParam(value = "Component identifier of the component to be removed", required = true) //
			@PathVariable("componentId") final String componentId) throws CMSItemNotFoundException
	{
		getPageContentSlotComponentFacade().removeComponentFromContentSlot(slotId, componentId);
	}

	protected PageContentSlotComponentFacade getPageContentSlotComponentFacade()
	{
		return pageContentSlotComponentFacade;
	}

	public void setPageContentSlotComponentFacade(final PageContentSlotComponentFacade contentSlotFacade)
	{
		this.pageContentSlotComponentFacade = contentSlotFacade;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
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
