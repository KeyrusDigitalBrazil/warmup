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
package de.hybris.platform.sap.productconfig.rules.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.impex.systemsetup.ImpExSystemSetup;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRulesSetupTest
{
	@InjectMocks
	private final ProductConfigRulesSetup classUnderTest = new ProductConfigRulesSetup();
	@Mock
	private SystemSetupContext context;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private SetupImpexService setupImpexService;
	@Mock
	private ImpExSystemSetup mockedImpexImporter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetListOfLanguageFiles() throws Exception
	{

		final List<Path> localeFiles = classUnderTest.getListOfLanguageFiles(classUnderTest.getBasedir(), context);

		assertNotNull(localeFiles);
		assertTrue(localeFiles.size() > 0);

		assertTrue(
				localeFiles.stream().anyMatch(path -> path.toString().endsWith("sapproductconfigrules-impexsupport_en.properties")));
	}

	@Test
	public void testGetListOfLanguageFilesFail() throws Exception
	{
		final List<Path> localeFiles = classUnderTest.getListOfLanguageFiles("fail", context);

		assertNotNull(localeFiles);
		assertTrue(localeFiles.size() == 0);
	}

	@Test
	public void testExtractLocaleOutOfFileName()
	{
		String fileName = "/sapproductconfigrules/resources/localization/sapproductconfigrules-impexsupport_en.properties";

		String locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("en", locale);

		fileName = "sapproductconfigrules-impexsupport_en.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("en", locale);

		fileName = "sapproductconfigrules-impexsupport_EN_en.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("EN_en", locale);

		fileName = "sapproductconfigrules-impexsupport_de.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("de", locale);

		fileName = "sapproductconfigrules-impexsupport.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertNull(locale);
	}

	@Test
	public void getInitializationOptions()
	{
		final List<SystemSetupParameter> initializationOptions = classUnderTest.getInitializationOptions();
		assertNotNull(initializationOptions);
		assertTrue(initializationOptions.isEmpty());
	}

	@Test
	public void testImport() throws IOException
	{
		final SearchResult<Object> mockedSerchResult = Mockito.mock(SearchResult.class);
		given(flexibleSearchService.search(Mockito.anyString())).willReturn(mockedSerchResult);
		given(mockedSerchResult.getCount()).willReturn(1);

		classUnderTest.processEssentialFiles(context);
		Mockito.verify(mockedImpexImporter).createAutoImpexProjectData(context);
		Mockito.verify(setupImpexService).importImpexFile(ProductConfigRulesSetup.RELATIVE_IMPEX_FOLDER
				+ classUnderTest.getExtensionName() + ProductConfigRulesSetup.IMPEX_ESSENTIAL_DEFINITIONS_SUFFIX, true);
	}

	@Test
	public void testImportWithoutParameters() throws IOException
	{
		final SearchResult<Object> mockedSerchResult = Mockito.mock(SearchResult.class);
		given(flexibleSearchService.search(Mockito.anyString())).willReturn(mockedSerchResult);
		given(mockedSerchResult.getCount()).willReturn(0);

		classUnderTest.processEssentialFiles(context);
		Mockito.verify(mockedImpexImporter).createAutoImpexProjectData(context);
		Mockito.verify(setupImpexService).importImpexFile(ProductConfigRulesSetup.RELATIVE_IMPEX_FOLDER
				+ classUnderTest.getExtensionName() + ProductConfigRulesSetup.IMPEX_ESSENTIAL_DEFINITIONS_SUFFIX, true);
	}

	@Test
	public void testGetImpexParameterMapFileNameWithoutLocale()
	{
		final String fileName = "sapproductconfigrules-impexsupport.properties";
		final ImpexMacroParameterData result = classUnderTest.getImpexParameterMap(null, Paths.get(fileName), null);
		assertNull(result);
	}

	@Test
	public void testGetImpexParameterMap()
	{
		final Map<String, String> defaultParameters = new HashMap<>();
		defaultParameters.put("test", "test");
		final Path file = Paths.get("sapproductconfigrules-impexsupport_en.properties");

		final SearchResult<Object> mockedSerchResult = Mockito.mock(SearchResult.class);

		given(flexibleSearchService.search(Mockito.anyString())).willReturn(mockedSerchResult);
		given(mockedSerchResult.getCount()).willReturn(1);

		final ImpexMacroParameterData result = classUnderTest.getImpexParameterMap(context, file, defaultParameters);
		assertNotNull(result);

		final Map<String, String> parameters = result.getAdditionalParameterMap();
		assertNotNull(parameters);
		assertEquals("en", parameters.get("lang"));
		assertEquals("test", parameters.get("test"));
	}

	@Test
	public void testGetImpexParameterMapWithoutSearchResult()
	{
		final Map<String, String> defaultParameters = new HashMap<>();
		defaultParameters.put("test", "test");
		final Path file = Paths.get("sapproductconfigrules-impexsupport_en.properties");

		final SearchResult<Object> mockedSerchResult = Mockito.mock(SearchResult.class);

		given(flexibleSearchService.search(Mockito.anyString())).willReturn(mockedSerchResult);
		given(mockedSerchResult.getCount()).willReturn(0);

		final ImpexMacroParameterData result = classUnderTest.getImpexParameterMap(context, file, defaultParameters);
		assertNull(result);
	}

	@Test
	public void testGetExtensionName()
	{
		assertEquals("sapproductconfigrules", classUnderTest.getExtensionName());
	}

	@Test
	public void testGetLocaleFileRegex()
	{
		assertEquals(".*sapproductconfigrules-impexsupport.*properties", classUnderTest.getLocaleFileRegex());
	}

	@Test
	public void testGetLocaleFileLanguageRegex()
	{
		assertEquals(".*sapproductconfigrules-impexsupport_([A-Za-z_]{2,5}).properties",
				classUnderTest.getLocaleFileLanguageRegex());
	}
}
