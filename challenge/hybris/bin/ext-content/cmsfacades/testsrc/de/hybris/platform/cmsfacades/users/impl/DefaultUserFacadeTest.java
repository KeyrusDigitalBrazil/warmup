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
package de.hybris.platform.cmsfacades.users.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUserFacadeTest
{
	private final String VALID_USER_ID = "validID";
	private final String MISSING_USER_ID = "invalidID";

	@Mock
	private UserService userService;

	@Mock
	private Converter<UserModel, UserData> cmsUserModelToDataConverter;

	@Mock
	private UserModel userModel;

	@Mock
	private UserData userData;

	@InjectMocks
	private DefaultUserFacade defaultUserFacade;

	@Before
	public void setUp()
	{
		when(userService.getUserForUID(VALID_USER_ID)).thenReturn(userModel);
		when(userService.getUserForUID(MISSING_USER_ID)).thenThrow(new UnknownIdentifierException("not found"));

		when(cmsUserModelToDataConverter.convert(userModel)).thenReturn(userData);
	}

	@Test
	public void givenValidId_WhenGetUserByIdIsCalled_ThenItMustReturnUserData() throws CMSItemNotFoundException
	{
		// GIVEN / WHEN
		UserData result = defaultUserFacade.getUserById(VALID_USER_ID);

		// THEN
		verify(cmsUserModelToDataConverter).convert(userModel);
		assertThat(result, is(userData));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void givenInvalidId_WhenGetUserByIdIsCalled_ThenItMustReturnException() throws CMSItemNotFoundException
	{
		// GIVEN / WHEN / THEN
		defaultUserFacade.getUserById(MISSING_USER_ID);
	}
}
