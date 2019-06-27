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

import de.hybris.platform.b2b.dao.B2BWorflowActionDao;
import de.hybris.platform.b2b.dao.B2BWorkflowDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2b.strategies.WorkflowTemplateStrategy;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link B2BWorkflowIntegrationService}
 */
public class DefaultB2BWorkflowIntegrationService implements B2BWorkflowIntegrationService
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BWorkflowIntegrationService.class);
	private UserService userService;
	private BaseDao baseDao;
	private B2BWorflowActionDao b2bWorkflowActionDao;
	private B2BWorkflowDao b2bWorkflowDao;
	private WorkflowService workflowService;
	private WorkflowActionService workflowActionService;
	private WorkflowTemplateService workflowTemplateService;
	private WorkflowProcessingService workflowProcessingService;
	private WorkflowAttachmentService workflowAttachmentService;
	private List<WorkflowTemplateStrategy> workflowTemplateStrategies;
	private ModelService modelService;
	private SessionService sessionService;

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getActionForCode(String)}
	 */
	@Override
	@Deprecated
	public WorkflowActionModel getActionByCode(final String code)
	{
		return getActionForCode(code);
	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated
	public WorkflowActionModel getActionForCode(final String code) // NOSONAR
	{
		return getB2bWorkflowActionDao().findWorkflowActionByCode(code);
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by
	 *             {@link #getWorkflowActionsForActionStatusAndUser(WorkflowActionStatus, String, UserModel)}
	 */
	@Override
	@Deprecated
	public Collection<WorkflowActionModel> findByActionStatusAndUser(final WorkflowActionStatus status, final String qualifier,
			final UserModel user)
	{
		return getWorkflowActionsForActionStatusAndUser(status, qualifier, user);
	}

	/**
	 * Gets all WorkflowActions by user, workflowActionStatus (eg IN_PROGRESS) and qualifier (eg APPROVAL)
	 *
	 * @param status
	 *           the work flow action status
	 * @param qualifier
	 *           the qualifier (eg APPROVAL)
	 * @param user
	 *           the user
	 * @return WorkflowActionModel the set of work flow actions
	 */
	@Override
	public Collection<WorkflowActionModel> getWorkflowActionsForActionStatusAndUser(final WorkflowActionStatus status,
			final String qualifier, final UserModel user)
	{

		return getB2bWorkflowActionDao().findWorkflowActionsByUserActionCodeAndStatus(status, qualifier, user);
	}


	@Override
	public Collection<WorkflowActionModel> getWorkflowActionsForUser(final UserModel user)
	{

		return getB2bWorkflowActionDao().findWorkflowActionsByUser(user);
	}

	/**
	 * @deprecated Since 6.2. Use {@link #decideAction(de.hybris.platform.workflow.model.WorkflowActionModel, String)}
	 *             decideAction(action, DECISIONCODES.APPROVE.name())
	 */
	@Override
	@Deprecated
	public void approveWorkflowAction(final WorkflowActionModel workflowActionModel)
	{
		this.decideAction(workflowActionModel, DECISIONCODES.APPROVE.name());
	}

	/**
	 * @deprecated Since 6.2. Use {@link #decideAction(de.hybris.platform.workflow.model.WorkflowActionModel, String)}
	 *             decideAction(action, DECISIONCODES.REJECT.name())
	 */
	@Override
	@Deprecated
	public void rejectWorkflowAction(final WorkflowActionModel workflowActionModel)
	{
		this.decideAction(workflowActionModel, DECISIONCODES.REJECT.name());

	}


	/**
	 * Makes a decision on an action based on the qualifier (The qualifier is used to look up a decision with its code
	 * build from {@link WorkflowActionModel#CODE}_<param>decisionQualifier</param>)
	 *
	 * @param workflowActionModel
	 *           The action to make a decision upon
	 * @param decisionQualifier
	 *           a qualifier based on {@link B2BWorkflowIntegrationService} DECISIONCODES enumeration
	 */
	@Override
	public void decideAction(final WorkflowActionModel workflowActionModel, final String decisionQualifier)
	{
		final Collection<WorkflowDecisionModel> decisions = workflowActionModel.getDecisions();
		// find the decision by qualifier attribute.
		final WorkflowDecisionModel workflowDecisionModel = (WorkflowDecisionModel) CollectionUtils.find(decisions,
				new BeanPropertyValueEqualsPredicate(WorkflowDecisionModel.QUALIFIER, decisionQualifier));
		Assert.notNull(workflowDecisionModel,
				String.format("Could not locate a decision %s for workflowAction %s out of decisions %s", decisionQualifier,
						workflowActionModel.getCode(),
						CollectionUtils.collect(decisions, new BeanToPropertyValueTransformer(WorkflowDecisionModel.CODE)).toString()));
		decideAction(workflowActionModel, workflowDecisionModel);
		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("selected %s decision on action %s", workflowDecisionModel.getCode(),
					workflowActionModel.getCode()));
		}
	}

	protected void decideAction(final WorkflowActionModel workflowActionModel, final WorkflowDecisionModel workflowDecisionModel)
	{
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final UserModel currentUser = getUserService().getCurrentUser();
				try
				{
					// The current user is checked by the workflow processing service and the check for admin user
					// is done first. Keep the switching to admin user for backward compatibility.
					getUserService().setCurrentUser(getUserService().getAdminUser());
					getWorkflowProcessingService().decideAction(workflowActionModel, workflowDecisionModel);
				}
				finally
				{
					getUserService().setCurrentUser(currentUser);
				}
			}
		});
	}

	@Override
	public OrderModel getOrderFromAction(final WorkflowActionModel workflowActionModel)
	{
		final B2BApprovalProcessModel attachment = (B2BApprovalProcessModel) CollectionUtils
				.find(workflowActionModel.getAttachmentItems(), PredicateUtils.instanceofPredicate(B2BApprovalProcessModel.class));

		if (attachment == null)
		{
			LOG.error("No attachment of type B2BApprovalProcessModel " + "is found on WorkflowAction " + workflowActionModel);
			return null;
		}
		else
		{
			return attachment.getOrder();
		}
	}

	/**
	 * @deprecated Since 4.4. Use
	 *             {@link de.hybris.platform.workflow.WorkflowService#createWorkflow(String, de.hybris.platform.workflow.model.WorkflowTemplateModel, java.util.List, de.hybris.platform.core.model.user.UserModel)}
	 *             usage of the method should be removed pending fix to https://jira.hybris.com/browse/PLA-10938
	 */
	@Override
	@Deprecated
	public WorkflowModel createWorkflow(final WorkflowTemplateModel template, final List<? extends ItemModel> attachments)
	{
		final WorkflowModel workflow = getWorkflowService().createWorkflow(template.getName(), template,
				new ArrayList<ItemModel>(attachments), template.getOwner());

		for (final WorkflowActionModel workAction : workflow.getActions())
		{
			this.getModelService().save(workAction);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created workflow %s with description %s from template %s", workflow.getCode(),
					workflow.getDescription(), template.getCode()));
		}
		return workflow;

	}

	/**
	 * @deprecated Since 4.4. Unused, will be removed in the next release
	 */
	@Deprecated
	protected WorkflowActionModel getWorkAction(final WorkflowActionTemplateModel templateAction,
			final Collection<WorkflowActionModel> workflowActions)
	{
		for (final WorkflowActionModel act : workflowActions)
		{
			if (act.getTemplate().equals(templateAction))
			{
				if (LOG.isInfoEnabled())
				{
					LOG.info(String.format("Found WorkflowAction %s for WorkflowActionTemplate %s", act.getCode(),
							templateAction.getCode()));
				}
				return act;
			}
		}
		return null;
	}

	@Override
	public WorkflowTemplateModel getWorkflowTemplateForCode(final String code)
	{
		try
		{
			return getWorkflowTemplateService().getWorkflowTemplateForCode(code);
		}
		catch (final UnknownIdentifierException uie)
		{
			return null;
		}
	}

	/**
	 * @deprecated Since 4.4. Use
	 *             {@link de.hybris.platform.workflow.WorkflowProcessingService#startWorkflow(de.hybris.platform.workflow.model.WorkflowModel)}
	 *             usage of the method should be removed pending fix to https://jira.hybris.com/browse/PLA-10938
	 */
	@Override
	@Deprecated
	public void startWorkflow(final WorkflowModel workflowModel)
	{
		getWorkflowProcessingService().startWorkflow(workflowModel);
		// startWorkflow is changing the status of actions but is not saving the models.
		for (final WorkflowActionModel action : workflowModel.getActions())
		{
			this.getModelService().save(action);
		}

	}

	@Override
	public String generateWorkflowTemplateCode(final String prefix, final List<? extends UserModel> users)
	{
		final StringBuilder id = new StringBuilder(prefix);
		final BeanToPropertyValueTransformer uidTransformer = new BeanToPropertyValueTransformer(UserModel.UID);
		// sort the transformed collection
		final Collection uidCollection = CollectionUtils.collect(users, uidTransformer);
		Collections.sort((List<Comparable>) uidCollection);
		id.append(StringUtils.join(Arrays.asList(uidCollection), "_"));
		return id.toString();
	}


	@Override
	public Collection<WorkflowActionModel> getStartWorkflowActions(final WorkflowModel workflow)
	{
		return getWorkflowActionService().getStartWorkflowActions(workflow);
	}

	@Override
	public WorkflowTemplateModel createWorkflowTemplate(final List<? extends UserModel> users, final String code,
			final String description, final WorkflowTemplateType templateType)
	{

		WorkflowTemplateModel workflowTemplateModel = getWorkflowTemplateForCode(code);
		if (workflowTemplateModel == null)
		{
			final WorkflowTemplateStrategy strategy = getWorkflowTempateStrategy(templateType);
			Assert.notNull(strategy, String.format("Expected to find a strategy of type %s for with type %s",
					WorkflowTemplateStrategy.class, templateType));
			workflowTemplateModel = strategy.createWorkflowTemplate(users, code, description);
		}
		return workflowTemplateModel;
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getWorkflowForOrder(OrderModel)}
	 */
	@Override
	@Deprecated
	public WorkflowModel findWorkflowForOrder(final OrderModel order)
	{
		return getWorkflowForOrder(order);
	}

	@Override
	public WorkflowModel getWorkflowForOrder(final OrderModel order)
	{
		return this.getB2bWorkflowDao().findWorkflowByOrder(order);
	}

	protected BaseDao getBaseDao()
	{
		return baseDao;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
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

	@Required
	public void setB2bWorkflowActionDao(final B2BWorflowActionDao b2bWorkflowActionDao)
	{
		this.b2bWorkflowActionDao = b2bWorkflowActionDao;
	}

	protected B2BWorflowActionDao getB2bWorkflowActionDao()
	{
		return b2bWorkflowActionDao;
	}

	protected B2BWorkflowDao getB2bWorkflowDao()
	{
		return b2bWorkflowDao;
	}

	@Required
	public void setB2bWorkflowDao(final B2BWorkflowDao b2bWorkflowDao)
	{
		this.b2bWorkflowDao = b2bWorkflowDao;
	}

	protected WorkflowService getWorkflowService()
	{
		return workflowService;
	}

	@Required
	public void setWorkflowService(final WorkflowService workflowService)
	{
		this.workflowService = workflowService;
	}

	protected WorkflowActionService getWorkflowActionService()
	{
		return workflowActionService;
	}

	@Required
	public void setWorkflowActionService(final WorkflowActionService workflowActionService)
	{
		this.workflowActionService = workflowActionService;
	}

	protected WorkflowTemplateService getWorkflowTemplateService()
	{
		return workflowTemplateService;
	}

	@Required
	public void setWorkflowTemplateService(final WorkflowTemplateService workflowTemplateService)
	{
		this.workflowTemplateService = workflowTemplateService;
	}

	protected WorkflowProcessingService getWorkflowProcessingService()
	{
		return workflowProcessingService;
	}

	@Required
	public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
	{
		this.workflowProcessingService = workflowProcessingService;
	}

	protected WorkflowAttachmentService getWorkflowAttachmentService()
	{
		return workflowAttachmentService;
	}

	/**
	 * Gets a workflow template strategy by template type
	 *
	 * @param workflowTemplateType
	 *           the template type
	 * @return the {@link WorkflowTemplateStrategy}
	 */
	public WorkflowTemplateStrategy getWorkflowTempateStrategy(final WorkflowTemplateType workflowTemplateType)
	{
		return (WorkflowTemplateStrategy) CollectionUtils.find(workflowTemplateStrategies,
				new BeanPropertyValueEqualsPredicate("workflowTemplateType", workflowTemplateType.getCode()));
	}

	@Autowired
	public void setWorkflowTemplateStrategies(final List<WorkflowTemplateStrategy> workflowTemplateStrategies)
	{
		this.workflowTemplateStrategies = workflowTemplateStrategies;
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
}
