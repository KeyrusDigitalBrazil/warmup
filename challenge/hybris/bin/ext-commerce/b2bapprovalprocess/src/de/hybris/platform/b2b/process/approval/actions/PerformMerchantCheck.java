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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.enums.MerchantCheckStatus;
import de.hybris.platform.b2b.enums.MerchantCheckStatusEmail;
import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BMerchantCheckResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BMerchantCheckService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class PerformMerchantCheck extends AbstractB2BApproveOrderDecisionAction
{
	private static final Logger LOG = Logger.getLogger(PerformMerchantCheck.class);
	private B2BMerchantCheckService b2bMerchantCheckService;
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	private UserService userService;
	private B2BApproverService<B2BCustomerModel> b2bApproverService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private WorkflowProcessingService workflowProcessingService;
	private WorkflowService workflowService;
	private B2BCostCenterService b2bCostCenterService;


	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException, Exception
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Check Credit Action execution for process" + process);
		}
		OrderModel order = null;
		Transition transition = null;

		try
		{
			order = process.getOrder();
			final List<UserModel> accountManagers = new LinkedList<UserModel>();

			// Order entries might belong to different Cost Centers, which possibly has more then one Account Manager, 
			// so notification to be delivered to the respective Account Managers.
			final Set<B2BCostCenterModel> b2bCostCenters = getB2bCostCenterService().getB2BCostCenters(order.getEntries());
			for (final B2BCostCenterModel b2bCostCenterModel : b2bCostCenters)
			{
				final UserModel userModel = getB2bUnitService().getAccountManagerForUnit(b2bCostCenterModel.getUnit());
				//Null check to prevent adding null object getting added to list and this prevents adding admin user if account manager is not there.
				if (userModel != null)
				{
					accountManagers.add(userModel);
				}
			}

			final Collection<UserModel> accountManagerApprovers = getB2bApproverService()
					.getAccountManagerApprovers(order.getUnit());
			//Since the process can be triggered without sales rep assigned to the unit default to an administrator
			// user if no sales rep
			if (CollectionUtils.isEmpty(accountManagers))
			{
				accountManagers.add(getUserService().getAdminUser());
			}

			final List<UserModel> userModelList = (List<UserModel>) CollectionUtils.union(accountManagerApprovers, accountManagers);

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Creating a sales quote worflow for order %s and approvers %s", order.getCode(),
						CollectionUtils.collect(userModelList, new BeanToPropertyValueTransformer(UserModel.UID, true))));
			}

			final B2BCustomerModel orderUser = (B2BCustomerModel) order.getUser();
			final Set<B2BMerchantCheckResultModel> merchantCheckResults = getB2bMerchantCheckService().evaluateMerchantChecks(order,
					orderUser);

			final boolean isRejected = hasRejectedMerchantResult(merchantCheckResults);

			if (isRejected)
			{
				// create a workflow for merchant to approve order has reached credit limit.
				final String workflowTemplateCode = getB2bWorkflowIntegrationService().generateWorkflowTemplateCode("MERCHANT_CHECK",
						userModelList);
				final WorkflowTemplateModel workflowTemplate = getB2bWorkflowIntegrationService().createWorkflowTemplate(
						userModelList, workflowTemplateCode, "Generated Merchant check workflow", WorkflowTemplateType.MERCHANT_CHECK);
				final WorkflowModel workflow = workflowService.createWorkflow(workflowTemplate.getName(), workflowTemplate,
						Arrays.asList(new ItemModel[]
						{ process, order }), workflowTemplate.getOwner());
				getWorkflowProcessingService().startWorkflow(workflow);
				getModelService().saveAll(); // workaround for PLA-10938
				order.setWorkflow(workflow);
				order.setStatus(OrderStatus.PENDING_APPROVAL_FROM_MERCHANT);
				transition = Transition.NOK;
			}
			else
			{
				for (final B2BMerchantCheckResultModel b2bMerchantCheckResultModel : merchantCheckResults)
				{
					// the order has reached the credit limit threshold, alert the sales rep or admin.
					if (MerchantCheckStatusEmail.ALERT.equals(b2bMerchantCheckResultModel.getStatusEmail()))
					{
						sendAlert(order, accountManagers, "Customer is about to reach credit limit with order: " + order.getCode());
					}

				}
				//If order was rejected by an approver don't approve here.
				if (!OrderStatus.REJECTED.equals(order.getStatus()))
				{
					order.setStatus(OrderStatus.APPROVED);
				}
				transition = Transition.OK;
			}
			getModelService().save(order);
		}
		catch (final Exception e)
		{
			this.handleError(order, e);
			transition = Transition.ERROR;
		}
		return transition;
	}

	public boolean hasRejectedMerchantResult(final Collection<B2BMerchantCheckResultModel> merchantCheckResults)
	{
		Assert.notNull(merchantCheckResults, "Should not have gotten an null collection of " + "B2BMerchantCheckResultModel(s)");
		return null != CollectionUtils.find(merchantCheckResults, new BeanPropertyValueEqualsPredicate(
				B2BMerchantCheckResultModel.STATUS, MerchantCheckStatus.REJECTED, true));
	}

	protected void sendAlert(final OrderModel order, final List<UserModel> salesReps, final String description)
	{
		for (final UserModel salesRep : salesReps)
		{
			final WorkflowTemplateModel workflowTemplate = getB2bWorkflowIntegrationService().createWorkflowTemplate(
					Collections.singletonList(salesRep), "B2B-Alert-" + salesRep.getUid(), description,
					WorkflowTemplateType.CREDIT_LIMIT_ALERT);

			final WorkflowModel workflow = getWorkflowService().createWorkflow(workflowTemplate.getName(), workflowTemplate,
					Collections.<ItemModel>singletonList(order), workflowTemplate.getOwner());
			getWorkflowProcessingService().startWorkflow(workflow);
		}
	}

	protected void handleError(final OrderModel order, final Exception e)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
			this.modelService.save(order);
		}
		LOG.error(e.getMessage(), e);
	}


	@Required
	public void setB2bMerchantCheckService(final B2BMerchantCheckService b2bMerchantCheckService)
	{
		this.b2bMerchantCheckService = b2bMerchantCheckService;
	}

	@Required
	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Required
	public void setB2bApproverService(final B2BApproverService<B2BCustomerModel> b2bApproverService)
	{
		this.b2bApproverService = b2bApproverService;
	}

	@Required
	public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
	{
		this.workflowProcessingService = workflowProcessingService;
	}

	@Required
	public void setWorkflowService(final WorkflowService workflowService)
	{
		this.workflowService = workflowService;
	}

	@Required
	public void setB2bCostCenterService(final B2BCostCenterService b2bCostCenterService)
	{
		this.b2bCostCenterService = b2bCostCenterService;
	}

	public B2BCostCenterService getB2bCostCenterService()
	{
		return b2bCostCenterService;
	}

	public B2BApproverService<B2BCustomerModel> getB2bApproverService()
	{
		return b2bApproverService;
	}

	public B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public B2BMerchantCheckService getB2bMerchantCheckService()
	{
		return b2bMerchantCheckService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public WorkflowProcessingService getWorkflowProcessingService()
	{
		return workflowProcessingService;
	}

	public WorkflowService getWorkflowService()
	{
		return workflowService;
	}
}
