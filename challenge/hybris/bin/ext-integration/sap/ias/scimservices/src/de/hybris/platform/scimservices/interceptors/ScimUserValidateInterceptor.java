/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimservices.interceptors;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Interceptor to validate user with scim user ID to ensure scim user ID is unique.
 */
public class ScimUserValidateInterceptor implements ValidateInterceptor<UserModel>
{

	private static final Logger LOG = Logger.getLogger(ScimUserValidateInterceptor.class);

	private FlexibleSearchService flexibleSearchService;

	@Override
	public void onValidate(final UserModel userModel, final InterceptorContext context) throws InterceptorException
	{
		if (StringUtils.isNotEmpty(userModel.getScimUserId()))
		{
			try
			{
				final UserModel exampleUserModel = new UserModel();
				exampleUserModel.setScimUserId(userModel.getScimUserId());

				final List<UserModel> userModels = flexibleSearchService.getModelsByExample(exampleUserModel);

				// Checks if it is a newly created model then no other user model should have the same scimUserId
				if (context.isNew(userModel) && CollectionUtils.isNotEmpty(userModels))
				{
					throw new InterceptorException("User with scimUserId=" + userModel.getScimUserId() + " already exists.");
				}

				// Checks if it is an update to existing user model then current user model matches the returned user models entry
				if (!context.isNew(userModel) && CollectionUtils.isNotEmpty(userModels) && !userModel.equals(userModels.get(0)))
				{
					throw new InterceptorException("User with scimUserId=" + userModel.getScimUserId() + " already exists.");
				}
			}
			catch (final ModelNotFoundException e)
			{
				LOG.error("No user model with scimUserId=" + userModel.getScimUserId());
				LOG.info("No validation needs to be done for this user.");
			}
		}
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
