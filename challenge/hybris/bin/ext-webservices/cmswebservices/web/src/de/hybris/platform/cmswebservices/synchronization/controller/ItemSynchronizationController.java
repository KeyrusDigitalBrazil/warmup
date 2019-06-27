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
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.ItemSynchronizationFacade;
import de.hybris.platform.cmswebservices.data.SyncItemStatusWsDTO;
import de.hybris.platform.cmswebservices.data.SynchronizationWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.*;


/**
 * Controller to retrieve complex synchronization status for and to perform a synchronization on a given
 * {@link AbstractPageModel}
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam sourceCatalogVersion identifier of the source catalog version
 * @pathparam sourceCatalogVersion identifier of the target catalog version
 */
@Controller
@RequestMapping(API_VERSION
		+ "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/synchronizations/versions/{targetCatalogVersion}")
public class ItemSynchronizationController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemSynchronizationController.class);

	@Autowired
	private ItemSynchronizationFacade itemSynchronizationFacade;

	@Autowired
	private DataMapper dataMapper;

	@RequestMapping(value = "/pages/{pageId}", method = GET)
	@ResponseBody
	@ApiOperation(value = "Get page synchronization status", notes = "Builds the synchronization status of a AbstractPageModel page.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "DTO containing the complex synchronization status of the AbstractPageModel page", response = SyncItemStatusWsDTO.class)
	})
	public SyncItemStatusWsDTO getPageSynchronizationStatus(
			@ApiParam(value = "The catalog id", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "The source catalog version from a synchronization perspective", required = true) @PathVariable final String versionId,
			@ApiParam(value = "The target catalog version from a synchronization perspective", required = true) @PathVariable final String targetCatalogVersion,
			@ApiParam(value = "The uid of the page from which to retrieve the synchronization status", required = true) @PathVariable final String pageId)
	{
		final ItemSynchronizationData itemSynchronizationData = getItemSynchronizationData(pageId, AbstractPageModel._TYPECODE);

		final SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);

		return getDataMapper().map(
				getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData),
				SyncItemStatusWsDTO.class);
	}

	@RequestMapping(value = "/slots/{slotId}", method = GET)
	@ResponseBody
	@ApiOperation(value = "Get slot synchronization status", notes = "Builds the synchronization status of a ContentSlotModel slot.")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "DTO containing the complex synchronization status of the ContentSlotModel page", response = SyncItemStatusWsDTO.class)
	})
	public SyncItemStatusWsDTO getSlotSynchronizationStatus(
			@ApiParam(value = "The catalog id", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "The source catalog version from a synchronization perspective", required = true) @PathVariable final String versionId,
			@ApiParam(value = "The target catalog version from a synchronization perspective", required = true) @PathVariable final String targetCatalogVersion,
			@ApiParam(value = "The uid of the slot from which to retrieve the synchronization status", required = true) @PathVariable final String slotId)
	{
		final ItemSynchronizationData itemSynchronizationData = getItemSynchronizationData(slotId, ContentSlotModel._TYPECODE);

		final SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);

		return getDataMapper().map(
				getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData),
				SyncItemStatusWsDTO.class);
	}

	@RequestMapping(value = "/items/{componentId}", method = GET)
	@ResponseBody
	@ApiOperation(value = "Get component synchronization status", notes = "uilds the synchronization status of a AbstractCMSComponentModel component")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	@ApiResponses(
	{
			@ApiResponse(code = 200, message = "DTO containing the complex synchronization status of the AbstractCMSComponentModel page", response = SyncItemStatusWsDTO.class)
	})
	public SyncItemStatusWsDTO getComponentSynchronizationStatus(
			@ApiParam(value = "The catalog id", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "The source catalog version from a synchronization perspective", required = true) @PathVariable final String versionId,
			@ApiParam(value = "The target catalog version from a synchronization perspective", required = true) @PathVariable final String targetCatalogVersion,
			@ApiParam(value = "The uid of the component from which to retrieve the synchronization status", required = true) @PathVariable final String componentId)
	{
		final ItemSynchronizationData itemSynchronizationData = getItemSynchronizationData(componentId,
				AbstractCMSComponentModel._TYPECODE);

		final SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);

		return getDataMapper().map(
				getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData),
				SyncItemStatusWsDTO.class);
	}

	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	@ApiOperation(value = "Perform sync", notes = "Will perform synchronization status on a list of ItemModel identifier by their ItemSynchronizationWsDTO")
	@ApiImplicitParam(name = "siteId", value = "The site identifier", required = true, dataType = "string", paramType = "path")
	public void performSync(
			@ApiParam(value = "The catalog id", required = true) @RequestBody final SynchronizationWsDTO synchronizationWsDTO,
			@ApiParam(value = "The source catalog version from a synchronization perspective", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "The target catalog version from a synchronization perspective", required = true) @PathVariable final String versionId,
			@ApiParam(value = "The SynchronizationWsDTO containing the list of requested synchronizations", required = true) @PathVariable final String targetCatalogVersion)
	{

		try
		{
			final SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);

			final SynchronizationData synchronizationData = getDataMapper().map(synchronizationWsDTO, SynchronizationData.class);

			getItemSynchronizationFacade().performItemSynchronization(syncRequestData, synchronizationData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Returns the synchronization request data
	 *
	 * @param catalogId
	 *           the content catalog to be synchronized
	 * @param sourceCatalogVersion
	 *           the source catalog version
	 * @param targetCatalogVersion
	 *           the target catalog version
	 * @return the synch request data
	 */
	protected SyncRequestData getSyncRequestData(final @PathVariable String catalogId,
			final @PathVariable String sourceCatalogVersion,
			final @PathVariable String targetCatalogVersion)
	{
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);
		syncRequestData.setSourceVersionId(sourceCatalogVersion);
		syncRequestData.setTargetVersionId(targetCatalogVersion);
		return syncRequestData;
	}

	/**
	 * Returns the Item Synchronization Data for a given itemId and item type
	 *
	 * @param itemId
	 *           the item id that will be used as a root for synchronization
	 * @param itemType
	 *           the item type
	 * @return the item synch data
	 */
	protected ItemSynchronizationData getItemSynchronizationData(final String itemId, final String itemType)
	{
		final ItemSynchronizationData itemSynchronizationData = new ItemSynchronizationData();
		itemSynchronizationData.setItemId(itemId);
		itemSynchronizationData.setItemType(itemType);
		return itemSynchronizationData;
	}

	protected ItemSynchronizationFacade getItemSynchronizationFacade()
	{
		return itemSynchronizationFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}
}
