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
package de.hybris.platform.cmswebservices.version.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.CMSVersionNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.version.CMSVersionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CMSVersionListWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSVersionWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

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
import org.springframework.web.bind.annotation.PostMapping;
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
 * Controller to deal with versions
 *
 * @pathparam siteId the site identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/cmsitems/{itemUUID}/versions")
public class CMSVersionController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CMSVersionController.class);

	private static final String VALIDATION_EXCEPTION = "Validation exception";

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

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation( //
			value = "Get all versions for an item filtered by a mask", notes = "Endpoint to retrieve CMSVersions "
					+ "by a search mask for the item identified by its itemUUID.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "itemUUID", value = "The uuid of the item", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the item has not been found (CMSItemNotFoundException)"),
			@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of "
					+ "CMSVersionWsDTO; never null", response = CMSVersionListWsDTO.class) })
	public CMSVersionListWsDTO findVersionsForItem(
			@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String itemUUID, //
			@ApiParam(value = "Search mask applied to the LABEL field only. Uses partial matching.", required = false) //
			@RequestParam(required = false) final String mask, //
			@ApiParam(value = "PageableWsDTO", required = true) //
			@ModelAttribute final PageableWsDTO pageableDto) throws CMSItemNotFoundException
	{
		final PageableData pageableData = getDataMapper().map(pageableDto, PageableData.class);
		final SearchResult<CMSVersionData> searchResult = getCmsVersionFacade().findVersionsForItem(itemUUID, mask, pageableData);

		final CMSVersionListWsDTO cmsVersionListWsDTO = new CMSVersionListWsDTO();
		cmsVersionListWsDTO.setResults(getDataMapper().mapAsList(searchResult.getResult(), CMSVersionWsDTO.class, null));
		cmsVersionListWsDTO.setPagination(getWebPaginationUtils().buildPagination(searchResult));

		return cmsVersionListWsDTO;
	}

	@RequestMapping(value = "/{versionId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation( //
			value = "Get a specific version for an item", notes = "Endpoint to retrieve a CMSVersion identified "
					+ "by its uid and for the item identified by its itemUUID.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "itemUUID", value = "The uuid of the item", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the version has not been found (CMSVersionNotFoundException)"),
			@ApiResponse(code = 200, message = "The dto containing version info.", response = CMSVersionWsDTO.class) })
	public CMSVersionWsDTO getVersion( //
			@ApiParam(value = "The uid of the cms version.", required = true) //
			@PathVariable final String versionId) throws CMSVersionNotFoundException
	{
		return getDataMapper().map(getCmsVersionFacade().getVersion(versionId), CMSVersionWsDTO.class);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation( //
			value = "Create a CMSVersion for an item", //
			notes = "Endpoint to create a CMSVersion for the item identified by its itemUUID.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "itemUUID", value = "The uuid of the item", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 201, message = "The dto containing version info.", response = CMSVersionWsDTO.class) })
	public CMSVersionWsDTO createVersion(
			@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String itemUUID, //
			@ApiParam(value = "The DTO object containing the label and description", required = true) //
			@RequestBody final CMSVersionWsDTO dto, //
			final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setItemUUID(itemUUID);
			cmsVersionData.setLabel(dto.getLabel());
			cmsVersionData.setDescription(dto.getDescription());

			final CMSVersionData newVersionData = getCmsVersionFacade().createVersion(cmsVersionData);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, newVersionData.getUid()));

			return getDataMapper().map(newVersionData, CMSVersionWsDTO.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/{versionId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation( //
			value = "Updates a CMSVersion for an item", notes = "Endpoint to update a CMSVersion identified by "
					+ "its uid and for the item identified by its itemUUID.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "itemUUID", value = "The uuid of the item", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 200, message = "The dto containing version info.", response = CMSVersionWsDTO.class) })
	public CMSVersionWsDTO updateVersion(
			@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String itemUUID, //
			@ApiParam(value = "The uid of the cms version.", required = true) //
			@PathVariable final String versionId, //
			@ApiParam(value = "The DTO object containing the label and description", required = true) //
			@RequestBody final CMSVersionWsDTO dto)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setUid(versionId);
			cmsVersionData.setItemUUID(itemUUID);
			cmsVersionData.setLabel(dto.getLabel());
			cmsVersionData.setDescription(dto.getDescription());

			return getDataMapper().map(getCmsVersionFacade().updateVersion(cmsVersionData), CMSVersionWsDTO.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@PostMapping(value = "/{versionId}/rollbacks")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation( //
			value = "Rolls back an item to a specific CMSVersion", notes = "Endpoint to rollback the item identified by "
					+ "its itemUUID to a previously saved CMSVersion")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "itemUUID", value = "The uuid of the item", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 204, message = "No Content") })
	public void rollbackVersion(
			@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String itemUUID, //
			@ApiParam(value = "The uid of the cms version.", required = true) //
			@PathVariable final String versionId)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setItemUUID(itemUUID);
			cmsVersionData.setUid(versionId);

			getCmsVersionFacade().rollbackVersion(cmsVersionData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/{versionId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	@ApiOperation( //
			value = "Deletes a CMSVersion for an item", //
			notes = "Endpoint to permanently delete a CMSVersion identified by its uid and for the item identified by its itemUUID.")
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "itemUUID", value = "The uuid of the item", required = true, dataType = "string", paramType = "path") })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException)."),
			@ApiResponse(code = 204, message = "No Content") })
	public void deleteVersion(@ApiParam(value = "The universally unique identifier of the item. The uuid is a composed key formed "
			+ "by the cms item uid + the catalog + the catalog version.", required = true) //
	@PathVariable final String itemUUID, //
			@ApiParam(value = "The uid of the cms version.", required = true) //
			@PathVariable final String versionId)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setUid(versionId);
			cmsVersionData.setItemUUID(itemUUID);

			getCmsVersionFacade().deleteVersion(cmsVersionData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected CMSVersionFacade getCmsVersionFacade()
	{
		return cmsVersionFacade;
	}

	public void setCmsVersionFacade(final CMSVersionFacade cmsVersionFacade)
	{
		this.cmsVersionFacade = cmsVersionFacade;
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

	protected ObjectFactory<CMSVersionData> getCmsVersionDataDataFactory()
	{
		return cmsVersionDataDataFactory;
	}

	public void setCmsVersionDataDataFactory(final ObjectFactory<CMSVersionData> cmsVersionDataDataFactory)
	{
		this.cmsVersionDataDataFactory = cmsVersionDataDataFactory;
	}

}
