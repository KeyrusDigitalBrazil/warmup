/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.common.filter;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class SapOutboundIdFilterTest
{
	SapOutboundIdFilter filter = new SapOutboundIdFilter();

	@Test
	public void testProcessId()
	{
		// 40 Characters Base16
		Assert.assertTrue(filter.processId("3958A7DC8D5F2F8FE41160CDB5E7419FC1C7A2BA").isPresent());
		Assert.assertFalse(filter.processId("Z958A7DC8D5F2F8FE41160CDB5E7419FC1C7A2BA").isPresent());

		Assert.assertFalse(filter.processId(null).isPresent());
		Assert.assertFalse(filter.processId("").isPresent());
		Assert.assertFalse(filter.processId("0123456789ABCDEF0123456789ABCDEF").isPresent()); // 32
	}

}
