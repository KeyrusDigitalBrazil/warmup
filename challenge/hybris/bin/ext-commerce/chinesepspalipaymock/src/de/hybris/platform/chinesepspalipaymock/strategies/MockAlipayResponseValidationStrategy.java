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
package de.hybris.platform.chinesepspalipaymock.strategies;

import de.hybris.platform.chinesepspalipayservices.strategies.impl.DefaultAlipayResponseValidationStrategy;

import java.util.Map;


/**
 * Mocks alipay validation after receiving alipay response
 */
public class MockAlipayResponseValidationStrategy extends DefaultAlipayResponseValidationStrategy
{

	@Override
	protected boolean validateNotifyId(final Map<String, String> params, final String partner)
	{
		return true;
	}
}
