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
package de.hybris.platform.ruleengineservices.converters;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.ruleengineservices.converters.populator.PaymentModeRaoPopulator;
import de.hybris.platform.ruleengineservices.rao.PaymentModeRAO;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class PaymentModeRaoConverterUnitTest
{

	private final static String PAYMENT_MODE_CODE = "paymentCode";
	private AbstractPopulatingConverter<PaymentModeModel, PaymentModeRAO> paymentModeRaoConverter;

	@Before
	public void setUp()
	{
		paymentModeRaoConverter = new AbstractPopulatingConverter<>();
		paymentModeRaoConverter.setTargetClass(PaymentModeRAO.class);
		paymentModeRaoConverter.setPopulators(Collections.singletonList(new PaymentModeRaoPopulator()));
	}

	@Test
	public void testConvert()
	{
		final PaymentModeModel source = new PaymentModeModel();
		source.setCode(PAYMENT_MODE_CODE);

		final PaymentModeRAO target = paymentModeRaoConverter.convert(source);
		assertThat(target.getCode()).isEqualTo(PAYMENT_MODE_CODE);
	}
}
