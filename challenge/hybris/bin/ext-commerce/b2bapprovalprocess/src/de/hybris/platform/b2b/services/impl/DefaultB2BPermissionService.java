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

import de.hybris.platform.b2b.dao.B2BPermissionDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.b2b.services.B2BPermissionService;
import de.hybris.platform.b2b.strategies.PermissionEvaluateStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BPermissionService}.
 *
 * @spring.bean b2bPermissionService
 */
public class DefaultB2BPermissionService implements B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel>
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BPermissionService.class);
	private Set<PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>> evaluateStrategies;
	private ModelService modelService;
	private SessionService sessionService;
	private B2BApproverService<B2BCustomerModel> b2bApproverService;
	private B2BPermissionResultHelperImpl permissionResultHelper;
	private UserService userService;
	private BaseDao baseDao;
	private List<String> permissionTypes;
	private SearchRestrictionService searchRestrictionService;
	private B2BCommentService<AbstractOrderModel> b2bCommentService;
	private B2BPermissionDao b2bPermissionDao;


	@Override
	public Set<B2BPermissionResultModel> evaluatePermissions(final AbstractOrderModel order, final B2BCustomerModel employee,
			final List<Class<? extends B2BPermissionModel>> permissionTypes)
	{
		final Set<B2BPermissionResultModel> permissionResults = new HashSet<B2BPermissionResultModel>();
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				getUserService().setCurrentUser(getUserService().getAdminUser());
				for (final PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel> evaluateStrategy : evaluateStrategies)
				{
					if (permissionTypes == null || permissionTypes.contains(evaluateStrategy.getPermissionType()))
					{
						final B2BPermissionResultModel evaluationResult = evaluateStrategy.evaluate(order, employee);
						if (evaluationResult != null)
						{
							permissionResults.add(evaluationResult);
						}
					}
				}
			}
		});
		if (LOG.isDebugEnabled())
		{
			for (final B2BPermissionResultModel r : permissionResults)
			{
				LOG.debug(String.format("PermissionResult %s|%s|%s ", r.getPermissionTypeCode(), r.getStatus(),
						r.getApprover().getUid()));
			}
		}

		return permissionResults;
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by
	 *             {@link #getApproversForOpenPermissions(AbstractOrderModel, B2BCustomerModel, Collection)}
	 */
	@Deprecated
	@Override
	public Set<B2BPermissionResultModel> findApproversForOpenPermissions(final AbstractOrderModel order,
			final B2BCustomerModel employee, final Collection<B2BPermissionResultModel> openPermissions)
	{
		return getApproversForOpenPermissions(order, employee, openPermissions);
	}

	@Override
	public Set<B2BPermissionResultModel> getApproversForOpenPermissions(final AbstractOrderModel order,
			final B2BCustomerModel employee, final Collection<B2BPermissionResultModel> openPermissions)
	{
		//Only return active approvers, otherwise inactive customers can be assigned as an approver for orders.
		final List<B2BCustomerModel> allApprovers = getB2bApproverService().getAllActiveApprovers(employee);

		final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval = getPermissionResultHelper()
				.extractPermissionTypes(openPermissions);
		return checkPermissionsOfApprovers(order, permissionsThatNeedApproval, allApprovers, Boolean.TRUE);

	}

	/**
	 * Get open permissions for approvers
	 *
	 * @param order
	 *           the order
	 * @param employee
	 *           the employee
	 * @param openPermissions
	 *           the permissions
	 * @param allApprovers
	 *           the approvers
	 * @return the {@link Set} of open {@link B2BPermissionResultModel}
	 */
	public Set<B2BPermissionResultModel> getOpenPermissonsForApprovers(final AbstractOrderModel order,
			final B2BCustomerModel employee, final Collection<B2BPermissionResultModel> openPermissions,
			final List<B2BCustomerModel> allApprovers)
	{
		final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval = getPermissionResultHelper()
				.extractPermissionTypes(openPermissions);
		return checkPermissionsOfApprovers(order, permissionsThatNeedApproval, allApprovers, Boolean.TRUE);

	}



	/**
	 * Checks the permissions of the approvers for an order.
	 *
	 * @param order
	 *           the order
	 * @param permissionsThatNeedApproval
	 *           A list of permissions types for which approval is needed.
	 * @param allApprovers
	 *           A list of approvers
	 * @param fastReturn
	 *           If true return once an approver can satisfy a workflow is found
	 * @return A set of permission results.
	 */
	protected Set<B2BPermissionResultModel> checkPermissionsOfApprovers(final AbstractOrderModel order,
			final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval, final List<B2BCustomerModel> allApprovers,
			final Boolean fastReturn) // NOSONAR
	{
		final Map<B2BCustomerModel, Collection<B2BPermissionResultModel>> pendingPermissions = new HashMap<B2BCustomerModel, Collection<B2BPermissionResultModel>>();
		final Set<B2BPermissionResultModel> permissionResultModels = new HashSet<B2BPermissionResultModel>();

		if (CollectionUtils.isNotEmpty(allApprovers))
		{
			for (final B2BCustomerModel approver : allApprovers)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Evaluating permissions %s for user %s",
							Arrays.toString(permissionsThatNeedApproval.toArray()), approver.getUid()));
				}
				final Set<B2BPermissionResultModel> permissionsOfApprover = this.evaluatePermissions(order, approver,
						permissionsThatNeedApproval);

				// if none of the permission results have open status the approver's permission result can be returned.
				if (!getPermissionResultHelper().hasOpenPermissionResult(permissionsOfApprover))
				{
					if (LOG.isDebugEnabled()) // NOSONAR
					{
						LOG.debug(String.format("%s has the correct permissions %s", approver.getUid(), permissionsThatNeedApproval));
					}
					permissionResultModels.addAll(permissionsOfApprover);

					// we have located an approver that can approve the order.
					if (BooleanUtils.isTrue(fastReturn)) // NOSONAR
					{
						return permissionResultModels;
					}
				}
				else
				{
					if (LOG.isDebugEnabled()) // NOSONAR
					{
						LOG.debug(permissionResultListToString(approver, permissionsOfApprover));
					}

					pendingPermissions.put(approver, getPermissionResultHelper().filterResultByPermissionStatus(permissionsOfApprover,
							PermissionStatus.PENDING_APPROVAL));
				}
			}

			if (CollectionUtils.isNotEmpty(permissionResultModels))
			{
				return permissionResultModels;
			}
			else
			{
				//FIXME: this may need to be refactored out... since this method is expected to return a result for approver that can approve the whole order. // NOSONAR
				final GroupPendingPermissionResults groupPendingPermissionResults = new GroupPendingPermissionResults(
						permissionsThatNeedApproval, pendingPermissions).invoke();
				if (groupPendingPermissionResults.is())
				{
					return groupPendingPermissionResults.getApproverPermissions();
				}
			}

		}
		handleError(order);
		return Collections.emptySet();
	}

	protected void handleError(final AbstractOrderModel order)
	{
		final String comment = String.format(
				"Could not find approvers or the permission is missing for order %s placed by employee %s", order.getCode(),
				order.getUser().getUid());
		LOG.error(comment);
		final B2BCommentModel b2bComment = this.getModelService().create(B2BCommentModel.class);
		b2bComment.setCode("Permissions");
		b2bComment.setComment(comment);
		getB2bCommentService().addComment(order, b2bComment);
		this.getModelService().save(order);
	}

	protected String permissionResultListToString(final B2BCustomerModel customer,
			final Set<B2BPermissionResultModel> approverPermissions)
	{
		final StringBuilder log = new StringBuilder();
		log.append(String.format("%s does not have correct permissions", customer.getUid())).append("\n");
		for (final B2BPermissionResultModel b2bPermissionResultModel : approverPermissions)
		{
			log.append(String.format("PermissionResult %s|%s|%s ", b2bPermissionResultModel.getPermissionTypeCode(),
					b2bPermissionResultModel.getStatus(), b2bPermissionResultModel.getApprover().getUid())).append("\n");
		}
		return log.toString();
	}

	@Override
	public boolean needsApproval(final AbstractOrderModel order)
	{
		final Set<B2BPermissionResultModel> approverPermissions = this.evaluatePermissions(order,
				(B2BCustomerModel) order.getUser(), null);
		return getPermissionResultHelper().hasOpenPermissionResult(approverPermissions);
	}

	@Override
	public Map<B2BCustomerModel, B2BPermissionResultModel> getEligableApprovers(final OrderModel order)
	{

		final Map<B2BCustomerModel, B2BPermissionResultModel> result = new HashMap<B2BCustomerModel, B2BPermissionResultModel>();
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				getUserService().setCurrentUser(getUserService().getAdminUser());
				final List<B2BCustomerModel> allApprovers = getB2bApproverService()
						.getAllApprovers((B2BCustomerModel) order.getUser());
				final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval = new ArrayList<Class<? extends B2BPermissionModel>>();
				for (final PermissionEvaluateStrategy<? extends B2BPermissionResultModel, ? extends AbstractOrderModel, ? extends B2BCustomerModel> evaluateStrategy : evaluateStrategies)
				{
					permissionsThatNeedApproval.add(evaluateStrategy.getPermissionType());
				}
				final Set<B2BPermissionResultModel> permissionResultModelSet = checkPermissionsOfApprovers(order,
						permissionsThatNeedApproval, allApprovers, Boolean.FALSE);
				for (final B2BPermissionResultModel permissionResult : permissionResultModelSet)
				{
					result.put(permissionResult.getApprover(), permissionResult);
				}
			}
		});

		return result;
	}

	@Override
	public List<B2BPermissionResultModel> getOpenPermissions(final AbstractOrderModel order)
	{
		return (List<B2BPermissionResultModel>) getPermissionResultHelper().filterResultByPermissionStatus(
				this.evaluatePermissions(order, (B2BCustomerModel) order.getUser(), null), PermissionStatus.OPEN);

	}

	protected Set<PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>> getEvaluateStrategies()
	{
		return evaluateStrategies;
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getB2BPermissionForCode(String)}
	 */
	@Override
	@Deprecated
	public B2BPermissionModel findB2BPermissionByCode(final String code)
	{
		return getB2BPermissionForCode(code);
	}

	@Override
	public B2BPermissionModel getB2BPermissionForCode(final String code)
	{
		return getB2bPermissionDao().findPermissionByCode(code);
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getAllB2BPermissions()}
	 */
	@Override
	@Deprecated
	public Set<B2BPermissionModel> findAllB2BPermissions()
	{
		return getAllB2BPermissions();
	}

	@Override
	public Set<B2BPermissionModel> getAllB2BPermissions()
	{
		final HashSet<B2BPermissionModel> models = new HashSet<B2BPermissionModel>();
		CollectionUtils.addAll(models, getBaseDao().findAll(-1, 0, B2BPermissionModel.class).iterator());
		return models;
	}

	@Required
	public void setEvaluateStrategies(
			final Set<PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>> evaluateStrategies)
	{
		this.evaluateStrategies = evaluateStrategies;
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
	public void setB2bApproverService(final B2BApproverService<B2BCustomerModel> b2bApproverService)
	{
		this.b2bApproverService = b2bApproverService;
	}

	protected B2BApproverService<B2BCustomerModel> getB2bApproverService()
	{
		return b2bApproverService;
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

	protected BaseDao getBaseDao()
	{
		return baseDao;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	private class GroupPendingPermissionResults
	{
		private boolean myResult;
		private final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval;
		private final Map<B2BCustomerModel, Collection<B2BPermissionResultModel>> pendingPermissions;
		private Set<B2BPermissionResultModel> approverPermissions;

		public GroupPendingPermissionResults(final List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval,
				final Map<B2BCustomerModel, Collection<B2BPermissionResultModel>> pendingPermissions)
		{
			this.permissionsThatNeedApproval = permissionsThatNeedApproval;
			this.pendingPermissions = pendingPermissions;
		}

		boolean is()
		{
			return myResult;
		}

		protected Set<B2BPermissionResultModel> getApproverPermissions()
		{
			return approverPermissions;
		}

		public GroupPendingPermissionResults invoke()
		{
			// Try to look over pending approvers and make a set of mulitple.
			//FIXME: Refactor into seperate method and test. // NOSONAR
			for (final Iterator<B2BCustomerModel> outerInter = pendingPermissions.keySet().iterator(); outerInter.hasNext();)
			{
				final B2BCustomerModel empl = outerInter.next();
				approverPermissions = new HashSet<B2BPermissionResultModel>(pendingPermissions.get(empl));
				for (final Iterator<B2BCustomerModel> innerInter = pendingPermissions.keySet().iterator(); innerInter.hasNext();)
				{
					final B2BCustomerModel innerEmpl = innerInter.next();
					if (empl.equals(innerEmpl))
					{
						break;
					}
					else
					{
						approverPermissions.addAll(pendingPermissions.get(innerEmpl));
						if (getPermissionResultHelper().extractPermissionTypes(approverPermissions)
								.containsAll(permissionsThatNeedApproval)) // NOSONAR
						{
							myResult = true;
							return this;
						}
					}
				}
			}
			myResult = false;
			return this;
		}
	}

	@Override
	public boolean permissionExists(final String code)
	{
		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return Boolean.valueOf(getB2BPermissionForCode(code) != null);
			}
		})).booleanValue();
	}

	protected List<String> getPermissionTypes()
	{
		return permissionTypes;
	}

	@Required
	public void setPermissionTypes(final List<String> permissionTypes)
	{
		this.permissionTypes = permissionTypes;
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getAllB2BPermissionTypes()}
	 */
	@Override
	@Deprecated
	public List<String> findAllB2BPermissionTypes()
	{
		return getAllB2BPermissionTypes();
	}

	@Override
	public List<String> getAllB2BPermissionTypes()
	{
		return getPermissionTypes();
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
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

	protected B2BPermissionDao getB2bPermissionDao()
	{
		return b2bPermissionDao;
	}

	@Required
	public void setB2bPermissionDao(final B2BPermissionDao b2bPermissionDao)
	{
		this.b2bPermissionDao = b2bPermissionDao;
	}
}
