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
package de.hybris.platform.scimfacades.user.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultScimUserFacadeTest
{

	private static final String EXTERNAL_ID = "external-id";

	@InjectMocks
	private final DefaultScimUserFacade scimUserFacade = new DefaultScimUserFacade();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private UserModel userModel;

	@Mock
	private Converter<EmployeeModel, ScimUser> scimUserConverter;

	@Mock
	private EmployeeModel employeeModel;

	@Mock
	private ModelService modelService;

	@Mock
	private Converter<ScimUser, EmployeeModel> scimUserReverseConverter;

	@Test
	public void testCreateUserForUserTypeEmployee()
	{
		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		scimUser.setUserType("employee");

		Mockito.when(modelService.create(EmployeeModel.class)).thenReturn(employeeModel);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));
		Mockito.when(scimUserConverter.convert(employeeModel)).thenReturn(scimUser);

		final ScimUser returnedScimUser = scimUserFacade.createUser(scimUser);

		Mockito.verify(scimUserReverseConverter).convert(scimUser, employeeModel);
		Mockito.verify(modelService).save(employeeModel);
		Assert.assertEquals(scimUser, returnedScimUser);
	}

	@Test(expected = ScimException.class)
	public void testCreateUserForUserTypeInvalid()
	{
		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		scimUser.setUserType("invalid");

		scimUserFacade.createUser(scimUser);
	}

	@Test
	public void testUpdateUserWhenUserExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));

		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		Mockito.when(scimUserConverter.convert(employeeModel)).thenReturn(scimUser);

		final ScimUser returnedScimUser = scimUserFacade.updateUser(EXTERNAL_ID, scimUser);

		Mockito.verify(scimUserReverseConverter).convert(scimUser, employeeModel);
		Mockito.verify(modelService).save(employeeModel);
		Assert.assertEquals(scimUser, returnedScimUser);
	}

	@Test(expected = ScimException.class)
	public void testUpdateUserWhenUserDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimUserFacade.updateUser(EXTERNAL_ID, new ScimUser());
	}

	@Test
	public void testGetUserWhenUserExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));

		scimUserFacade.getUser(EXTERNAL_ID);

		Mockito.verify(scimUserConverter).convert(employeeModel);
	}

	@Test(expected = ScimException.class)
	public void testGetUserWhenUserDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimUserFacade.getUser(EXTERNAL_ID);
	}

	@Test
	public void testGetUserForScimUserIdWhenModelExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(userModel));

		Assert.assertEquals(userModel, scimUserFacade.getUserForScimUserId(EXTERNAL_ID));
	}

	@Test
	public void testGetUserForScimUserIdWhenModelDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenThrow(new ModelNotFoundException("exception"));

		Assert.assertNull(scimUserFacade.getUserForScimUserId(EXTERNAL_ID));
	}

	@Test
	public void testDeleteUserWhenUserExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(userModel));

		Assert.assertTrue(scimUserFacade.deleteUser(EXTERNAL_ID));
	}

	@Test(expected = ScimException.class)
	public void testDeleteUserWhenUserDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimUserFacade.deleteUser(EXTERNAL_ID);
	}

}
