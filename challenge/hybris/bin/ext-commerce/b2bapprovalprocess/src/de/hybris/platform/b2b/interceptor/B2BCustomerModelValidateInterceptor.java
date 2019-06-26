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
package de.hybris.platform.b2b.interceptor;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This interceptor ensures that all new B2B Customers are associated with a B2BUnit {@link B2BCustomerModel},
 * {@link B2BUnitModel}
 */

public class B2BCustomerModelValidateInterceptor implements ValidateInterceptor
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private UserService userService;
	private L10NService l10NService;
	private static final Logger LOG = Logger.getLogger(B2BUnitModelValidateInterceptor.class);

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{

		if (model instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) model;
			final B2BUnitModel parentUnit = b2bUnitService.getParent(customer);

			// A b2bUnit without a parent is only allowed to be created by a user belonging to 'admingroup',
			// check this on newly created models.
			if (null == parentUnit)
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.b2bcustomer.b2bunit.missing"));
			}

			final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(
					(customer.getGroups() != null ? customer.getGroups() : Collections.emptySet()));

			CollectionUtils.filter(groups, PredicateUtils.instanceofPredicate(B2BUnitModel.class));
			if (customer.getActive().booleanValue() && !parentUnit.getActive().booleanValue())
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.b2bcustomer.enable.b2bunit.disabled"));
			}


			// ensure all approvers of the unit are members of the b2bapprovergroup
			if (customer.getApprovers() != null)
			{
				final HashSet<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(customer.getApprovers());
				if (CollectionUtils.isNotEmpty(approvers))
				{
					final UserGroupModel b2bApproverGroup = userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);

					for (final B2BCustomerModel approver : approvers)
					{
						if (!userService.isMemberOfGroup(approver, b2bApproverGroup))
						{
							// remove approvers who are not in the b2bapprovergroup.
							approvers.remove(approver);
							LOG.warn(String.format("Removed approver %s from customer %s due to lack of membership of group %s",
									approver.getUid(), customer.getUid(), B2BConstants.B2BAPPROVERGROUP));

						}
					}
					customer.setApprovers(approvers);
				}
			}

			makeSureThatB2BUnitIsInGroups(customer, parentUnit);
		}
	}

	/**
	 * Method check if B2BUnit is in groups for customer. If not it is added to groups
	 */
	protected void makeSureThatB2BUnitIsInGroups(final B2BCustomerModel customer, final B2BUnitModel parentUnit)
	{
		if (parentUnit != null && !isB2BUnitInGroupList(customer.getGroups(), parentUnit))
		{
			final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(
					(customer.getGroups() != null ? customer.getGroups() : new HashSet<PrincipalGroupModel>()));
			groups.add(parentUnit);
			customer.setGroups(groups);
		}
	}

	protected boolean isB2BUnitInGroupList(final Set<PrincipalGroupModel> groups, final B2BUnitModel parentUnit)
	{
		if (groups == null || groups.isEmpty())
		{
			return false;
		}

		for (final PrincipalGroupModel group : groups)
		{
			if (group instanceof B2BUnitModel && group.getUid().equals(parentUnit.getUid()))
			{
				return true;
			}
		}
		return false;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}
}
