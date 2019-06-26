/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ordercancel.DefaultOrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelPossibilityTest;
import de.hybris.platform.ordercancel.impl.denialstrategies.ConsignmentPaymentCapturedDenialStrategy;

import org.apache.commons.lang.ArrayUtils;


/**
 * Re-implements test {@link OrderCancelPossibilityTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = OrderCancelPossibilityTest.class)
public class OrderManagementOrderCancelPossibilityTest extends OrderCancelPossibilityTest
{
	private final static DefaultOrderCancelDenialReason consignmentPaymentCapturedDenialStrategyReason = new DefaultOrderCancelDenialReason(
			1, ConsignmentPaymentCapturedDenialStrategy.class.getName());

	@Override
	protected DefaultOrderCancelDenialReason[] getReasons()
	{
		return (DefaultOrderCancelDenialReason[]) ArrayUtils
				.add(super.getReasons(), consignmentPaymentCapturedDenialStrategyReason);
	}
}

