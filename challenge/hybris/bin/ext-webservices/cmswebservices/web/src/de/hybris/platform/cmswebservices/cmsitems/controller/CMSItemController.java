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
package de.hybris.platform.cmswebservices.cmsitems.controller;

import static com.google.common.collect.Maps.newHashMap;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.version.CMSVersionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CMSItemSearchWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSItemUuidListWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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


/**
 * Generic controller to deal with CMS items (Components, Pages, Restrictions, etc...). Any item that extends CMSItem is
 * supported using this interface.
 *
 * @pathparam siteId Site identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/cmsitems")
public class CMSItemController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CMSItemController.class);

	@Resource
	private CMSItemFacade cmsItemFacade;

	@Resource
	private CMSVersionFacade cmsVersionFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private ObjectFactory<CMSVersionData> cmsVersionDataDataFactory;

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Get CMS Item by uuid", notes = "Endpoint to retrieve the item that matches the given item uuid (Universally Unique Identifier).")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the item has not been found (CMSItemNotFoundException) "
					+ "or if there is a conversion error (ConversionException)"),
			@ApiResponse(code = 200, message = "Map&lt;String, Object&gt; representation of the CMS Item object.", response = Map.class) })
	public Map<String, Object> getCMSItemByUUid(
			@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String uuid) throws CMSItemNotFoundException
	{
		return getCmsItemFacade().getCMSItemByUuid(uuid);
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, params =
	{ "versionId" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Get CMS Item by uuid", notes = "Endpoint to retrieve the item that matches the given item uuid (Universally Unique Identifier).")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = " If there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "Map&lt;String, Object&gt; representation of the CMS Item object.", response = Map.class) })
	public Map<String, Object> getCMSItemByUUidAndVersion(
			@ApiParam(value = "The uid of the cms version.", required = true) @RequestParam("versionId") final String versionId,
			@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String uuid)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setItemUUID(uuid);
			cmsVersionData.setUid(versionId);

			return getCmsVersionFacade().getItemByVersion(cmsVersionData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Remove CMS Item by uuid", notes = "Endpoint to remove a content item (CMSItem) from the system.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find the content item (CMSItemNotFoundException).") })
	public void removeCMSItembyUUid( //
			@ApiParam(value = "The universally unique identifier of the item", required = true) //
			@PathVariable final String uuid) throws CMSItemNotFoundException
	{
		getCmsItemFacade().deleteCMSItemByUuid(uuid);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Create CMS Item", notes = "Endpoint to create a new CMS Item.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find the content item (CMSItemNotFoundException) "
					+ "or if there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 201, message = "The multi-level Map representing the newly created CMS Item.", response = Map.class) })
	public Map<String, Object> createCMSItem( //
			@ApiParam(value = "Map representing the CMS item to create", required = true) //
			@RequestBody final Map<String, Object> inputMap, //
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		try
		{
			final Map<String, Object> outputMap = getCmsItemFacade().createItem(inputMap);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, outputMap.get(FIELD_UUID)));
			return outputMap;
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(method = RequestMethod.POST, params =
	{ "dryRun=true" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Validate CMS Item for creation", notes = "Endpoint to validate the new CMS Item in a Dry Run mode.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find the content item (CMSItemNotFoundException) "
					+ "or if there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "The multi-level Map representing the validated CMS Item.", response = Map.class) })
	public Map<String, Object> validateCMSItemForCreation(
			@ApiParam(value = "When set to TRUE, the request is executed in Dry Run mode", required = true) //
			@RequestParam("dryRun") final Boolean dryRun,
			@ApiParam(value = "Map representing the CMS item to create in Dry Run mode", required = true) //
			@RequestBody final Map<String, Object> inputMap, //
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		try
		{
			return getCmsItemFacade().validateItemForCreate(inputMap);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Update CMS Item", notes = "Endpoint to update a CMS Item.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find the content item (CMSItemNotFoundException) "
					+ "or if there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "The multi-level Map representing the updated CMS Item.", response = Map.class) })
	public Map<String, Object> updateCMSItem( //
			@ApiParam(value = "Unique Identifier of a CMS Item", required = true) //
			@PathVariable final String uuid, //
			@ApiParam(value = "Map representing the CMS item to update", required = true) //
			@RequestBody final Map<String, Object> inputMap) throws CMSItemNotFoundException
	{
		try
		{
			return getCmsItemFacade().updateItem(uuid, inputMap);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.PUT, params =
	{ "dryRun=true" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Validate CMS Item for update", notes = "Endpoint to validate a CMS Item in a Dry Run mode.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find the content item (CMSItemNotFoundException) "
					+ "or if there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "The multi-level Map representing the validated CMS Item.", response = Map.class) })
	public Map<String, Object> validateCMSItemForUpdate( //
			@ApiParam(value = "When set to TRUE, the request is executed in Dry Run mode", required = true) //
			@RequestParam("dryRun") final Boolean dryRun, @ApiParam(value = "Unique Identifier of a CMS Item", required = true) //
			@PathVariable final String uuid, @ApiParam(value = "Map representing the CMS item to validate", required = true) //
			@RequestBody final Map<String, Object> inputMap) throws CMSItemNotFoundException
	{
		try
		{
			return getCmsItemFacade().validateItemForUpdate(uuid, inputMap);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/uuids", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Find cms items by uuids in body", notes = "Find CMSItems matching the given uuids by POSTing the uuids in the request body.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If it cannot find one of the items (CMSItemNotFoundException)."),
			@ApiResponse(code = 200, message = "A map list of elements in the form of Map&lt;String, Object&gt;, "
					+ "each representing a CMSItem.", response = Map.class) })
	public Map<String, Object> findCmsItemsByUuidsInBody(
			@ApiParam(value = "CMSItemUuidListWsDTO", required = true) @RequestBody final CMSItemUuidListWsDTO dto)
			throws CMSItemNotFoundException
	{
		final List<Map<String, Object>> searchResults = getCmsItemFacade().findCMSItems(dto.getUuids());
		return Collections.singletonMap(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_RESULTS, searchResults);
	}

	@RequestMapping(method = RequestMethod.GET, params =
	{ "pageSize", "currentPage" })
	@ResponseBody
	@ApiOperation(value = "Find CMS items", notes = "Endpoint for paged Search for CMSItems.")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If the required fields are missing (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "A map of paging info and results. Results are in the form of "
					+ "Map&lt;String, Object&gt;, each representing a CMSItem. Never null.") })
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "Page size for paging", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "currentPage", value = "Catalog on which to search", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "catalogId", value = "CatalogVersion on which to search", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "catalogVersion", value = "CatalogVersion on which to search", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "mask", value = "Search mask applied to the UID and NAME fields, Uses partial matching", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "typeCode", value = "TypeCode filter. Exact matches only. Either typeCode or typeCodes can be set.", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "typeCodes", value = "Search using a comma separated list of type code. Either typeCode or typeCodes can be set.", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "itemSearchParams", value = "Search on additional fields using a comma separated list of field name "
					+ "and value pairs which are separated by a colon. Exact matches only. You can use {@code null} as value.", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "The requested ordering for the search results.", dataType = "string", paramType = "query") })
	public Map<String, Object> findCmsItems( //
			@ApiParam(value = "CMS Item search DTO", required = true) //
			@ModelAttribute final CMSItemSearchWsDTO cmsItemSearchWsDTO, //
			@ApiParam(value = "Pageable DTO", required = true) //
			@ModelAttribute final PageableWsDTO pageableDto)
	{
		final Map<String, Object> results = newHashMap();
		try
		{
			final PageableData pageableData = getDataMapper().map(pageableDto, PageableData.class);
			final CMSItemSearchData cmsItemSearchData = getDataMapper().map(cmsItemSearchWsDTO, CMSItemSearchData.class);

			final SearchResult<Map<String, Object>> searchResults = getCmsItemFacade().findCMSItems(cmsItemSearchData, pageableData);

			final PaginationWsDTO paginationWsDTO = getWebPaginationUtils().buildPagination(searchResults);

			results.put(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_RESULTS, searchResults.getResult());
			results.put(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_PAGINATION, paginationWsDTO);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}

		return results;
	}

	protected CMSItemFacade getCmsItemFacade()
	{
		return cmsItemFacade;
	}

	public void setCmsItemFacade(final CMSItemFacade cmsItemFacade)
	{
		this.cmsItemFacade = cmsItemFacade;
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

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected CMSVersionFacade getCmsVersionFacade()
	{
		return cmsVersionFacade;
	}

	public void setCmsVersionFacade(final CMSVersionFacade cmsVersionFacade)
	{
		this.cmsVersionFacade = cmsVersionFacade;
	}

	protected ObjectFactory<CMSVersionData> getCmsVersionDataDataFactory()
	{
		return cmsVersionDataDataFactory;
	}

	public void setCmsVersionDataDataFactory(final ObjectFactory<CMSVersionData> cmsVersionDataDataFactory)
	{
		this.cmsVersionDataDataFactory = cmsVersionDataDataFactory;
	}

}
