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
package de.hybris.platform.chinesepspalipayservices.strategies;

import java.util.Map;


/**
 * Validates alipay response after reception
 */
public interface AlipayResponseValidationStrategy
{
	/**
	 * Validates response map from alipay. Returns true if response is correct
	 *
	 * @param params
	 *           alipay request map
	 * @return true if NotifyId and Signature are valid, returns false otherwise
	 */
	boolean validateResponse(final Map<String, String> params);
}
