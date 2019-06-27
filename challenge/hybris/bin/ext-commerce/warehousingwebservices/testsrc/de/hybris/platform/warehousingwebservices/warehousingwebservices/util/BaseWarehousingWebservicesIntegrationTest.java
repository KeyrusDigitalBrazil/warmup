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

package de.hybris.platform.warehousingwebservices.warehousingwebservices.util;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.commercewebservicescommons.dto.order.ConsignmentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryWsDTO;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.ordermanagementwebservices.dto.payment.PaymentTransactionEntryWsDTO;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.constants.WarehousingConstants;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.returns.service.impl.WarehousingReturnService;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import de.hybris.platform.warehousing.util.models.Addresses;
import de.hybris.platform.warehousing.util.models.BaseSites;
import de.hybris.platform.warehousing.util.models.BaseStores;
import de.hybris.platform.warehousing.util.models.CommentTypes;
import de.hybris.platform.warehousing.util.models.Components;
import de.hybris.platform.warehousing.util.models.DeliveryModes;
import de.hybris.platform.warehousing.util.models.Orders;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.StockLevels;
import de.hybris.platform.warehousing.util.models.Users;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.dto.asn.AsnWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentEntrySearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentReallocationWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentStatusListWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.DeclineEntryWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.DeclineReasonListWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.PackagingInfoWsDTO;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelWsDto;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentReasonsWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentsWsDTO;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseCodesWsDto;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseWsDto;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class BaseWarehousingWebservicesIntegrationTest extends BaseWebservicesIntegrationTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseWarehousingWebservicesIntegrationTest.class);
	
	protected static final String DEFAULT_FIELDS = "DEFAULT";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	protected static final String DEFAULT_PAGE_SIZE = "100";
	protected static final String STOCKLEVELS = "stocklevels";
	protected static final String ASNS = "asns";

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
	protected BusinessProcessService businessProcessService;
	@Resource
	protected Users users;
	@Resource
	protected DeliveryModes deliveryModes;
	@Resource
	protected EventService eventService;
	@Resource
	protected ReturnCallbackService returnCallbackService;
	@Resource
	protected BaseSites baseSites;
	@Resource
	protected Components components;
	@Resource
	protected CommentTypes commentTypes;
	@Resource
	protected FlexibleSearchService flexibleSearchService;
	private int timeOut = 4;

	public void setup()
	{
		cleanUpData();

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

		users.Nancy();
		baseStores.NorthAmerica().setPointsOfService(Lists.newArrayList( //
				pointsOfService.Boston(), //
				pointsOfService.Montreal_Downtown() //
		));
		saveAll();
	}

	/**
	 * Saves any unsaved models.
	 */
	protected void saveAll()
	{
		modelService.saveAll();
	}

	protected void cleanUpData()
	{
		cleanUpModel("Order");
		cleanUpModel("Consignment");
		cleanUpModel("BusinessProcess");
		cleanUpModel("InventoryEvent");
		cleanUpModel("ConsignmentEntryEvent");
		cleanUpModel("SourcingBan");
		cleanUpModel("PickUpDeliveryMode");
		cleanUpModel("TaskCondition");
		cleanUpModel("Task");
		cleanUpModel("StockLevel");
		cleanUpModel("OrderCancelConfig");
		cleanUpModel("RestockConfig");
		cleanUpModel("BaseStore");
		cleanUpModel("PointOfService");
		cleanUpModel("Warehouse");
	}

	protected void cleanUpModel(final String modelName)
	{
		try
		{
			final SearchResult<FlexibleSearchQuery> result = flexibleSearchService.search("SELECT {pk} FROM {" + modelName + "}");
			if (result.getCount() != 0)
				modelService.removeAll(result.getResult());
		}
		catch (final NullPointerException e)
		{
			//do nothing
		}
	}

	protected StockLevelSearchPageWsDto getStockLevelsForWarehouseCodeByDefault(final String code)
	{
		return getDefaultRestCall("stocklevels/warehouses/" + code, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				StockLevelSearchPageWsDto.class);
	}

	protected WarehouseSearchPageWsDto getAllWarehousesByDefault()
	{
		return getDefaultRestCall("basestores/north-america/warehouses", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				WarehouseSearchPageWsDto.class);
	}

	protected ConsignmentSearchPageWsDto getAllConsignmentsByDefault()
	{
		return getDefaultRestCall("consignments", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ConsignmentSearchPageWsDto.class);
	}

	protected WarehouseWsDto getWarehouseByDefault(final String code)
	{
		return getDefaultRestCall("warehouses/" + code, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				WarehouseWsDto.class);
	}

	protected ConsignmentWsDTO getConsignmentsForCodeByDefault(final String code)
	{
		return getDefaultRestCall("consignments/" + code, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ConsignmentWsDTO.class);
	}

	protected ConsignmentStatusListWsDTO getConsignmentStatusByDefault()
	{
		return getDefaultRestCall("consignments/statuses", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ConsignmentStatusListWsDTO.class);
	}

	protected DeclineReasonListWsDTO getDeclineReasonsByDefault()
	{
		return getDefaultRestCall("consignments/decline-reasons", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				DeclineReasonListWsDTO.class);
	}

	protected ConsignmentEntrySearchPageWsDto getConsignmentEntriesByDefault(final String code)
	{
		return getDefaultRestCall("consignments/" + code + "/entries", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				ConsignmentEntrySearchPageWsDto.class);
	}

	protected WarehouseSearchPageWsDto getSourcingLocationsByDefault(final String code)
	{
		return getDefaultRestCall("consignments/" + code + "/sourcing-locations", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE,
				DEFAULT_PAGE_SIZE, WarehouseSearchPageWsDto.class);
	}

	protected PointOfServiceWsDTO getPointOfServiceByDefault(final String name)
	{
		return getDefaultRestCall("/pointofservices/" + name, DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				PointOfServiceWsDTO.class);
	}

	protected WarehouseSearchPageWsDto getWarehouseForPointOfServiceByDefault(final String pointOfService)
	{
		return getDefaultRestCall("/pointofservices/" + pointOfService + "/warehouses", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE,
				DEFAULT_PAGE_SIZE, WarehouseSearchPageWsDto.class);
	}

	protected PointOfServiceWsDTO postUpdatePointOfServiceWarehouses(final String name,
			final WarehouseCodesWsDto warehouseCodesWsDTO)
	{
		return postDefaultRestCall("/pointofservices/" + name + "/warehouses", DEFAULT_FIELDS, warehouseCodesWsDTO,
				PointOfServiceWsDTO.class);
	}

	protected Response deleteWarehousesFromPointOfService(final String name, final String warehouseCode)
	{
		return deleteDefaultRestCall("/pointofservices/" + name + "/warehouses/" + warehouseCode, DEFAULT_FIELDS);
	}

	protected PointOfServiceWsDTO putUpdatePointOfServiceAddress(final String pointOfService, final AddressWsDTO addressWsDTO)
	{
		return putDefaultRestCall("/pointofservices/" + pointOfService + "/address", DEFAULT_FIELDS, addressWsDTO,
				PointOfServiceWsDTO.class);
	}

	protected Response postAcceptGoodsByDefault(final String code)
	{
		return postDefaultRestCall("returns/" + code + "/accept-goods", DEFAULT_FIELDS, null);
	}

	protected Response postStockLevelByDefault(final StockLevelWsDto newStock)
	{
		return postDefaultRestCall(STOCKLEVELS, DEFAULT_FIELDS, newStock);
	}

	protected Response postAsnByDefault(final AsnWsDTO newAsn)
	{
		return postDefaultRestCall(ASNS, DEFAULT_FIELDS, newAsn);
	}

	protected Response postConfirmAsnReceiptByDefault(final String asnInternalId)
	{
		return postDefaultRestCall(ASNS + "/" + asnInternalId + "/confirm-receipt", DEFAULT_FIELDS, null);
	}

	protected Response postStockLevelAdjustmentByDefault_Reponse(final String productCode, final String warehouseCode,
			final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO)
	{
		return postDefaultRestCall(STOCKLEVELS + "/product/" + productCode + "/warehouse/" + warehouseCode + "/adjustment",
				DEFAULT_FIELDS, stockLevelAdjustmentsWsDTO);
	}

	protected StockLevelAdjustmentsWsDTO postStockLevelAdjustmentByDefault(final String productCode, final String warehouseCode,
			final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO)
	{
		return postDefaultRestCall(STOCKLEVELS + "/product/" + productCode + "/warehouse/" + warehouseCode + "/adjustment",
				DEFAULT_FIELDS, stockLevelAdjustmentsWsDTO, StockLevelAdjustmentsWsDTO.class);
	}

	protected StockLevelWsDto postStockLevelByDefault_WithReturnType_StockLevelWsDto(final StockLevelWsDto newStock)
	{
		return postDefaultRestCall(STOCKLEVELS, DEFAULT_FIELDS, newStock, StockLevelWsDto.class);
	}

	protected StockLevelAdjustmentReasonsWsDTO getStockLevelAdjustmentReasons()
	{
		return getDefaultRestCall(STOCKLEVELS + "/adjustment-reasons", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE,
				StockLevelAdjustmentReasonsWsDTO.class);
	}

	protected PackagingInfoWsDTO getPackagingInfoByDefault(final String code)
	{
		return getDefaultRestCall("consignments/" + code + "/packaging-info", DEFAULT_FIELDS, DEFAULT_CURRENT_PAGE,
				DEFAULT_PAGE_SIZE, PackagingInfoWsDTO.class);
	}

	protected ConsignmentWsDTO updatePackagingInfoByDefault(final String code, final PackagingInfoWsDTO packagingInfoWsDTO)
	{
		return putDefaultRestCall("consignments/" + code + "/packaging-info", DEFAULT_FIELDS, packagingInfoWsDTO,
				ConsignmentWsDTO.class);
	}

	protected Response postResourceOrder(final String orderCode)
	{
		return postDefaultRestCall("orders/" + orderCode + "/re-source", DEFAULT_FIELDS, null);
	}

	protected Response postPutOrderOnHold(final String orderCode)
	{
		return postDefaultRestCall("orders/" + orderCode + "/on-hold", DEFAULT_FIELDS, null);
	}

	protected Response postPackConsignmentDefault(final String consignmentCode)
	{
		return postEmptyBodyRestCall("consignments/" + consignmentCode + "/pack");
	}

	protected Response postPackConsignmentWithPrintSlip(final String consignmentCode, final String printSlip)
	{
		return getWsRequestBuilder().path("consignments/" + consignmentCode + "/pack").queryParam("printSlip", printSlip).build()
				.post(Entity.entity(null, MediaType.TEXT_HTML));
	}

	protected Response postPickConsignmentDefault(final String consignmentCode)
	{
		return postEmptyBodyRestCall("consignments/" + consignmentCode + "/pick");
	}

	protected Response postPickConsignmentWithPrintSlip(final String consignmentCode, final String printSlip)
	{
		return getWsRequestBuilder().path("consignments/" + consignmentCode + "/pick").queryParam("printSlip", printSlip).build()
				.post(Entity.entity(null, MediaType.TEXT_HTML));
	}

	protected PaymentTransactionEntryWsDTO postTakePaymentRestCall(final String consignmentCode)
	{
		return postDefaultRestCall("consignments/" + consignmentCode + "/take-payment", DEFAULT_FIELDS, null,
				PaymentTransactionEntryWsDTO.class);
	}

	protected Response manuallyReleasePaymentCaptureRestCall(final String consignmentCode)
	{
		return postDefaultRestCall("consignments/" + consignmentCode + "/manual/capture-payment", DEFAULT_FIELDS, null);
	}

	protected Response manuallyReleaseTaxCommitRestCall(final String consignmentCode)
	{
		return postDefaultRestCall("consignments/" + consignmentCode + "/manual/commit-tax", DEFAULT_FIELDS, null);
	}

	/**
	 * Builds a GET rest call.
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param currentPage
	 * 		the current page of the request
	 * @param pageSize
	 * 		total page size
	 * @return the result of the call
	 */
	protected <T> T getDefaultRestCall(final String path, final String fields, final String currentPage, final String pageSize,
			final Class<T> responseType)
	{
		final Response result = getWsRequestBuilder().path(path).queryParam("fields", fields).queryParam("currentPage", currentPage)
				.queryParam("pageSize", pageSize).build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		return result.readEntity(responseType);
	}

	/**
	 * Builds a POST rest call.
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param <T>
	 * @return the result of the call
	 */
	protected <T> Response postDefaultRestCall(final String path, final String fields, final T requestBodyWsDTO)
	{
		final Response result = getWsRequestBuilder().path(path).queryParam("fields", fields).build()
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		return result;
	}

	/**
	 * this method is to build the rest call post with the return type <T>
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param <T>
	 * 		the current dto which is to be updated
	 * @param responseType
	 * 		type of class to return
	 * @param <T>
	 * @return the request class to return after the execution of the call
	 */
	protected <S, T> T postDefaultRestCall(final String path, final String fields, final S requestBodyWsDTO,
			final Class<T> responseType)
	{
		return getWsRequestBuilder().path(path).queryParam("fields", fields).build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON), responseType);
	}

	/**
	 * this method is to build the rest call with null body for post with the return type Response
	 *
	 * @param path
	 * 		the url for the call
	 * @return {@link Response} the result of the call
	 */
	protected Response postEmptyBodyRestCall(final String path)
	{
		return getWsRequestBuilder().path(path).build().post(Entity.entity(null, MediaType.TEXT_HTML));
	}

	/**
	 * this method is to build the rest call with body for post with the return type Response
	 *
	 * @param path
	 * 		the url of the call
	 * @param requestBodyWsDTO
	 * 		the request body
	 * @return {@link Response} the result of the call
	 */
	protected <T> Response postBodyRestCall(final String path, final T requestBodyWsDTO)
	{
		return getWsRequestBuilder().path(path).build().accept(MediaType.TEXT_HTML)
				.post(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON));
	}

	/**
	 * this method is to build the rest call with null body for get with the return type Response
	 *
	 * @param path
	 * @return {@link Response}
	 */
	protected Response getEmptyRestCall(final String path)
	{
		return getWsRequestBuilder().path(path).build().get();
	}

	/**
	 * Builds a PUT rest call
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @param requestBodyWsDTO
	 * 		the current dto which is to be updated
	 * @param responseType
	 * 		type of class to return
	 * @param <T>
	 * @return the request class to return after the execution of the call
	 */
	protected <S, T> T putDefaultRestCall(final String path, final String fields, final S requestBodyWsDTO,
			final Class<T> responseType)
	{
		return getWsRequestBuilder().path(path).queryParam("fields", fields).build().accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(requestBodyWsDTO, MediaType.APPLICATION_JSON), responseType);
	}

	/**
	 * Builds a DELETE rest call
	 *
	 * @param path
	 * 		the url for the call
	 * @param fields
	 * 		contains pagination information
	 * @return the request class to return after the execution of the call
	 */
	protected Response deleteDefaultRestCall(final String path, final String fields)
	{
		return getWsRequestBuilder().path(path).queryParam("fields", fields).build().delete();
	}

	/**
	 * Validates the first {@link ErrorWsDTO} content of the {@link ErrorListWsDTO} of the bad request
	 *
	 * @param response
	 * 		bad request response
	 * @param errorReason
	 * 		error reason
	 * @param errorSubject
	 * 		error subject
	 * @param ErrorSubjectType
	 * 		error subject type
	 */
	protected void assertBadRequestWithContent(final Response response, final String errorReason, final String errorSubject,
			final String ErrorSubjectType)
	{
		assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals(error.getReason(), errorReason);
		assertEquals(error.getSubject(), errorSubject);
		assertEquals(error.getSubjectType(), ErrorSubjectType);
	}

	/**
	 * Creates a default order and consignment.
	 *
	 * @return OrderModel the newly created order
	 */
	protected OrderModel createShippedConsignmentAndOrder()
	{
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 4);
		final OrderModel order = orders.Camera_Shipped(7L);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);
		order.setStatus(OrderStatus.COMPLETED);
		consignmentResult.forEach(result -> {
			result.setStatus(ConsignmentStatus.SHIPPED);
			startConsignmentProcess(result);
		});
		modelService.saveAll();
		return order;
	}

	/**
	 * Creates an order that fails sourcing.
	 *
	 * @return {@link OrderModel} that failed sourcing
	 */
	protected OrderModel createFailedSourcedOrder()
	{
		final OrderModel order = orders.Camera_Shipped(7L);
		sourcingService.sourceOrder(order);
		order.setStatus(OrderStatus.SUSPENDED);
		modelService.saveAll();

		return order;
	}

	/**
	 * Stars a business process for the given {@link ConsignmentModel}
	 *
	 * @param consignment
	 * 		the {@link ConsignmentModel} for which a process will be started
	 */
	protected void startConsignmentProcess(final ConsignmentModel consignment)
	{
		final ConsignmentProcessModel subProcess = getBusinessProcessService()
				.createProcess(consignment.getCode() + WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX, "consignment-process");
		subProcess.setConsignment(consignment);
		modelService.save(subProcess);
		LOGGER.info("Start Consignment sub-process: '" + subProcess.getCode() + "'");
		getBusinessProcessService().startProcess(subProcess);
	}

	/**
	 * Creates a default return request and approves it.
	 *
	 * @param order
	 * 		the order with which to create the return request
	 * @return RefundEntryModel the created refund entry
	 */
	protected RefundEntryModel createApprovedReturnRequest(final OrderModel order)
	{
		//when
		final ReturnRequestModel request = warehousingReturnService.createReturnRequest(order);
		final RefundEntryModel refundEntry = warehousingReturnService
				.createRefund(request, order.getEntries().get(0), "", 1L, ReturnAction.HOLD, RefundReason.DAMAGEDINTRANSIT);
		final CreateReturnEvent createReturnEvent = new CreateReturnEvent();
		createReturnEvent.setReturnRequest(request);
		getEventService().publishEvent(createReturnEvent);
		try
		{
			waitForReturnProcessComplete(request.getReturnProcess());
			getReturnCallbackService().onReturnApprovalResponse(new ReturnActionResponse(request));
			waitForReturnProcessComplete(request.getReturnProcess());
		}
		catch (final OrderReturnException e)
		{
			LOGGER.info("Error happened during approval for the return request [%s]", request.getRMA());
		}
		modelService.saveAll();
		return refundEntry;
	}

	/**
	 * Waits for the process to complete before the time out.
	 *
	 * @param returnProcessModels
	 * 		a collection of return processes
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
	 * Creates a US address.
	 *
	 * @return the newly created address
	 */
	protected AddressWsDTO createUsAddress()
	{
		final AddressWsDTO addressWsDTO = new AddressWsDTO();
		addressWsDTO.setTown("New York");
		addressWsDTO.setLine1("5th Avenue");
		addressWsDTO.setPostalCode("79777");
		final CountryWsDTO countryWsDTO = new CountryWsDTO();
		countryWsDTO.setIsocode("US");
		addressWsDTO.setCountry(countryWsDTO);
		return addressWsDTO;
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
	 * @return the new {@link PackagingInfoWsDTO}
	 */
	protected PackagingInfoWsDTO createPackagingInfo(final String width, final String height, final String length,
			final String grossWeight, final String insuredValue, final String dimensionUnit, final String weightUnit)
	{
		final PackagingInfoWsDTO packagingInfoWsDTO = new PackagingInfoWsDTO();
		packagingInfoWsDTO.setWidth(width);
		packagingInfoWsDTO.setHeight(height);
		packagingInfoWsDTO.setLength(length);
		packagingInfoWsDTO.setGrossWeight(grossWeight);
		packagingInfoWsDTO.setInsuredValue(insuredValue);
		packagingInfoWsDTO.setDimensionUnit(dimensionUnit);
		packagingInfoWsDTO.setWeightUnit(weightUnit);

		return packagingInfoWsDTO;
	}

	/**
	 * Creates a {@link ConsignmentReallocationWsDTO}
	 *
	 * @return the newly created {@link ConsignmentReallocationWsDTO}
	 */
	protected ConsignmentReallocationWsDTO createConsignmentReallocationWsDTO()
	{
		final ConsignmentReallocationWsDTO consignmentReallocationWsDTO = new ConsignmentReallocationWsDTO();
		final DeclineEntryWsDTO declineEntryWsDTO = new DeclineEntryWsDTO();
		declineEntryWsDTO.setQuantity(2L);
		declineEntryWsDTO.setProductCode("camera");
		declineEntryWsDTO.setReason(DeclineReason.DAMAGED.toString());
		consignmentReallocationWsDTO.setDeclineEntries(Collections.singletonList(declineEntryWsDTO));
		return consignmentReallocationWsDTO;
	}

	/**
	 * Creates a return request in a {@link de.hybris.platform.basecommerce.enums.ReturnStatus#WAIT}.
	 *
	 * @return RefundEntryModel the created return request
	 */
	protected RefundEntryModel createReturnAndReadyToAcceptGoods()
	{
		final RefundEntryModel refundEntry = createApprovedReturnRequest(createShippedConsignmentAndOrder());
		modelService.saveAll();
		return refundEntry;
	}

	public EventService getEventService()
	{
		return eventService;
	}

	public ReturnCallbackService getReturnCallbackService()
	{
		return returnCallbackService;
	}

	public void setReturnCallbackService(final ReturnCallbackService returnCallbackService)
	{
		this.returnCallbackService = returnCallbackService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
