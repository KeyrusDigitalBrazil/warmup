/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.navigations.validator;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates fields of {@link NavigationNodeData} upon update
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade} instead.
 */
@Deprecated
public class NavigationNodeUpdateValidator implements Validator
{
	private static final String UID = "uid";
	private static final String PARENT_UID = "parentUid";
	private static final String NAME = "name";
	private static final String POSITION = "position";

	private Predicate<String> validateUidPredicate;
	private CMSNavigationService navigationService;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return NavigationNodeData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final NavigationNodeData target = (NavigationNodeData) obj;

		ValidationUtils.rejectIfEmpty(errors, UID, CmsfacadesConstants.FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, PARENT_UID, CmsfacadesConstants.FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, NAME, CmsfacadesConstants.FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, POSITION, CmsfacadesConstants.FIELD_REQUIRED);
		if (!getValidateUidPredicate().test(target.getUid()))
		{
			errors.rejectValue(UID, CmsfacadesConstants.INVALID_ROOT_NODE_UID);
		}
		if (!StringUtils.isEmpty(target.getUid()))
		{
			try
			{
				final CMSNavigationNodeModel navigationNodeModel = getNavigationService().getNavigationNodeForId(target.getUid());
				if (target.getParentUid() != null
						&& !navigationNodeModel.getParent().getUid().equalsIgnoreCase(target.getParentUid()))
				{
					errors.rejectValue(PARENT_UID, CmsfacadesConstants.INVALID_NAVIGATION_NODE_PARENT_UID);
				}
			}
			catch (final CMSItemNotFoundException e)
			{
				errors.rejectValue(UID, CmsfacadesConstants.INVALID_NAVIGATION_NODE_UID);
			}
		}

	}

	protected Predicate<String> getValidateUidPredicate()
	{
		return validateUidPredicate;
	}

	@Required
	public void setValidateUidPredicate(final Predicate<String> validateUidPredicate)
	{
		this.validateUidPredicate = validateUidPredicate;
	}

	protected CMSNavigationService getNavigationService()
	{
		return navigationService;
	}

	@Required
	public void setNavigationService(final CMSNavigationService navigationService)
	{
		this.navigationService = navigationService;
	}
}
