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

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.impex.systemsetup.ImpExSystemSetup;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * This Setup implementation loads multiple language files for the CPQ rules implementation.
 *
 * It is expected, that the language files are delivered as properties file, following the file name format
 * <strong>sapproductconfigrules-impexsupport_[<i>two character language code</i>].properties</strong>, and are placed
 * in the resources/localization folder.
 *
 * The properties files are used as parameter sets for the essential Impex file (
 * <strong>essentialdata-sapproductconfigrules_languages.impex</strong>).
 */
@SystemSetup(extension = SapproductconfigrulesConstants.EXTENSIONNAME)
public class ProductConfigRulesSetup extends AbstractSystemSetup
{
	static final String RELATIVE_IMPEX_FOLDER = "/impex/";
	private static final String RELATIVE_LOCALIZATION_FOLDER = "/resources/localization";

	static final String IMPEX_ESSENTIAL_DEFINITIONS_SUFFIX = "_definitions.impex";
	private static final String IMPEX_ESSENTIAL_LANGUAGES_SUFFIX = "_languages.impex";
	private static final String LOCALE_FILE_REGIX_SUFFIX = "-impexsupport.*properties";
	private static final String LOCALE_FILE_LANGUAGE_REGIX_SUFFIX = "-impexsupport_([A-Za-z_]{2,5}).properties";

	private FlexibleSearchService flexibleSearchService;
	private ImpExSystemSetup impexImporter;

	/**
	 * The method processes first the base definitions for the CPQ rules, and updates the definitions values with the
	 * language specific property files. Each language property file will trigger an import for the language essential
	 * Impex.
	 *
	 * @param context
	 *           System context, provided by the initialize or update run
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void processEssentialFiles(final SystemSetupContext context)
	{
		logInfo(context,
				"############# SAP PRODUCT CONFIG RULES " + getExtensionName() + " STARTING ESSENTIAL IMPEX IMPORT ##############");

		getSetupImpexService().importImpexFile(RELATIVE_IMPEX_FOLDER + getExtensionName() + IMPEX_ESSENTIAL_DEFINITIONS_SUFFIX,
				true);

		final List<Path> localeFiles = getListOfLanguageFiles(getBasedir(), context);
		final Path defaultLanguageFile = localeFiles.stream().filter(path -> path.toString().endsWith("en.properties")).findFirst()
				.orElse(null);

		final Map<String, String> defaultParameters = getPropertyFileAsMap(context, defaultLanguageFile);
		localeFiles.stream().map(localFile -> getImpexParameterMap(context, localFile, defaultParameters))
				.filter(parameters -> parameters != null).forEach(this::importImpex);


		getImpexImporter().createAutoImpexProjectData(context);
		logInfo(context,
				"############# SAP PRODUCT CONFIG RULES " + getExtensionName() + " END ESSENTIAL IMPEX IMPORT ##############");
	}

	protected String getBasedir()
	{
		return Utilities.getExtensionInfo(getExtensionName()).getExtensionDirectory() + RELATIVE_LOCALIZATION_FOLDER;
	}

	protected void importImpex(final ImpexMacroParameterData parameters)
	{
		getSetupImpexService().importImpexFile(RELATIVE_IMPEX_FOLDER + getExtensionName() + IMPEX_ESSENTIAL_LANGUAGES_SUFFIX,
				parameters, true);
	}

	protected ImpexMacroParameterData getImpexParameterMap(final SystemSetupContext context, final Path localeFile,
			final Map<String, String> defaultParameters)
	{
		final ImpexMacroParameterData impexParameters = new ImpexMacroParameterData();

		final String language = extractLocaleOutOfFileName(localeFile.toString());
		if (language == null)
		{
			return null;
		}


		final SearchResult<LanguageModel> searchResult = getFlexibleSearchService()
				.search("Select * from {language} where {isocode}='" + language + "'");
		if (searchResult.getCount() == 0)
		{
			logInfo(context, "Found a language file with key=" + language
					+ " but this is not a valid hybris language - Language file will not be imported");
			return null;
		}

		final Map<String, String> loadedParameters = getPropertyFileAsMap(context, localeFile);
		final Map<String, String> parameters = new HashMap<>();
		parameters.putAll(defaultParameters);
		parameters.putAll(loadedParameters);
		parameters.put("lang", language);

		impexParameters.setAdditionalParameterMap(parameters);

		return impexParameters;
	}

	protected Map<String, String> getPropertyFileAsMap(final SystemSetupContext context, final Path localeFile)
	{
		try
		{
			final Properties props = new Properties();
			props.load(Files.newBufferedReader(localeFile));

			return props.entrySet().stream()
					.collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));
		}
		catch (final IOException ex)
		{
			logError(context, "Failed to load language property file - " + localeFile.toString(), ex);
			return Collections.emptyMap();
		}
	}

	protected String extractLocaleOutOfFileName(final String fileName)
	{
		final String languageCode = fileName.replaceAll(getLocaleFileLanguageRegex(), "$1");

		if (fileName.equals(languageCode))
		{
			return null;
		}

		return languageCode;
	}

	protected List<Path> getListOfLanguageFiles(final String basedir, final SystemSetupContext context)
	{

		List<Path> result = Collections.emptyList();

		try (Stream<Path> pathList = Files.list(Paths.get(basedir)))
		{
			result = pathList.filter(path -> path.toString().matches(getLocaleFileRegex())).collect(Collectors.toList());
		}
		catch (final IOException ex)
		{
			logError(context, "Failed to load language file for impex import", ex);
		}

		return result;
	}

	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		return new ArrayList<>();
	}

	protected String getLocaleFileRegex()
	{
		return ".*" + getExtensionName() + LOCALE_FILE_REGIX_SUFFIX;
	}

	protected String getLocaleFileLanguageRegex()
	{
		return ".*" + getExtensionName() + LOCALE_FILE_LANGUAGE_REGIX_SUFFIX;
	}

	protected String getExtensionName()
	{
		return SapproductconfigrulesConstants.EXTENSIONNAME;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected ImpExSystemSetup getImpexImporter()
	{
		return impexImporter;
	}

	@Required
	public void setImpexImporter(final ImpExSystemSetup impexImporter)
	{
		this.impexImporter = impexImporter;
	}

}
