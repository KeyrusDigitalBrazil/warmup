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
package de.hybris.platform.cmswebservices.synchronization.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.SynchronizationFacade;
import de.hybris.platform.cmswebservices.data.SyncJobData;
import de.hybris.platform.cmswebservices.data.SyncJobRequestData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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


/*
 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
 * perfectly acceptable not to handle "e" here
 */
@SuppressWarnings("squid:S1166")
/**
 * Controller that handles synchronization of catalogs
 *
 * @pathparam catalogId Catalog name
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}")
public class CatalogVersionSynchronizationController
{

	@Resource
	private SynchronizationFacade synchronizationFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get synchronization status", notes = "Get synchronization status")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "The synchronization status", response = SyncJobData.class)
	})
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "sourceVersionId", value = "Catalog version used as a starting point in this synchronization", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "targetVersionId", value = "Catalog version destination to be synchronized", required = false, dataType = "string", paramType = "query")
	})
	public SyncJobData getSynchronizationByCatalogSourceTarget(
			@ApiParam(value = "Contains the synchronization request data", required = true) @ModelAttribute final SyncJobRequestData syncJobRequest)
	{
		try
		{
			final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest, SyncRequestData.class);

			final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
					.getSynchronizationByCatalogSourceTarget(convertedSyncJobRequest);

			return getDataMapper().map(syncJobResult, SyncJobData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Creates a catalog synchronization", notes = "Creates a catalog synchronization")
	@ApiResponses(
	{
			@ApiResponse(code = 400, message = "When one of the catalogs does not exist (CMSItemNotFoundException)."),
			@ApiResponse(code = 200, message = "The synchronization status", response = SyncJobData.class)
	})
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "sourceVersionId", value = "Catalog version used as a starting point in this synchronization", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "targetVersionId", value = "Catalog version destination to be synchronized", required = false, dataType = "string", paramType = "query")
	})
	public SyncJobData createSynchronizationByCatalogSourceTarget(
			@ApiParam(value = "Contains the synchronization request data", required = true) @ModelAttribute final SyncJobRequestData syncJobRequest)
			throws CMSItemNotFoundException
	{
		try
		{
			final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest, SyncRequestData.class);

			final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
					.createCatalogSynchronization(convertedSyncJobRequest);

			return getDataMapper().map(syncJobResult, SyncJobData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/synchronizations/targetversions/{targetVersionId}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get last synchronization by target catalog", notes = "Get the status of the last synchronization job. Information is retrieved based on the catalog version target.")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "The synchronization status", response = SyncJobData.class)
	})
	@ApiImplicitParams(
	{
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "targetVersionId", value = "Catalog version destination to be synchronized", required = true, dataType = "string", paramType = "query")
	})
	public SyncJobData getLastSynchronizationByCatalogTarget(
			@ApiParam(value = "Contains the synchronization request data", required = true) @ModelAttribute final SyncJobRequestData syncJobRequest)
	{
		try
		{
			final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest, SyncRequestData.class);

			final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
					.getLastSynchronizationByCatalogTarget(convertedSyncJobRequest);

			return getDataMapper().map(syncJobResult, SyncJobData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	public SynchronizationFacade getSynchronizationFacade()
	{
		return synchronizationFacade;
	}

	public void setSynchronizationFacade(final SynchronizationFacade synchronizationFacade)
	{
		this.synchronizationFacade = synchronizationFacade;
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
