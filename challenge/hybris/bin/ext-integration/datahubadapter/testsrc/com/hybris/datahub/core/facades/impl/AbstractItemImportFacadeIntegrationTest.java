/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.facades.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;

import com.hybris.datahub.core.config.impl.DefaultImportConfigStrategy;
import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ImportTestUtils;
import com.hybris.datahub.core.facades.ItemImportResult;
import com.hybris.datahub.core.io.TextFile;
import com.hybris.datahub.core.services.impl.DataHubFacade;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

@Ignore("Prevent Abstract Integration test from running without implementation")
@IntegrationTest
public abstract class AbstractItemImportFacadeIntegrationTest extends ServicelayerTest
{
	private static final String IMPEX_FILE_PATH = "/impex/media/test.impex";

	@Resource
	private DefaultItemImportFacade importFacade;
	@Resource
	private DefaultImportConfigStrategy importConfigStrategy;

	private static ItemImportTaskData createImportData(final String impexScript) throws IOException
	{
		impexFile().save(impexScript);
		final ItemImportTaskData data = new ItemImportTaskData();
		data.setImpexMetaData(impexScript.getBytes());
		data.setPoolName("Test pool");
		data.setPublicationId(1L);
		data.setResultCallbackUrl("http://localhost ");
		return data;
	}

	@BeforeClass
	public static void saveImpexSourceBeforeAllTests() throws IOException
	{
		impexFile().save("CreateFile");
	}

	@AfterClass
	public static void deleteImpexSourceAfterAllTestsDone() throws IOException
	{
		impexFile().delete();
	}

	private static TextFile impexFile()
	{
		final String dataDir = ConfigUtil.getPlatformConfig(Registry.class).getSystemConfig().getDataDir().getPath();
		final String mediaDir = dataDir + File.separator + "media";
		final String tenantDir = mediaDir + File.separator + "sys_master";
		return new TextFile(tenantDir, IMPEX_FILE_PATH);
	}

	private DataHubFacade mockDataHubFacade()
	{
		return Mockito.mock(DataHubFacade.class);
	}

	@Before
	public void setUp() throws Exception
	{
		// Don't want to communicate back to datahub
		importFacade.setDataHubFacade(mockDataHubFacade());

		importImpexScript(apparelProductCatalog());
	}

	@Test
	public void testSuccessfulImportDoesNotContainErrorsInTheResult() throws Exception
	{
		importImpexAndExpectSuccess(successfulImpexScript());
	}

	@Test
	public void testInvalidUnitReferenceImpexScript() throws Exception
	{
		importImpexAndExpectErrors(invalidUnitReferenceImpexScript(), 1);
	}

	@Test
	public void testMissingMandatoryAttributeImpex() throws Exception
	{
		importImpexAndExpectErrors(missingMandatoryAttributeImpexScript(), 1);
	}

	@Test
	public void testMissingReferenceVersionImpex() throws Exception
	{
		importImpexAndExpectErrors(missingReferenceVersionImpexScript(), 1);
	}

	@Test
	public void testNonUniqueCode() throws Exception
	{
		importImpexAndExpectErrors(nonUniqueCodeImpexScript(), 2);
	}

	@Test
	public void testInvalidDataFormat() throws Exception
	{
		importImpexAndExpectErrors(invalidDataFormatImpexScript(), 1);
	}

	private void importImpexAndExpectErrors(final String impexScript, final int numOfErrors) throws IOException
	{
		final ItemImportResult res = importImpexScript(impexScript);

		assertNumberOfErrors(res, numOfErrors);
	}

	private ItemImportResult importImpexScript(final String impexScript) throws IOException
	{
		final ItemImportTaskData importData = createImportData(impexScript);
		return importFacade.importItems(importData);
	}

	private void assertNumberOfErrors(final ItemImportResult res, final int expectedNumOfErrors)
	{
		assertThat(res.isSuccessful()).isFalse();
		assertThat(res.getErrors()).hasSize(expectedNumOfErrors);
	}

	private void importImpexAndExpectSuccess(final String impexScript) throws IOException
	{
		final ItemImportResult res = importImpexScript(impexScript);

		assertThat(res.isSuccessful()).isTrue();
		assertThat(res.getErrors()).isEmpty();
	}

	protected void enableDistributedImpexAndSld(final boolean isEnabled, final boolean isSld)
	{
		importConfigStrategy.setSld(isSld);
		importConfigStrategy.setDistributedImpex(isEnabled);
		importFacade.setImportConfigStrategy(importConfigStrategy);
	}

	private static String successfulImpexScript()
	{
		// Lines of this script are used in the test results. Any changes done to the script should be reflected in the tests.
		return ImportTestUtils
				.toText(
						"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
						"$baseProduct=baseProduct(code,catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))",
						"INSERT_UPDATE Category;;$catalogVersion;supercategories(code,$catalogVersion);code[unique=true]",
						";1;;<ignore>;1",
						"##########################",
						"INSERT_UPDATE Category;;$catalogVersion;description[lang=en];name[lang=en];code[unique=true]",
						";1;;category description 1;Category 1;1");
	}

	private static String nonUniqueCodeImpexScript()
	{
		return ImportTestUtils.toText(
				"INSERT Title;code[unique=true]",
				";foo",
				";foo",
				";foo"
		);
	}

	private static String missingReferenceVersionImpexScript()
	{
		return ImportTestUtils.toText(
				"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
				"$baseProduct=baseProduct(code,catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))",
				"INSERT_UPDATE Category;;$catalogVersion;supercategories(code,$catalogVersion);code[unique=true]",
				";1;12;<ignore>;1");
	}

	private static String missingMandatoryAttributeImpexScript()
	{
		return ImportTestUtils.toText(
				"$baseProduct=baseProduct(code, catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))",
				"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
				"INSERT_UPDATE Category;;description[lang=en];name[lang=en];code[unique=true];$catalogVersion",
				";123;;Snowwear women;;");
	}

	private static String invalidUnitReferenceImpexScript()
	{
		return ImportTestUtils.toText(
				"$baseProduct=baseProduct(code, catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))",
				"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
				"INSERT_UPDATE Product;;description[lang=de];name[lang=de];code[unique=true];unit(code);$catalogVersion",
				";321;;Andover Jacke;95385;piece;");
	}

	private static String invalidDataFormatImpexScript()
	{
		return ImportTestUtils.toText(
				"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
				"$baseProduct=baseProduct(code,catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))",
				"INSERT_UPDATE Product;;code[unique=true];$catalogVersion;ean;supercategories(code,$catalogVersion);numberContentUnits;unit(code)",
				";58;M1-invalid-data-format;;<ignore>;<ignore>;one;pieces");
	}

	private static String apparelProductCatalog()
	{
		return ImportTestUtils.toText(
				"$productCatalog=apparelProductCatalog",
				"$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]",
				"$languages=en,de",
				"INSERT_UPDATE Catalog;id[unique=true]",
				";$productCatalog",
				"INSERT_UPDATE CatalogVersion;catalog(id)[unique=true];version[unique=true];active;languages(isoCode);readPrincipals(uid)",
				";$productCatalog;Staged;false;$languages;employeegroup",
				";$productCatalog;Online;true;$languages;employeegroup");
	}
}
