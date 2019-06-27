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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.processengine.definition.ProcessDefinitionFactory;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.StandardDateRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueChangeClosure;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of the {@link B2BUnitService}
 *
 * @spring.bean b2bUnitService
 */
public class DefaultB2BUnitService implements B2BUnitService<B2BUnitModel, B2BCustomerModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BUnitService.class);

	private B2BUnitDao b2bUnitDao;
	private PrincipalGroupMembersDao principalGroupMembersDao;
	private ModelService modelService;
	private SessionService sessionService;
	private UserService userService;
	private ConfigurationService configurationService;
	private SearchRestrictionService searchRestrictionService;
	private TypeService typeService;
	private ProcessDefinitionFactory processDefinitionFactory;

	/**
	 * @deprecated Since 6.0. Use {@link #getBranch(B2BUnitModel)} instead
	 */
	@Deprecated
	@Override
	public Set<B2BUnitModel> getAllUnitsOfOrganization(final B2BUnitModel unit)
	{
		final Set<B2BUnitModel> organizationSet = new HashSet<B2BUnitModel>();
		this.getBranch(unit, organizationSet);
		return organizationSet;
	}

	@Override
	public Set<B2BUnitModel> getBranch(final B2BUnitModel unit)
	{
		final Set<B2BUnitModel> organizationSet = new HashSet<B2BUnitModel>();
		this.getBranch(unit, organizationSet);
		return organizationSet;
	}

	@Override
	public void disableBranch(final B2BUnitModel unit)
	{
		validateParameterNotNullStandardMessage("unit", unit);
		this.toggleBranch(unit, Boolean.FALSE);
	}

	@Override
	public void enableBranch(final B2BUnitModel unit)
	{
		validateParameterNotNullStandardMessage("unit", unit);
		this.toggleBranch(unit, Boolean.TRUE);
	}

	@Override
	public void disableUnit(final B2BUnitModel unit)
	{
		validateParameterNotNullStandardMessage("unit", unit);
		this.toggleUnit(unit, Boolean.FALSE);
		this.getModelService().save(unit);
	}

	@Override
	public void enableUnit(final B2BUnitModel unit)
	{
		validateParameterNotNullStandardMessage("unit", unit);
		this.toggleUnit(unit, Boolean.TRUE);
		this.getModelService().save(unit);
	}

	@Override
	public void setCurrentUnit(final B2BCustomerModel customer, final B2BUnitModel unit)
	{
		customer.setDefaultB2BUnit(unit);
		this.getModelService().save(customer);
		this.updateBranchInSession(this.getSessionService().getCurrentSession(), customer);
	}


	/**
	 * Enables or disables a branch and it sibling types {@link B2BCostCenterModel} {@link B2BBudgetModel} and
	 * {@link B2BCustomerModel} assigned to all units in the branch.
	 *
	 * @param unit
	 *           A unit to look up a branch for via {@link #getBranch(de.hybris.platform.b2b.model.B2BUnitModel)}
	 * @param enable
	 *           If {@link Boolean#TRUE} all units in the branch will be activated by setting active attribute to true.
	 *           If false the reverse will happen.
	 */
	protected void toggleBranch(final B2BUnitModel unit, final Boolean enable)
	{
		// disable all units first
		final Set<B2BUnitModel> branch = getBranch(unit);
		CollectionUtils.forAllDo(branch, new Closure()
		{
			@Override
			public void execute(final Object object)
			{
				final B2BUnitModel b2bUnitModel = (B2BUnitModel) object;
				toggleUnit(b2bUnitModel, enable);
			}
		});
		this.getModelService().saveAll(branch);

	}

	protected void toggleUnit(final B2BUnitModel b2bUnitModel, final Boolean enable)
	{
		b2bUnitModel.setActive(enable);
		//disable budgets
		final List<B2BBudgetModel> budgets = b2bUnitModel.getBudgets();
		CollectionUtils.forAllDo(budgets, new BeanPropertyValueChangeClosure(B2BBudgetModel.ACTIVE, enable));
		getModelService().saveAll(budgets);

		// disable cost centers
		final List<B2BCostCenterModel> costCenterModels = b2bUnitModel.getCostCenters();
		CollectionUtils.forAllDo(costCenterModels, new BeanPropertyValueChangeClosure(B2BCostCenterModel.ACTIVE, enable));
		getModelService().saveAll(costCenterModels);

		// disable customers and hmc login
		final Set<B2BCustomerModel> customers = new HashSet<B2BCustomerModel>(getB2BCustomers(b2bUnitModel));
		CollectionUtils.forAllDo(customers, new BeanPropertyValueChangeClosure(B2BCustomerModel.ACTIVE, enable));
		CollectionUtils.forAllDo(customers,
				new BeanPropertyValueChangeClosure(B2BCustomerModel.HMCLOGINDISABLED, BooleanUtils.negate(enable)));
		getModelService().saveAll(customers);

	}

	@Override
	public Set<B2BUnitModel> getB2BUnits(final B2BUnitModel unit)
	{
		return new HashSet<B2BUnitModel>(getPrincipalGroupMembersDao().findAllMembersByType(unit, B2BUnitModel.class));
	}

	@Override
	public Set<B2BCustomerModel> getB2BCustomers(final B2BUnitModel unit)
	{
		return new HashSet<B2BCustomerModel>(getPrincipalGroupMembersDao().findAllMembersByType(unit, B2BCustomerModel.class));
	}


	protected void getBranch(final B2BUnitModel parent, final Set<B2BUnitModel> organizationSet)
	{
		final Set<B2BUnitModel> decendants = new HashSet<B2BUnitModel>();
		final Set<B2BUnitModel> rootDecendants = new HashSet<B2BUnitModel>(getB2BUnits(parent));

		// remove elements that are already in the passed in collection from a recursive call,
		// otherwise an infinite recursion will occur.
		rootDecendants.removeAll(organizationSet);

		// update the local collection for which a recursive call will be made to walk up or down the organizaton
		// structure
		decendants.addAll(rootDecendants);

		// update the result collection.
		organizationSet.add(parent);
		organizationSet.addAll(decendants);

		for (final B2BUnitModel unit : decendants)
		{
			// recursive call to evaluate the children.
			this.getBranch(unit, organizationSet);
		}
	}

	@Override
	public Set<B2BUnitModel> getAllUnitsOfOrganization(final B2BCustomerModel employee)
	{
		return this.getBranch(this.getParent(employee));
	}

	@Override
	public B2BUnitModel getParent(final B2BCustomerModel employee)
	{
		if (employee == null)
		{
			return null;
		}
		else if (employee.getDefaultB2BUnit() != null)
		{
			// the customer has selected a unit to use.
			return employee.getDefaultB2BUnit();
		}
		else
		{
			// customer has not selected a default parent unit, fist one in the list will be chosen.
			return (B2BUnitModel) CollectionUtils.find(employee.getGroups(), PredicateUtils.instanceofPredicate(B2BUnitModel.class));
		}
	}

	@Override
	public B2BUnitModel getParent(final B2BUnitModel unit)
	{
		final Set<PrincipalGroupModel> groups = unit.getGroups();
		return (B2BUnitModel) CollectionUtils.find(groups, PredicateUtils.instanceofPredicate(B2BUnitModel.class));

	}

	@Override
	public B2BUnitModel getRootUnit(final B2BUnitModel unit)
	{
		B2BUnitModel parent = unit;
		B2BUnitModel root = parent;
		while (parent != null)
		{
			parent = this.getParent(parent);
			if (parent != null)
			{
				root = parent;
			}
		}

		return root;
	}

	@Override
	public List<B2BUnitModel> getAllParents(final B2BUnitModel unit)
	{
		final List<B2BUnitModel> parents = new ArrayList<B2BUnitModel>();
		B2BUnitModel parent = unit;
		parents.add(parent);
		while (parent != null)
		{
			parent = this.getParent(parent);
			if (parent != null)
			{
				parents.add(parent);
			}
		}

		return parents;
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getUnitForUid(String)} instead
	 */
	@Deprecated
	@Override
	public B2BUnitModel findUnitByUid(final String uid)
	{
		return getUnitForUid(uid);
	}

	@Override
	public B2BUnitModel getUnitForUid(final String uid)
	{
		B2BUnitModel unit;
		try
		{
			unit = getUserService().getUserGroupForUID(uid, B2BUnitModel.class);
		}
		catch (final UnknownIdentifierException | ClassMismatchException e)
		{
			unit = null;
			LOG.error("Failed to get unit: " + uid, e);
		}
		return unit;
	}

	@Override
	public Set<B2BCustomerModel> getCustomers(final Set<B2BUnitModel> branch)
	{
		final Set<B2BCustomerModel> employeesOfBranch = new HashSet<B2BCustomerModel>();
		for (final B2BUnitModel b2bUnitModel : branch)
		{
			employeesOfBranch.addAll(getB2BCustomers(b2bUnitModel));
		}
		return employeesOfBranch;

	}

	@Override
	public <M extends PrincipalModel> Set<M> getAllUserGroupMembersForType(final UserGroupModel userGroup,
			final Class<M> memberType)
	{
		return new HashSet<M>(getPrincipalGroupMembersDao().findAllMembersByType(userGroup, memberType));
	}

	@Override
	public void addMember(final B2BUnitModel group, final PrincipalModel member)
	{
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>((member.getGroups() != null ? member.getGroups()
				: Collections.emptySet()));
		// for units only one parent is allowed however customers can belong to multiple units
		if (member instanceof B2BUnitModel)
		{
			CollectionUtils.filter(groups, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUnitModel.class)));
		}
		groups.add(group);
		member.setGroups(groups);
	}

	@Override
	public void updateParentB2BUnit(final B2BUnitModel parentB2BUnit, final PrincipalModel member)
	{
		Assert.notNull(parentB2BUnit, "parent unit can't be null");
		Assert.notNull(member, "the member parameter can't be null");
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>((member.getGroups() != null ? member.getGroups()
				: Collections.emptySet()));
		// if the member of the group is a B2BCustomer update his defaultB2BUnit attribute.
		if (member instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) member;
			b2BCustomerModel.setDefaultB2BUnit(parentB2BUnit);
		}
		else if (member instanceof B2BUnitModel)
		{
			// remove existing b2bunit from members groups, because unit can only have one parent
			CollectionUtils.filter(groups, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUnitModel.class)));
		}
		groups.add(parentB2BUnit);
		member.setGroups(groups);
	}

	/**
	 * @deprecated Since 6.0. Use
	 *             {@link #getUsersOfUserGroup(de.hybris.platform.b2b.model.B2BUnitModel, String, boolean)}
	 */
	@Deprecated
	@Override
	public Collection<B2BCustomerModel> getUsersOfUserGroup(final B2BUnitModel unit, final String userGroupId)
	{
		return this.getUsersOfUserGroup(unit, userGroupId, true);
	}


	@Override
	public Collection<B2BCustomerModel> getUsersOfUserGroup(final B2BUnitModel unit, final String userGroupId,
			final boolean recursive)
	{
		final Collection<B2BCustomerModel> employeesOfGroup = getB2bUnitDao().findB2BUnitMembersByGroup(unit, userGroupId);

		if (CollectionUtils.isNotEmpty(employeesOfGroup))
		{
			return employeesOfGroup;
		}
		else if (recursive)
		{

			return (this.getParent(unit) == null ? Collections.<B2BCustomerModel> emptyList() : getUsersOfUserGroup(
					this.getParent(unit), userGroupId, recursive));
		}
		else
		{
			return Collections.<B2BCustomerModel> emptyList();
		}
	}

	@Override
	public void updateBranchInSession(final Session session, final UserModel currentUser)
	{
		if (currentUser instanceof B2BCustomerModel)
		{
			final Object[] branchInfo = (Object[]) getSessionService().executeInLocalView(new SessionExecutionBody()
			{
				@Override
				public Object[] execute()
				{
					getSearchRestrictionService().disableSearchRestrictions();
					final B2BCustomerModel currentCustomer = (B2BCustomerModel) currentUser;
					final B2BUnitModel unitOfCustomer = getParent(currentCustomer);


					/**
					 * Europe1PriceFactory does not allow a user to belong to multiple price groups with themselves have
					 * different UPGs assigned see https://jira.hybris.com/browse/BTOB-488 get the upg assigned to the parent
					 * unit and set it in the context if none is assigned default to 'B2B_DEFAULT_PRICE_GROUP'
					 */
					final EnumerationValueModel userPriceGroup = (unitOfCustomer.getUserPriceGroup() != null ? getTypeService()
							.getEnumerationValue(unitOfCustomer.getUserPriceGroup()) : lookupPriceGroupFromClosestParent(unitOfCustomer));
					return new Object[]
					{ getRootUnit(unitOfCustomer), getBranch(unitOfCustomer), unitOfCustomer, userPriceGroup };
				}
			});

			getSessionService().setAttribute(B2BConstants.CTX_ATTRIBUTE_ROOTUNIT, branchInfo[0]);
			getSessionService().setAttribute(B2BConstants.CTX_ATTRIBUTE_BRANCH, branchInfo[1]);
			getSessionService().setAttribute(B2BConstants.CTX_ATTRIBUTE_UNIT, branchInfo[2]);
			getSessionService().setAttribute(Europe1Constants.PARAMS.UPG, branchInfo[3]);


		}
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getApprovalProcessCodeForUnit(B2BUnitModel)} instead
	 */
	@Deprecated
	@Override
	public String findApprovalProcessCodeForUnit(final B2BUnitModel unit)
	{
		return getApprovalProcessCodeForUnit(unit);
	}

	@Override
	public String getApprovalProcessCodeForUnit(final B2BUnitModel unit)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2b.services.impl.B2BUnitServiceProxy.getApprovalProcessCodeForUnit(B2BUnitModel) of b2bapprovalprocess extenstion.");
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @deprecated Since 6.0. Use {@link #getAllProcessDefinitionsNames()}
	 */
	@Override
	@Deprecated
	public List<String> getAllApprovalProcesses()
	{
		return new ArrayList<String>(this.getAllProcessDefinitionsNames());
	}

	@Override
	public Set<String> getAllProcessDefinitionsNames()
	{
		return getProcessDefinitionFactory().getAllProcessDefinitionsNames();
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getAccountManagerForUnit(B2BUnitModel)} instead
	 */
	@Deprecated
	@Override
	public EmployeeModel findAccountManagerForUnit(final B2BUnitModel unit)
	{
		return getAccountManagerForUnit(unit);
	}

	@Override
	public EmployeeModel getAccountManagerForUnit(final B2BUnitModel unit)
	{
		B2BUnitModel parent = unit;
		EmployeeModel accountManager = unit.getAccountManager();
		while (parent != null && accountManager == null)
		{
			parent = this.getParent(parent);

			if (parent != null && parent.getAccountManager() != null)
			{
				accountManager = parent.getAccountManager();
			}
		}
		return accountManager;
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getUnitWithCreditLimit(B2BUnitModel)} instead
	 */
	@Deprecated
	@Override
	public B2BUnitModel findUnitWithCreditLimit(final B2BUnitModel unit, final CurrencyModel currency)
	{
		return getUnitWithCreditLimit(unit);
	}

	@Override
	public B2BUnitModel getUnitWithCreditLimit(final B2BUnitModel unit)
	{
		B2BUnitModel parent = unit;

		if (getActiveCreditLimit(unit) == null)
		{
			while (parent != null)
			{
				parent = this.getParent(parent);

				if (parent != null && getActiveCreditLimit(parent) != null)
				{
					break;
				}
			}
		}
		return parent;
	}

	protected B2BCreditLimitModel getActiveCreditLimit(final B2BUnitModel unit)
	{
		B2BCreditLimitModel creditLimit = null;

		if (unit.getCreditLimit() != null && unit.getCreditLimit().getActive())
		{
			if (unit.getCreditLimit().getDatePeriod() != null)
			{
				final StandardDateRange datePeriod = unit.getCreditLimit().getDatePeriod();
				if (datePeriod != null && datePeriod.getEnd().after(new Date()))
				{
					creditLimit = unit.getCreditLimit();
				}
			}
			else
			{
				creditLimit = unit.getCreditLimit();
			}

		}
		return creditLimit;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}


	protected EnumerationValueModel lookupPriceGroupFromClosestParent(final B2BUnitModel unitOfCustomer)
	{
		for (final B2BUnitModel unitModel : getAllParents(unitOfCustomer))
		{
			if (unitModel.getUserPriceGroup() != null)
			{
				return getTypeService().getEnumerationValue(unitModel.getUserPriceGroup());
			}
		}
		return getTypeService().getEnumerationValue(UserPriceGroup._TYPECODE, B2BConstants.B2BDEFAULTPRICEGROUP);
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	@Required
	public void setB2bUnitDao(final B2BUnitDao b2bUnitDao)
	{
		this.b2bUnitDao = b2bUnitDao;
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

	protected UserService getUserService()
	{
		return userService;
	}

	protected ProcessDefinitionFactory getProcessDefinitionFactory()
	{
		return processDefinitionFactory;
	}

	@Required
	public void setProcessDefinitionFactory(final ProcessDefinitionFactory processDefinitionFactory)
	{
		this.processDefinitionFactory = processDefinitionFactory;
	}

	protected PrincipalGroupMembersDao getPrincipalGroupMembersDao()
	{
		return principalGroupMembersDao;
	}

	@Required
	public void setPrincipalGroupMembersDao(final PrincipalGroupMembersDao principalGroupMembersDao)
	{
		this.principalGroupMembersDao = principalGroupMembersDao;
	}

	protected B2BUnitDao getB2bUnitDao()
	{
		return b2bUnitDao;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected TypeService getTypeService()
	{
		return this.typeService;
	}
}
