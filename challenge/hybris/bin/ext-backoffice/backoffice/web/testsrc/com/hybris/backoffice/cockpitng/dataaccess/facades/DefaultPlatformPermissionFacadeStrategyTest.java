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
package com.hybris.backoffice.cockpitng.dataaccess.facades;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.locking.ItemLockingService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.DefaultPlatformPermissionFacadeStrategy;
import com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.custom.InstancePermissionAdvisor;
import com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.custom.impl.LockedItemPermissionAdvisor;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;


@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformPermissionFacadeStrategyTest
{
	private static final String ENGLISH_ISO_CODE = "en";
	private static final String GERMAN_ISO_CODE = "de";
	private static final String TYPE = "TYPE";

	private final LanguageModel userProfileLanEnglish = new LanguageModel();
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private UserService userService;
	@Mock
	private UserModel user;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private TypeService typeService;
	@Mock
	private InstancePermissionAdvisor permissionAdvisor;
	@Mock
	private ItemModel lockedItem;
	@Mock
	private ItemLockingService itemLockingService;
	@Spy
	@InjectMocks
	private LockedItemPermissionAdvisor lockedItemPermissionAdvisor;
	@Spy
	@InjectMocks
	private DefaultPlatformPermissionFacadeStrategy permissionFacade;

	private List<InstancePermissionAdvisor> permissionAdvisors;
	private CatalogVersionModel catalog;
	private ProductModel product;
	private Locale englishLocale;
	private Locale germanLocale;

	@Before
	public void setUp()
	{
		final Set<Locale> readableLanguages = new HashSet<>();
		final Locale english = new Locale(ENGLISH_ISO_CODE);
		readableLanguages.add(english);
		doReturn(readableLanguages).when(permissionFacade).getAllReadableLocalesForCurrentUser();
		doReturn(readableLanguages).when(permissionFacade).getAllWritableLocalesForCurrentUser();

		permissionAdvisors = new ArrayList<>();
		permissionAdvisors.add(lockedItemPermissionAdvisor);
		permissionFacade.setPermissionAdvisors(permissionAdvisors);

		catalog = new CatalogVersionModel();

		final LanguageModel catalogVersionLanEnglish = new LanguageModel();

		catalogVersionLanEnglish.setIsocode(ENGLISH_ISO_CODE);

		final LanguageModel catalogVersionLanGerman = new LanguageModel();
		catalogVersionLanGerman.setIsocode(GERMAN_ISO_CODE);

		final Collection<LanguageModel> allLang = new ArrayList<>();
		allLang.add(catalogVersionLanEnglish);
		allLang.add(catalogVersionLanGerman);

		catalog.setLanguages(allLang);

		product = new ProductModel();
		product.setCatalogVersion(catalog);

		when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(product))).thenReturn(Boolean.TRUE);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(product)).thenReturn(catalog);
		when(userService.getCurrentUser()).thenReturn(user);
		when(Boolean.valueOf(userService.isAdmin(user))).thenReturn(Boolean.FALSE);

		englishLocale = new Locale(ENGLISH_ISO_CODE);
		germanLocale = new Locale(GERMAN_ISO_CODE);

		when(commonI18NService.getLocaleForLanguage(userProfileLanEnglish)).thenReturn(englishLocale);
		when(commonI18NService.getLocaleForLanguage(catalogVersionLanEnglish)).thenReturn(englishLocale);
		when(commonI18NService.getLocaleForLanguage(catalogVersionLanGerman)).thenReturn(germanLocale);
		when(itemLockingService.isLocked(lockedItem)).thenReturn(true);
		when(itemLockingService.isLocked(argThat(new ArgumentMatcher<ItemModel>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o != lockedItem;
			}
		}))).thenReturn(false);
	}

	@Test
	public void testGetReadableLocalesForInstance()
	{
		final Set<Locale> expectedLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertNotNull(expectedLocales);
		Assert.assertTrue(expectedLocales.contains(englishLocale));
	}

	@Test
	public void testGetWritableLocalesForInstance()
	{
		final Set<Locale> expectedLocales = permissionFacade.getWritableLocalesForInstance(product);
		Assert.assertNotNull(expectedLocales);
		Assert.assertTrue(expectedLocales.contains(englishLocale));
	}

	@Test
	public void testCatalogVersionAndReadableLanguageAreNull()
	{
		final DefaultPlatformPermissionFacadeStrategy permFacade = new DefaultPlatformPermissionFacadeStrategy()
		{
			@Override
			public Set<Locale> getAllReadableLocalesForCurrentUser()
			{
				return Collections.emptySet();
			}

			@Override
			public Set<Locale> getAllWritableLocalesForCurrentUser()
			{
				return Collections.emptySet();
			}
		};

		when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(product))).thenReturn(Boolean.FALSE);
		permFacade.setCatalogTypeService(catalogTypeService);

		final Set<Locale> expectedReadableLocales = permFacade.getReadableLocalesForInstance(product);
		Assert.assertTrue(CollectionUtils.isEmpty(expectedReadableLocales));

		final Set<Locale> expectedWritableLocales = permFacade.getReadableLocalesForInstance(product);
		Assert.assertTrue(CollectionUtils.isEmpty(expectedWritableLocales));

	}

	@Test
	public void testCatalogVersionLanguagesNotNullAndAllLanguageIsNull()
	{
		final DefaultPlatformPermissionFacadeStrategy permFacade = new DefaultPlatformPermissionFacadeStrategy()
		{
			@Override
			public Set<Locale> getAllReadableLocalesForCurrentUser()
			{
				return Collections.emptySet();
			}

			@Override
			public Set<Locale> getAllWritableLocalesForCurrentUser()
			{
				return Collections.emptySet();
			}

			@Override
			protected Set<Locale> getLocalesForLanguage(final Collection<LanguageModel> languages)
			{
				final Set<Locale> localesForLanguage = new HashSet<>();
				localesForLanguage.add(englishLocale);
				localesForLanguage.add(germanLocale);

				return localesForLanguage;
			}
		};

		permFacade.setCatalogTypeService(catalogTypeService);
		permFacade.setCommonI18NService(commonI18NService);

		final Set<Locale> expectedReadableLocales = permFacade.getReadableLocalesForInstance(product);
		Assert.assertNotNull(expectedReadableLocales);
		Assert.assertTrue(expectedReadableLocales.size() == 2);

		final Set<Locale> expectedWritableLocales = permFacade.getWritableLocalesForInstance(product);
		Assert.assertNotNull(expectedWritableLocales);
		Assert.assertTrue(expectedWritableLocales.size() == 2);
	}

	@Test
	public void testCatalogVersionLanguagesNullAndAllLanguageNotNull()
	{
		when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(product))).thenReturn(Boolean.FALSE);

		final Set<Locale> expectedReadableLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertNotNull(expectedReadableLocales);
		Assert.assertTrue(expectedReadableLocales.size() == 1);

		final Set<Locale> expectedWritableLocales = permissionFacade.getWritableLocalesForInstance(product);
		Assert.assertNotNull(expectedWritableLocales);
		Assert.assertTrue(expectedWritableLocales.size() == 1);
	}

	@Test
	public void testGetReadableLocalesForInstanceAsAdmin()
	{
		final Set<Locale> locales = new HashSet<>();
		locales.add(Locale.ENGLISH);
		locales.add(Locale.GERMAN);
		locales.add(Locale.JAPAN);

		final DefaultPlatformPermissionFacadeStrategy permFacade = new DefaultPlatformPermissionFacadeStrategy()
		{
			@Override
			public Set<Locale> getAllReadableLocalesForCurrentUser()
			{
				return locales;
			}

			@Override
			public Set<Locale> getAllWritableLocalesForCurrentUser()
			{
				return locales;
			}
		};

		permFacade.setCatalogTypeService(catalogTypeService);
		permFacade.setCommonI18NService(commonI18NService);
		permFacade.setUserService(userService);

		when(Boolean.valueOf(userService.isAdmin(user))).thenReturn(Boolean.TRUE);

		final LanguageModel german = new LanguageModel();
		german.setIsocode(GERMAN_ISO_CODE);
		catalog.setLanguages(Collections.singletonList(german));

		final Set<Locale> readableLocalesForInstance = permFacade.getReadableLocalesForInstance(product);

		// Even though catalog is restricted for geramn language only - in case of admin, facade returns all readable
		// languages of the admin user - all defined. In this test - English
		Assert.assertTrue(readableLocalesForInstance.containsAll(locales));
	}

	@Test
	public void testCanRemoveInstanceWhenAdvisorListIsEmpty()
	{
		// given
		final Object objectToRemove = new Object();
		when(typeFacade.getType(objectToRemove)).thenReturn(TYPE);

		// when
		final boolean result = permissionFacade.canRemoveInstance(objectToRemove);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testCanRemoveInstanceWhenAdvisorIsApplicableAndInstanceCanBeRemoved()
	{
		// given
		final Object objectToRemove = new Object();
		when(typeFacade.getType(objectToRemove)).thenReturn(TYPE);
		permissionAdvisors.add(permissionAdvisor);
		when(Boolean.valueOf(permissionAdvisor.isApplicableTo(objectToRemove))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(permissionAdvisor.canDelete(objectToRemove))).thenReturn(Boolean.TRUE);

		// when
		final boolean result = permissionFacade.canRemoveInstance(objectToRemove);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testCanRemoveInstanceWhenAdvisorIsNotApplicable()
	{
		// given
		final Object objectToRemove = new Object();
		when(typeFacade.getType(objectToRemove)).thenReturn(TYPE);
		permissionAdvisors.add(permissionAdvisor);
		when(Boolean.valueOf(permissionAdvisor.isApplicableTo(objectToRemove))).thenReturn(Boolean.FALSE);

		// when
		final boolean result = permissionFacade.canRemoveInstance(objectToRemove);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testCanRemoveInstanceWhenAdvisorIsApplicableAndInstanceCannotBeRemoved()
	{
		// given
		final Object objectToRemove = new Object();
		when(typeFacade.getType(objectToRemove)).thenReturn(TYPE);
		permissionAdvisors.add(permissionAdvisor);
		when(Boolean.valueOf(permissionAdvisor.isApplicableTo(objectToRemove))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(permissionAdvisor.canDelete(objectToRemove))).thenReturn(Boolean.FALSE);

		// when
		final boolean result = permissionFacade.canRemoveInstance(objectToRemove);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void deleteOfLockedItemDisallowed()
	{
		final boolean result = permissionFacade.canRemoveInstance(lockedItem);
		assertThat(result).isFalse();
		verify(lockedItemPermissionAdvisor).canDelete(lockedItem);
	}

	@Test
	public void changeOfLockedItemDisallowed()
	{
		final boolean result = permissionFacade.canChangeInstance(lockedItem);
		assertThat(result).isFalse();
		verify(lockedItemPermissionAdvisor).canModify(lockedItem);
	}

	@Test
	public void changePropertyOfLockedItemDisallowed()
	{
		final boolean result = permissionFacade.canChangeInstanceProperty(lockedItem, ItemModel.OWNER);
		assertThat(result).isFalse();
		verify(lockedItemPermissionAdvisor).canModify(lockedItem);
	}

	@Test
	public void readOfLockedItemAllowed()
	{
		final boolean result = permissionFacade.canReadInstance(lockedItem);

		assertThat(result).isTrue();
		verifyZeroInteractions(lockedItemPermissionAdvisor);
	}

	@Test
	public void shouldCheckingPermissionsOnManyInstancesBeInterruptedWhenFirstCheckFails()
	{
		// given
		final Object passed1 = new Object();
		final Object passed2 = new Object();
		final Object notPassed = new Object();
		final Object passed3 = new Object();

		doReturn(true).when(permissionFacade).canChangeInstance(passed1);
		doReturn(true).when(permissionFacade).canChangeInstance(passed2);
		doReturn(false).when(permissionFacade).canChangeInstance(notPassed);
		doReturn(true).when(permissionFacade).canChangeInstance(passed3);

		// when
		permissionFacade.canChangeInstances(Lists.newArrayList(passed1, passed2, notPassed, passed3));

		// then
		verify(permissionFacade, times(3)).canChangeInstance(any());
	}

	@Test
	public void shouldCheckingPermissionsOnManyInstancesPropertyBeInterruptedWhenFirstCheckFails()
	{
		// given
		final Object passed1 = new Object();
		final Object passed2 = new Object();
		final Object notPassed = new Object();
		final Object passed3 = new Object();
		final String property = "someProperty";

		doReturn(true).when(permissionFacade).canChangeInstanceProperty(passed1, property);
		doReturn(true).when(permissionFacade).canChangeInstanceProperty(passed2, property);
		doReturn(false).when(permissionFacade).canChangeInstanceProperty(notPassed, property);
		doReturn(true).when(permissionFacade).canChangeInstanceProperty(passed3, property);

		// when
		permissionFacade.canChangeInstancesProperty(Lists.newArrayList(passed1, passed2, notPassed, passed3), property);

		// then
		verify(permissionFacade, times(3)).canChangeInstanceProperty(any(), any());
	}

}
