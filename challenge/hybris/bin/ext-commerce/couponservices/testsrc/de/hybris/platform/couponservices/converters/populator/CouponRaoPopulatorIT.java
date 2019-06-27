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
package de.hybris.platform.couponservices.converters.populator;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.AbstractCouponAwareCartIT;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class CouponRaoPopulatorIT extends AbstractCouponAwareCartIT
{

	@Resource
	private CouponRaoPopulator couponRaoPopulator;

	@Test
	public void testPopulateOk() throws ConversionException
	{
		final CartRAO cartRao = new CartRAO();
		couponRaoPopulator.populate(getCartTestContextBuilder().getCartModel(), cartRao);

		assertThat(cartRao.getCoupons()).hasSize(1);
		assertThat(cartRao.getCoupons().get(0)).isEqualTo(getExpectedCouponRAO());
	}

}
