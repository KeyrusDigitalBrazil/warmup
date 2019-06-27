/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.retention.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This Hook removes order related objects such as address, payment info, and promotion order restriction.
 *
 */
public class CommerceServicesOrderCleanupHook implements ItemCleanupHook<OrderModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CommerceServicesOrderCleanupHook.class);

	private static final String ORDER_PROCESSES_QUERY = "SELECT {" + OrderProcessModel.PK + "} FROM {" + OrderProcessModel._TYPECODE + "} "
			+ "WHERE {" + OrderProcessModel.ORDER + "} = ?order";
	private static final String CONSIGNMENT_PROCESSES_QUERY = "SELECT {" + ConsignmentProcessModel.PK + "} FROM {"
			+ ConsignmentProcessModel._TYPECODE + "} " + "WHERE {" + ConsignmentProcessModel.CONSIGNMENT + "} IN (?consignments)";

	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private WriteAuditRecordsDAO writeAuditRecordsDAO;
	private TimeService timeService;

	@Override
	public void cleanupRelatedObjects(final OrderModel orderModel)
	{
		validateParameterNotNullStandardMessage("orderModel", orderModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Cleaning up order related objects for: {}", orderModel);
		}

		// remove payment address and its audit records
		final AddressModel paymentAddress = orderModel.getPaymentAddress();
		if (paymentAddress != null)
		{
			getModelService().remove(paymentAddress);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(AddressModel._TYPECODE, paymentAddress.getPk());
		}

		// remove delivery address and its audit records
		final AddressModel deliveryAddress = orderModel.getDeliveryAddress();
		if (deliveryAddress != null)
		{
			getModelService().remove(deliveryAddress);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(AddressModel._TYPECODE, deliveryAddress.getPk());
		}

		// remove payment info and its audit records
		final PaymentInfoModel paymentInfo = orderModel.getPaymentInfo();
		if (paymentInfo != null)
		{
			getModelService().remove(paymentInfo);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(PaymentInfoModel._TYPECODE, paymentInfo.getPk());
		}

		// remove comments and comments audit records
		for (final CommentModel comment : orderModel.getComments())
		{
			getModelService().remove(comment);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(CommentModel._TYPECODE, comment.getPk());
		}

		// deactivate customer if the customer is a guest
		if (orderModel.getUser() instanceof CustomerModel
				&& CustomerType.GUEST.equals(((CustomerModel) orderModel.getUser()).getType()))
		{
			orderModel.getUser().setDeactivationDate(getTimeService().getCurrentTime());
			getModelService().save(orderModel.getUser());
		}

		// Remove consignments and consignment entries
		for (final ConsignmentModel consignment : orderModel.getConsignments())
		{
			// Remove consignment entries
			for (final ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries())
			{
				getModelService().remove(consignmentEntry);
				getWriteAuditRecordsDAO().removeAuditRecordsForType(ConsignmentEntryModel._TYPECODE, consignmentEntry.getPk());
			}
			getModelService().remove(consignment);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(ConsignmentModel._TYPECODE, consignment.getPk());
		}

		// If order processes exist then remove corresponding consignment processes
		final FlexibleSearchQuery orderProcessesQuery = new FlexibleSearchQuery(ORDER_PROCESSES_QUERY);
		orderProcessesQuery.addQueryParameter("order", orderModel);
		final SearchResult<OrderProcessModel> orderProcessSearchResult = flexibleSearchService.search(orderProcessesQuery);
		if (CollectionUtils.isNotEmpty(orderProcessSearchResult.getResult())
				&& CollectionUtils.isNotEmpty(orderModel.getConsignments()))
		{
			final List<ConsignmentProcessModel> consignmentProcesses = getConsignmentProcesses(new ArrayList(orderModel.getConsignments()));
			for (final ConsignmentProcessModel consignmentProcess : consignmentProcesses)
			{
				getModelService().remove(consignmentProcess);
				getWriteAuditRecordsDAO().removeAuditRecordsForType(ConsignmentProcessModel._TYPECODE, consignmentProcess.getPk());
			}
		}
		for (final OrderProcessModel orderProcess : orderProcessSearchResult.getResult())
		{
			getModelService().remove(orderProcess);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(OrderProcessModel._TYPECODE, orderProcess.getPk());
		}
	}

	protected List<ConsignmentProcessModel> getConsignmentProcesses(final List<ConsignmentModel> consignments)
	{
		final FlexibleSearchQuery consignmentProcessesQuery = new FlexibleSearchQuery(CONSIGNMENT_PROCESSES_QUERY);
		consignmentProcessesQuery.addQueryParameter("consignments", consignments);
		return new ArrayList(flexibleSearchService.search(consignmentProcessesQuery).getResult());
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected WriteAuditRecordsDAO getWriteAuditRecordsDAO()
	{
		return writeAuditRecordsDAO;
	}

	@Required
	public void setWriteAuditRecordsDAO(final WriteAuditRecordsDAO writeAuditRecordsDAO)
	{
		this.writeAuditRecordsDAO = writeAuditRecordsDAO;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
