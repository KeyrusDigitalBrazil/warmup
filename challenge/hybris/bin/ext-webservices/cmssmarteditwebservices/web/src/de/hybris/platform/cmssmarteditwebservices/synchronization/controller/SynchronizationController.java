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
package de.hybris.platform.cmssmarteditwebservices.synchronization.controller;

import static de.hybris.platform.cms2.model.pages.AbstractPageModel._TYPECODE;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.*;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.ItemSynchronizationFacade;
import de.hybris.platform.cmssmarteditwebservices.data.SyncItemStatusWsDTO;
import de.hybris.platform.cmssmarteditwebservices.data.SynchronizationWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.*;


/**
 * Controller to retrieve complex synchronization status for and to perform a synchronization on a given
 * {@link AbstractPageModel}
 */
@Controller
@RequestMapping(
		API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/synchronizations/versions/{targetCatalogVersion}")
public class SynchronizationController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationController.class);

	@Resource
	private ItemSynchronizationFacade itemSynchronizationFacade;

	@Resource
	private SyncItemStatusConfig cmsSyncItemStatusConfig;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/pages/{pageId}", method = GET)
	@ResponseBody
	@ApiOperation(value = "Get synchronization status", notes = "Will build the synchronization status of a page including detailed status of its content slots and their cms components.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "DTO containing the complex synchronization status of the AbstractPageModel page", response = SyncItemStatusWsDTO.class)
	})
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	public SyncItemStatusWsDTO getSyncStatus(
			@ApiParam(value = "The catalog id", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "The source catalog version from a synchronization perspective", required = true) @PathVariable final String versionId,
			@ApiParam(value = "The target catalog version from a synchronization perspective", required = true) @PathVariable final String targetCatalogVersion,
			@ApiParam(value = "The uid of the page from which to retrieve the synchronization status", required = true) @PathVariable final String pageId)
	{
		final SyncRequestData syncRequestData = buildSyncRequestData(catalogId, versionId, targetCatalogVersion);
		final ItemSynchronizationData itemSynchronizationData = new ItemSynchronizationData();
		itemSynchronizationData.setItemId(pageId);
		itemSynchronizationData.setItemType(_TYPECODE);

		final SyncItemStatusData syncItemStatus = getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData,
				itemSynchronizationData, getCmsSyncItemStatusConfig());
		return getDataMapper().map(syncItemStatus, SyncItemStatusWsDTO.class);
	}

	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	@ApiOperation(value = "Perform synchronization", notes = "Will perform synchronization status on a list of item identifier by their ItemSynchronizationWsDTO.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	public void performSync(
			@ApiParam(value = "The SynchronizationWsDTO containing the list of requested synchronizations", required = true) @RequestBody final SynchronizationWsDTO synchronizationWsDTO,
			@ApiParam(value = "The catalog id", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "The source catalog version from a synchronization perspective", required = true) @PathVariable final String versionId,
			@ApiParam(value = "The target catalog version from a synchronization perspective", required = true) @PathVariable final String targetCatalogVersion)
	{
		try
		{
			final SyncRequestData syncRequestData = buildSyncRequestData(catalogId, versionId, targetCatalogVersion);
			final SynchronizationData synchronizationData = getDataMapper().map(synchronizationWsDTO, SynchronizationData.class);

			getItemSynchronizationFacade().performItemSynchronization(syncRequestData, synchronizationData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected SyncRequestData buildSyncRequestData(final String catalogId, final String versionId,
			final String targetCatalogVersion)
	{
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);
		syncRequestData.setSourceVersionId(versionId);
		syncRequestData.setTargetVersionId(targetCatalogVersion);
		return syncRequestData;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected SyncItemStatusConfig getCmsSyncItemStatusConfig()
	{
		return cmsSyncItemStatusConfig;
	}

	protected ItemSynchronizationFacade getItemSynchronizationFacade()
	{
		return itemSynchronizationFacade;
	}
}
