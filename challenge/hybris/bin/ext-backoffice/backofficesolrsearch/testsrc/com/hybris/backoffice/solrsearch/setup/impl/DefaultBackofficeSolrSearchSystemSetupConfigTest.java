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
package com.hybris.backoffice.solrsearch.setup.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeSolrSearchSystemSetupConfigTest
{
	private static final String FILE_1 = "file1";
	private static final String FILE_2 = "file2";
	private static final String UTF_8 = "UTF-8";
	private static final String FILE_ENCODING = "fileEncoding";
	private static final String UNDERSCORE = "_";
	private static final String LANGUAGE_SEPARATOR = "language_separator";
	private static final String COMMA = ",";
	private static final String SEMICOLON = ";";
	private static final String BACKOFFICE_SOLR_SEARCH_ROOTS_SEPARATOR_KEY = "backoffice.solr.search.roots.separator";
	private static final String BACKOFFICE_SOLR_SEARCH_LOCALIZED_ROOTS_KEY = "backoffice.solr.search.localized.roots";
	private static final String BACKOFFICE_SOLR_SEARCH_NON_LOCALIZED_ROOTS_KEY = "backoffice.solr.search.nonlocalized.files";
	private static final String BACKOFFICE_SOLR_SEARCH_ROOTS_FILE_ENCODING_KEY = "backoffice.solr.search.roots.file.encoding";
	private static final String BACKOFFICE_SOLR_SEARCH_ROOTS_LANGUAGE_SEPARATOR_KEY = "backoffice.solr.search.roots.language.separator";
	private static final String CONFIGURED_FILE_LIST = "file1;file2";

	@Mock
	private ConfigStringResolver configStringResolver;

	@InjectMocks
	private DefaultBackofficeSolrSearchSystemSetupConfig config;

	@Test
	public void shouldReturnEmptyCollectionOfLocalizedImpexFilesRootsWhenConfigurationKeyIsSNotSet()
	{
		//given
		mockConfigResolverWithEmptyKeys();

		//when
		final Collection<String> roots = config.getLocalizedRootNames();

		//then
		assertThat(roots).isEmpty();
	}

	@Test
	public void shouldReturnConfiguredLocalizedImpexFilesRootsWhenConfigurationKeyIsSet()
	{
		//given
		mockConfigResolverWithNonEmptyKeys();

		//when
		final Collection<String> roots = config.getLocalizedRootNames();

		//then
		assertThat(roots).contains(FILE_1, FILE_2);
	}

	@Test
	public void shouldReturnEmptyCollectionOfNonLocalizedImpexFilesRootsWhenConfigurationKeyIsSNotSet()
	{
		//given
		mockConfigResolverWithEmptyKeys();

		//when
		final Collection<String> roots = config.getNonLocalizedRootNames();

		//then
		assertThat(roots).isEmpty();
	}

	@Test
	public void shouldReturnConfiguredNonLocalizedImpexFilesRootsWhenConfigurationKeyIsSet()
	{
		//given
		mockConfigResolverWithNonEmptyKeys();
		mockNonLocalizedRoots();

		//when
		final Collection<String> roots = config.getNonLocalizedRootNames();

		//then
		assertThat(roots).contains(FILE_1, FILE_2);
	}

	@Test
	public void shouldReturnDefaultFileEncodingWhenConfigurationForKeyIsNotSet()
	{
		//given
		mockConfigResolverWithEmptyKeys();

		//when
		final String fileEncoding = config.getFileEncoding();

		//then
		assertThat(fileEncoding).isEqualTo(UTF_8);
	}

	@Test
	public void shouldReturnConfiguredFileEncodingWhenConfigurationForKeyIsSet()
	{
		//given
		mockConfigResolverWithNonEmptyKeys();

		//when
		final String fileEncoding = config.getFileEncoding();

		//then
		assertThat(fileEncoding).isEqualTo(FILE_ENCODING);
	}

	@Test
	public void shouldReturnDefaultLanguageSeparatorWhenConfigurationForKeyIsNotSet()
	{
		//given
		mockConfigResolverWithEmptyKeys();

		//when
		final String languageSeparator = config.getRootNameLanguageSeparator();

		//then
		assertThat(languageSeparator).isEqualTo(UNDERSCORE);
	}

	@Test
	public void shouldReturnConfiguredLanguageSeparatorWhenConfigurationForKeyIsSet()
	{
		//given
		mockConfigResolverWithNonEmptyKeys();

		//when
		final String languageSeparator = config.getRootNameLanguageSeparator();

		//then
		assertThat(languageSeparator).isEqualTo(LANGUAGE_SEPARATOR);
	}

	@Test
	public void shouldReturnDefaultListSeparatorWhenConfigurationForKeyIsNotSet()
	{
		//given
		mockConfigResolverWithEmptyKeys();

		//when
		final String listSeparator = config.getListSeparator();

		//then
		assertThat(listSeparator).isEqualTo(COMMA);
	}

	@Test
	public void shouldReturnConfiguredListSeparatorWhenConfigurationForKeyIsSet()
	{
		//given
		mockConfigResolverWithNonEmptyKeys();

		//when
		final String listSeparator = config.getListSeparator();

		//then
		assertThat(listSeparator).isEqualTo(SEMICOLON);
	}

	@Test
	public void shouldReturnConstructorInjectedConfigStringResolver()
	{
		//when
		final ConfigStringResolver injectedConfigStringResolver = config.getConfigStringResolver();

		//then
		assertThat(injectedConfigStringResolver).isSameAs(configStringResolver);
	}

	@Test
	public void shouldReturnConfiguredListOfPaths()
	{
		//given
		mockConfigResolverWithNonEmptyKeys();

		//when
		final Collection<String> roots = config.readRoots(BACKOFFICE_SOLR_SEARCH_LOCALIZED_ROOTS_KEY);

		//then
		assertThat(roots).contains(FILE_1, FILE_2);
	}

	private void mockConfigResolverWithNonEmptyKeys()
	{
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_SEPARATOR_KEY)).thenReturn(SEMICOLON);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_LOCALIZED_ROOTS_KEY)).thenReturn(CONFIGURED_FILE_LIST);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_FILE_ENCODING_KEY))
				.thenReturn(FILE_ENCODING);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_LANGUAGE_SEPARATOR_KEY))
				.thenReturn(LANGUAGE_SEPARATOR);
	}

	private void mockConfigResolverWithEmptyKeys()
	{
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_SEPARATOR_KEY)).thenReturn(null);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_LOCALIZED_ROOTS_KEY)).thenReturn(null);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_FILE_ENCODING_KEY)).thenReturn(null);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_LANGUAGE_SEPARATOR_KEY))
				.thenReturn(null);
	}

	private void mockNonLocalizedRoots()
	{
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_NON_LOCALIZED_ROOTS_KEY)).thenReturn(CONFIGURED_FILE_LIST);
	}
}
