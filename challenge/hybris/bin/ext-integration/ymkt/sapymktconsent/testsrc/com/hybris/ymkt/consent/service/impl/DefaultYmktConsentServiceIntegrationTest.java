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
package com.hybris.ymkt.consent.service.impl;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.consent.YmktConsentService;


@IntegrationTest
public class DefaultYmktConsentServiceIntegrationTest extends ServicelayerBaseTest
{

	@Resource(name = "ymktConsentService")
	YmktConsentService ymktConsentService;

	@Before
	public void setUp() throws Exception
	{
		//
	}

	@Test
	public void testGetAnonymousUserConsent()
	{
		Assert.assertFalse(ymktConsentService.getUserConsent("---"));
	}

	@Test
	public void testGetRegisteredUserConsent()
	{
		Assert.assertFalse(ymktConsentService.getUserConsent("", "---"));
	}

}
