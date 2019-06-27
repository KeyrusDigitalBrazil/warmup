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
package com.hybris.backoffice.cockpitng.dataaccess.facades.permissions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import org.junit.Before;
import org.junit.Test;


public class DefaultReadPermissionCacheTest
{
	private static final String READABLE_TYPE = "readableType";
	private static final String NOT_READABLE_TYPE = "notReadableType";
	private static final String NON_EXISTED_TYPE = "nonExistedType";
	private static final String READABLE_ATTRIBUTE = "readableAttribute";
	private static final String NOT_READABLE_ATTRIBUTE = "notReadableAttribute";
	private static final String NON_EXISTED_ATTRIBUTE = "nonExistedAttribute";

	private PermissionCRUDService permissionCRUDService;

	private DefaultReadPermissionCache defaultReadPermissionCache;

	@Before
	public void setUp()
	{
		permissionCRUDService = mock(PermissionCRUDService.class);
		when(permissionCRUDService.canReadType(READABLE_TYPE)).thenReturn(true);
		when(permissionCRUDService.canReadType(NOT_READABLE_TYPE)).thenReturn(false);
		when(permissionCRUDService.canReadType(NON_EXISTED_TYPE)).thenThrow(UnknownIdentifierException.class);
		when(permissionCRUDService.canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE)).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(any(), eq(NOT_READABLE_ATTRIBUTE))).thenReturn(false);
		when(permissionCRUDService.canReadAttribute(any(), eq(NON_EXISTED_ATTRIBUTE))).thenReturn(false);
		when(permissionCRUDService.canReadAttribute(eq(NOT_READABLE_TYPE), any())).thenReturn(false);
		when(permissionCRUDService.canReadAttribute(eq(NON_EXISTED_TYPE), any())).thenReturn(false);
		defaultReadPermissionCache = spy(new DefaultReadPermissionCache(permissionCRUDService));
	}

	@Test
	public void shouldCallPermissionCRUDServiceOnlyOneTimeWhenCanReadTypeForSameTypeNameCalledMultipleTimes()
	{
		//when
		defaultReadPermissionCache.canReadType(READABLE_TYPE);
		defaultReadPermissionCache.canReadType(READABLE_TYPE);
		defaultReadPermissionCache.canReadType(READABLE_TYPE);

		//then
		verify(permissionCRUDService, times(1)).canReadType(READABLE_TYPE);
	}

	@Test
	public void shouldAlwaysReturnTrueWhenTypeIsReadable()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadType(READABLE_TYPE);
		final boolean cachedResult = defaultReadPermissionCache.canReadType(READABLE_TYPE);

		//then
		assertThat(nonCachedResult).isTrue();
		assertThat(cachedResult).isTrue();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenTypeIsNotReadable()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadType(NOT_READABLE_TYPE);
		final boolean cachedResult = defaultReadPermissionCache.canReadType(NOT_READABLE_TYPE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenTypeNotExists()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadType(NON_EXISTED_TYPE);
		final boolean cachedResult = defaultReadPermissionCache.canReadType(NON_EXISTED_TYPE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}

	@Test
	public void shouldCallPermissionCRUDServiceOnlyOneTimeWhenCanReadAttributeForSameTypeNameAndAttributeNameCalledMultipleTimes()
	{
		//when
		defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE);
		defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE);
		defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE);

		//then
		verify(permissionCRUDService, times(1)).canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE);
	}

	@Test
	public void shouldAlwaysReturnTrueWhenAttributeForTypeIsReadable()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE);
		final boolean cachedResult = defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, READABLE_ATTRIBUTE);

		//then
		assertThat(nonCachedResult).isTrue();
		assertThat(cachedResult).isTrue();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenAttributeForReadableTypeIsNotReadable()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, NOT_READABLE_ATTRIBUTE);
		final boolean cachedResult = defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, NOT_READABLE_ATTRIBUTE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenAttributeForReadableTypeNotExists()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, NON_EXISTED_ATTRIBUTE);
		final boolean cachedResult = defaultReadPermissionCache.canReadAttribute(READABLE_TYPE, NON_EXISTED_ATTRIBUTE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenTypeIsNotReadableAndAttributeIsReadable()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadAttribute(NOT_READABLE_TYPE, READABLE_ATTRIBUTE);
		final boolean cachedResult = defaultReadPermissionCache.canReadAttribute(NOT_READABLE_TYPE, READABLE_ATTRIBUTE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenTypeIsNotReadableAndAttributeIsNotReadable()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadAttribute(NOT_READABLE_TYPE, NOT_READABLE_ATTRIBUTE);
		final boolean cachedResult = defaultReadPermissionCache.canReadAttribute(NOT_READABLE_TYPE, NOT_READABLE_ATTRIBUTE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}

	@Test
	public void shouldAlwaysReturnFalseWhenTypeIsNotReadableAndAttributeNotExists()
	{
		//when
		final boolean nonCachedResult = defaultReadPermissionCache.canReadAttribute(NOT_READABLE_TYPE, NON_EXISTED_ATTRIBUTE);
		final boolean cachedResult = defaultReadPermissionCache.canReadAttribute(NOT_READABLE_TYPE, NON_EXISTED_ATTRIBUTE);

		//then
		assertThat(nonCachedResult).isFalse();
		assertThat(cachedResult).isFalse();
	}
}
