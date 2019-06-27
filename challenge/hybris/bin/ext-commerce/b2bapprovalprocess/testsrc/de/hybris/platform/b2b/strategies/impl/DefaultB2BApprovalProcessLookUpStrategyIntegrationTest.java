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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BApprovalProcessLookUpStrategyIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultB2BApprovalProcessLookUpStrategy b2bApprovalProcessLookUpStrategy;

	@Test
	public void testGetProcesses() throws Exception
	{
		// For now the baseStore value doesn't matter
		final Map<String, String> processes = b2bApprovalProcessLookUpStrategy.getProcesses(null);
		Assert.assertNotNull(processes);
		Assert.assertEquals(1, processes.size());
		Assert.assertEquals("Escalation Approval with Merchant Check", processes.get("accApproval"));
	}
}
