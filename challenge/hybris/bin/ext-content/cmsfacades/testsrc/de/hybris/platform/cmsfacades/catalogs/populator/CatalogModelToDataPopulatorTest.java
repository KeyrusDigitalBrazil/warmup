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
package de.hybris.platform.cmsfacades.catalogs.populator;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashSet;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogModelToDataPopulatorTest
{
	private static final String CATALOG_NAME_EN = "Test Catalog - EN";
	private static final String CATALOG_NAME_FR = "Test Catalog - FR";
	private static final String CATALOG_ID = "test-catalog-id";
	private static final String FR = "fr";
	private static final String EN = "en";

	@InjectMocks
	private CatalogModelToDataPopulator populator;

	@InjectMocks
	private final DefaultLocalizedPopulator localizedPopulator = new DefaultLocalizedPopulator();

	@Mock
	private CMSUserService cmsUserService;
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CatalogModel catalogModel;

	private CatalogData catalogData;

	@Before
	public void setUp()
	{
		catalogData = new CatalogData();
		populator.setLocalizedPopulator(localizedPopulator);

		when(catalogModel.getId()).thenReturn(CATALOG_ID);
		when(catalogModel.getName(ENGLISH)).thenReturn(CATALOG_NAME_EN);
		when(catalogModel.getName(FRENCH)).thenReturn(CATALOG_NAME_FR);

		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);

		// Language Permissions
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
	}

	@Test
	public void shouldPopulateAllFields()
	{
		populator.populate(catalogModel, catalogData);

		assertThat(catalogData.getCatalogId(), equalTo(CATALOG_ID));
		assertThat(catalogData.getName().get(EN), equalTo(CATALOG_NAME_EN));
		assertThat(catalogData.getName().get(FR), equalTo(CATALOG_NAME_FR));
	}

}
