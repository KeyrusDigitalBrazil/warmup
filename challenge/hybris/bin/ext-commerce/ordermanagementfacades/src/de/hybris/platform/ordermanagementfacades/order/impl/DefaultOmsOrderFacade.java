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
package de.hybris.platform.ordermanagementfacades.order.impl;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.fraud.data.FraudReportData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.ordermanagementfacades.order.cancel.OrderCancelRecordEntryData;
import de.hybris.platform.ordermanagementfacades.order.data.OrderEntryRequestData;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.ordermanagementfacades.search.dao.impl.OrderByNullVersionIdPagedDao;
import de.hybris.platform.ordermanagementfacades.search.dao.impl.SearchByStatusPagedGenericDao;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfAnyResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.springframework.util.Assert.isTrue;


/**
 * Default implementation of {@link OmsOrderFacade}
 */
public class DefaultOmsOrderFacade extends OmsBaseFacade implements OmsOrderFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOmsOrderFacade.class);

	protected static final String ORDER_EVENT_NAME = "CSAOrderVerified";
	protected static final String MANUAL_VOID_PAYMENT_EVENT = "ManualVoidPaymentEvent";
	protected static final String MANUAL_VOID_TAX_EVENT = "ManualVoidTaxEvent";
	protected static final String MANUAL_COMMIT_TAX_EVENT = "ManualCommitTaxEvent";
	protected static final String MANUAL_REQUOTE_TAX_EVENT = "ManualTaxRequoteEvent";
	protected static final String MANUAL_REAUTH_PAYMENT_EVENT = "ManualPaymentReauthEvent";
	protected static final String MANUAL_DELIVERY_COST_COMMIT_EVENT = "ManualCommitDeliveryCostEvent";

	private Converter<FraudReportModel, FraudReportData> fraudReportConverter;
	private GenericDao<FraudReportModel> fraudReportGenericDao;
	private Converter<OrderModel, OrderData> orderConverter;
	private Converter<OrderRequestData, OrderModel> orderRequestReverseConverter;
	private Converter<OrderEntryModel, OrderEntryData> orderEntryConverter;
	private Converter<OrderCancelRecordEntryModel, OrderCancelRecordEntryData> orderCancelRecordEntryConverter;
	private EnumerationService enumerationService;
	private OrderService orderService;
	private BusinessProcessService businessProcessService;
	private ImpersonationService impersonationService;
	private OrderCancelService orderCancelService;
	private UserService userService;
	private BaseSiteService baseSiteService;
	private BaseStoreService baseStoreService;
	private Converter<CustomerData, CustomerModel> customerReverseConverter;
	private SearchByStatusPagedGenericDao<OrderModel> orderSearchByStatusPagedDao;
	private PagedGenericDao<OrderEntryModel> orderEntryPagedGenericDao;
	private OrderByNullVersionIdPagedDao orderByNullVersionIdPagedDao;

	@Override
	public SearchPageData<OrderData> getOrders(final PageableData pageableData)
	{
		final SearchPageData<OrderModel> orderSearchPageData = getOrderByNullVersionIdPagedDao().find(pageableData);
		return convertSearchPageData(orderSearchPageData, getOrderConverter());
	}

	@Override
	public OrderData getOrderForCode(final String orderCode)
	{
		return getOrderConverter().convert(getOrderModelForCode(orderCode));
	}

	@Override
	public SearchPageData<OrderData> getOrdersByStatuses(final PageableData pageableData, final Set<OrderStatus> orderStatusSet)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(OrderModel.STATUS, orderStatusSet);
		return convertSearchPageData(getOrderSearchByStatusPagedDao().find(params, pageableData), getOrderConverter());
	}

	@Override
	public List<OrderStatus> getOrderStatuses()
	{
		return getEnumerationService().getEnumerationValues(OrderStatus._TYPECODE);
	}

	@Override
	public SearchPageData<OrderEntryData> getOrderEntriesForOrderCode(final String orderCode, final PageableData pageableData)
	{
		final OrderModel order = getOrderModelForCode(orderCode);

		final Map<String, OrderModel> orderEntryParams = new HashMap<>();
		orderEntryParams.put(OrderEntryModel.ORDER, order);
		return convertSearchPageData(getOrderEntryPagedGenericDao().find(orderEntryParams, pageableData), getOrderEntryConverter());
	}

	@Override
	public OrderEntryData getOrderEntryForOrderCodeAndEntryNumber(final String orderCode, final Integer entryNumber)
	{
		final OrderModel order = getOrderModelForCode(orderCode);
		return getOrderEntryConverter().convert(getOrderService().getEntryForNumber(order, entryNumber));
	}

	@Override
	public List<FraudReportData> getOrderFraudReports(final String orderCode)
	{
		final Map<String, OrderModel> fraudParam = new HashMap<>();
		fraudParam.put(FraudReportModel.ORDER, getOrderModelForCode(orderCode));
		final List<FraudReportModel> fraudReports = getFraudReportGenericDao().find(fraudParam);
		return Converters.convertAll(fraudReports, getFraudReportConverter());
	}

	@Override
	public void approvePotentiallyFraudulentOrder(final String orderCode) throws IllegalStateException
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);
		if (canPerformFraudCheck(order))
		{
			executeFraudCheckOperation(order, Boolean.FALSE);
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.approvefraudcheck.error.wrongstatus"),
							OrderStatus.WAIT_FRAUD_MANUAL_CHECK));
		}
	}

	@Override
	public void rejectPotentiallyFraudulentOrder(final String orderCode) throws IllegalStateException
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);
		if (canPerformFraudCheck(order))
		{
			executeFraudCheckOperation(order, Boolean.TRUE);
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.rejectfraudcheck.error.wrongstatus"),
							OrderStatus.WAIT_FRAUD_MANUAL_CHECK));
		}
	}

	@Override
	public List<CancelReason> getCancelReasons()
	{
		return getEnumerationService().getEnumerationValues(CancelReason._TYPECODE);
	}

	@Override
	public OrderCancelRecordEntryData createRequestOrderCancel(final OrderCancelRequestData orderCancelRequestData)
	{

		validateReturnRequestData(orderCancelRequestData);
		final UserModel user = userService.getUserForUID(orderCancelRequestData.getUserId());
		final String orderCode = orderCancelRequestData.getOrderCode();
		final OrderModel order = getOrderModelForCode(orderCode);
		if (!orderCancelService.isCancelPossible(order, user, isPartialCancel(orderCancelRequestData, order),
				isPartialEntryCancel(orderCancelRequestData, order)).isAllowed())
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.orders.validation.false.isCancelPossible"),
							orderCode));
		}
		OrderCancelRecordEntryData orderCancelRecordEntryData = null;
		if (!isCancelQuantityPossible(orderCancelRequestData, order, user))
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("ordermanagementfacade.orders.validation.false.isCancelQuantityPossible"),
					orderCode));
		}
		else
		{
			try
			{
				orderCancelRecordEntryData = getOrderCancelRecordEntryConverter()
						.convert(orderCancelService.requestOrderCancel(buildOrderCancelRequest(orderCancelRequestData, order), user));
			}
			catch (final OrderCancelException e) //NOSONAR
			{
				LOGGER.info("Order Cancellation Failed '{}'", order.getCode());
			}
		}
		return orderCancelRecordEntryData;
	}

	@Override
	public OrderData submitOrder(final OrderRequestData orderRequestData)
	{
		validateOrderData(orderRequestData);
		validatePaymentTransactions(orderRequestData.getPaymentTransactions());
		return submitValidatedOrder(orderRequestData);
	}

	@Override
	public void manuallyReleasePaymentVoid(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.PAYMENT_NOT_VOIDED.equals(order.getStatus()))
		{
			executeManualReleaseStepOperation(order, MANUAL_VOID_PAYMENT_EVENT, OrderStatus.CANCELLED);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("ordermanagementfacade.orders.validation.manualpaymentvoid.wrongstatus"),
					OrderStatus.PAYMENT_NOT_VOIDED));
		}
	}

	@Override
	public void manuallyReleaseTaxVoid(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.TAX_NOT_VOIDED.equals(order.getStatus()))
		{
			executeManualReleaseStepOperation(order, MANUAL_VOID_TAX_EVENT, OrderStatus.CANCELLED);
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.orders.validation.manualtaxvoid.wrongstatus"),
							OrderStatus.TAX_NOT_VOIDED));
		}
	}

	@Override
	public void manuallyReleaseTaxCommit(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.TAX_NOT_COMMITTED.equals(order.getStatus()))
		{
			executeManualReleaseStepOperation(order, MANUAL_COMMIT_TAX_EVENT, OrderStatus.COMPLETED);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("ordermanagementfacade.orders.validation.manualtaxcommit.wrongstatus"),
					OrderStatus.TAX_NOT_COMMITTED));
		}
	}

	@Override
	public void manuallyReleaseTaxRequote(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.TAX_NOT_REQUOTED.equals(order.getStatus()))
		{
			executeManualReleaseStepOperation(order, MANUAL_REQUOTE_TAX_EVENT, OrderStatus.CANCELLING);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("ordermanagementfacade.orders.validation.manualtaxrequote.wrongstatus"),
					OrderStatus.TAX_NOT_REQUOTED));
		}
	}

	@Override
	public void manuallyReleasePaymentReauth(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.PAYMENT_NOT_AUTHORIZED.equals(order.getStatus()))
		{
			executeManualReleaseStepOperation(order, MANUAL_REAUTH_PAYMENT_EVENT, OrderStatus.CANCELLING);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("ordermanagementfacade.orders.validation.manualpaymentreauth.wrongstatus"),
					OrderStatus.PAYMENT_NOT_AUTHORIZED));
		}
	}

	@Override
	public void manuallyReleaseDeliveryCostCommit(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.TAX_NOT_COMMITTED.equals(order.getStatus()))
		{
			executeManualReleaseStepOperation(order, MANUAL_DELIVERY_COST_COMMIT_EVENT, OrderStatus.CANCELLED);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("ordermanagementfacade.orders.validation.manualtaxcommit.wrongstatus"),
					OrderStatus.TAX_NOT_COMMITTED));
		}
	}

	/**
	 * Executes a manual release step for the given event by calling the {@link BusinessProcessService}.
	 *
	 * @param order
	 * 		the {@link OrderModel} to be released
	 * @param event
	 * 		the business process event to be triggered
	 */
	protected void executeManualReleaseStepOperation(final OrderModel order, final String event, final OrderStatus orderStatus)
	{
		order.getOrderProcess().stream()
				.filter(process -> process.getCode().startsWith(order.getStore().getSubmitOrderProcessCode()))
				.forEach(filteredProcess -> getBusinessProcessService().triggerEvent(filteredProcess.getCode() + "_" + event));
		LOGGER.info("Manual Release completed. {} triggered.", event);
		order.setStatus(orderStatus);
		getModelService().save(order);
	}

	/**
	 * Builds an {@link OrderCancelRequest}.
	 *
	 * @param orderCancelRequestData
	 * 		the {@link OrderCancelRequestData} out of which the request will be built
	 * @param order
	 * 		the {@link OrderModel} associated with this request
	 * @return the created {@link OrderCancelRequest}
	 */
	protected OrderCancelRequest buildOrderCancelRequest(final OrderCancelRequestData orderCancelRequestData,
			final OrderModel order)
	{
		final List<OrderCancelEntry> orderCancelEntries = new ArrayList<>();
		orderCancelRequestData.getEntries().stream().forEach(entryData -> {
			final AbstractOrderEntryModel orderEntry = getOrderService().getEntryForNumber(order, entryData.getOrderEntryNumber());

			final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry, entryData.getCancelQuantity(),
					entryData.getNotes());
			orderCancelEntries.add(cancellationEntry);
		});

		return new OrderCancelRequest(order, orderCancelEntries);
	}

	/**
	 * submit order after the validation applied
	 *
	 * @param orderRequestData
	 * 		{@link OrderRequestData}
	 * @return {@link OrderData}
	 */
	public OrderData submitValidatedOrder(final OrderRequestData orderRequestData)
	{
		orderRequestData.getUser().setUid(createGuestIfNotExisting(orderRequestData.getUser()));

		final OrderModel orderModel = getOrderRequestReverseConverter().convert(orderRequestData);
		getModelService().save(orderModel);
		orderModel.setCalculated(orderRequestData.isCalculated());
		getModelService().save(orderModel);

		return submitOrderInContext(orderModel);
	}

	/**
	 * prepare impersonation context and submit order
	 *
	 * @param orderModel
	 * 		{@link OrderModel}
	 * @return {@link OrderData}
	 */
	protected OrderData submitOrderInContext(final OrderModel orderModel)
	{
		final ImpersonationContext context = new ImpersonationContext();
		context.setUser(orderModel.getUser());
		context.setSite(orderModel.getSite());
		getImpersonationService()
				.executeInContext(context, (ImpersonationService.Executor<Object, ImpersonationService.Nothing>) () -> {
					getOrderService().submitOrder(orderModel);
					return null;
				});

		getModelService().refresh(orderModel);
		return getOrderConverter().convert(orderModel);
	}

	/**
	 * Validates for null check and essential fields in {@link OrderRequestData}
	 *
	 * @param orderRequestData
	 * 		{@link OrderRequestData} to be validated
	 */
	protected void validateOrderData(final OrderRequestData orderRequestData)
	{
		validateParameterNotNullStandardMessage("orderRequestData", orderRequestData);
		validateParameterNotNullStandardMessage("orderRequestData.getDeliveryModeCode()", orderRequestData.getDeliveryModeCode());
		validateParameterNotNullStandardMessage("orderRequestData.getLanguageIsocode()", orderRequestData.getLanguageIsocode());
		validateParameterNotNullStandardMessage("orderRequestData.getCurrencyIsocode()", orderRequestData.getCurrencyIsocode());
		isTrue(orderRequestData.isCalculated(),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.orderRequestData.isCalculated"));
		isTrue(StringUtils.isNotEmpty(orderRequestData.getExternalOrderCode()),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.orderRequestData.externalOrderCode"));
		validateUserData(orderRequestData.getUser());
		validateBaseStoreInSite(orderRequestData.getStoreUid(), orderRequestData.getSiteUid());
		validateAddressData(orderRequestData.getDeliveryAddress());
		validateOrderEntryRequestData(orderRequestData.getEntries());
	}

	/**
	 * Validates for essential fields to assign {@link UserModel} to the submitted {@link OrderModel}
	 *
	 * @param customerData
	 * 		the {@link CustomerData}
	 */
	protected void validateUserData(final CustomerData customerData)
	{
		validateParameterNotNullStandardMessage("customerData", customerData);
		isTrue(StringUtils.isNotEmpty(customerData.getUid()),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.customerData.uid"));
	}

	protected void validateBaseStoreInSite(final String storeUid, final String siteUid)
	{
		validateParameterNotNullStandardMessage("orderRequestData.getStoreUid()", storeUid);
		validateParameterNotNullStandardMessage("orderRequestData.getSiteUid()", siteUid);
		final BaseSiteModel baseSite = getBaseSiteService().getBaseSiteForUID(siteUid);
		final BaseStoreModel baseStore = getBaseStoreService().getBaseStoreForUid(storeUid);

		if (baseSite == null)
		{
			throw new UnknownIdentifierException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.orders.validation.notfound.basesite"),
							siteUid));
		}

		isTrue(baseSite.getStores().contains(baseStore), String.format(
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.false.orderRequestData.basestore"), siteUid,
				storeUid));
	}

	/**
	 * Validates for null check and essential fields in {@link PaymentTransactionData}
	 * If PaymentTransactions not provide correctly, manual refund payment action may require during the return process
	 *
	 * @param paymentTransactionDatas
	 * 		the {@link PaymentTransactionData}(s) to be validated
	 */
	protected void validatePaymentTransactions(final List<PaymentTransactionData> paymentTransactionDatas)
	{
		validateIfAnyResult(paymentTransactionDatas,
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.paymentTransactionData"));
		paymentTransactionDatas.forEach(paymentTransactionData -> validateIfAnyResult(paymentTransactionData.getEntries(),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.paymentTransactionData.entry")));
	}

	/**
	 * Validates for null check and essential fields in {@link AddressData}
	 *
	 * @param addressData
	 * 		address to be validated
	 */
	protected void validateAddressData(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);
		validateParameterNotNullStandardMessage("addressData.getTown()", addressData.getTown());
		validateParameterNotNullStandardMessage("addressData.getCountry()", addressData.getCountry());
		validateParameterNotNullStandardMessage("addressData.getPostalCode()", addressData.getPostalCode());
		validateParameterNotNullStandardMessage("addressData.getLine1()", addressData.getLine1());
		isTrue((!StringUtils.isEmpty(addressData.getFirstName()) && !StringUtils.isEmpty(addressData.getLastName())) || !StringUtils
						.isEmpty(addressData.getCompanyName()),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.addressData.CustomerNameAndCompany"));
	}

	/**
	 * Validates for null check and essential fields in {@link OrderEntryRequestData}
	 *
	 * @param orderEntryRequestData
	 * 		{@link OrderEntryRequestData} to be validated
	 */
	protected void validateOrderEntryRequestData(final List<OrderEntryRequestData> orderEntryRequestData)
	{
		validateIfAnyResult(orderEntryRequestData,
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.null.orderEntryData"));

		orderEntryRequestData.forEach(entry -> {
			validateParameterNotNullStandardMessage("entry.getEntryNumber()", entry.getEntryNumber());
			validateParameterNotNullStandardMessage("entry.getUnitCode()", entry.getUnitCode());
			validateParameterNotNullStandardMessage("entry.getProductCode()", entry.getProductCode());
		});
	}

	/**
	 * Save the order with the new attribute value for fraudulent. <br>
	 * Send the event to business process service.
	 *
	 * @param order
	 * 		an {@link OrderModel}
	 * @param fraudulent
	 * 		whether the order is actually fraudulent
	 */
	protected void executeFraudCheckOperation(final OrderModel order, final Boolean fraudulent)
	{
		order.setFraudulent(fraudulent);
		getModelService().save(order);
		order.getOrderProcess().stream()
				.filter(process -> process.getCode().startsWith(order.getStore().getSubmitOrderProcessCode())).forEach(
				filteredProcess -> getBusinessProcessService().triggerEvent(filteredProcess.getCode() + "_" + ORDER_EVENT_NAME));
	}

	/**
	 * Verifies if a fraud check can be performed on this order depending on its status.
	 *
	 * @param order
	 * 		the order whose status will be checked
	 * @return true whether the fraud check can be performed; false otherwise
	 */
	protected boolean canPerformFraudCheck(final OrderModel order)
	{
		validateParameterNotNullStandardMessage("order", order);
		return OrderStatus.WAIT_FRAUD_MANUAL_CHECK.equals(order.getStatus());
	}

	/**
	 * Validates for null check and mandatory fields in OrderCancelRequestData
	 *
	 * @param orderCancelRequestData
	 * 		orderCancelRequest to be validated
	 */
	protected void validateReturnRequestData(final OrderCancelRequestData orderCancelRequestData)
	{
		validateParameterNotNullStandardMessage("orderCancelRequestData", orderCancelRequestData);
		validateParameterNotNullStandardMessage("orderCancelRequestData.getOrderCode()", orderCancelRequestData.getOrderCode());
		validateParameterNotNullStandardMessage("orderCancelRequestData.getUserId()", orderCancelRequestData.getUserId());
		validateIfAnyResult(orderCancelRequestData.getEntries(),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.invalid.orderEntries"));
		isTrue(orderCancelRequestData.getEntries().stream().map(OrderCancelEntryData::getOrderEntryNumber)
						.collect(Collectors.toSet()).size() == orderCancelRequestData.getEntries().size(),
				Localization.getLocalizedString(" ordermanagementfacades.orders.validation.invalid.orderEntries"));
		orderCancelRequestData.getEntries().forEach(this::validateCancelEntryData);
	}

	/**
	 * Validates for null check and mandatory fields in OrderCancelEntryData
	 *
	 * @param entry
	 */
	protected void validateCancelEntryData(final OrderCancelEntryData entry)
	{
		validateParameterNotNullStandardMessage("entry", entry);
		validateParameterNotNullStandardMessage("entry.getCancelQuantity()", entry.getCancelQuantity());
		validateParameterNotNullStandardMessage("entry.getOrderEntryNumber()", entry.getOrderEntryNumber());
		validateParameterNotNullStandardMessage("entry.getCancelReason()", entry.getCancelReason());

		isTrue(entry.getCancelQuantity() >= 0,
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.equalOrAboveZero.orderCancelEntryQuantity"));
		isTrue(getCancelReasons().contains(entry.getCancelReason()),
				Localization.getLocalizedString("ordermanagementfacade.orders.validation.false.reason"));
	}

	/**
	 * check if is a partial cancel
	 *
	 * @param orderCancelRequestData
	 * @param order
	 * @return true if is a partial cancel
	 */
	protected Boolean isPartialCancel(final OrderCancelRequestData orderCancelRequestData, final OrderModel order)
	{
		return !(order.getEntries().stream().map(AbstractOrderEntryModel::getEntryNumber).collect(Collectors.toList())
				.containsAll(orderCancelRequestData.getEntries()));
	}

	/**
	 * check if is a partial order entry cancel
	 *
	 * @param orderCancelRequestData
	 * @param order
	 * @return true if is a partial entry cancel
	 */
	protected Boolean isPartialEntryCancel(final OrderCancelRequestData orderCancelRequestData, final OrderModel order)
	{
		return orderCancelRequestData.getEntries().stream().allMatch(entry -> {
			final OrderEntryModel orderEntry = getOrderService().getEntryForNumber(order, entry.getOrderEntryNumber());
			return orderEntry != null && !Objects.equals(orderEntry.getQuantity(), entry.getCancelQuantity());
		});
	}

	/**
	 * check the given quantity is valid to cancel
	 *
	 * @param orderCancelRequestData
	 * @param order
	 * @param user
	 * @return true if quantity is valid to cancel
	 */
	protected Boolean isCancelQuantityPossible(final OrderCancelRequestData orderCancelRequestData, final OrderModel order,
			final UserModel user)
	{
		final Map<AbstractOrderEntryModel, Long> orderEntryMap = getOrderCancelService().getAllCancelableEntries(order, user);

		return orderCancelRequestData.getEntries().stream().allMatch(entryData -> orderEntryMap.entrySet().stream().anyMatch(
				orderEntry -> Objects.equals(entryData.getOrderEntryNumber(), orderEntry.getKey().getEntryNumber())
						&& entryData.getCancelQuantity() <= orderEntry.getValue()));
	}

	/**
	 * Create a guest CustomerModel from {@link CustomerData}
	 *
	 * @param customerData
	 * 		the {@link CustomerData}
	 * @return the customer uid
	 */
	protected String createGuestCustomerModel(CustomerData customerData)
	{
		isTrue(!Strings.isNullOrEmpty(customerData.getFirstName()),
				Localization.getLocalizedString("ordermanagementfacade.newuser.validation.empty.firstname"));
		isTrue(!Strings.isNullOrEmpty(customerData.getLastName()),
				Localization.getLocalizedString("ordermanagementfacade.newuser.validation.empty.lastname"));

		final CustomerModel customerModel = this.getModelService().create(CustomerModel.class);
		getCustomerReverseConverter().convert(customerData, customerModel);
		customerModel.setType(CustomerType.GUEST);
		customerModel.setCustomerID(UUID.randomUUID().toString());
		customerModel.setUid(UUID.randomUUID() + "|" + customerData.getUid());
		getModelService().save(customerModel);
		return customerModel.getUid();
	}

	/**
	 * Find {@link CustomerModel} based on given {@link CustomerData#getUid()}, otherwise a new guest user will be created
	 * based on {@link CustomerData}.If the {@link CustomerData#getUid()} exists, it extracts the existing user using this id,
	 * and ignores the other {@link CustomerData}'s properties provided in the request.
	 *
	 * @param customerData
	 * 		the {@link CustomerData}
	 * @return the customer uid
	 */
	protected String createGuestIfNotExisting(final CustomerData customerData)
	{
		customerData.setUid(customerData.getUid().toLowerCase());
		final Map<String, String> params = new HashMap<>();
		params.put(CustomerModel.UID, customerData.getUid());

		if (!getUserService().isUserExisting(customerData.getUid()))
		{
			return createGuestCustomerModel(customerData);
		}
		return customerData.getUid();
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

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	protected Converter<OrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<OrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
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

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected Converter<FraudReportModel, FraudReportData> getFraudReportConverter()
	{
		return fraudReportConverter;
	}

	@Required
	public void setFraudReportConverter(final Converter<FraudReportModel, FraudReportData> fraudReportConverter)
	{
		this.fraudReportConverter = fraudReportConverter;
	}

	protected GenericDao<FraudReportModel> getFraudReportGenericDao()
	{
		return fraudReportGenericDao;
	}

	@Required
	public void setFraudReportGenericDao(final GenericDao<FraudReportModel> fraudReportGenericDao)
	{
		this.fraudReportGenericDao = fraudReportGenericDao;
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

	protected Converter<OrderRequestData, OrderModel> getOrderRequestReverseConverter()
	{
		return orderRequestReverseConverter;
	}

	@Required
	public void setOrderRequestReverseConverter(final Converter<OrderRequestData, OrderModel> orderRequestReverseConverter)
	{
		this.orderRequestReverseConverter = orderRequestReverseConverter;
	}

	protected OrderCancelService getOrderCancelService()
	{
		return orderCancelService;
	}

	@Required
	public void setOrderCancelService(final OrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected Converter<CustomerData, CustomerModel> getCustomerReverseConverter()
	{
		return customerReverseConverter;
	}

	@Required
	public void setCustomerReverseConverter(final Converter<CustomerData, CustomerModel> customerReverseConverter)
	{
		this.customerReverseConverter = customerReverseConverter;
	}

	protected SearchByStatusPagedGenericDao<OrderModel> getOrderSearchByStatusPagedDao()
	{
		return orderSearchByStatusPagedDao;
	}

	@Required
	public void setOrderSearchByStatusPagedDao(final SearchByStatusPagedGenericDao<OrderModel> orderSearchByStatusPagedDao)
	{
		this.orderSearchByStatusPagedDao = orderSearchByStatusPagedDao;
	}

	protected OrderByNullVersionIdPagedDao getOrderByNullVersionIdPagedDao()
	{
		return orderByNullVersionIdPagedDao;
	}

	@Required
	public void setOrderByNullVersionIdPagedDao(final OrderByNullVersionIdPagedDao orderByNullVersionIdPagedDao)
	{
		this.orderByNullVersionIdPagedDao = orderByNullVersionIdPagedDao;
	}

	protected PagedGenericDao<OrderEntryModel> getOrderEntryPagedGenericDao()
	{
		return orderEntryPagedGenericDao;
	}

	@Required
	public void setOrderEntryPagedGenericDao(final PagedGenericDao<OrderEntryModel> orderEntryPagedGenericDao)
	{
		this.orderEntryPagedGenericDao = orderEntryPagedGenericDao;
	}

	protected Converter<OrderCancelRecordEntryModel, OrderCancelRecordEntryData> getOrderCancelRecordEntryConverter()
	{
		return orderCancelRecordEntryConverter;
	}

	@Required
	public void setOrderCancelRecordEntryConverter(
			final Converter<OrderCancelRecordEntryModel, OrderCancelRecordEntryData> orderCancelRecordEntryConverter)
	{
		this.orderCancelRecordEntryConverter = orderCancelRecordEntryConverter;
	}
}
