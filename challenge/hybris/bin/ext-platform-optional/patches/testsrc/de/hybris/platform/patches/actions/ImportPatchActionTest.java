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
package de.hybris.platform.patches.actions;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.patches.data.ImpexDataFile;
import de.hybris.platform.patches.data.ImpexHeaderOption;
import de.hybris.platform.patches.data.ImpexImportUnit;
import de.hybris.platform.patches.organisation.ImportLanguage;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test which is checking updating options for ImpexImportUnit.
 */
@UnitTest
public class ImportPatchActionTest
{
	/** Tested class */
	private ImportPatchAction importPatchAction;

	private ImpexImportUnit impexImportUnit;

	@Before
	public void setUp()
	{
		this.importPatchAction = new ImportPatchAction();
		final ImpexDataFile impexDataFile = new ImpexDataFile();
		impexDataFile.setName("baseStore");
		impexDataFile.setFilePath("baseStore.impex");
		this.impexImportUnit = new ImpexImportUnit();
		this.impexImportUnit.setImpexDataFile(impexDataFile);
	}

	/**
	 * Test checks if data file's path was properly changed.
	 */
	@Test
	public void shouldUpdateFilePathWithGivenSuffix()
	{
		// WHEN
		this.importPatchAction.updatedFilePathWithString(this.impexImportUnit, "lang");
		// THEN
		assertThat(this.impexImportUnit.getImpexDataFile().getFilePath()).isEqualTo("baseStore_lang.impex");
	}

	/**
	 * Test checks if HeaderOption was added to ImportUnit.
	 */
	@Test
	public void shouldAddHeaderOptionToImportUnit()
	{
		// GIVEN
		final ImpexHeaderOption headerOption = new ImpexHeaderOption();
		headerOption.setMacro("$distanceUnit=km");
		// WHEN
		this.importPatchAction.updatedHeaderOptions(this.impexImportUnit, headerOption);
		// THEN
		assertThat(this.impexImportUnit.getImpexHeaderOptions().length).isEqualTo(1);
		assertThat(this.impexImportUnit.getImpexHeaderOptions()[0].getMacro()).isEqualTo("$distanceUnit=km");
	}

	/**
	 * Test checks if list of HeaderOption's was added to ImportUnit.
	 */
	@Test
	public void shouldAddHeaderOptionsToImportUnit()
	{
		// GIVEN
		final ImpexHeaderOption headerOption1 = new ImpexHeaderOption();
		headerOption1.setMacro("$pickupInStoreMode=BUY_AND_COLLECT");
		final ImpexHeaderOption headerOption2 = new ImpexHeaderOption();
		headerOption2.setMacro("$code=CA");
		final ImpexHeaderOption[] headerOptions = new ImpexHeaderOption[]
		{ headerOption1, headerOption2 };
		// WHEN
		this.importPatchAction.updatedHeaderOptions(this.impexImportUnit, headerOptions);
		// THEN
		assertThat(this.impexImportUnit.getImpexHeaderOptions().length).isEqualTo(2);
		assertThat(this.impexImportUnit.getImpexHeaderOptions()[0].getMacro()).isEqualTo("$pickupInStoreMode=BUY_AND_COLLECT");
		assertThat(this.impexImportUnit.getImpexHeaderOptions()[1].getMacro()).isEqualTo("$code=CA");
	}

	/**
	 * Test checks if HeaderOption with proper macro was added to ImportUnit and if file path was updated.
	 */
	@Test
	public void shouldUpdatedHeaderOptionsWithLanguage()
	{
		// GIVEN
		final ImportLanguage language = Language.DE_DE;
		// WHEN
		this.importPatchAction.updatedHeaderOptionsWithLanguageAndAddLangFileSuffix(this.impexImportUnit, language);
		// THEN
		assertThat(this.impexImportUnit.getImpexHeaderOptions().length).isEqualTo(1);
		assertThat(this.impexImportUnit.getImpexHeaderOptions()[0].getMacro()).isEqualTo("$lang=de_DE");
		assertThat(this.impexImportUnit.getImpexDataFile().getFilePath()).isEqualTo("baseStore_lang.impex");
	}

	/**
	 * Test checks if HeaderOption with proper macro was added to ImportUnit and if file path was not changed.
	 */
	@Test
	public void shouldUpdateOnlyHeaderOptionsWithLanguage()
	{
		// GIVEN
		final ImportLanguage language = Language.DE_DE;
		// WHEN
		this.importPatchAction.updateHeaderOptionsWithLanguage(this.impexImportUnit, language);
		// THEN
		assertThat(this.impexImportUnit.getImpexHeaderOptions().length).isEqualTo(1);
		assertThat(this.impexImportUnit.getImpexHeaderOptions()[0].getMacro()).isEqualTo("$lang=de_DE");
		assertThat(this.impexImportUnit.getImpexDataFile().getFilePath()).isEqualTo("baseStore.impex");
	}

	/**
	 * Test checks if given file path is correct - suffix is ".impex".
	 */
	@Test
	public void shouldValidateImpexFileSuffixAndReturnTrueForCorrectSuffix()
	{
		assertThat(this.importPatchAction.isValidImpexFileSuffix("test.impex")).isTrue();
	}

	/**
	 * Test checks if given file path is incorrect - suffix is case sensitive.
	 */
	@Test
	public void shouldValidateImpexFileSuffixAndReturnFalseForIncorrectSuffix()
	{
		assertThat(this.importPatchAction.isValidImpexFileSuffix("test.imPex")).isFalse();
	}

	/**
	 * Test checks if given file path is incorrect - when there is no file path.
	 */
	@Test
	public void shouldValidateImpexFileSuffixAndReturnFalseForEmptyPath()
	{
		assertThat(this.importPatchAction.isValidImpexFileSuffix("")).isFalse();
		assertThat(this.importPatchAction.isValidImpexFileSuffix(null)).isFalse();
	}

	/**
	 * A list of defined languages.
	 */
	private enum Language implements ImportLanguage
	{
		DE_DE("de_DE"), FR_CA("fr_CA");
		private final String code;

		Language(final String code)
		{
			this.code = code;
		}

		@Override
		public String getCode()
		{
			return this.code;
		}
	}
}
