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

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Sets the owner and modifiedDate attribute of a {@link B2BCommentModel}
 */
public class B2BCommentPrepareInterceptor implements PrepareInterceptor
{
	private static final Logger LOG = Logger.getLogger(B2BCommentPrepareInterceptor.class);
	private UserService userService;

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BCommentModel)
		{
			final B2BCommentModel comment = (B2BCommentModel) model;
			if (comment.getOwner() == null)
			{
				comment.setOwner(userService.getCurrentUser());
			}
			comment.setModifiedDate(new Date());
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Comment %s owner attribute is set to %s", comment.getCode(), comment.getOwner().getUid()));
			}
		}
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
}
