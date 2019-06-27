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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.EscalationTaskModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BEscalationService;
import de.hybris.platform.b2b.services.B2BPermissionService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BEscalationService}.
 *
 * @spring.bean b2bEscalationService
 */

public class DefaultB2BEscalationService implements B2BEscalationService
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BEscalationService.class);
	private ModelService modelService;
	private SessionService sessionService;
	private UserService userService;
	private B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2bPermissionService;
	private B2BApproverService<B2BCustomerModel> b2bApproverService;
	private TaskService taskService;
	private String escalationTaskRunnerBeanId;
	private B2BPermissionResultHelperImpl permissionResultHelper;
	private CommonI18NService commonI18NService;
	private ConfigurationService configurationService;
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

	protected boolean escalateInternal(final OrderModel order)
	{
		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody() 
		{
			@Override
			public Object execute() 
			{
				getUserService().setCurrentUser(getUserService().getAdminUser());
				final WorkflowModel workflow = order.getWorkflow();
				if (workflow == null)
				{
					LOG.error(String.format("Order %s  placed by user %s did not have a workflow assigned the "
							+ "approval process has not been started the order status is %s", order.getCode(), order.getUser().getUid(),
							order.getStatus()));
					return Boolean.FALSE;
				}
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Starting escalationTask for order: %s", order.getCode()));
				}
				// check if the work-flow is still active.
				if (CronJobStatus.PAUSED.equals(workflow.getStatus()))
				{
					// get approval actions from the workflow.
					final Collection<WorkflowActionModel> approveActions = CollectionUtils.select(workflow.getActions(),
							new BeanPropertyValueEqualsPredicate(WorkflowActionModel.QUALIFIER,
									B2BWorkflowIntegrationService.ACTIONCODES.APPROVAL.name(), true));

					final List<B2BPermissionResultModel> orderPermissionResults = new ArrayList<B2BPermissionResultModel>();
					// get permissions that need to be fulfilled by approvers
					final List<B2BPermissionResultModel> openPermissions = getB2bPermissionService().getOpenPermissions(order);

					final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval = getPermissionResultHelper()
							.extractPermissionTypes(openPermissions);
					final B2BCustomerModel orderUser = (B2BCustomerModel) order.getUser();
					//Only return active approvers, otherwise inactive customers can be assigned as an approver for orders.
					final List<B2BCustomerModel> allApprovers = new ArrayList(getB2bApproverService().getAllActiveApprovers(orderUser));
					final Set<B2BCustomerModel> exhaustedApprovers = new HashSet<B2BCustomerModel>(order.getExhaustedApprovers());
					allApprovers.removeAll(exhaustedApprovers);

					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("Order %s exausted the following approvers %s", order.getCode(),
								principalListToUidString(IteratorUtils.toList(exhaustedApprovers.iterator()))));
						LOG.debug(String.format("User %s has the following approvers %s", orderUser.getUid(),
								principalListToUidString(IteratorUtils.toList(allApprovers.iterator()))));
					}

					for (final WorkflowActionModel workflowActionModel : approveActions) 
					{
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("WorkflowAction action %s qualified by %s assigned to %s has status of %s",
									workflowActionModel.getCode(), workflowActionModel.getQualifier(), workflowActionModel
											.getPrincipalAssigned().getUid(), workflowActionModel.getStatus()));
						}

						if (workflowActionModel.getStatus().equals(WorkflowActionStatus.IN_PROGRESS)
								|| workflowActionModel.getStatus().equals(WorkflowActionStatus.PAUSED))
						{

							for (final B2BCustomerModel customer : allApprovers) 
							{

								if (LOG.isDebugEnabled())
								{
									LOG.debug(String.format("evaluating permissions %s for user %s", permissionsThatNeedApproval,
											customer.getUid()));
								}
								final Set<B2BPermissionResultModel> approverPermission = getB2bPermissionService().evaluatePermissions(
										order, customer, permissionsThatNeedApproval);

								if (!getPermissionResultHelper().hasOpenPermissionResult(approverPermission))
								{
									if (LOG.isDebugEnabled())
									{
										LOG.debug(String.format("Updated approver for order %s to %s for action %s", customer.getUid(),
												order.getCode(), workflowActionModel.getCode()));
									}
									workflowActionModel.setPrincipalAssigned(customer);
									getModelService().save(workflowActionModel);
									orderPermissionResults.addAll(approverPermission);
									exhaustedApprovers.add(customer);
									break;
								}
							}
						}
					}

					if (CollectionUtils.isNotEmpty(orderPermissionResults))
					{

						order.setPermissionResults(orderPermissionResults);
						order.setExhaustedApprovers(exhaustedApprovers);
						order.setStatus(OrderStatus.PENDING_APPROVAL);
						getModelService().save(order);
						return Boolean.TRUE;

					}
					else
					{
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("%s has not been escalated no eligable approvers could be found", order.getCode()));
						}
					}

				}
				else
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("%s has already been completed workflow status is %s ", workflow.getCode(),
								workflow.getStatus()));
					}
				}
				return Boolean.FALSE;
			}
		})).booleanValue();

	}

	@Override
	public boolean escalate(final OrderModel order)
	{

		final boolean hasEscalated = this.escalateInternal(order);
		if (!hasEscalated)
		{
			handleEscalationFailure(order);
		}
		return hasEscalated;
	}

	@Override
	public boolean canEscalate(final OrderModel order)
	{

		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody() 
		{
			@Override
			public Object execute()
			{
				getUserService().setCurrentUser(getUserService().getAdminUser());
				try
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("**************** Start check if the Order %s can escalate *************** ",
								order.getCode()));
					}

					final List<B2BPermissionResultModel> openPermissions = getB2bPermissionService().getOpenPermissions(order);
					final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval = getPermissionResultHelper()
							.extractPermissionTypes(openPermissions);
					final B2BCustomerModel orderUser = (B2BCustomerModel) order.getUser();
					//Only return active approvers, otherwise inactive customers can be assigned as an approver for orders.
					final List<B2BCustomerModel> allApprovers = new ArrayList(getB2bApproverService().getAllActiveApprovers(orderUser));

					final Set<B2BCustomerModel> exhaustedApprovers = new HashSet<B2BCustomerModel>(order.getExhaustedApprovers());
					allApprovers.removeAll(exhaustedApprovers);

					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("Order %s requires the following permissions %s", order.getCode(),
								permissionsThatNeedApproval));
						LOG.debug(String.format("Order %s exausted the following approvers %s", order.getCode(),
								principalListToUidString(IteratorUtils.toList(exhaustedApprovers.iterator()))));
						LOG.debug(String.format("User %s has the following approvers %s", orderUser.getUid(),
								principalListToUidString(IteratorUtils.toList(allApprovers.iterator()))));
					}

					for (final B2BCustomerModel customer : allApprovers)
					{

						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("evaluating permissions %s for user %s", permissionsThatNeedApproval,
									customer.getUid()));
						}
						final Set<B2BPermissionResultModel> approverPermission = getB2bPermissionService().evaluatePermissions(order,
								customer, permissionsThatNeedApproval);

						if (!getPermissionResultHelper().hasOpenPermissionResult(approverPermission))
						{
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;


				}
				finally
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("**************** End check if the Order %s can escalate *************** ",
								order.getCode()));
					}

				}
			}
		})).booleanValue();

	}

	@Override
	public void scheduleEscalationTask(final OrderModel order)
	{
		final EscalationTaskModel escalationTask = this.getModelService().create(EscalationTaskModel.class);
		escalationTask.setOrder(order);
		escalationTask.setCreationtime(new Date());
		final Date executionTime = new Date(System.currentTimeMillis()
				+ getConfigurationService().getConfiguration().getLong("escalationtask.executiontime.milliseconds",
						24 * 60 * 60 * 1000L));
		escalationTask.setExecutionDate(executionTime);
		escalationTask.setRunnerBean(escalationTaskRunnerBeanId);
		this.getModelService().save(escalationTask);
		getTaskService().scheduleTask(escalationTask);
	}

	@Override
	public boolean handleEscalationFailure(final OrderModel order)
	{
		//FIXME: this method should send an email to the first user with b2badmin role associated to the orders user 
		return false;
	}


	protected String principalListToUidString(final List<B2BCustomerModel> allApprovers)
	{
		final BeanToPropertyValueTransformer uidTransformer = new BeanToPropertyValueTransformer(B2BCustomerModel.UID);
		return StringUtils.join(CollectionUtils.collect(allApprovers, uidTransformer).toArray(), ",");
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public void setB2bPermissionService(final B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2bPermissionService)
	{
		this.b2bPermissionService = b2bPermissionService;
	}

	@Required
	public void setB2bApproverService(final B2BApproverService<B2BCustomerModel> b2bApproverService)
	{
		this.b2bApproverService = b2bApproverService;
	}

	@Required
	public void setTaskService(final TaskService taskService)
	{
		this.taskService = taskService;
	}

	@Required
	public void setEscalationTaskRunnerBeanId(final String escalationTaskRunnerBeanId)
	{
		this.escalationTaskRunnerBeanId = escalationTaskRunnerBeanId;
	}

	protected B2BPermissionResultHelperImpl getPermissionResultHelper()
	{
		return permissionResultHelper;
	}

	@Required
	public void setPermissionResultHelper(final B2BPermissionResultHelperImpl permissionResultHelper)
	{
		this.permissionResultHelper = permissionResultHelper;
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
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

	protected B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> getB2bPermissionService()
	{
		return b2bPermissionService;
	}

	protected B2BApproverService<B2BCustomerModel> getB2bApproverService()
	{
		return b2bApproverService;
	}

	protected TaskService getTaskService()
	{
		return taskService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}
}
