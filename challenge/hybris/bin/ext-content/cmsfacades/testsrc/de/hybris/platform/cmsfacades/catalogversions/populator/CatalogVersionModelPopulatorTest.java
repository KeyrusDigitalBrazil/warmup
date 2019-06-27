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
package de.hybris.platform.cmsfacades.catalogversions.populator;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionModelPopulatorTest
{
	private static final String CATALOG_VERSION = "test-version";
	private static final Boolean CATALOG_ACTIVE = true;
	private static final String CATALOG_VERSION_UUID = "test-version-uuid";
	private static final String CATALOG_NAME_EN = "test-version-catalog-name-en";
	private static final String CATALOG_NAME_FR = "test-version-catalog-name-fr";
	private static final String EN = ENGLISH.getLanguage();
	private static final String FR = FRENCH.getLanguage();

	@InjectMocks
	private CatalogVersionModelPopulator populator;
	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Spy
	private CatalogVersionData versionDto = new CatalogVersionData();
	@Mock
	private ItemData itemData;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CMSUserService cmsUserService;

	@Before
	public void setup()
	{
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));

		when(catalogVersionModel.getVersion()).thenReturn(CATALOG_VERSION);
		when(catalogVersionModel.getActive()).thenReturn(CATALOG_ACTIVE);
		when(uniqueItemIdentifierService.getItemData(catalogVersionModel)).thenReturn(Optional.of(itemData));
		when(itemData.getItemId()).thenReturn(CATALOG_VERSION_UUID);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogModel.getName(ENGLISH)).thenReturn(CATALOG_NAME_EN);
		when(catalogModel.getName(FRENCH)).thenReturn(CATALOG_NAME_FR);
		mockLocalization();

		populator.setLocalizedPopulator(localizedPopulator);
		populator.setUniqueItemIdentifierService(uniqueItemIdentifierService);
	}

	private void mockLocalization()
	{
		populator.setLocalizedPopulator(localizedPopulator);

		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);
	}

	@Test
	public void shouldPopulateAllFields() throws Exception
	{
		populator.populate(catalogVersionModel, versionDto);

		verify(versionDto).setActive(CATALOG_ACTIVE);
		verify(versionDto).setVersion(CATALOG_VERSION);
		verify(versionDto).setUuid(CATALOG_VERSION_UUID);
		verify(versionDto).setName(Matchers.anyMap());
		assertThat(versionDto.getName().get(EN), equalTo(CATALOG_NAME_EN + " - " + CATALOG_VERSION));
		assertThat(versionDto.getName().get(FR), equalTo(CATALOG_NAME_FR + " - " + CATALOG_VERSION));
	}
}
