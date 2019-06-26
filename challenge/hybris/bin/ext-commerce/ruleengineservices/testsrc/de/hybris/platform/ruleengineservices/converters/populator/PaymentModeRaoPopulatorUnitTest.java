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
package de.hybris.platform.ruleengineservices.converters.populator;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.ruleengineservices.rao.PaymentModeRAO;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class PaymentModeRaoPopulatorUnitTest
{
	private final static String PAYMENT_CODE = "paymentCode";
	private PaymentModeRaoPopulator populator;

	@Before
	public void setUp()
	{
		populator = new PaymentModeRaoPopulator();
	}

	@Test
	public void testPopulate()
	{
		final PaymentModeModel source = new PaymentModeModel();
		source.setCode(PAYMENT_CODE);
		final PaymentModeRAO target = new PaymentModeRAO();
		populator.populate(source, target);

		assertThat(target.getCode()).isEqualTo(PAYMENT_CODE);
	}

}
