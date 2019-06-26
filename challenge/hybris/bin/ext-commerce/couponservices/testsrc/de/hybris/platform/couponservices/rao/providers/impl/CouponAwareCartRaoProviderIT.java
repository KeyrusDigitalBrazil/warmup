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
package de.hybris.platform.couponservices.rao.providers.impl;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.AbstractCouponAwareCartIT;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.providers.impl.AbstractExpandedRAOProvider;
import de.hybris.platform.ruleengineservices.ruleengine.impl.CartTestContextBuilder;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@IntegrationTest
public class CouponAwareCartRaoProviderIT extends AbstractCouponAwareCartIT
{

	private final static Logger logger = LoggerFactory.getLogger(CouponAwareCartRaoProviderIT.class);

	@Resource
	private AbstractExpandedRAOProvider cartRaoProvider;

	@Test
	public void testExpandFactModelExpandWithCoupons()
	{
		logger.debug("Provider class: {}", cartRaoProvider.getClass());

		final Set<?> facts = cartRaoProvider.expandFactModel(getCartTestContextBuilder().getCartModel(),
				singletonList(CouponCartRaoExtractor.EXPAND_COUPONS));

		final CartTestContextBuilder contextBuilder = getCartTestContextBuilder();
		final CartRAO cartRAO = contextBuilder.getCartRAO();
		cartRAO.setCode(contextBuilder.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, getExpectedCouponRAO());
	}

	@Test
	public void testExpandFactModelExpand()
	{
		final Set<?> facts = cartRaoProvider.expandFactModel(getCartTestContextBuilder().getCartModel());

		final CartTestContextBuilder contextBuilder = getCartTestContextBuilder();
		final CartRAO cartRAO = contextBuilder.getCartRAO();
		cartRAO.setCode(contextBuilder.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, getExpectedCouponRAO());
	}

}
