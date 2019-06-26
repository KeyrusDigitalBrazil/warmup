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
 *
 */
package de.hybris.platform.ordermanagementfacades.returns.impl;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryModificationData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationData;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.localization.Localization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;


/**
 * Order management default Return Facade implementation for the {@link OmsReturnFacade}
 */
public class DefaultOmsReturnFacade extends OmsBaseFacade implements OmsReturnFacade
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultOmsReturnFacade.class);

	private Converter<ReturnRequestModel, ReturnRequestData> returnConverter;
	private Converter<ReturnRequestModel, ReturnRequestData> returnHistoryConverter;
	private Converter<ReturnEntryModel, ReturnEntryData> returnEntryConverter;
	private PagedGenericDao<ReturnRequestModel> returnPagedGenericDao;
	private PagedGenericDao<ReturnEntryModel> returnEntryPagedGenericDao;
	private GenericDao<ReturnRequestModel> returnGenericDao;
	private EnumerationService enumerationService;
	private ImpersonationService impersonationService;
	private ReturnCallbackService returnCallbackService;
	private ReturnService returnService;
	private RefundService refundService;
	private OrderService orderService;
	private EventService eventService;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private CustomerAccountService customerAccountService;
	private Set<ReturnStatus> invalidReturnStatusForRefundDeliveryCost;


	@Override
	public SearchPageData<ReturnRequestData> getReturns(PageableData pageableData)
	{
		SearchPageData<ReturnRequestModel> returnSearchPageData = getReturnPagedGenericDao().find(pageableData);
		return convertSearchPageData(returnSearchPageData, getReturnConverter());
	}

	@Override
	public SearchPageData<ReturnRequestData> getPagedReturnRequestsByCurrentUser(final PageableData pageableData,
			final ReturnStatus... returnStatuses)
	{
		validateParameterNotNull(pageableData, "PageableData cannot be null");
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<ReturnRequestModel> returnRequests = getCustomerAccountService()
				.getReturnRequestsByCustomerAndStore(customer, currentBaseStore, returnStatuses, pageableData);

		return convertSearchPageData(returnRequests, getReturnHistoryConverter());
	}

	@Override
	public SearchPageData<ReturnRequestData> getReturnsByStatuses(PageableData pageableData, Set<ReturnStatus> returnStatusSet)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(ReturnRequestModel.STATUS, returnStatusSet);
		return convertSearchPageData(getReturnPagedGenericDao().find(params, pageableData), getReturnConverter());
	}

	@Override
	public ReturnRequestData getReturnForReturnCode(String code)
	{
		return getReturnConverter().convert(getReturnRequestModelForCode(code));
	}

	@Override
	public List<CancelReason> getCancelReasons()
	{
		return getEnumerationService().getEnumerationValues(CancelReason._TYPECODE);
	}

	@Override
	public List<ReturnStatus> getReturnStatuses()
	{
		return getEnumerationService().getEnumerationValues(ReturnStatus._TYPECODE);
	}

	@Override
	public List<RefundReason> getRefundReasons()
	{
		return getEnumerationService().getEnumerationValues(RefundReason._TYPECODE);
	}

	@Override
	public List<ReturnAction> getReturnActions()
	{
		return getEnumerationService().getEnumerationValues(ReturnAction._TYPECODE);
	}

	@Override
	public SearchPageData<ReturnEntryData> getReturnEntriesForReturnCode(String code, PageableData pageableData)
	{
		final ReturnRequestModel returnReq = getReturnRequestModelForCode(code);

		final Map<String, ReturnRequestModel> returnEntryParams = new HashMap<>();
		returnEntryParams.put(ReturnEntryModel.RETURNREQUEST, returnReq);
		return convertSearchPageData(getReturnEntryPagedGenericDao().find(returnEntryParams, pageableData),
				getReturnEntryConverter());
	}

	@Override
	public void approveReturnRequest(final String code)
	{
		final ReturnRequestModel returnRequestModel = getReturnRequestModelForCode(code);
		try
		{
			if (returnRequestModel.getStatus().equals(ReturnStatus.APPROVAL_PENDING))
			{
				getReturnCallbackService().onReturnApprovalResponse(new ReturnActionResponse(returnRequestModel));
			}
			else
			{
				throw new IllegalStateException(
						String.format(Localization.getLocalizedString("ordermanagementfacade.approvereturnrequest.error.wrongstatus"),
								ReturnStatus.APPROVAL_PENDING));
			}

		}
		catch (OrderReturnException e) //NOSONAR
		{
			LOGGER.error(String.format("Error happened during approval for the return request [%s]",
					returnRequestModel.getRMA())); //NOSONAR
		}
	}

	@Override
	public ReturnRequestData updateReturnRequest(final String code,
			final ReturnRequestModificationData returnRequestModificationData)
	{
		validateParameterNotNullStandardMessage("returnRequestModificationData", returnRequestModificationData);
		if (returnRequestModificationData.getReturnEntries() != null)
		{
			returnRequestModificationData.getReturnEntries()
					.forEach(entry -> validateParameterNotNullStandardMessage("returnEntryModificationData", entry));
			isTrue(validateReturnEntryModificationData(returnRequestModificationData),
					Localization.getLocalizedString("ordermanagementfacade.returns.error.duplicatereturnentry"));
		}
		final ReturnRequestModel returnRequest = getReturnRequestModelForCode(code);
		validateReturnStatus(returnRequest, ReturnStatus.APPROVAL_PENDING);
		if (returnRequestModificationData.getRefundDeliveryCost() != null)
		{
			updateRefundDeliveryCost(returnRequestModificationData, returnRequest);
		}
		if (returnRequestModificationData.getReturnEntries() != null)
		{
			updateReturnEntries(returnRequestModificationData, returnRequest);
		}
		return getReturnConverter().convert(returnRequest);
	}

	@Override
	public void cancelReturnRequest(final CancelReturnRequestData cancelReturnRequestData)
	{
		// TODO: this must be refactored to account for the reason and notes in the CancelReturnRequestData after OMSE-1565 is done //NOSONAR
		notNull(cancelReturnRequestData,
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.multiple.cancelreturnrequestdata"));
		final ReturnRequestModel returnRequestModel = getReturnRequestModelForCode(cancelReturnRequestData.getCode());
		isTrue(isReturnCancellable(returnRequestModel.getStatus()),
				String.format(Localization.getLocalizedString("ordermanagementfacade.cancelreturnrequest.error.wrongstatus"),
						returnRequestModel.getStatus()));
		try
		{
			getReturnCallbackService().onReturnCancelResponse(new ReturnActionResponse(returnRequestModel));
		}
		catch (final OrderReturnException e)  //NOSONAR
		{
			LOGGER.error(
					String.format("Error happened during cancelling of return request [%s]", returnRequestModel.getRMA()));  //NOSONAR
		}
	}

	@Override
	public void requestManualPaymentReversalForReturnRequest(final String code)
	{
		notNull(code, Localization.getLocalizedString("ordermanagementfacade.returns.paymentreverse.validation.null.code"));
		final ReturnRequestModel returnRequestModel = getReturnRequestModelForCode(code);
		try
		{
			if (ReturnStatus.PAYMENT_REVERSAL_FAILED.equals(returnRequestModel.getStatus()))
			{
				getReturnService().requestManualPaymentReversalForReturnRequest(returnRequestModel);
			}
			else
			{
				throw new IllegalStateException(
						String.format(Localization.getLocalizedString("ordermanagementfacade.paymentreverse.error.wrongstatus"),
								ReturnStatus.PAYMENT_REVERSAL_FAILED));
			}
		}
		catch (final OrderReturnException e) //NOSONAR
		{
			LOGGER.error(String.format("Error happened during manual payment reversal for the return request [%s]",
					returnRequestModel.getRMA())); //NOSONAR
		}
	}

	@Override
	public void requestManualTaxReversalForReturnRequest(final String code)
	{
		validateParameterNotNull(code,
				Localization.getLocalizedString("ordermanagementfacade.returns.taxreverse.validation.null.code"));
		final ReturnRequestModel returnRequestModel = getReturnRequestModelForCode(code);
		try
		{
			if (ReturnStatus.TAX_REVERSAL_FAILED.equals(returnRequestModel.getStatus()))
			{
				getReturnService().requestManualTaxReversalForReturnRequest(returnRequestModel);
			}
			else
			{
				throw new IllegalStateException(
						String.format(Localization.getLocalizedString("ordermanagementfacade.taxreverse.error.wrongstatus"),
								ReturnStatus.TAX_REVERSAL_FAILED));
			}
		}
		catch (final OrderReturnException e) //NOSONAR
		{
			LOGGER.error(String.format("Error happened during manual tax reversal for the return request [%s]",
					returnRequestModel.getRMA())); //NOSONAR
		}
	}

	@Override
	public ReturnRequestData createReturnRequest(final ReturnRequestData returnRequestData)
	{
		validateReturnRequestData(returnRequestData);

		final OrderModel order = getOrderModelForCode(returnRequestData.getOrder().getCode());
		final ImpersonationContext context = new ImpersonationContext();
		context.setSite(order.getSite());
		final ReturnRequestModel returnRequestModel = getImpersonationService()
				.executeInContext(context, () -> createReturnRequestInContext(order, returnRequestData));

		return getReturnConverter().convert(returnRequestModel);
	}

	@Override
	public boolean isDeliveryCostRefundable(final String orderCode, final String returnRequestRMA)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		validateParameterNotNullStandardMessage("returnRequestRMA", returnRequestRMA);

		final List<ReturnRequestModel> previousReturns = returnService.getReturnRequests(orderCode);
		final boolean isDeliveryCostAlreadyRefunded = previousReturns.stream()
				.filter(previousReturn -> !getInvalidReturnStatusForRefundDeliveryCost().contains(previousReturn.getStatus())).
						anyMatch(previousReturn -> !returnRequestRMA.equals(previousReturn.getRMA()) && (
								previousReturn.getRefundDeliveryCost() != null && previousReturn.getRefundDeliveryCost()));
		return !isDeliveryCostAlreadyRefunded;
	}

	@Override
	public Boolean isCompleteReturn(final OrderModel orderModel, final ReturnRequestData returnRequestData)
	{
		validateParameterNotNullStandardMessage("orderModel", orderModel);
		validateParameterNotNullStandardMessage("returnRequestData", returnRequestData);

		Boolean completeReturn = (orderModel.getEntries().size() == returnRequestData.getReturnEntries().size());

		final Iterator<ReturnEntryData> returnEntryIterator = returnRequestData.getReturnEntries().iterator();
		while (returnEntryIterator.hasNext() && completeReturn)
		{
			final ReturnEntryData returnEntry = returnEntryIterator.next();
			completeReturn = orderModel.getEntries().stream().noneMatch(
					orderEntry -> orderEntry.getEntryNumber().equals(returnEntry.getOrderEntry().getEntryNumber()) && !orderEntry
							.getQuantity().equals(returnEntry.getExpectedQuantity()));
		}
		return completeReturn;
	}

	/**
	 * Recalculate the sub total after updating a refund amount
	 *
	 * @param returnRequest
	 * 		{@link ReturnRequestModel}
	 */
	protected BigDecimal recalculateSubtotal(final ReturnRequestModel returnRequest, final boolean completeReturn)
	{
		validateParameterNotNullStandardMessage("returnRequest", returnRequest);
		validateParameterNotNullStandardMessage("orderModel", returnRequest.getOrder());
		return completeReturn ? BigDecimal.valueOf(returnRequest.getOrder().getSubtotal()) : getTotalReturnEntriesAmount(returnRequest);
	}

	/**
	 * Sums the amount of {@link RefundEntryModel#getAmount()} in the {@link ReturnRequestModel}
	 *
	 * @param returnRequest
	 * 		{@link ReturnRequestModel}
	 */
	protected BigDecimal getTotalReturnEntriesAmount(final ReturnRequestModel returnRequest)
	{
		return returnRequest.getReturnEntries().stream().filter(returnEntry -> returnEntry instanceof RefundEntryModel)
				.map(refundEntry -> ((RefundEntryModel) refundEntry).getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Creates {@link ReturnRequestModel} in the {@link ImpersonationContext}
	 *
	 * @param order
	 * 		the {@link OrderModel} for which returnRequest needs to be created
	 * @param returnRequestData
	 * 		the {@link ReturnRequestData} containing required data to create {@link ReturnRequestModel}
	 * @return the newly created {@link ReturnRequestModel}
	 */
	protected ReturnRequestModel createReturnRequestInContext(final OrderModel order, final ReturnRequestData returnRequestData)
	{
		final Map<Integer, AbstractOrderEntryModel> orderEntries = new HashMap<>();
		returnRequestData.getReturnEntries().forEach(returnEntryData -> {
			final AbstractOrderEntryModel orderEntry = getOrderService()
					.getEntryForNumber(order, returnEntryData.getOrderEntry().getEntryNumber());
			validateParameterNotNullStandardMessage("orderEntry", orderEntry);
			if (!getReturnService().isReturnable(order, orderEntry, returnEntryData.getExpectedQuantity()))
			{
				throw new IllegalArgumentException(
						Localization.getLocalizedString("ordermanagementfacade.returns.validation.false.isReturnableQuantityPossible"));
			}
			orderEntries.put(returnEntryData.getOrderEntry().getEntryNumber(), orderEntry);
		});

		final boolean completeReturn = isCompleteReturn(order, returnRequestData);

		final ReturnRequestModel returnRequest = getReturnService().createReturnRequest(order);
		returnRequest.setRefundDeliveryCost(canRefundDeliveryCost(order.getCode(), returnRequestData.getRefundDeliveryCost()));

		returnRequestData.getReturnEntries().forEach(returnEntryData -> {
			final AbstractOrderEntryModel orderEntry = orderEntries.get(returnEntryData.getOrderEntry().getEntryNumber());
			final RefundEntryModel refundEntryToBeCreated = getReturnService()
					.createRefund(returnRequest, orderEntry, returnEntryData.getNotes(), returnEntryData.getExpectedQuantity(),
							returnEntryData.getAction(), returnEntryData.getRefundReason());
			refundEntryToBeCreated
					.setAmount(calculateRefundEntryAmount(orderEntry, returnEntryData.getExpectedQuantity(), completeReturn));
			getModelService().save(refundEntryToBeCreated);
		});
		returnRequest.setSubtotal(recalculateSubtotal(returnRequest, completeReturn));
		getModelService().save(returnRequest);

		try
		{
			getRefundService().apply(returnRequest.getOrder(), returnRequest);
		}
		catch (final OrderReturnRecordsHandlerException e) //NOSONAR
		{
			LOGGER.info("Return record already in progress for Order: " + order.getCode()); //NOSONAR
		}
		catch (final IllegalStateException ise) //NOSONAR
		{
			LOGGER.info("Order " + order.getCode() + " Return record already in progress"); //NOSONAR
		}
		final CreateReturnEvent createReturnEvent = new CreateReturnEvent();
		createReturnEvent.setReturnRequest(returnRequest);
		getEventService().publishEvent(createReturnEvent);

		return returnRequest;
	}

	/**
	 * Checks whether the return can be cancelled
	 *
	 * @param status
	 * 		current status of the return request
	 * @return whether the return request is cancellable
	 */
	protected boolean isReturnCancellable(final ReturnStatus status)
	{
		final List<ReturnStatus> cancellableStatuses = Arrays
				.asList(ReturnStatus.APPROVAL_PENDING, ReturnStatus.WAIT, ReturnStatus.PAYMENT_REVERSAL_FAILED);
		return cancellableStatuses.contains(status);
	}

	/**
	 * Validates for null check and mandatory fields in returnRequestData
	 *
	 * @param returnRequestData
	 * 		returnRequest to be validated
	 */
	protected void validateReturnRequestData(final ReturnRequestData returnRequestData)
	{
		notNull(returnRequestData,
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.returnrequestdata"));
		notNull(returnRequestData.getOrder(),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.order"));
		isTrue(Objects.nonNull(returnRequestData.getReturnEntries()) && CollectionUtils
						.isNotEmpty(returnRequestData.getReturnEntries()),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.returnentries"));

		final Boolean refundDeliveryCostRequested = returnRequestData.getRefundDeliveryCost();
		if (refundDeliveryCostRequested != null && refundDeliveryCostRequested)
		{
			isTrue(canRefundDeliveryCost(returnRequestData.getOrder().getCode(), returnRequestData.getRefundDeliveryCost()),
					String.format(Localization.getLocalizedString("ordermanagementfacade.returns.error.deliverycost"),
							returnRequestData.getOrder().getCode()));
		}
		final Set<Integer> entryNumbers = new HashSet<>();
		returnRequestData.getReturnEntries().forEach(entry -> {
			validateReturnEntryData(entry);
			if (entryNumbers.contains(entry.getOrderEntry().getEntryNumber()))
			{
				throw new IllegalArgumentException(
						String.format(Localization.getLocalizedString("ordermanagementfacade.returns.error.duplicateorderentry"),
								entry.getOrderEntry().getEntryNumber()));
			}
			entryNumbers.add(entry.getOrderEntry().getEntryNumber());
		});
	}

	/**
	 * Validates for null check and mandatory fields in returnEntryData
	 *
	 * @param returnEntryData
	 * 		returnEntry to be validated
	 */
	protected void validateReturnEntryData(final ReturnEntryData returnEntryData)
	{
		notNull(returnEntryData.getExpectedQuantity(),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.expectedquantity"));
		notNull(returnEntryData.getAction(),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.action"));
		notNull(returnEntryData.getRefundReason(),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.refundreason"));
		isTrue(getRefundReasons().contains(returnEntryData.getRefundReason()),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.false.refundreason"));
		isTrue(getReturnActions().contains(returnEntryData.getAction()),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.false.returnaction"));

		validateOrderEntryForReturnEntry(returnEntryData.getOrderEntry());
	}

	/**
	 * Validates for null check and mandatory fields in returnEntryData
	 *
	 * @param orderEntry
	 * 		orderEntry to be validated
	 */
	protected void validateOrderEntryForReturnEntry(final OrderEntryData orderEntry)
	{
		notNull(orderEntry, Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.orderentry"));
		notNull(orderEntry.getEntryNumber(),
				Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.orderentrynumber"));
	}

	/**
	 * Evaluates refundAmount for the {@link RefundEntryModel} to be created.
	 * If the order is not discounted, base price is used. Else:
	 * - For a complete order return: Discounted value is applied.
	 * - For a partial order return: A value of zero is applied.
	 *
	 * @param orderEntryModel
	 * 		the basePrice for the product to be refunded
	 * @param expectedQuantity
	 * 		expectedQuantity for the product to be refunded
	 * @param completeReturn
	 * 		if the return is a complete order return
	 * @return the amount in BigDecimal to be refunded for the {@link RefundEntryModel}
	 */
	protected BigDecimal calculateRefundEntryAmount(final AbstractOrderEntryModel orderEntryModel, final Long expectedQuantity,
			final boolean completeReturn)
	{
		final BigDecimal entryAmount;
		if (completeReturn)
		{
			entryAmount = BigDecimal.valueOf(orderEntryModel.getTotalPrice());
		}
		else if (CollectionUtils.isEmpty(orderEntryModel.getDiscountValues()))
		{
			entryAmount = BigDecimal.valueOf(orderEntryModel.getBasePrice() * expectedQuantity);
		}
		else
		{
			entryAmount = BigDecimal.ZERO;
		}
		return entryAmount;
	}

	/**
	 * Evaluates if deliveryCost should be refunded for the requested {@link ReturnRequestModel} to be created
	 *
	 * @param orderCode
	 * 		the orderCode's code for the requested returnRequest to be created
	 * @param isDeliveryCostRequested
	 * 		is deliveryCost requested in the request
	 * @return the boolean to indicate if deliveryCost should be refunded
	 */
	protected Boolean canRefundDeliveryCost(final String orderCode, final Boolean isDeliveryCostRequested)
	{
		boolean canRefundDeliveryCost = false;

		if (isDeliveryCostRequested != null && isDeliveryCostRequested)
		{
			final List<ReturnRequestModel> returnRequestsForOrder = getReturnService().getReturnRequests(orderCode);
			canRefundDeliveryCost = returnRequestsForOrder.stream()
					.noneMatch(returnReq -> returnReq.getRefundDeliveryCost() && !returnReq.getStatus().equals(ReturnStatus.CANCELED));
		}
		return canRefundDeliveryCost;
	}

	/**
	 * Finds {@link ReturnRequestModel} for the given {@value de.hybris.platform.returns.model.ReturnRequestModel#CODE}
	 *
	 * @param code
	 * 		the returnRequest's code
	 * @return the requested return for the given code
	 */
	protected ReturnRequestModel getReturnRequestModelForCode(final String code)
	{

		final Map<String, String> params = new HashMap<>();
		params.put(ReturnRequestModel.CODE, code);

		final List<ReturnRequestModel> resultSet = getReturnGenericDao().find(params);
		validateIfSingleResult(resultSet,
				String.format(Localization.getLocalizedString("ordermanagementfacade.returns.validation.missing.code"), code),
				String.format(Localization.getLocalizedString("ordermanagementfacade.returns.validation.missing.code"), code));

		return resultSet.get(0);
	}

	/**
	 * Update return entries if possible
	 *
	 * @param returnRequestModificationData
	 * 		{@link ReturnRequestModificationData}
	 * @param returnRequest
	 * 		{@link ReturnRequestModel}
	 */
	protected void updateReturnEntries(final ReturnRequestModificationData returnRequestModificationData,
			final ReturnRequestModel returnRequest)
	{

		returnRequestModificationData.getReturnEntries().forEach(entry -> {
			final List<ReturnEntryModel> resultList = returnRequest.getReturnEntries().stream().filter(
					returnEntry -> (returnEntry instanceof RefundEntryModel) && returnEntry.getOrderEntry().getProduct().getCode()
							.equals(entry.getProductCode()) && (entry.getDeliveryModeCode() == null || returnEntry.getOrderEntry()
							.getDeliveryMode().getCode().equals(entry.getDeliveryModeCode()))).collect(Collectors.toList());
			evaluateResultList(entry, resultList);
		});
		returnRequest.setSubtotal(getTotalReturnEntriesAmount(returnRequest));
		getModelService().save(returnRequest);

	}

	/**
	 * Verifies that the list is neither missing or duplicated and sets the {@link RefundEntryModel#AMOUNT} with the {@link ReturnEntryModificationData#refundAmount}
	 *
	 * @param entry
	 * 		the {@link ReturnEntryModificationData}
	 * @param resultList
	 * 		the result list to evaluate
	 */
	protected void evaluateResultList(final ReturnEntryModificationData entry, final List<ReturnEntryModel> resultList)
	{
		if (resultList.isEmpty())
		{
			throw new IllegalArgumentException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.returnentry.validation.missing"),
							entry.getProductCode()));
		}
		if (resultList.size() == 1)
		{
			((RefundEntryModel) resultList.get(0)).setAmount(entry.getRefundAmount());
			getModelService().save(resultList.get(0));
		}
		else
		{
			throw new AmbiguousIdentifierException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.returnentry.validation.duplicated"),
							entry.getProductCode()));
		}
	}

	/**
	 * Update refundDeliveryCost if possible
	 *
	 * @param returnRequestModificationData
	 * 		{@link ReturnRequestModificationData}
	 * @param returnRequest
	 * 		{@link ReturnRequestModel}
	 */
	protected void updateRefundDeliveryCost(final ReturnRequestModificationData returnRequestModificationData,
			final ReturnRequestModel returnRequest)
	{
		if (returnRequestModificationData.getRefundDeliveryCost() == false || isDeliveryCostRefundable(
				returnRequest.getOrder().getCode(), returnRequest.getRMA()))
		{
			returnRequest.setRefundDeliveryCost(returnRequestModificationData.getRefundDeliveryCost());
			getModelService().save(returnRequest);
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.updatereturnrequest.error.deliverycost")));
		}
	}

	/**
	 * If {@link ReturnRequestModel} is not in the expected status for an update, a IllegalStateException will be thrown
	 *
	 * @param returnRequest
	 * 		{@link ReturnRequestModel}
	 * @param expectedReturnStatus
	 * 		{@link ReturnStatus}
	 */
	protected void validateReturnStatus(final ReturnRequestModel returnRequest, final ReturnStatus expectedReturnStatus)
	{
		if (!expectedReturnStatus.equals(returnRequest.getStatus()))
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.updatereturnrequest.error.wrongstatus"),
							expectedReturnStatus));
		}
	}

	/**
	 * Validate duplicated entries for {@link ReturnEntryModificationData}
	 *
	 * @param returnRequestModificationData
	 * 		{@link ReturnRequestModificationData}
	 * @return true {@link ReturnEntryModificationData} the entry valid
	 */
	protected boolean validateReturnEntryModificationData(final ReturnRequestModificationData returnRequestModificationData)
	{
		boolean result = false;
		final Map<String, Collection<ReturnEntryModificationData>> productReturnEntryModificationDataMap = new HashMap<>();
		returnRequestModificationData.getReturnEntries().forEach(entry -> {

			Collection<ReturnEntryModificationData> returnEntryModificationResultData = productReturnEntryModificationDataMap
					.get(entry.getProductCode());

			if (returnEntryModificationResultData == null)
			{
				returnEntryModificationResultData = new ArrayList<>();
				productReturnEntryModificationDataMap.put(entry.getProductCode(), returnEntryModificationResultData);
			}
			returnEntryModificationResultData.add(entry);
		});

		for (final Map.Entry entry : productReturnEntryModificationDataMap.entrySet())
		{
			final Collection<String> deliveryModeCodeList = productReturnEntryModificationDataMap.get(entry.getKey()).stream()
					.map(ReturnEntryModificationData::getDeliveryModeCode).collect(Collectors.toList());

			if (deliveryModeCodeList.size() > 1)
			{
				result = deliveryModeCodeList.stream().filter(
						deliveryModeCode -> Collections.frequency(deliveryModeCodeList, deliveryModeCode) > 1
								|| deliveryModeCode == null).count() == 0;
			}
			else
			{
				result = true;
			}
		}

		return result;
	}

	protected PagedGenericDao<ReturnRequestModel> getReturnPagedGenericDao()
	{
		return returnPagedGenericDao;
	}

	@Required
	public void setReturnPagedGenericDao(final PagedGenericDao<ReturnRequestModel> returnPagedGenericDao)
	{
		this.returnPagedGenericDao = returnPagedGenericDao;
	}

	protected Converter<ReturnRequestModel, ReturnRequestData> getReturnConverter()
	{
		return returnConverter;
	}

	@Required
	public void setReturnConverter(final Converter<ReturnRequestModel, ReturnRequestData> returnConverter)
	{
		this.returnConverter = returnConverter;
	}

	protected Converter<ReturnRequestModel, ReturnRequestData> getReturnHistoryConverter()
	{
		return returnHistoryConverter;
	}

	@Required
	public void setReturnHistoryConverter(final Converter<ReturnRequestModel, ReturnRequestData> returnHistoryConverter)
	{
		this.returnHistoryConverter = returnHistoryConverter;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected GenericDao<ReturnRequestModel> getReturnGenericDao()
	{
		return returnGenericDao;
	}

	@Required
	public void setReturnGenericDao(final GenericDao<ReturnRequestModel> returnGenericDao)
	{
		this.returnGenericDao = returnGenericDao;
	}

	protected PagedGenericDao<ReturnEntryModel> getReturnEntryPagedGenericDao()
	{
		return returnEntryPagedGenericDao;
	}

	@Required
	public void setReturnEntryPagedGenericDao(final PagedGenericDao<ReturnEntryModel> returnEntryPagedGenericDao)
	{
		this.returnEntryPagedGenericDao = returnEntryPagedGenericDao;
	}

	protected Converter<ReturnEntryModel, ReturnEntryData> getReturnEntryConverter()
	{
		return returnEntryConverter;
	}

	@Required
	public void setReturnEntryConverter(final Converter<ReturnEntryModel, ReturnEntryData> returnEntryConverter)
	{
		this.returnEntryConverter = returnEntryConverter;
	}

	protected ReturnService getReturnService()
	{
		return returnService;
	}

	@Required
	public void setReturnService(final ReturnService returnService)
	{
		this.returnService = returnService;
	}

	protected ReturnCallbackService getReturnCallbackService()
	{
		return returnCallbackService;
	}

	@Required
	public void setReturnCallbackService(final ReturnCallbackService returnCallbackService)
	{
		this.returnCallbackService = returnCallbackService;
	}

	protected OrderService getOrderService()
	{
		return orderService;
	}

	@Required
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected RefundService getRefundService()
	{
		return refundService;
	}

	@Required
	public void setRefundService(final RefundService refundService)
	{
		this.refundService = refundService;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected Set<ReturnStatus> getInvalidReturnStatusForRefundDeliveryCost()
	{
		return invalidReturnStatusForRefundDeliveryCost;
	}

	@Required
	public void setInvalidReturnStatusForRefundDeliveryCost(final Set<ReturnStatus> invalidReturnStatusForRefundDeliveryCost)
	{
		this.invalidReturnStatusForRefundDeliveryCost = invalidReturnStatusForRefundDeliveryCost;
	}
}
