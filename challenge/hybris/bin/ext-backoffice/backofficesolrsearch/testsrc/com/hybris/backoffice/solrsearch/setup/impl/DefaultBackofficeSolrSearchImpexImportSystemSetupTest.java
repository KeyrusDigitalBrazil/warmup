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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.FileBasedImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Optional;

import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.solrsearch.setup.BackofficeSolrSearchSystemSetupConfig;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeSolrSearchImpexImportSystemSetupTest
{

	private static final String ZH_CODE = "zh";
	private static final String DE_CODE = "de";
	private static final String EN_CODE = "en";
	private static final String COMMA = ",";
	private static final String DOT = ".";
	private static final String UNDERSCORE = "_";
	private static final String IMPEX_EXTENSION = "impex";
	private static final String UTF_8 = "UTF-8";
	private static final String ISO = "iso";
	private static final String LOCALIZED_ROOT_1 = "/test/test";
	private static final String LOCALIZED_ROOT_2 = "/test/test1";
	private static final String NON_LOCALIZED_ROOT_1 = "/test/test1.impex";
	private static final String NON_LOCALIZED_ROOT_2 = "/test/test2.impex";
	private static final String TEST_TEST_ZH_IMPEX = "/test/test_zh.impex";
	private static final String TEST_TEST_EN_IMPEX = "/test/test_en.impex";
	private static final String TEST_TEST_DE_IMPEX = "/test/test_de.impex";
	private static final String TEST_TEST1_EN_IMPEX = "/test/test1_en.impex";
	private static final String TEST_TEST1_DE_IMPEX = "/test/test1_de.impex";
	private static final String TEST_TEST1_ZH_IMPEX = "/test/test1_zh.impex";

	@Mock
	private ImportService importService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ModelService modelService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private Configuration configuration;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private FileBasedImpExResourceFactory fileBasedImpExResourceFactory;
	@Mock
	private BackofficeSolrSearchSystemSetupConfig backofficeSolrSearchSystemSetupConfig;

	@InjectMocks
	@Spy
	private DefaultBackofficeSolrSearchImpexImportSystemSetup systemSetup;


	@Before
	public void setUp()
	{
		mockDefaultConfig();
		mockDefaultBehaviour();
	}

	@Test
	public void shouldImportConfiguredImpexFiles()
	{
		mockNonLocalizedFilesConfig();
		//given
		final ArgumentCaptor<String> rootsCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> filePathsCaptor = ArgumentCaptor.forClass(String.class);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configurationService.getConfiguration().getString(anyString(), anyString())).thenReturn(StringUtils.EMPTY);

		//when
		systemSetup.importImpex();

		//then
		verify(systemSetup, times(2)).importLocalizedImpexFiles(rootsCaptor.capture());
		verify(systemSetup, times(8)).tryToImportImpexFile(filePathsCaptor.capture());
		assertThat(rootsCaptor.getAllValues()).containsExactly(LOCALIZED_ROOT_1, LOCALIZED_ROOT_2);
		assertThat(filePathsCaptor.getAllValues()).containsExactly(NON_LOCALIZED_ROOT_1, NON_LOCALIZED_ROOT_2, TEST_TEST_EN_IMPEX,
				TEST_TEST_DE_IMPEX, TEST_TEST_ZH_IMPEX, TEST_TEST1_EN_IMPEX, TEST_TEST1_DE_IMPEX, TEST_TEST1_ZH_IMPEX);
	}

	@Test
	public void shouldImportExistingLocalizedFilesWhenGivenLocalizedRootName()
	{
		//given
		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		//when
		systemSetup.importLocalizedImpexFiles(LOCALIZED_ROOT_1);

		//then
		verify(systemSetup, times(3)).tryToImportImpexFile(captor.capture());
		assertThat(captor.getAllValues()).containsExactly(TEST_TEST_EN_IMPEX, TEST_TEST_DE_IMPEX, TEST_TEST_ZH_IMPEX);
	}

	@Test
	public void shouldResolveLocalizedFileNameWhenRootNameAndLanguageModelProvided()
	{
		//given
		final LanguageModel languageModel = mock(LanguageModel.class);
		when(languageModel.getIsocode()).thenReturn(ISO);

		//when
		final String resolveLocalizedFilePath = systemSetup.resolveLocalizedFilePath(LOCALIZED_ROOT_1, languageModel);

		//then
		assertThat(resolveLocalizedFilePath).isEqualTo(LOCALIZED_ROOT_1 + UNDERSCORE + ISO + DOT + IMPEX_EXTENSION);
	}

	@Test
	public void shouldReturnNonNullImportResultWhenFileExists()
	{
		//when
		final Optional<ImportResult> importResult = systemSetup.tryToImportImpexFile(TEST_TEST_EN_IMPEX);

		//then
		assertThat(importResult.isPresent()).isTrue();
	}

	@Test
	public void shouldReturnNulledImportResultWhenFileNotExists()
	{
		//when
		final Optional<ImportResult> importResult = systemSetup.tryToImportImpexFile(TEST_TEST_ZH_IMPEX);

		//then
		assertThat(importResult.isPresent()).isFalse();
	}

	@Test
	public void shouldAdjustCronjob()
	{
		//given
		final String nodeGroup = "TEST";
		final CronJobModel cronJob = spy(new CronJobModel());
		when(cronJobService.getCronJob("update-backofficeIndex-CronJob")).thenReturn(cronJob);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString("backofficesearch.cronjob.nodegroup", StringUtils.EMPTY)).thenReturn(nodeGroup);

		//when
		systemSetup.adjustIndexUpdatingCronjob();

		//then
		verify(cronJob).setNodeGroup(nodeGroup);
		verify(modelService).save(cronJob);
		assertThat(cronJob.getNodeGroup()).isEqualTo(nodeGroup);
	}

	@Test
	public void shouldNotSetNodeGroupInCronJoBWhenNodeGroupIsEmpty()
	{
		//given
		final String nodeGroup = StringUtils.EMPTY;
		final CronJobModel cronJob = spy(new CronJobModel());
		when(cronJobService.getCronJob("update-backofficeIndex-CronJob")).thenReturn(cronJob);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configurationService.getConfiguration().getString("backofficesearch.cronjob.nodegroup")).thenReturn(nodeGroup);

		//when
		systemSetup.adjustIndexUpdatingCronjob();

		//then
		verify(cronJob, times(0)).setNodeGroup(anyString());
		verify(modelService, times(0)).save(cronJob);
		assertThat(cronJob.getNodeGroup()).isNull();
	}

	private void mockDefaultConfig()
	{
		when(backofficeSolrSearchSystemSetupConfig.getFileEncoding()).thenReturn(UTF_8);
		when(backofficeSolrSearchSystemSetupConfig.getRootNameLanguageSeparator()).thenReturn(UNDERSCORE);
		when(backofficeSolrSearchSystemSetupConfig.getListSeparator()).thenReturn(COMMA);
		when(backofficeSolrSearchSystemSetupConfig.getLocalizedRootNames())
				.thenReturn(Arrays.asList(LOCALIZED_ROOT_1, LOCALIZED_ROOT_2));
	}

	private void mockNonLocalizedFilesConfig()
	{
		when(backofficeSolrSearchSystemSetupConfig.getNonLocalizedRootNames())
				.thenReturn(Arrays.asList(NON_LOCALIZED_ROOT_1, NON_LOCALIZED_ROOT_2));
	}

	private void mockDefaultBehaviour()
	{
		final ImportConfig importConfig = mock(ImportConfig.class);
		final FileBasedImpExResource fileBasedImpExResource = mock(FileBasedImpExResource.class);
		doNothing().when(importConfig).setScript(any(FileBasedImpExResource.class));
		when(fileBasedImpExResourceFactory.createFileBasedImpExResource(any(), any())).thenReturn(fileBasedImpExResource);
		when(importService.importData(any(ImportConfig.class))).thenReturn(mock(ImportResult.class));

		final LanguageModel languageModelEn = mock(LanguageModel.class);
		when(languageModelEn.getIsocode()).thenReturn(EN_CODE);
		final LanguageModel languageModelDe = mock(LanguageModel.class);
		when(languageModelDe.getIsocode()).thenReturn(DE_CODE);
		final LanguageModel languageModelZh = mock(LanguageModel.class);
		when(languageModelZh.getIsocode()).thenReturn(ZH_CODE);

		when(commonI18NService.getAllLanguages()).thenReturn(Arrays.asList(languageModelEn, languageModelDe, languageModelZh));
	}

}
