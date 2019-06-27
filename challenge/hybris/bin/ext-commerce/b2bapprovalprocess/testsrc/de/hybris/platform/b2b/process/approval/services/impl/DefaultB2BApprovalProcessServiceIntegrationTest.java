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
package de.hybris.platform.b2b.process.approval.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BApprovalProcessServiceIntegrationTest extends ServicelayerTest
{

	@Resource
	private DefaultB2BApprovalProcessService b2bApprovalProcessService;

	@Test
	public void testGetProcesses() throws Exception
	{
		// For now the baseStore value doesn't matter
		final Map<String, String> processes = b2bApprovalProcessService.getProcesses(null);
		Assert.assertNotNull(processes);
		Assert.assertEquals(1, processes.size());
		Assert.assertEquals("Escalation Approval with Merchant Check", processes.get("accApproval"));
	}
}
