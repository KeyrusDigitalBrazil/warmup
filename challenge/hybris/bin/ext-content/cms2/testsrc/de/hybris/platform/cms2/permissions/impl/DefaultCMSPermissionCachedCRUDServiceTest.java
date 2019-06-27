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
package de.hybris.platform.cms2.permissions.impl;

import static de.hybris.platform.cms2.constants.Cms2Constants.GENERATED_ATTRIBUTE_PERMISSION_CACHED_RESULTS;
import static de.hybris.platform.cms2.constants.Cms2Constants.GENERATED_TYPE_PERMISSION_CACHED_RESULTS;
import static de.hybris.platform.cms2.permissions.impl.DefaultCMSPermissionCachedCRUDService.KEY_DELIMITER;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.service.SessionCachedContextProvider;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSPermissionCachedCRUDServiceTest
{
	private final String TYPE_CODE = "typeCode";
	private final String ATTRIBUTE_QUALIFIER = "attributeQualifier";

	@Mock
	private SessionCachedContextProvider sessionCachedContextProvider;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@InjectMocks
	private DefaultCMSPermissionCachedCRUDService defaultCMSPermissionCachedCRUDService;

	@Test
	public void shouldGetTypePermissionFromDatabaseIfNotInCache()
	{
		// GIVEN
		final Map<Object, Object> cache = new HashMap<>();
		final String uniqueKey = PermissionsConstants.CREATE + KEY_DELIMITER + TYPE_CODE;
		when(sessionCachedContextProvider.getAllItemsFromMapCache(GENERATED_TYPE_PERMISSION_CACHED_RESULTS)).thenReturn(cache);
		when(permissionCRUDService.canCreateTypeInstance(TYPE_CODE)).thenReturn(true);

		// WHEN
		final boolean result = defaultCMSPermissionCachedCRUDService.canCreateTypeInstance(TYPE_CODE);

		// THEN
		verify(permissionCRUDService).canCreateTypeInstance(TYPE_CODE);
		verify(sessionCachedContextProvider).addItemToMapCache(GENERATED_TYPE_PERMISSION_CACHED_RESULTS, uniqueKey, true);
	}

	@Test
	public void shouldGetTypePermissionFromCacheIfInCache()
	{
		// GIVEN
		final String uniqueKey = PermissionsConstants.CREATE + KEY_DELIMITER + TYPE_CODE;
		final boolean hasPermission = true;
		final Map<Object, Object> cache = new HashMap<>();
		cache.put(uniqueKey, hasPermission);
		when(sessionCachedContextProvider.getAllItemsFromMapCache(GENERATED_TYPE_PERMISSION_CACHED_RESULTS)).thenReturn(cache);

		// WHEN
		final boolean result = defaultCMSPermissionCachedCRUDService.canCreateTypeInstance(TYPE_CODE);

		// THEN
		verify(permissionCRUDService, never()).canCreateTypeInstance(TYPE_CODE);
		verify(sessionCachedContextProvider, never()).addItemToMapCache(GENERATED_TYPE_PERMISSION_CACHED_RESULTS, uniqueKey, hasPermission);
	}

	@Test
	public void shouldGetAttributePermissionFromDatabaseIfNotInCache()
	{
		// GIVEN
		final Map<Object, Object> cache = new HashMap<>();
		final String uniqueKey = PermissionsConstants.READ + KEY_DELIMITER + TYPE_CODE + KEY_DELIMITER + ATTRIBUTE_QUALIFIER;
		final boolean hasPermission = true;
		when(sessionCachedContextProvider.getAllItemsFromMapCache(GENERATED_ATTRIBUTE_PERMISSION_CACHED_RESULTS)).thenReturn(cache);
		when(permissionCRUDService.canReadAttribute(TYPE_CODE, ATTRIBUTE_QUALIFIER)).thenReturn(hasPermission);

		// WHEN
		final boolean result = defaultCMSPermissionCachedCRUDService.canReadAttribute(TYPE_CODE, ATTRIBUTE_QUALIFIER);

		// THEN
		verify(permissionCRUDService).canReadAttribute(TYPE_CODE, ATTRIBUTE_QUALIFIER);
		verify(sessionCachedContextProvider).addItemToMapCache(GENERATED_ATTRIBUTE_PERMISSION_CACHED_RESULTS, uniqueKey, hasPermission);
	}

	@Test
	public void shouldGetAttributePermissionFromCacheIfInCache()
	{
		// GIVEN
		final Map<Object, Object> cache = new HashMap<>();
		final String uniqueKey = PermissionsConstants.READ + KEY_DELIMITER + TYPE_CODE + KEY_DELIMITER + ATTRIBUTE_QUALIFIER;
		final boolean hasPermission = true;
		cache.put(uniqueKey, hasPermission);
		when(sessionCachedContextProvider.getAllItemsFromMapCache(GENERATED_ATTRIBUTE_PERMISSION_CACHED_RESULTS)).thenReturn(cache);
		when(permissionCRUDService.canReadAttribute(TYPE_CODE, ATTRIBUTE_QUALIFIER)).thenReturn(hasPermission);

		// WHEN
		final boolean result = defaultCMSPermissionCachedCRUDService.canReadAttribute(TYPE_CODE, ATTRIBUTE_QUALIFIER);

		// THEN
		verify(permissionCRUDService, never()).canReadAttribute(TYPE_CODE, ATTRIBUTE_QUALIFIER);
		verify(sessionCachedContextProvider, never()).addItemToMapCache(GENERATED_ATTRIBUTE_PERMISSION_CACHED_RESULTS, uniqueKey, hasPermission);
	}
}
