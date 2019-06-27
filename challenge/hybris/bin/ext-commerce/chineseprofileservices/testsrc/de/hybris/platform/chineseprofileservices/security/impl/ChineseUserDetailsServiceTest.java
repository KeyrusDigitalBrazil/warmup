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

package de.hybris.platform.chineseprofileservices.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chineseprofileservices.security.ChineseUserDetailsService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.spring.security.CoreUserDetails;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@IntegrationTest
public class ChineseUserDetailsServiceTest extends ServicelayerTransactionalTest
{
	private final String USER_1 = "a@sap.com";
	private final String USER_1_MOBILE = "18108050323";
	private final String USER_2 = "b@sap.com";

	@Resource
	private ChineseUserDetailsService chineseUserDetailsService;


	@Before
	public void prepare() throws ImpExException
	{
		importCsv("/chineseprofileservices/test/impex/chineseprofileservices-test-data.impex", "utf-8");
	}


	@Test
	public void test_Load_Exists_User() throws JaloInvalidParameterException, JaloSecurityException, JaloBusinessException
	{
		final CoreUserDetails coreUesrDetail = chineseUserDetailsService.loadUserByUsername(USER_1);

		assertEquals("12341234", coreUesrDetail.getPassword());
		assertEquals(new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP"), coreUesrDetail.getAuthorities().toArray()[0]);
	}

	@Test
	public void test_Load_Exists_User_Mobile() throws JaloInvalidParameterException, JaloSecurityException, JaloBusinessException
	{
		final CoreUserDetails coreUesrDetail = chineseUserDetailsService.loadUserByUsername(USER_1_MOBILE);

		assertEquals("12341234", coreUesrDetail.getPassword());
		assertEquals(new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP"), coreUesrDetail.getAuthorities().toArray()[0]);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void test_Load_Unkown_User()
	{
		chineseUserDetailsService.loadUserByUsername(USER_2);
	}

	@Test
	public void testLoadUserByNullName()
	{
		final CoreUserDetails result;
		result = chineseUserDetailsService.loadUserByUsername(null);
		assertNull(result);
	}
}
