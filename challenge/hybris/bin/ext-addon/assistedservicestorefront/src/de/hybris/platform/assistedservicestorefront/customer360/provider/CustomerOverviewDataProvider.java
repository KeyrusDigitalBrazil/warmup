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
package de.hybris.platform.assistedservicestorefront.customer360.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicestorefront.customer360.CustomerOverviewData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class CustomerOverviewDataProvider implements FragmentModelProvider<CustomerOverviewData>
{
	private UserService userService;
	private Converter<CustomerModel, CustomerOverviewData> customerOverviewDataConverter;

	@Override
	public CustomerOverviewData getModel(final Map parameters)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();

		return getCustomerOverviewDataConverter().convert(currentUser);
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

	protected Converter<CustomerModel, CustomerOverviewData> getCustomerOverviewDataConverter()
	{
		return customerOverviewDataConverter;
	}

	@Required
	public void setCustomerOverviewDataConverter(
			final Converter<CustomerModel, CustomerOverviewData> customerOverviewDataConverter)
	{
		this.customerOverviewDataConverter = customerOverviewDataConverter;
	}



}
