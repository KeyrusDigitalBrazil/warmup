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
package de.hybris.platform.cmsfacades.users.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSUserServiceTest
{
	private Collection<String> ALL_LANGUAGES = new HashSet<>(Arrays.asList("EN", "ES", "FR", "JA", "ZH", "RU"));

	@Mock
	private UserGroupModel europeanUsers;

	@Mock
	private UserGroupModel asianUsers;

	@Mock
	private PrincipalGroupModel principalGroup;

	@Mock
	private UserModel adminUser;

	@Mock
	private UserModel regularUser;

	@Mock
	private UserService userService;

	@Mock
	private StoreSessionFacade storeSessionFacade;

	@Spy
	@InjectMocks
	private DefaultCMSUserService cmsUserService;

	@Before
	public void setUp()
	{
		when(userService.isAdmin(adminUser)).thenReturn(true);
		when(userService.isAdmin(regularUser)).thenReturn(false);

		when(regularUser.getAllGroups()).thenReturn(Collections.set(principalGroup, europeanUsers, asianUsers));
		when(europeanUsers.getReadableLanguages())
				.then(answer -> getLanguagesModel(Collections.set("EN", "FR", "ES")));
		when(asianUsers.getReadableLanguages())
				.then(answer -> getLanguagesModel(Collections.set("JA", "ZH")));
		when(europeanUsers.getWriteableLanguages())
				.then(answer -> getLanguagesModel(Collections.set("FR", "ES")));
		when(asianUsers.getWriteableLanguages())
				.then(answer -> getLanguagesModel(Collections.set("JA", "ZH")));

		when(storeSessionFacade.getAllLanguages()).thenReturn(getLanguagesData(ALL_LANGUAGES));
	}

	@Test
	public void givenUserIsAdmin_WhenGetWriteableLanguagesForUserIsCalled_ThenItReturnsAllSiteLanguages()
	{
		// WHEN
		Set<String> result = cmsUserService.getWriteableLanguagesForUser(adminUser);

		// THEN
		assertThat(result, containsInAnyOrder(ALL_LANGUAGES.toArray()));
	}

	@Test
	public void givenUserIsAdmin_WhenGetReadableLanguagesForUserIsCalled_ThenItReturnsAllSiteLanguages()
	{
		// WHEN
		Set<String> result = cmsUserService.getReadableLanguagesForUser(adminUser);

		// THEN
		assertThat(result, containsInAnyOrder(ALL_LANGUAGES.toArray()));
	}

	@Test
	public void givenRegularUser_WhenGetWriteableLanguagesForUserIsCalled_ThenItReturnsWriteableLanguagesAvailableToHerUserGroups()
	{
		// GIVEN
		String[] expectedLanguages = { "ES", "FR", "JA", "ZH" };

		// WHEN
		Set<String> result = cmsUserService.getWriteableLanguagesForUser(regularUser);

		// THEN
		assertThat(result, containsInAnyOrder(expectedLanguages));
	}

	@Test
	public void givenRegularUser_WhenGetReadableLanguagesForUserIsCalled_ThenItReturnsReadableLanguagesAvailableToHerUserGroups()
	{
		// GIVEN
		String[] expectedLanguages = { "ES", "FR", "JA", "ZH", "EN" };

		// WHEN
		Set<String> result = cmsUserService.getReadableLanguagesForUser(regularUser);

		// THEN
		assertThat(result, containsInAnyOrder(expectedLanguages));
	}

	@Test
	public void givenCurrentUserIsAdmin_WhenGetWriteableLanguagesForCurrentUser_ThenItReturnsAllSiteLanguages()
	{
		// GIVEN
		when(userService.getCurrentUser()).thenReturn(adminUser);

		// WHEN
		Set<String> result = cmsUserService.getWriteableLanguagesForCurrentUser();

		// THEN
		verify(cmsUserService).getWriteableLanguagesForUser(adminUser);
		assertThat(result, containsInAnyOrder(ALL_LANGUAGES.toArray()));
	}


	@Test
	public void givenCurrentUserIsAdmin_WhenGetReadableLanguagesForCurrentUser_ThenItReturnsAllSiteLanguages()
	{
		// GIVEN
		when(userService.getCurrentUser()).thenReturn(adminUser);

		// WHEN
		Set<String> result = cmsUserService.getReadableLanguagesForCurrentUser();

		// THEN
		verify(cmsUserService).getReadableLanguagesForUser(adminUser);
		assertThat(result, containsInAnyOrder(ALL_LANGUAGES.toArray()));
	}

	@Test
	public void givenCurrentUserIsARegularUser_WhenGetWriteableLanguagesForCurrentUserIsCalled_ThenItReturnsWriteableLanguagesAvailableToHerUserGroups()
	{
		// GIVEN
		String[] expectedLanguages = { "ES", "FR", "JA", "ZH" };
		when(userService.getCurrentUser()).thenReturn(regularUser);

		// WHEN
		Set<String> result = cmsUserService.getWriteableLanguagesForCurrentUser();

		// THEN
		verify(cmsUserService).getWriteableLanguagesForUser(regularUser);
		assertThat(result, containsInAnyOrder(expectedLanguages));
	}

	@Test
	public void givenCurrentUserIsRegularUser_WhenGetReadableLanguagesForCurrentUserIsCalled_ThenItReturnsReadableLanguagesAvailableToHerUserGroups()
	{
		// GIVEN
		String[] expectedLanguages = { "ES", "FR", "JA", "ZH", "EN" };
		when(userService.getCurrentUser()).thenReturn(regularUser);

		// WHEN
		Set<String> result = cmsUserService.getReadableLanguagesForCurrentUser();

		// THEN
		verify(cmsUserService).getReadableLanguagesForUser(regularUser);
		assertThat(result, containsInAnyOrder(expectedLanguages));
	}

	// --------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------
	protected Collection<LanguageData> getLanguagesData(final Collection<String> languages)
	{
		return languages.stream()
				.map(languageCode -> {
					LanguageData lang = new LanguageData();
					lang.setIsocode(languageCode);
					return lang;
				})
				.collect(Collectors.toSet());
	}

	protected Collection<LanguageModel> getLanguagesModel(final Collection<String> languages)
	{
		return languages.stream()
				.map(languageCode -> {
					LanguageModel languageModel = Mockito.mock(LanguageModel.class);
					when(languageModel.getIsocode()).thenReturn(languageCode);
					return languageModel;
				})
				.collect(Collectors.toSet());
	}
}
