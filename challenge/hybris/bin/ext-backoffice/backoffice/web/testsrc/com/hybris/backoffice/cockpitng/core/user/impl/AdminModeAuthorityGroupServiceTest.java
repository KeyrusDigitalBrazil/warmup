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
package com.hybris.backoffice.cockpitng.core.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Session;

import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.core.user.impl.AuthorityGroup;


@RunWith(MockitoJUnitRunner.class)
public class AdminModeAuthorityGroupServiceTest
{
	private static final String TEST_USER_ID = "TestUserId";

	@InjectMocks
	@Spy
	private AdminModeAuthorityGroupService adminModeAuthorityGroupService;

	@Mock
	private CockpitUserService cockpitUserService;

	@Mock
	private Session session;
	@Mock
	private AuthorityGroup authorityGroup;

	@Before
	public void init()
	{
		doReturn(session).when(adminModeAuthorityGroupService).getCurrentSession();
	}

	@Test
	public void testGetActiveAuthorityGroupForNonAdminUser()
	{
		// given
		when(cockpitUserService.isAdmin(TEST_USER_ID)).thenReturn(Boolean.FALSE);
		when(session.getAttribute(AdminModeAuthorityGroupService.IMPERSONATED_AUTHORITY_GROUP)).thenReturn(authorityGroup);

		// when
		final AuthorityGroup outputAuthorityGroup = adminModeAuthorityGroupService.getActiveAuthorityGroupForUser(TEST_USER_ID);

		// then
		assertThat(outputAuthorityGroup).isNull();
	}

	@Test
	public void testGetActiveAuthorityGroupForAdminUser()
	{
		// given
		when(cockpitUserService.isAdmin(TEST_USER_ID)).thenReturn(Boolean.TRUE);
		when(session.getAttribute(AdminModeAuthorityGroupService.IMPERSONATED_AUTHORITY_GROUP)).thenReturn(authorityGroup);

		// when
		final AuthorityGroup outputAuthorityGroup = adminModeAuthorityGroupService.getActiveAuthorityGroupForUser(TEST_USER_ID);

		// then
		assertThat(authorityGroup).isSameAs(outputAuthorityGroup);
	}

	@Test
	public void testSetActiveAuthorityGroupForUser()
	{
		// when
		adminModeAuthorityGroupService.setActiveAuthorityGroupForUser(authorityGroup);

		// then
		verify(session).setAttribute(AdminModeAuthorityGroupService.IMPERSONATED_AUTHORITY_GROUP, authorityGroup);
	}
}
