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
package de.hybris.platform.sap.saprevenuecloudorder.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.actions.SapCpiOmsOrderOutboundAction;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.saporderexchangeoms.constants.SapOmsOrderExchangeConstants;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudOrderOutboundService;
import rx.Single;

public class SapCpiSendRevenueCloudOrderOutboundAction extends SapCpiOmsOrderOutboundAction {

	private SapRevenueCloudOrderOutboundService sapRevenueCloudOrderOutboundService;
	private static final Logger LOG = Logger.getLogger(SapCpiSendRevenueCloudOrderOutboundAction.class);

	@Override
	protected Single<ResponseEntity<Map>> sendOrderToScpi(OrderModel order, Set<ConsignmentModel> consignments,
			List<OrderHistoryEntryModel> orderHistoryList, List<SAPOrderModel> sapOrders) {

		Predicate<ConsignmentModel> hasSubscriptionProduct = entry -> entry.isSubscriptionProducts() == Boolean.TRUE
				? Boolean.TRUE
				: Boolean.FALSE;

		Predicate<ConsignmentModel> hasPhysicalProduct = entry -> entry.isSubscriptionProducts() ? Boolean.FALSE
				: Boolean.TRUE;

		SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
				order.getStore(), consignments.stream().findFirst().get().getWarehouse().getCode());

		final OrderHistoryEntryModel orderHistoryEntry = createOrderHistory(order,
				sapPlantLogSysOrgModel.getLogSys().getSapLogicalSystemName());

		// Add send SAP order action to the order history
		orderHistoryList.add(orderHistoryEntry);

		createSapOrders(sapOrders, orderHistoryEntry, consignments);

		// Clone the order
		OrderModel clonedOrder = getOrderService().clone(null, null, order,
				orderHistoryEntry.getPreviousOrderVersion().getVersionID());

		List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

		// Copy order entries
		consignments.stream().forEach(consignment -> consignment.getConsignmentEntries().stream()
				.forEach(entry -> orderEntries.add(entry.getOrderEntry())));

		boolean hasAllSubscriptionProducts = consignments.stream().allMatch(hasSubscriptionProduct);

		if (hasAllSubscriptionProducts) {
			// Send to Revenue Cloud
			// Set cloned order attributes
			clonedOrder.setConsignments(consignments);
			clonedOrder.setEntries(orderEntries);
			clonedOrder.setPaymentTransactions(order.getPaymentTransactions());
			return getSapRevenueCloudOrderOutboundService().sendOrder(clonedOrder).toSingle();
		}

		// handle else condition as well

		boolean hasAllPhysicalProducts = consignments.stream().allMatch(hasPhysicalProduct);
		if (hasAllPhysicalProducts) {
			// Send to S4-Hana System

			// Set cloned order attributes
			clonedOrder.setConsignments(consignments);
			clonedOrder.setSapLogicalSystem(sapPlantLogSysOrgModel.getLogSys().getSapLogicalSystemName());
			clonedOrder.setSapSalesOrganization(sapPlantLogSysOrgModel.getSalesOrg());
			clonedOrder.setEntries(orderEntries);
			clonedOrder.setSapSystemType(sapPlantLogSysOrgModel.getLogSys().getSapSystemType());
			clonedOrder.setPaymentTransactions(order.getPaymentTransactions());

			// Send cloned order to SCPI
			return getSapCpiOutboundService()
					.sendOrder(getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(clonedOrder))
					.toSingle();
		}

		// if both cases fail - Handle this
		return null;

	}

	/**
	 * * Start an SAP consignment process for every hybris consignment - skips
	 * consignment process for Subscription Order
	 *
	 * @param consignments
	 * @param process
	 */
	@Override
	protected void startSapConsignmentSubProcess(final Collection<ConsignmentModel> consignments,
			final OrderProcessModel process) {

		for (final ConsignmentModel consignment : consignments) {

			if (consignment.isSubscriptionProducts()) {
				LOG.info("Consignment process not required for subscription products");
				continue;
			}

			String processCode = new StringBuilder(process.getOrder().getCode())//
					.append(SapOmsOrderExchangeConstants.SAP_CONS)//
					.append(consignment.getCode())//
					.toString();

			final SapConsignmentProcessModel subProcess = getBusinessProcessService()
					.<SapConsignmentProcessModel>createProcess(processCode,
							SapOmsOrderExchangeConstants.CONSIGNMENT_SUBPROCESS_NAME);

			subProcess.setParentProcess(process);
			subProcess.setConsignment(consignment);
			save(subProcess);

			getBusinessProcessService().startProcess(subProcess);
			LOG.info(String.format("SAP consignment sub-process %s has started!", subProcess.getCode()));
		}
	}

	/**
	 * Save SAP orders and Set Subscription Order status
	 *
	 * @param order
	 * @param sapOrders
	 */
	@Override
	protected void saveSapOrders(final OrderModel order, final List<SAPOrderModel> sapOrders) {
		sapOrders.stream().forEach(sapOrder -> {
			sapOrder.getConsignments().stream().forEach(consignment -> {
				LOG.info("Iterating SAPOrders .. ");
				if (consignment.isSubscriptionProducts()) {
					sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_REVENUE_CLOUD);
					sapOrder.setSubscriptionOrder(Boolean.TRUE);
				} else {
					sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);
				}
				sapOrder.setOrder(order);
				getModelService().save(sapOrder);
			});
		});
	}

	public SapRevenueCloudOrderOutboundService getSapRevenueCloudOrderOutboundService() {
		return sapRevenueCloudOrderOutboundService;
	}

	public void setSapRevenueCloudOrderOutboundService(
			SapRevenueCloudOrderOutboundService sapRevenueCloudOrderOutboundService) {
		this.sapRevenueCloudOrderOutboundService = sapRevenueCloudOrderOutboundService;
	}


}
