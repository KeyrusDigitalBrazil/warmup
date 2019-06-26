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
package de.hybris.platform.subscriptionservices.subscription.impl;

import de.hybris.platform.subscriptionservices.daos.BillingTimeDao;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;


/**
 * Default implementation of {@link BillingTimeService}.
 * 
 */
public class DefaultBillingTimeService implements BillingTimeService
{
	private BillingTimeDao billingTimeDao;

	@Override
	@Nonnull
	public List<BillingTimeModel> getAllBillingTimes()
	{
		return getBillingTimeDao().findAllBillingTimes();
	}

	@Override
	@Nonnull
	public BillingTimeModel getBillingTimeForCode(@Nonnull final String code)
	{
		return getBillingTimeDao().findBillingTimeByCode(code);
	}

	protected BillingTimeDao getBillingTimeDao()
	{
		return billingTimeDao;
	}

	@Required
	public void setBillingTimeDao(final BillingTimeDao billingTimeDao)
	{
		this.billingTimeDao = billingTimeDao;
	}

}
