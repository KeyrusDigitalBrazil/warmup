/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class GigyaConfigValidateInterceptorTest
{

	private final GigyaConfigValidateInterceptor interceptor = new GigyaConfigValidateInterceptor();

	private GigyaConfigModel gigyaConfig;

	@Before
	public void setUp()
	{
		gigyaConfig = new GigyaConfigModel();
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenApiKeyIsMissing() throws InterceptorException
	{
		interceptor.onValidate(gigyaConfig, null);
		fail("Not yet implemented");
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenUserSecretIsMissing() throws InterceptorException
	{
		gigyaConfig.setGigyaApiKey("api-key");
		gigyaConfig.setGigyaUserKey("user-key");
		interceptor.onValidate(gigyaConfig, null);
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenUserKeyIsMissing() throws InterceptorException
	{
		gigyaConfig.setGigyaApiKey("api-key");
		gigyaConfig.setGigyaUserSecret("user-secret");
		interceptor.onValidate(gigyaConfig, null);
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenUserKeyAndSecretIsMissing() throws InterceptorException
	{
		gigyaConfig.setGigyaApiKey("api-key");
		interceptor.onValidate(gigyaConfig, null);
	}

}
