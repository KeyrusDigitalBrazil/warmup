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
package de.hybris.platform.cmsfacades.common.populator.impl;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComposedTypeData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComposedTypeModelPopulatorTest
{
	private static final String CODE = "code";
	private static final String NAME_EN = "name-EN";
	private static final String NAME_FR = "name-FR";
	private static final String DESCRIPTION_EN = "description-EN";
	private static final String DESCRIPTION_FR = "description-FR";
	private static final String EN = Locale.ENGLISH.getLanguage();
	private static final String FR = Locale.FRENCH.getLanguage();

	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CMSUserService cmsUserService;

	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;
	@InjectMocks
	private ComposedTypeModelPopulator populator;

	private ComposedTypeData composedTypeData;

	@Before
	public void setUp()
	{
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));

		composedTypeData = new ComposedTypeData();
		when(composedTypeModel.getName(Locale.ENGLISH)).thenReturn(NAME_EN);
		when(composedTypeModel.getName(Locale.FRENCH)).thenReturn(NAME_FR);
		when(composedTypeModel.getDescription(Locale.ENGLISH)).thenReturn(DESCRIPTION_EN);
		when(composedTypeModel.getDescription(Locale.FRENCH)).thenReturn(DESCRIPTION_FR);
		when(composedTypeModel.getCode()).thenReturn(CODE);

		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);

		populator.setLocalizedPopulator(localizedPopulator);
	}

	@Test
	public void shouldPopulateNonLocalizedAttributes()
	{
		populator.populate(composedTypeModel, composedTypeData);
		assertThat(composedTypeData.getCode(), equalTo(CODE));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_NullMaps()
	{
		composedTypeData.setName(null);
		composedTypeData.setDescription(null);

		populator.populate(composedTypeModel, composedTypeData);

		assertThat(composedTypeData.getName().get(EN), equalTo(NAME_EN));
		assertThat(composedTypeData.getName().get(FR), equalTo(NAME_FR));
		assertThat(composedTypeData.getDescription().get(EN), equalTo(DESCRIPTION_EN));
		assertThat(composedTypeData.getDescription().get(FR), equalTo(DESCRIPTION_FR));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_AllLanguages()
	{
		populator.populate(composedTypeModel, composedTypeData);

		assertThat(composedTypeData.getName().get(EN), equalTo(NAME_EN));
		assertThat(composedTypeData.getName().get(FR), equalTo(NAME_FR));
		assertThat(composedTypeData.getDescription().get(EN), equalTo(DESCRIPTION_EN));
		assertThat(composedTypeData.getDescription().get(FR), equalTo(DESCRIPTION_FR));
	}
}
