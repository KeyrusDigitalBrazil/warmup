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
package com.hybris.backoffice.excel.translators.generic.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ImpexHeaderValue;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.parser.DefaultImportParameterParser;
import com.hybris.backoffice.excel.importing.parser.ParsedValues;
import com.hybris.backoffice.excel.importing.parser.matcher.DefaultExcelParserMatcher;
import com.hybris.backoffice.excel.importing.parser.splitter.DefaultExcelParserSplitter;


public class DefaultImportImpexFactoryTest
{

	private final ReferenceImportImpexFactoryStrategy referenceImportImpexFactoryStrategy = new ReferenceImportImpexFactoryStrategy();

	@Test
	public void shouldPrepareImpexHeaderForCatalogVersion()
	{
		// given

		// when
		final ImpexHeaderValue impexHeaderValue = referenceImportImpexFactoryStrategy
				.prepareImpexHeader(RequiredAttributeTestFactory.prepareStructureForCatalogVersion(), true, true);

		// then
		assertThat(impexHeaderValue.isUnique()).isTrue();
		assertThat(impexHeaderValue.getName()).isEqualTo("catalogVersion(version,catalog(id))");
	}

	@Test
	public void shouldPrepareImpexHeaderForSupercategories()
	{
		// given

		// when
		final ImpexHeaderValue impexHeaderValue = referenceImportImpexFactoryStrategy
				.prepareImpexHeader(RequiredAttributeTestFactory.prepareStructureForSupercategories(), false, true);

		// then
		assertThat(impexHeaderValue.isUnique()).isFalse();
		assertThat(impexHeaderValue.getName()).isEqualTo("supercategories(code,catalogVersion(version,catalog(id)))");
	}

	@Test
	public void shouldPrepareImpexValueForCatalogVersion()
	{
		// given

		final ParsedValues parsedValues = createDefaultImportParameterParser().parseValue("CatalogVersion.version:Catalog.id", "",
				"Online:Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());

		// when
		final String impexValue = referenceImportImpexFactoryStrategy
				.prepareImpexValue(RequiredAttributeTestFactory.prepareStructureForCatalogVersion(), importParameters);

		// then
		assertThat(impexValue).isEqualTo("Online:Default");
	}

	@Test
	public void shouldPrepareImpexValueForSupercategories()
	{
		// given
		final ParsedValues parsedValues = createDefaultImportParameterParser().parseValue(
				"Category.code:CatalogVersion.version:Catalog.id", ":Online:Default",
				"First:Online:Default,Second:Online:,Third::Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());

		// when
		final String impexValue = referenceImportImpexFactoryStrategy
				.prepareImpexValue(RequiredAttributeTestFactory.prepareStructureForSupercategories(), importParameters);

		// then
		assertThat(impexValue).isEqualTo("First:Online:Default,Second:Online:Default,Third:Online:Default");
	}

	private DefaultImportParameterParser createDefaultImportParameterParser()
	{
		final DefaultImportParameterParser parser = new DefaultImportParameterParser();
		parser.setMatcher(new DefaultExcelParserMatcher());
		parser.setSplitter(new DefaultExcelParserSplitter());
		return parser;
	}
}
