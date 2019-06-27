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
package com.hybris.backoffice.widgets.selectivesync.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;


@RunWith(MockitoJUnitRunner.class)
public class SelectiveSyncRendererTest
{
	@Mock
	private PermissionFacade permissionFacade;

	@InjectMocks
	private SelectiveSyncRenderer renderer;

	@Test
	public void testDisabledByPermissions()
	{
		// given
		when(permissionFacade.canChangeType(any())).thenReturn(false);

		// when
		final boolean result = renderer.isEditable();

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void testEnabled()
	{
		// given
		when(permissionFacade.canChangeType(any())).thenReturn(true);

		// when
		final boolean result = renderer.isEditable();

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testDisabledByCatalogVersionSyncJobModel()
	{
		// given
		when(permissionFacade.canChangeType(CatalogVersionSyncJobModel._TYPECODE)).thenReturn(false);
		when(permissionFacade.canChangeType(SyncAttributeDescriptorConfigModel._TYPECODE)).thenReturn(true);

		// when
		final boolean result = renderer.isEditable();

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void testDisabledBySyncAttributeDescriptorConfigModel()
	{
		// given
		when(permissionFacade.canChangeType(CatalogVersionSyncJobModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(SyncAttributeDescriptorConfigModel._TYPECODE)).thenReturn(false);

		// when
		final boolean result = renderer.isEditable();

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void testEnabledByExplicitTypes()
	{
		// given
		when(permissionFacade.canChangeType(CatalogVersionSyncJobModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(SyncAttributeDescriptorConfigModel._TYPECODE)).thenReturn(true);

		// when
		final boolean result = renderer.isEditable();

		// then
		assertThat(result).isTrue();
	}

}
