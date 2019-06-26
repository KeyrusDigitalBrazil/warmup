/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.warehousingfacades.util;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.returns.service.impl.WarehousingReturnService;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import de.hybris.platform.warehousing.util.models.Addresses;
import de.hybris.platform.warehousing.util.models.BaseStores;
import de.hybris.platform.warehousing.util.models.DeliveryModes;
import de.hybris.platform.warehousing.util.models.Orders;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.StockLevels;
import de.hybris.platform.warehousing.util.models.Users;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousingfacades.order.data.PackagingInfoData;

import javax.annotation.Resource;

import java.util.Collection;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@IntegrationTest
public class BaseWarehousingFacadeIntegrationTest extends BaseFacadeIntegrationTest
{
	@Resource
	protected SourcingService sourcingService;
	@Resource
	protected ModelService modelService;
	@Resource
	protected Orders orders;
	@Resource
	protected BaseStores baseStores;
	@Resource
	protected Warehouses warehouses;
	@Resource
	protected Addresses addresses;
	@Resource
	protected StockLevels stockLevels;
	@Resource
	protected PointsOfService pointsOfService;
	@Resource
	protected Products products;
	@Resource
	protected AllocationService allocationService;
	@Resource
	protected WarehousingReturnService warehousingReturnService;
	@Resource
	protected Users users;
	@Resource
	protected DeliveryModes deliveryModes;
	@Resource
	protected EventService eventService;
	@Resource
	protected ReturnCallbackService returnCallbackService;
	@Resource
	protected EnumerationService enumerationService;

	private int timeOut = 2;

	protected static final int DEFAULT_CURRENT_PAGE = 0;
	protected static final int DEFAULT_PAGE_SIZE = 10;
	protected static final String DEFAULT_SORT = "asc";

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseWarehousingFacadeIntegrationTest.class);

	@Before
	public void setupShopper()
	{
		users.Nancy();
	}

	@Before
	public void setupBaseStore()
	{
		baseStores.NorthAmerica().setPointsOfService(Lists.newArrayList( //
				pointsOfService.Boston(), //
				pointsOfService.Montreal_Downtown() //
		));
		modelService.save(baseStores.NorthAmerica());
	}

	@Before
	public void setup()
	{
		try
		{
			importCsv("/impex/projectdata-dynamic-business-process-order.impex", WarehousingTestConstants.ENCODING);
			importCsv("/impex/projectdata-dynamic-business-process-consignment.impex", WarehousingTestConstants.ENCODING);
			importCsv("/impex/projectdata-dynamic-business-process-return.impex", WarehousingTestConstants.ENCODING);
			importCsv("/impex/projectdata-dynamic-business-process-sendReturnLabelEmail.impex", WarehousingTestConstants.ENCODING);
		}
		catch (final ImpExException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * create a default order and consignment
	 *
	 * @return OrderModel
	 */
	protected OrderModel createDefaultConsignmentAndOrder()
	{
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 4);
		final OrderModel order = orders.Camera_Shipped(7L);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);
		order.setStatus(OrderStatus.COMPLETED);
		modelService.save(order);
		consignmentResult.stream().forEach(result ->
		{
			result.setStatus(ConsignmentStatus.SHIPPED);
			modelService.save(result);
		});
		return order;
	}

	/**
	 * create a default returnRefund and approve it
	 *
	 * @param order
	 * @return RefundEntryModel
	 */
	protected RefundEntryModel createDefaultReturnRequest(final OrderModel order)
	{
		//when
		final ReturnRequestModel request = warehousingReturnService.createReturnRequest(order);
		final RefundEntryModel refundEntry = warehousingReturnService
				.createRefund(request, order.getEntries().get(0), "", 1L, ReturnAction.HOLD, RefundReason.DAMAGEDINTRANSIT);
		modelService.save(request);
		modelService.save(refundEntry);
		final CreateReturnEvent createReturnEvent = new CreateReturnEvent();
		createReturnEvent.setReturnRequest(request);
		getEventService().publishEvent(createReturnEvent);
		waitForReturnProcessComplete(request.getReturnProcess());
		return refundEntry;
	}

	/**
	 * approve the return
	 *
	 * @param refundEntry
	 * @return RefundEntryModel
	 */
	protected RefundEntryModel approveDefaultReturn(final RefundEntryModel refundEntry)
	{
		final ReturnRequestModel request = refundEntry.getReturnRequest();
		try
		{
			getReturnCallbackService().onReturnApprovalResponse(new ReturnActionResponse(request));
			waitForReturnProcessComplete(request.getReturnProcess());
		}
		catch (final OrderReturnException e)
		{
			LOGGER.info("Error happened during approval for the return request [%s]", request.getRMA());
		}
		modelService.save(request);
		return refundEntry;
	}

	/**
	 * wait for the process to be complete before time out
	 *
	 * @param returnProcessModels
	 */
	protected void waitForReturnProcessComplete(final Collection<ReturnProcessModel> returnProcessModels)
	{
		int timeCount = 0;
		do
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				LOGGER.info("Error happened during Thread.sleep(1000)");
			}
			modelService.refresh(returnProcessModels.iterator().next());
		}
		while (ProcessState.RUNNING.equals(returnProcessModels.iterator().next().getProcessState()) && timeCount++ < timeOut);
	}

	/**
	 * create a default order and consignment
	 *
	 * @return RefundEntryModel
	 */
	protected RefundEntryModel createReturnAndReadyToAcceptGoods()
	{
		final RefundEntryModel refundEntry = approveDefaultReturn(createDefaultReturnRequest(createDefaultConsignmentAndOrder()));
		refundEntry.getReturnRequest().setStatus(ReturnStatus.WAIT);
		modelService.save(refundEntry);
		return refundEntry;
	}

	/**
	 * Creates a pageableData with provided page, pageSize and sort
	 *
	 * @param page
	 * 		current page number
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return a pageableData
	 */
	protected PageableData createPageable(final int page, final int pageSize, final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(page);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		return pageableData;
	}

	/**
	 * Creates a new packaging information for a consignment with default values.
	 *
	 * @return the new {@link PackagingInfoData}
	 */
	protected PackagingInfoData createPackagingInfo()
	{
		return createPackagingInfo("1", "1", "1", "1", "1", "in", "lb");
	}

	/**
	 * Creates a new packaging information for a consignment with the given attributes.
	 *
	 * @param width
	 * 		the width of the package
	 * @param height
	 * 		the height of the package
	 * @param length
	 * 		the length of the package
	 * @param grossWeight
	 * 		the gross weight of the package
	 * @param insuredValue
	 * 		the insured value of the package
	 * @param dimensionUnit
	 * 		the dimension unit of the package
	 * @param weightUnit
	 * 		the weight unit of the package
	 * @return the new {@link PackagingInfoData}
	 */
	protected PackagingInfoData createPackagingInfo(final String width, final String height, final String length,
			final String grossWeight, final String insuredValue, final String dimensionUnit, final String weightUnit)
	{
		final PackagingInfoData packagingInfoData = new PackagingInfoData();
		packagingInfoData.setWidth(width);
		packagingInfoData.setHeight(height);
		packagingInfoData.setLength(length);
		packagingInfoData.setGrossWeight(grossWeight);
		packagingInfoData.setInsuredValue(insuredValue);
		packagingInfoData.setDimensionUnit(dimensionUnit);
		packagingInfoData.setWeightUnit(weightUnit);

		return packagingInfoData;
	}

	/**
	 * Creates a default pageableData
	 *
	 * @return a pageableData
	 */
	protected PageableData createPageable()
	{
		return createPageable(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
	}

	public EventService getEventService()
	{
		return eventService;
	}

	protected ReturnCallbackService getReturnCallbackService()
	{
		return returnCallbackService;
	}

	public void setReturnCallbackService(final ReturnCallbackService returnCallbackService)
	{
		this.returnCallbackService = returnCallbackService;
	}

	public EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
