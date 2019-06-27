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
package de.hybris.platform.gigya.gigyaservices.login.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;


/**
 * Test class for GigyaLoginService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaLoginServiceTest
{

	private static final String SAMPLE_UID = "uid";
	private static final String SAMPLE_UID_SIGNATURE = "uid-sig";
	private static final String SAMPLE_UID_SIGNATURE_TIMESTAMP = "uid-sig-ts";
	private static final String SITE_UID = "site-uid";

	@InjectMocks
	private final DefaultGigyaLoginService gigyaLoginService = new DefaultGigyaLoginService();

	@InjectMocks
	private final DefaultGigyaLoginService gigyaLoginServiceSpy = Mockito.spy(gigyaLoginService);

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private CMSSiteModel cmsSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GenericDao<CustomerModel> gigyaUserGenericDao;

	private final List<GigyaConfigModel> gigyaConfigs = new ArrayList<>();

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private ModelService modelService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private GSObject gsObject;

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private Converter<CustomerModel, GSObject> gigyaUserConverter;

	@Test
	public void testVerifyGigyaCallWhenConfigIsDoesntExist()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(null);

		Assert.assertFalse(
				gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallWhenConfigExistsAndSiteSecretExists()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getGigyaSiteSecret()).thenReturn("site-secret");
		Mockito.doReturn(Boolean.TRUE).when(gigyaLoginServiceSpy).verifyGigyaCallSiteSecret(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		Assert.assertTrue(
				gigyaLoginServiceSpy.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallWhenConfigExistsAndUserSecretExists()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getGigyaSiteSecret()).thenReturn(null);
		Mockito.when(gigyaConfig.getGigyaUserSecret()).thenReturn("user-secret");

		Mockito.doReturn(Boolean.TRUE).when(gigyaLoginServiceSpy).verifyGigyaCallApiUser(Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any());

		Assert.assertTrue(
				gigyaLoginServiceSpy.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallWhenConfigExistsAndNeitherUserSecretOrSiteSecretExists()
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getGigyaSiteSecret()).thenReturn(null);
		Mockito.when(gigyaConfig.getGigyaUserSecret()).thenReturn(null);

		Assert.assertFalse(
				gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}


	@Test
	public void testFindCustomerByGigyaUidWhenCustomerExists()
	{
		Mockito.when(gigyaUserGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaUser));

		Assert.assertNotNull(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID));
	}

	@Test
	public void testFindCustomerByGigyaUidWhenCustomerDoesntExists()
	{
		Mockito.when(gigyaUserGenericDao.find(Mockito.anyMap())).thenReturn(null);

		Assert.assertNull(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID));
	}

	@Test
	public void testFetchGigyaInfoWhenGigyaUserMgmtModeIsRaas() throws GigyaApiException
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getMode()).thenReturn(GigyaUserManagementMode.RAAS);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.when(gsObject.toJsonString()).thenReturn("{\"UID\": \"123\", \"profile\" : { \"UID\": \"123\"}}");

		final GigyaUserObject userObject = gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID);

		Assert.assertNotNull(userObject);
		Assert.assertEquals("123", userObject.getUID());
	}

	@Test
	public void testFetchGigyaInfoWhenGigyaUserMgmtModeIsNotRaas() throws GigyaApiException
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getMode()).thenReturn(null);

		Assert.assertNull(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID));
	}

	@Test
	public void testNotifyGigyaOfLogout() throws GigyaApiException
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));

		gigyaLoginService.notifyGigyaOfLogout(gigyaConfig, SAMPLE_UID);

		final LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("UID", SAMPLE_UID);

		Mockito.verify(gigyaService).callRawGigyaApiWithConfig(Mockito.refEq("accounts.logout"), Mockito.refEq(params),
				Mockito.refEq(gigyaConfig), Mockito.eq(2), Mockito.eq(1));
	}

	@Test
	public void testNotifyGigyaOfLogoutWhenConfigIsNull() throws GigyaApiException
	{
		Mockito.when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(null);

		gigyaLoginService.notifyGigyaOfLogout(null, SAMPLE_UID);

		Mockito.verifyNoMoreInteractions(gigyaService);
	}

	@Test
	public void testSendUserToGigyaWhenUserIsNotGigyaUser() throws GigyaApiException
	{
		Assert.assertFalse(gigyaLoginService.sendUserToGigya(Mockito.mock(EmployeeModel.class)));
	}

	@Test
	public void testSendUserToGigyaSuccessfully() throws GigyaApiException
	{
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.EMPTY_LIST);
		Mockito.when(gigyaConfigGenericDao.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.when(gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.when(gsResponse.getErrorCode()).thenReturn(1);

		Assert.assertFalse(gigyaLoginService.sendUserToGigya(gigyaUser));
	}


	@Test
	public void testSendUserToGigyaUnSuccessfully() throws GigyaApiException
	{
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.EMPTY_LIST);
		Mockito.when(gigyaConfigGenericDao.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.when(gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.when(gsResponse.getErrorCode()).thenReturn(0);

		Assert.assertTrue(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

	@Test
	public void testSendUserToGigyaSuccessfullyWithFieldMappings() throws GigyaApiException
	{
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));
		Mockito.when(fieldMapping.isCustom()).thenReturn(Boolean.FALSE);
		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.when(fieldMapping.getGigyaConfig()).thenReturn(gigyaConfig);

		Mockito.when(gigyaConfigGenericDao.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));

		Mockito.when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.when(gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.when(gsResponse.getErrorCode()).thenReturn(0);

		Assert.assertTrue(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

	@Test
	public void testSendUserToGigyaUnSuccessfullyWithFieldMappings() throws GigyaApiException
	{
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));
		Mockito.when(fieldMapping.isCustom()).thenReturn(Boolean.FALSE);
		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.when(fieldMapping.getGigyaConfig()).thenReturn(gigyaConfig);

		Mockito.when(gigyaConfigGenericDao.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));

		Mockito.when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.when(gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.when(gsResponse.getErrorCode()).thenReturn(1);

		Assert.assertFalse(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

}