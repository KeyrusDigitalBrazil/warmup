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
package de.hybris.platform.gigya.gigyaservices.retention;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSResponse;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaUserCleanupHookTest
{

	@InjectMocks
	private final GigyaUserCleanupHook cleanUpHook = new GigyaUserCleanupHook();

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Test
	public void testWhenGigyaConfigDoesntExist()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.EMPTY_LIST);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);

		Mockito.verifyZeroInteractions(gigyaService);
	}

	@Test
	public void testWhenGigyaConfigExistsWithDeleteDisabled()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getDeleteUser()).thenReturn(Boolean.FALSE);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);

		Mockito.verifyZeroInteractions(gigyaService);
	}

	@Test
	public void testWhenGigyaConfigExistsWithDeleteEnabled()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getDeleteUser()).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaUser.getGyUID()).thenReturn("123");
		final GSResponse gsResponse = Mockito.mock(GSResponse.class);
		Mockito
				.when(gigyaService.callRawGigyaApiWithConfig(Mockito.eq("accounts.deleteAccount"),
						Mockito.eq(Collections.singletonMap("UID", "123")), Mockito.eq(gigyaConfig), Mockito.eq(2), Mockito.eq(1)))
				.thenReturn(gsResponse);
		Mockito.when(gsResponse.getErrorCode()).thenReturn(0);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);

		Mockito.verify(gigyaService).callRawGigyaApiWithConfig(Mockito.eq("accounts.deleteAccount"),
				Mockito.eq(Collections.singletonMap("UID", "123")), Mockito.eq(gigyaConfig), Mockito.eq(2), Mockito.eq(1));
	}

	@Test(expected = GigyaApiException.class)
	public void testWhenGigyaConfigExistsWithDeleteEnabledWithException()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getDeleteUser()).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaUser.getGyUID()).thenReturn("123");

		final GSResponse gsResponse = Mockito.mock(GSResponse.class);
		Mockito
				.when(gigyaService.callRawGigyaApiWithConfig(Mockito.eq("accounts.deleteAccount"),
						Mockito.eq(Collections.singletonMap("UID", "123")), Mockito.eq(gigyaConfig), Mockito.eq(2), Mockito.eq(1)))
				.thenReturn(gsResponse);
		Mockito.when(gsResponse.getErrorCode()).thenReturn(1);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);
	}
}
