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
package de.hybris.platform.cmsfacades.users.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.UserData;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UserModelToDataPopulatorTest
{
	private final String USER_UID = "userUID";
	private Set<String> readableLanguages = new HashSet<>(Arrays.asList("en", "fr"));
	private Set<String> writeableLanguages = new HashSet<>(Arrays.asList("fr", "de"));

	@Mock
	private UserModel userModel;

	@Mock
	private CMSUserService cmsUserService;

	@InjectMocks
	private UserModelToDataPopulator userModelToDataPopulator;

	@Before
	public void setUp()
	{
		when(userModel.getUid()).thenReturn(USER_UID);
		when(cmsUserService.getReadableLanguagesForUser(userModel)).thenReturn(readableLanguages);
		when(cmsUserService.getWriteableLanguagesForUser(userModel)).thenReturn(writeableLanguages);
	}

	@Test
	public void givenUserModel_WhenPopulateCalled_ItSuccessfullyPopulatesUserData()
	{
		// GIVEN
		UserData userData = new UserData();

		// WHEN
		userModelToDataPopulator.populate(userModel, userData);

		// THEN
		assertThat(userData.getUid(), is(USER_UID));
		assertThat(userData.getReadableLanguages(), is(readableLanguages));
		assertThat(userData.getWriteableLanguages(), is(writeableLanguages));
	}
}
