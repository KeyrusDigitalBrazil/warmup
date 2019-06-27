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
package de.hybris.platform.commerceservices.organization.interceptor;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This interceptor validates:
 * <ul>
 * <li>1. Restrain orgUnit from being in more than one orgUnit group (eg 1 parent).</li>
 * <li>2. Do not allow to activate units whose parents have be disabled.</li>
 * </ul>
 *
 */
public class OrgUnitModelValidateInterceptor implements ValidateInterceptor
{
	private static final String ERROR_ORGUNIT_NO_MULTIPLE_PARENT = "error.orgunit.no.multiple.parent";
	private static final String ERROR_ORGUNIT_ENABLE_ORGUNITPARENT_DISABLED = "error.orgunit.enable.orgunitparent.disabled";

	private OrgUnitService orgUnitService;
	private ModelService modelService;
	private UserService userService;
	private L10NService l10NService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof OrgUnitModel)
		{
			final OrgUnitModel unit = (OrgUnitModel) model;

			// Restrain orgUnit from being in more than one orgUnit group (e.g 1 parent).
			if (unit.getGroups() != null)
			{
				// Filter groups by OrgUnit type
				final Set<OrgUnitModel> groups = unit.getGroups().stream().filter(grp -> grp.getItemtype().equals(unit.getItemtype()))
						.filter(grp -> grp instanceof OrgUnitModel).map(grp -> (OrgUnitModel) grp).collect(Collectors.toSet());
				if (groups.size() > 1)
				{
					throw new InterceptorException(getL10NService().getLocalizedString(ERROR_ORGUNIT_NO_MULTIPLE_PARENT, new Object[]
					{ unit.getClass().getSimpleName(), unit.getUid() }));
				}

				final OrgUnitModel parentUnit = groups.stream().findFirst().orElse(null);

				// Do not allow to activate units whose parents have be disabled.
				if (unit.getActive().booleanValue() && parentUnit != null && !parentUnit.getActive().booleanValue())
				{
					throw new InterceptorException(
							getL10NService().getLocalizedString(ERROR_ORGUNIT_ENABLE_ORGUNITPARENT_DISABLED, new Object[]
							{ unit.getClass().getSimpleName(), unit.getUid(), parentUnit.getUid() }));
				}
			}
		}
	}

	protected OrgUnitService getOrgUnitService()
	{
		return orgUnitService;
	}

	@Required
	public void setOrgUnitService(final OrgUnitService orgUnitService)
	{
		this.orgUnitService = orgUnitService;
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

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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
