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
package com.sap.hybris.sapomsreturnprocess.actions;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapmodel.model.SAPReturnRequestsModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.model.RestockConfigModel;
import de.hybris.platform.warehousing.returns.RestockException;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;
import de.hybris.platform.warehousing.returns.strategy.RestockWarehouseSelectionStrategy;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;


import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;



/**
 * This class will update stock level data once the item is returned. It will fetch warehouse details from
 * conisgnmententry and create a stock level.
 *
 */
public class OmsInventoryUpdateAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(OmsInventoryUpdateAction.class);
	private RestockConfigService restockConfigService;
	private TimeService timeService;
	private RestockWarehouseSelectionStrategy restockWarehouseSelectionStrategy;
	private WarehouseStockService warehouseStockService;

	@Override
	public void executeAction(final ReturnProcessModel process) throws RetryLaterException, RestockException
	{
		LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();
		final RestockConfigModel restockConfig = getRestockConfigService().getRestockConfig();
		if (restockConfig != null && Boolean.TRUE.equals(restockConfig.getIsUpdateStockAfterReturn()))
		{
			Assert.notNull(restockConfig.getReturnedBinCode(), "Bin code cannot be null");
			final Set<SAPReturnRequestsModel> sapReturnRequests = getCorrespondingSapReturnRequests(returnRequest);
			if (sapReturnRequests != null)
			{
				Assert.isTrue(CollectionUtils.isNotEmpty(returnRequest.getReturnEntries()),
						String.format("No return entries found for the ReturnRequest: [%s]", returnRequest.getRMA()));


				for (final SAPReturnRequestsModel sapReturnRequestsModel : sapReturnRequests)
				{
					
					updateStockForSapReturnRequest(sapReturnRequestsModel);
				}

			}
			else
			{
				LOG.info("Stock could not be updated. Please update it manually");
			}
		}
	}

	private void updateStockForSapReturnRequest(final SAPReturnRequestsModel sapReturnRequestsModel) throws RetryLaterException, RestockException {
		final Set<ConsignmentEntryModel> consignmentEntries = sapReturnRequestsModel.getConsignmentsEntry();
		for (final ConsignmentEntryModel consignmentEntryModel : consignmentEntries)
					{
						final String productCode = consignmentEntryModel.getOrderEntry().getProduct().getCode();
						final WarehouseModel warehouse = sapReturnRequestsModel.getReturnWarehouse();
						final int initialQuantityOnHand = consignmentEntryModel.getReturnQuantity().intValue();
						final Date releaseDate = getCurrentDateWithDelayDaysBeforeRestock();
						final InStockStatus inStockStatus = null;
						final String bin = getRestockConfigService().getRestockConfig().getReturnedBinCode();
						getWarehouseStockService().createStockLevel(productCode, warehouse, initialQuantityOnHand, inStockStatus,
								releaseDate, bin);

					}

	}
	/**
	 * Finds the {@link WarehouseModel}, which can accept the returned good(s)
	 *
	 * @param returnRequest
	 *           the {@link ReturnRequestModel} for which goods need to be put back in stock
	 * @return the {@link WarehouseModel} which can accept the returned good(s) from the given {@link ReturnRequestModel}
	 *         .
	 */
	protected Set<SAPReturnRequestsModel> getCorrespondingSapReturnRequests(final ReturnRequestModel returnRequest)
	{
		final Set<SAPReturnRequestsModel> sapReturnRequests = returnRequest.getSapReturnRequests();
		if (sapReturnRequests == null)
		{
			LOG.info(String.format(
					"No return warehouse set for the Return Request: [%s], applying RestockWarehouseSelectionStrategy to find the warehouse",
					returnRequest.getRMA()));
		}
		return sapReturnRequests;
	}

	/**
	 * Calculates the current date - # of delay days before restock according to a property.
	 */
	protected Date getCurrentDateWithDelayDaysBeforeRestock() throws RestockException
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getTimeService().getCurrentTime());

		if (getRestockConfigService().getRestockConfig() != null)
		{
			final int delayDays = getRestockConfigService().getRestockConfig().getDelayDaysBeforeRestock();

			cal.add(Calendar.DATE, delayDays);
		}
		return cal.getTime();
	}

	@Required
	public void setRestockConfigService(final RestockConfigService restockConfigService)
	{
		this.restockConfigService = restockConfigService;
	}

	protected RestockConfigService getRestockConfigService()
	{
		return restockConfigService;
	}

	protected RestockWarehouseSelectionStrategy getRestockWarehouseSelectionStrategy()
	{
		return restockWarehouseSelectionStrategy;
	}

	@Required
	public void setRestockWarehouseSelectionStrategy(final RestockWarehouseSelectionStrategy restockWarehouseSelectionStrategy)
	{
		this.restockWarehouseSelectionStrategy = restockWarehouseSelectionStrategy;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}


	protected WarehouseStockService getWarehouseStockService()
	{
		return warehouseStockService;
	}

	@Required
	public void setWarehouseStockService(final WarehouseStockService warehouseStockService)
	{
		this.warehouseStockService = warehouseStockService;
	}

}
