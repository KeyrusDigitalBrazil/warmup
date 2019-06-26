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
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class assigned the {@link UserGroupModel} type group to the B2bCustomerGroup.
 */
public class B2BCustomerInitDefaultsInterceptor implements InitDefaultsInterceptor
{
	private UserService userService;
	private ModelService modelService;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BCustomerInitDefaultsInterceptor.class);

	@Override
	public void onInitDefaults(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BCustomerModel)
		{
			final UserGroupModel employeeGroup = getOrCreateGroup(B2BConstants.B2BCUSTOMERGROUP);
			final B2BCustomerModel customer = (B2BCustomerModel) model;
			customer.setGroups(Collections.<PrincipalGroupModel> singleton(employeeGroup));
		}
	}

	protected UserGroupModel getOrCreateGroup(final String b2bcustomergroup)
	{
		UserGroupModel employeeGroup;
		try
		{
			employeeGroup = getUserService().getUserGroupForUID(b2bcustomergroup);
		}
		catch (UnknownIdentifierException e)
		{
			employeeGroup = getModelService().create(UserGroupModel.class);
			employeeGroup.setUid(b2bcustomergroup);
			employeeGroup.setGroups(Collections.<PrincipalGroupModel> singleton(getUserService().getUserGroupForUID(
					B2BConstants.B2BGROUP)));
			getModelService().save(employeeGroup);
		}
		return employeeGroup;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}
}
