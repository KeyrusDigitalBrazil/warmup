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
package de.hybris.platform.commerceservices.model;

import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import org.springframework.beans.factory.annotation.Required;

/**
 * Dynamic attribute handler for the Customer.contactEmail attribute.
 * The Customer.contactEmail attribute is determined by {@link CustomerEmailResolutionService}.
 */
public class ContactEmailAttribute extends AbstractDynamicAttributeHandler<String, CustomerModel>
{
	private CustomerEmailResolutionService customerEmailResolutionService;

	protected CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	@Required
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	@Override
	public String get(final CustomerModel customerModel)
	{
		if (customerModel == null)
		{
			throw new IllegalArgumentException("customer model is required");
		}

		return getCustomerEmailResolutionService().getEmailForCustomer(customerModel);
	}
}
