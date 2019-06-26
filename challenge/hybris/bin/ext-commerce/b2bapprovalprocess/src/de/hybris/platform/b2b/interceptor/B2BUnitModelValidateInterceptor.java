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
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This interceptor validates that only a member of groups b2badmingroup and admingroup can create a new root.
 */
public class B2BUnitModelValidateInterceptor implements ValidateInterceptor
{
	private static final String ERROR_B2BUNIT_ROOT_CREATE_NONADMIN = "error.b2bunit.root.create.nonadmin";

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private UserService userService;
	private ModelService modelService;
	private L10NService l10NService;

	private static final Logger LOG = Logger.getLogger(B2BUnitModelValidateInterceptor.class);

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BUnitModel)
		{
			final B2BUnitModel unit = (B2BUnitModel) model;

			// check this on newly created models.
			// A b2b Unit without a parent is only allowed to be created by a user belonging to 'admingroup'.
			if (ctx.getModelService().isNew(model) && getB2bUnitService().getParent(unit) == null
					&& !getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getAdminUserGroup()))
			{
				throw new InterceptorException(getL10NService().getLocalizedString(ERROR_B2BUNIT_ROOT_CREATE_NONADMIN));
			}

			// ensure all approvers of the unit are members of the b2bapprovergroup
			if (unit.getApprovers() != null)
			{
				final HashSet<B2BCustomerModel> unitApprovers = new HashSet<B2BCustomerModel>(unit.getApprovers());
				if (CollectionUtils.isNotEmpty(unitApprovers))
				{
					final UserGroupModel b2bApproverGroup = getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);

					final Iterator<B2BCustomerModel> iterator = unitApprovers.iterator();
					while (iterator.hasNext())
					{
						final B2BCustomerModel approver = iterator.next();
						if (!getUserService().isMemberOfGroup(approver, b2bApproverGroup))
						{
							// remove approvers who are not in the b2bapprovergroup.
							iterator.remove();
							LOG.warn(String.format("Removed approver %s from unit %s due to lack of membership of group %s",
									approver.getUid(), unit.getUid(), B2BConstants.B2BAPPROVERGROUP));

						}
					}
					unit.setApprovers(unitApprovers);
				}
			}

			//ensures that all of a deactivated unit's subunit's are also deactivated (except in case of new unit).
			if (!unit.getActive().booleanValue() && !ctx.getModelService().isNew(model))
			{
				final Set<B2BUnitModel> childUnits = getB2bUnitService().getB2BUnits(unit);

				for (final B2BUnitModel child : childUnits)
				{
					if (child.getActive().booleanValue())
					{
						child.setActive(Boolean.FALSE);
						getModelService().save(child);
					}
				}
			}
		}
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}
}
