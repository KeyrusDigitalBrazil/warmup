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
package de.hybris.platform.gigya.gigyafacades.login.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSResponse;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaLoginFacadeTest
{

	private static final String SAMPLE_UID = "sample-uid";
	private static final String SAMPLE_UID_SIGNATURE = "sample-uid-sig";
	private static final String SAMPLE_UID_SIGNATURE_TS = "sample-uid-sig-ts";

	@InjectMocks
	private final DefaultGigyaLoginFacade gigyaLoginFacade = new DefaultGigyaLoginFacade();

	@InjectMocks
	private final DefaultGigyaLoginFacade spy = Mockito.spy(gigyaLoginFacade);

	@Mock
	private GigyaLoginService gigyaLoginService;

	@Mock
	private CMSSiteModel cmsSite;

	@Mock
	private GigyaJsOnLoginInfo jsInfo;

	@Mock
	private UserModel userModel;

	@Mock
	private GigyaUserObject gigyaUserObject;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private ModelService modelService;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private TaskModel task;

	@Mock
	private TaskService taskService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private CustomerAccountService customerAccountService;

	@Before
	public void setUp()
	{
		Mockito.when(jsInfo.getUID()).thenReturn(SAMPLE_UID);
		Mockito.when(jsInfo.getUIDSignature()).thenReturn(SAMPLE_UID_SIGNATURE);
		Mockito.when(jsInfo.getSignatureTimestamp()).thenReturn(SAMPLE_UID_SIGNATURE_TS);
	}

	@Test
	public void testProcessGigyaLoginWhenGigyaCallVerificationFails()
	{
		Mockito.when(gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TS))
				.thenReturn(Boolean.FALSE);

		Assert.assertFalse(gigyaLoginFacade.processGigyaLogin(jsInfo, gigyaConfig));
	}

	@Test
	public void testProcessGigyaLoginWhenGigyaCallVerificationSucceedsAndUserExists()
			throws GSKeyNotFoundException, GigyaApiException
	{
		Mockito.when(gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TS))
				.thenReturn(Boolean.TRUE);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID)).thenReturn(gigyaUser);
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.when(gsResponse.hasData()).thenReturn(Boolean.FALSE);
		Mockito.when(gigyaUser.getGyUID()).thenReturn("uid");

		Assert.assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, gigyaConfig));
		Mockito.verify(modelService).save(gigyaUser);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testProcessGigyaLoginWhenGigyaCallVerificationSucceedsAndUserDoesntExists()
			throws GigyaApiException, DuplicateUidException
	{
		Mockito.when(gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TS))
				.thenReturn(Boolean.TRUE);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID)).thenReturn(null);

		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(CustomerModel.class)).thenReturn(gigyaUser);


		Assert.assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, gigyaConfig));
		Mockito.verify(customerAccountService).register(gigyaUser, null);
	}

	@Test
	public void testProcessGigyaLoginWhenGigyaCallVerificationSucceedsAndExceptionOccurs()
			throws GigyaApiException, DuplicateUidException
	{
		Mockito.when(gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE, SAMPLE_UID_SIGNATURE_TS))
				.thenReturn(Boolean.TRUE);

		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(CustomerModel.class)).thenReturn(gigyaUser);

		Mockito.doThrow(DuplicateUidException.class).when(customerAccountService).register(Mockito.any(), Mockito.any());

		Assert.assertFalse(gigyaLoginFacade.processGigyaLogin(jsInfo, gigyaConfig));
		Mockito.verify(gigyaLoginService).notifyGigyaOfLogout(gigyaConfig, SAMPLE_UID);
	}

	@Test
	public void testGetUidForGigyaUserWhenUserExists()
	{
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID)).thenReturn(userModel);
		Mockito.when(userModel.getUid()).thenReturn(SAMPLE_UID);

		Assert.assertNotNull(gigyaLoginFacade.getHybrisUidForGigyaUser(SAMPLE_UID));
	}

	@Test
	public void testGetUidForGigyaUserWhenUserDoesntExists()
	{
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID)).thenReturn(null);

		Assert.assertTrue(StringUtils.isEmpty(gigyaLoginFacade.getHybrisUidForGigyaUser(SAMPLE_UID)));
	}

	@Test(expected = GigyaApiException.class)
	public void testCreateNewCustomerWhenEmailIsMissing() throws GigyaApiException, DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(null);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);

		gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID);
	}

	@Test(expected = DuplicateUidException.class)
	public void testCreateNewCustomerWhenCustomerWithGigyaUidExists() throws GigyaApiException, DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID)).thenReturn(userModel);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);


		gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID);
	}

	@Test
	public void testCreateNewCustomerWhenGigyaConfigDoesntExist() throws GigyaApiException, DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(null, SAMPLE_UID)).thenReturn(gigyaUserObject);

		Assert.assertNull(gigyaLoginFacade.createNewCustomer(null, SAMPLE_UID));
	}

	@Test
	public void testCreateNewCustomerWhenGigyaConfigExists() throws GigyaApiException, DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(CustomerModel.class)).thenReturn(gigyaUser);

		Assert.assertEquals(gigyaUser, gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID));
		Mockito.verify(customerAccountService).register(gigyaUser, null);
	}

	@Test
	public void testUpdateUser() throws GSKeyNotFoundException, GigyaApiException
	{
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.when(gsResponse.hasData()).thenReturn(Boolean.FALSE);

		gigyaLoginFacade.updateUser(gigyaConfig, gigyaUser);

		Mockito.verify(modelService).save(gigyaUser);
		Mockito.verify(taskService).scheduleTask(task);
	}

}
