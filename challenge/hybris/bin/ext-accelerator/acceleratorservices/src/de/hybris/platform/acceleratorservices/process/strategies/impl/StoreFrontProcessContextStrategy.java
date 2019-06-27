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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import java.util.Optional;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Strategy to impersonate site and initialize session context from an instance of StoreFrontProcessModel.
 */
public class StoreFrontProcessContextStrategy extends AbstractProcessContextStrategy
{
	@Override
	public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcessModel)
	{
		ServicesUtil.validateParameterNotNull(businessProcessModel, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		return Optional.of(businessProcessModel)
				.filter(businessProcess -> businessProcess instanceof StoreFrontProcessModel)
				.map(businessProcess -> ((StoreFrontProcessModel) businessProcess).getSite())
				.orElse(null);
	}

	@Override
	protected CustomerModel getCustomer(final BusinessProcessModel businessProcess)
	{
		return Optional.of(businessProcess)
				.filter(bp -> bp instanceof StoreFrontCustomerProcessModel)
				.map(bp -> ((StoreFrontCustomerProcessModel) businessProcess).getCustomer())
				.orElse(null);
	}
}
