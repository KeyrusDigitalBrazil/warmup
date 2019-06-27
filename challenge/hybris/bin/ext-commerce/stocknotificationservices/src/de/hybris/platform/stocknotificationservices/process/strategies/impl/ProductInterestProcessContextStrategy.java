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
package de.hybris.platform.stocknotificationservices.process.strategies.impl;

import de.hybris.platform.acceleratorservices.process.strategies.impl.AbstractProcessContextStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestsProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Optional;


/**
 * New strategy to handle product interest process
 */
public class ProductInterestProcessContextStrategy extends AbstractProcessContextStrategy
{

	@Override
	public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcessModel)
	{
		ServicesUtil.validateParameterNotNull(businessProcessModel, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		return Optional.of(businessProcessModel).filter(businessProcess -> businessProcess instanceof ProductInterestsProcessModel)
				.map(businessProcess -> ((ProductInterestsProcessModel) businessProcess).getProductInterest().getBaseSite())
				.orElse(null);
	}

	@Override
	protected CustomerModel getCustomer(final BusinessProcessModel businessProcess)
	{
		return Optional.of(businessProcess).filter(bp -> bp instanceof ProductInterestsProcessModel)
				.map(bp -> ((ProductInterestsProcessModel) businessProcess).getProductInterest().getCustomer()).orElse(null);
	}

	@Override
	protected LanguageModel computeLanguage(final BusinessProcessModel businessProcess)
	{
		return Optional.of(businessProcess).filter(bp -> bp instanceof ProductInterestsProcessModel)
				.map(bp -> ((ProductInterestsProcessModel) businessProcess).getProductInterest().getLanguage())
				.orElse(super.computeLanguage(businessProcess));
	}


}
