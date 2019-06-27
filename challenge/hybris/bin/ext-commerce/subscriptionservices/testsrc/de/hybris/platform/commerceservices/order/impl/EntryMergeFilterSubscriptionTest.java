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
package de.hybris.platform.commerceservices.order.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
public class EntryMergeFilterSubscriptionTest
{
	@InjectMocks
	private EntryMergeFilterSubscription subscriptionFilter = new EntryMergeFilterSubscription();
	@Mock
	private SubscriptionProductService subscriptionProductService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAllowMergeOfRegularProducts()
	{
		final AbstractOrderEntryModel candidate = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel target = new AbstractOrderEntryModel();
		when(Boolean.valueOf(subscriptionProductService.isSubscription(null))).thenReturn(Boolean.FALSE);

		final Boolean result = subscriptionFilter.apply(candidate, target);

		assertTrue(result.booleanValue());
	}

	@Test
	public void shouldDenyMergeIfCandidateIsSubscription()
	{
		final AbstractOrderEntryModel candidate = new AbstractOrderEntryModel();
		candidate.setProduct(new ProductModel());
		final AbstractOrderEntryModel target = new AbstractOrderEntryModel();
		when(Boolean.valueOf(subscriptionProductService.isSubscription(candidate.getProduct()))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(target.getProduct()))).thenReturn(Boolean.FALSE);

		final Boolean result = subscriptionFilter.apply(candidate, target);

		assertFalse(result.booleanValue());
	}

	@Test
	public void shouldDenyMergeIfTargetIsSubscription()
	{
		final AbstractOrderEntryModel candidate = new AbstractOrderEntryModel();
		candidate.setProduct(new ProductModel());
		final AbstractOrderEntryModel target = new AbstractOrderEntryModel();
		when(Boolean.valueOf(subscriptionProductService.isSubscription(candidate.getProduct()))).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(target.getProduct()))).thenReturn(Boolean.TRUE);

		final Boolean result = subscriptionFilter.apply(candidate, target);

		assertFalse(result.booleanValue());
	}

	@Test
	public void shouldDenyMergeIfBothArgsAreSubscription()
	{
		final AbstractOrderEntryModel candidate = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel target = new AbstractOrderEntryModel();
		when(Boolean.valueOf(subscriptionProductService.isSubscription(null))).thenReturn(Boolean.TRUE);

		final Boolean result = subscriptionFilter.apply(candidate, target);

		assertFalse(result.booleanValue());
	}
}
