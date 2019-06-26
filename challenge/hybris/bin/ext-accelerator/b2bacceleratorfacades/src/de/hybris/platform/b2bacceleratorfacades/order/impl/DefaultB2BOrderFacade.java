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
package de.hybris.platform.b2bacceleratorfacades.order.impl;


import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BSaleQuoteService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.exception.PrincipalAssignedValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorservices.customer.B2BCustomerAccountService;
import de.hybris.platform.b2bacceleratorservices.dao.PagedB2BWorkflowActionDao;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.constants.ProcessengineConstants;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;




/**
 * Default b2b implementation of {@link OrderFacade}.
 */
public class DefaultB2BOrderFacade extends DefaultOrderFacade implements B2BOrderFacade
{
	private B2BOrderService b2bOrderService;
	private Converter<CartToOrderCronJobModel, ScheduledCartData> scheduledCartConverter;
	private ModelService modelService;
	private Converter<WorkflowActionModel, B2BOrderApprovalData> b2bOrderApprovalDataConverter;
	private Converter<WorkflowActionModel, B2BOrderApprovalData> b2bOrderApprovalDashboardConverter;
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	private PagedB2BWorkflowActionDao pagedB2BWorkflowActionDao;
	private Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> b2bOrderHistoryEntryDataConverter;
	private B2BCommentService<AbstractOrderModel> b2bCommentService;
	private B2BSaleQuoteService b2bQuoteOrderService;
	private OrderHistoryService orderHistoryService;
	private ProcessParameterHelper processParameterHelper;
	private BusinessProcessService businessProcessService;


	/**
	 * @deprecated As of 6.5, replaced by {@link DefaultOrderFacade#getOrderDetailsForCode(String)}
	 */
	@Deprecated
	@Override
	public OrderData getOrderDetailsForCode(final String code)
	{
		return super.getOrderDetailsForCode(code);
	}

	@Override
	public ScheduledCartData getReplenishmentOrderDetailsForCode(final String code, final String user)
	{
		ScheduledCartData scheduledCartData = null;
		final CartToOrderCronJobModel cronJob = this.<B2BCustomerAccountService> getCustomerAccountService()
				.getCartToOrderCronJobForCode(code, getUserService().getUserForUID(user));
		if (cronJob != null)
		{
			scheduledCartData = getScheduledCartConverter().convert(cronJob);
		}

		return scheduledCartData;
	}

	@Override
	public List<ScheduledCartData> getReplenishmentHistory()
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final List<? extends CartToOrderCronJobModel> jobModels = this.<B2BCustomerAccountService> getCustomerAccountService()
				.getCartToOrderCronJobsForUser(currentCustomer);
		return Converters.convertAll(jobModels, getScheduledCartConverter());
	}

	@Override
	public SearchPageData<ScheduledCartData> getPagedReplenishmentHistory(final PageableData pageableData)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final SearchPageData<CartToOrderCronJobModel> jobResults = this.<B2BCustomerAccountService> getCustomerAccountService()
				.getPagedCartToOrderCronJobsForUser(currentCustomer, pageableData);
		return convertPageData(jobResults, getScheduledCartConverter());
	}

	@Override
	public void cancelReplenishment(final String jobCode, final String user)
	{
		final CartToOrderCronJobModel cronJob = this.<B2BCustomerAccountService> getCustomerAccountService()
				.getCartToOrderCronJobForCode(jobCode, getUserService().getUserForUID(user));
		if (cronJob != null)
		{
			cronJob.setActive(Boolean.FALSE);
			this.getModelService().save(cronJob);
		}
	}


	@Override
	public List<? extends OrderHistoryData> getReplenishmentOrderHistory(final String jobCode, final String user)
	{
		final CartToOrderCronJobModel cronJob = this.<B2BCustomerAccountService> getCustomerAccountService()
				.getCartToOrderCronJobForCode(jobCode, getUserService().getUserForUID(user));
		if (cronJob != null)
		{
			return Converters.convertAll(cronJob.getOrders(), getOrderHistoryConverter());
		}
		else
		{
			return Collections.emptyList();
		}
	}

	@Override
	public SearchPageData<? extends OrderHistoryData> getPagedReplenishmentOrderHistory(final String jobCode,
			final PageableData pageableData)
	{
		final SearchPageData<OrderModel> ordersForJob = this.<B2BCustomerAccountService> getCustomerAccountService()
				.getOrdersForJob(jobCode, pageableData);
		return convertPageData(ordersForJob, getOrderHistoryConverter());
	}

	@Override
	public List<B2BOrderApprovalData> getOrdersForApproval()
	{
		final CustomerModel currentApprover = (CustomerModel) getUserService().getCurrentUser();
		final Collection<WorkflowActionModel> workFlowActionModelList = getB2bWorkflowIntegrationService()
				.getWorkflowActionsForUser(currentApprover);

		return Converters.convertAll(workFlowActionModelList, getB2bOrderApprovalDataConverter());
	}

	@Override
	public SearchPageData<B2BOrderApprovalData> getPagedOrdersForApproval(final WorkflowActionType[] actionTypes,
			final PageableData pageableData)
	{

		final SearchPageData<WorkflowActionModel> actions = getPagedB2BWorkflowActionDao()
				.findPagedWorkflowActionsByUserAndActionTypes(getUserService().getCurrentUser(), actionTypes, pageableData);
		return convertPageData(actions, getB2bOrderApprovalDashboardConverter());
	}

	@Override
	public B2BOrderApprovalData getOrderApprovalDetailsForCode(final String code)
	{

		final WorkflowActionModel workflowActionModel = getB2bWorkflowIntegrationService().getActionForCode(code);

		if ((workflowActionModel != null)
				&& !Objects.equals(workflowActionModel.getPrincipalAssigned(), getUserService().getCurrentUser()))
		{
			throw new PrincipalAssignedValidationException("Assigned principal of WorkflowAction does not match current user");
		}

		return getB2bOrderApprovalDataConverter().convert(workflowActionModel);

	}

	@Override
	public B2BOrderApprovalData setOrderApprovalDecision(final B2BOrderApprovalData b2bOrderApprovalData)
	{
		final WorkflowActionModel workflowActionModel = getB2bWorkflowIntegrationService()
				.getActionForCode(b2bOrderApprovalData.getWorkflowActionModelCode());

		if ((workflowActionModel != null)
				&& !Objects.equals(workflowActionModel.getPrincipalAssigned(), getUserService().getCurrentUser()))
		{
			throw new PrincipalAssignedValidationException("Assigned principal of WorkflowAction does not match current user");
		}

		addCommentToWorkflowAction(workflowActionModel, b2bOrderApprovalData.getApprovalComments());
		getB2bWorkflowIntegrationService().decideAction(workflowActionModel,
				B2BWorkflowIntegrationService.DECISIONCODES.valueOf(b2bOrderApprovalData.getSelectedDecision().toUpperCase()).name());

		return getB2bOrderApprovalDataConverter().convert(workflowActionModel);
	}

	protected void addCommentToWorkflowAction(final WorkflowActionModel workflowActionModel, final String comment)
	{
		final PrincipalModel principalAssigned = workflowActionModel.getPrincipalAssigned();

		final B2BApprovalProcessModel attachment = (B2BApprovalProcessModel) CollectionUtils
				.find(workflowActionModel.getAttachmentItems(), PredicateUtils.instanceofPredicate(B2BApprovalProcessModel.class));
		final OrderModel order = attachment.getOrder();
		final Collection<B2BPermissionResultModel> b2bPermissionResults = order.getPermissionResults();
		for (final B2BPermissionResultModel b2bPermissionResultModel : b2bPermissionResults)
		{
			if (b2bPermissionResultModel.getApprover().getUid().equals(principalAssigned.getUid()))
			{
				b2bPermissionResultModel.setNote(comment);
				getModelService().save(b2bPermissionResultModel);
			}
		}
	}

	@Override
	public List<B2BOrderHistoryEntryData> getOrderHistoryEntryData(final String code)
	{
		final OrderModel order = b2bOrderService.getOrderForCode(code);
		return !order.getHistoryEntries().isEmpty()
				? Converters.convertAll(order.getHistoryEntries(), getB2bOrderHistoryEntryDataConverter()) : Collections.EMPTY_LIST;
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public void createAndSetNewOrderFromRejectedQuote(final String orderCode)
	{
		final OrderModel order = getB2bOrderService().getOrderForCode(orderCode);
		if (order.getStatus() != null && order.getStatus().equals(OrderStatus.REJECTED_QUOTE))
		{
			this.getB2bQuoteOrderService().placeOrderFromRejectedQuote(order);
		}
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public void createAndSetNewOrderFromNegotiateQuote(final String orderCode, final String comment)
	{
		final OrderModel order = getB2bOrderService().getOrderForCode(orderCode);
		if (order.getWorkflow() != null)
		{
			getModelService().remove(order.getWorkflow());
			getModelService().save(order);
		}
		order.setStatus(OrderStatus.PENDING_QUOTE);
		order.setQuoteExpirationDate(null);
		this.setB2BComment(comment, order);
		this.getB2bQuoteOrderService().placeQuoteOrder(order);
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public void createAndSetNewOrderFromApprovedQuote(final String orderCode, final String comment)
	{
		final OrderModel order = getB2bOrderService().getOrderForCode(orderCode);
		this.setB2BComment(comment, order);
		if (order.getStatus() != null && order.getStatus().equals(OrderStatus.APPROVED_QUOTE))
		{
			getB2bQuoteOrderService().placeQuoteOrder(order);
		}
	}

	@Override
	public void cancelOrder(final String orderCode, final String comment)
	{
		final OrderModel order = getB2bOrderService().getOrderForCode(orderCode);
		if (order == null)
		{
			throw new UnknownIdentifierException(
					"Order with code " + orderCode + " not found for current user in current  BaseStore");
		}

		order.setStatus(OrderStatus.CANCELLED);

		final WorkflowModel orderWorkflow = order.getWorkflow();
		if (orderWorkflow != null)
		{
			final List<WorkflowItemAttachmentModel> attachments = orderWorkflow.getAttachments();
			for (final WorkflowItemAttachmentModel itemAttachment : attachments)
			{
				final ItemModel attachment = itemAttachment.getItem();
				if (attachment instanceof BusinessProcessModel)
				{
					final BusinessProcessModel process = (BusinessProcessModel) attachment;
					if (process.getState().equals(ProcessState.RUNNING))
					{
						final String eventName = (String) getProcessParameterHelper()
								.getProcessParameterByName(process, ProcessengineConstants.EVENT_AFTER_WORKFLOW_PARAM_NAME).getValue();
						if (eventName != null)
						{
							getBusinessProcessService().triggerEvent(process.getCode() + "_" + eventName);
						}
					}
				}
			}
			getModelService().remove(orderWorkflow);
			getModelService().save(order);
		}
		this.addAdditionalComment(orderCode, comment);
	}

	@Override
	public void addAdditionalComment(final String orderCode, final String comment)
	{
		final OrderModel order = getB2bOrderService().getOrderForCode(orderCode);
		if (order == null)
		{
			throw new UnknownIdentifierException(
					"Order with code " + orderCode + " not found for current user in current  BaseStore");
		}

		this.setB2BComment(comment, order);
		final B2BCommentModel commentModel = getModelService().create(B2BCommentModel.class);
		commentModel.setComment(comment);
		commentModel.setOwner(getUserService().getCurrentUser());

		final OrderModel version = getOrderHistoryService().createHistorySnapshot(order);
		version.setB2bcomments(Collections.singleton(commentModel));
		getOrderHistoryService().saveHistorySnapshot(version);

		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);
		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(version);
		//TODO: localize the history description.
		historyEntry.setDescription("The status has changed for the quote");
		historyEntry.setTimestamp(new Date());
		historyEntry.setOwner(getUserService().getCurrentUser());
		getModelService().save(historyEntry);
	}

	protected void setB2BComment(final String comment, final AbstractOrderModel order)
	{
		final B2BCommentModel b2bComment = this.getModelService().create(B2BCommentModel.class);
		b2bComment.setComment(comment);
		b2bComment.setOwner(getUserService().getCurrentUser());
		this.getB2bCommentService().addComment(order, b2bComment);
	}

	protected B2BOrderService getB2bOrderService()
	{
		return b2bOrderService;
	}

	@Required
	public void setB2bOrderService(final B2BOrderService b2bOrderService)
	{
		this.b2bOrderService = b2bOrderService;
	}

	protected Converter<CartToOrderCronJobModel, ScheduledCartData> getScheduledCartConverter()
	{
		return scheduledCartConverter;
	}

	@Required
	public void setScheduledCartConverter(final Converter<CartToOrderCronJobModel, ScheduledCartData> scheduledCartConverter)
	{
		this.scheduledCartConverter = scheduledCartConverter;
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

	protected Converter<WorkflowActionModel, B2BOrderApprovalData> getB2bOrderApprovalDataConverter()
	{
		return b2bOrderApprovalDataConverter;
	}

	@Required
	public void setB2bOrderApprovalDataConverter(
			final Converter<WorkflowActionModel, B2BOrderApprovalData> b2bOrderApprovalDataConverter)
	{
		this.b2bOrderApprovalDataConverter = b2bOrderApprovalDataConverter;
	}

	protected B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	@Required
	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	protected PagedB2BWorkflowActionDao getPagedB2BWorkflowActionDao()
	{
		return pagedB2BWorkflowActionDao;
	}

	@Required
	public void setPagedB2BWorkflowActionDao(final PagedB2BWorkflowActionDao pagedB2BWorkflowActionDao)
	{
		this.pagedB2BWorkflowActionDao = pagedB2BWorkflowActionDao;
	}

	protected Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> getB2bOrderHistoryEntryDataConverter()
	{
		return b2bOrderHistoryEntryDataConverter;
	}

	@Required
	public void setB2bOrderHistoryEntryDataConverter(
			final Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> b2bOrderHistoryEntryDataConverter)
	{
		this.b2bOrderHistoryEntryDataConverter = b2bOrderHistoryEntryDataConverter;
	}

	protected B2BCommentService<AbstractOrderModel> getB2bCommentService()
	{
		return b2bCommentService;
	}

	@Required
	public void setB2bCommentService(final B2BCommentService<AbstractOrderModel> b2bCommentService)
	{
		this.b2bCommentService = b2bCommentService;
	}

	protected B2BSaleQuoteService getB2bQuoteOrderService()
	{
		return b2bQuoteOrderService;
	}

	@Required
	public void setB2bQuoteOrderService(final B2BSaleQuoteService b2bQuoteOrderService)
	{
		this.b2bQuoteOrderService = b2bQuoteOrderService;
	}

	protected OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	@Required
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	protected ProcessParameterHelper getProcessParameterHelper()
	{
		return processParameterHelper;
	}

	@Required
	public void setProcessParameterHelper(final ProcessParameterHelper processParameterHelper)
	{
		this.processParameterHelper = processParameterHelper;
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

	protected Converter<WorkflowActionModel, B2BOrderApprovalData> getB2bOrderApprovalDashboardConverter()
	{
		return b2bOrderApprovalDashboardConverter;
	}

	@Required
	public void setB2bOrderApprovalDashboardConverter(
			final Converter<WorkflowActionModel, B2BOrderApprovalData> b2bOrderApprovalDashboardConverter)
	{
		this.b2bOrderApprovalDashboardConverter = b2bOrderApprovalDashboardConverter;
	}

}
