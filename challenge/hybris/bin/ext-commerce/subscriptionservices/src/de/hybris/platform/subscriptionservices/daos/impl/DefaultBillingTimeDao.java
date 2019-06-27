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
package de.hybris.platform.subscriptionservices.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.subscriptionservices.daos.BillingTimeDao;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;


/**
 * Default implementation of the {@link BillingTimeDao}.
 */
public class DefaultBillingTimeDao extends AbstractItemDao implements BillingTimeDao
{

	private static final String FIND_ALL_BILLINGFREQUENCIES_QUERY = "SELECT {" + BillingTimeModel.PK + "} FROM {"
			+ BillingTimeModel._TYPECODE + "} ";

	@Override
	@Nonnull
	public List<BillingTimeModel> findAllBillingTimes()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_ALL_BILLINGFREQUENCIES_QUERY);

		final SearchResult<BillingTimeModel> results = search(flexibleSearchQuery);

		return results.getResult();
	}

	@Override
	@Nonnull
	public BillingTimeModel findBillingTimeByCode(@Nonnull final String code)
	{
		validateParameterNotNullStandardMessage("code", code);

		final BillingTimeModel example = new BillingTimeModel();
		example.setCode(code);
		try
		{
			return getFlexibleSearchService().getModelByExample(example);
		}
		catch (final ModelNotFoundException exp)
		{
			if ("paynow".equalsIgnoreCase(code))
			{
				// in case the code is not found and this happens only in integration testing for Pay Now billing time
				final BillingEventModel payNowBillingTime = new BillingEventModel();
				payNowBillingTime.setCartAware(Boolean.TRUE);
				payNowBillingTime.setCode(code);
				payNowBillingTime.setOrder(Integer.valueOf(1));
				payNowBillingTime.setDescription("Pay Now", Locale.US);
				payNowBillingTime.setNameInOrder("Paid on order", Locale.US);
				payNowBillingTime.setNameInCart("Pay on Checkout", Locale.US);
				getModelService().save(payNowBillingTime);

				return payNowBillingTime;
			}
			throw exp;
		}
	}
}
