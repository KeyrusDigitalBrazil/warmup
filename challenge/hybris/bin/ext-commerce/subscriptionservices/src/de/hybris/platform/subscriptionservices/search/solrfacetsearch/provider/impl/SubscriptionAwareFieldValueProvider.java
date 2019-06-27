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
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Extension of {@link AbstractPropertyFieldValueProvider} with {@link SubscriptionProductService}.
 */
public abstract class SubscriptionAwareFieldValueProvider extends AbstractPropertyFieldValueProvider
{
	private SubscriptionProductService subscriptionProductService;

	/**
	 * @return subscription product service
	 */
	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}

}
