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
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Collection;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class B2BUserGroupModelValidateInterceptor implements ValidateInterceptor
{
	private L10NService l10NService;
	private UserService userService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		final B2BUserGroupModel thisUserGroup = (B2BUserGroupModel) model;

		if (thisUserGroup.getUid().equals(B2BConstants.TWOPOE_APPROVERS_A_GROUP)
				|| thisUserGroup.getUid().equals(B2BConstants.TWOPOE_APPROVERS_B_GROUP))
		{
			final String other2POEUserGroupId = thisUserGroup.getUid().equals(B2BConstants.TWOPOE_APPROVERS_A_GROUP) ? B2BConstants.TWOPOE_APPROVERS_B_GROUP
					: B2BConstants.TWOPOE_APPROVERS_A_GROUP;
			final B2BUserGroupModel otherUserGroup = (B2BUserGroupModel) userService.getUserGroupForUID(other2POEUserGroupId);

			final Collection<PrincipalModel> approversInBothGroups = CollectionUtils.intersection(thisUserGroup.getMembers(),
					otherUserGroup.getMembers());

			if (!approversInBothGroups.isEmpty())
			{
				throw new InterceptorException(getL10NService().getLocalizedString(
						"error.b2busergroup.2poeappover.already.in.other.group"));
			}
		}
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

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
