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
package com.hybris.backoffice.excel.integration;

import static com.hybris.backoffice.excel.integration.FeatureListFactory.MULTIPLE_BOOLEAN;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.MULTIPLE_NUMBER_WITH_UNIT;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.MULTI_STRING;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.RANGE_MULTIPLE_NUMBER_WITH_UNIT;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.RANGE_SINGLE_NUMBER_WITHOUT_UNIT;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_BOOLEAN;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_DATE;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_ENUM;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_NUMBER_WITHOUT_UNIT;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_NUMBER_WITH_UNIT;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_RANGE_DATE;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_REFERENCE;
import static com.hybris.backoffice.excel.integration.FeatureListFactory.SINGLE_STRING;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.testframework.Transactional;
import de.hybris.platform.testframework.seed.ClassificationSystemTestDataCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hybris.backoffice.excel.ExcelIntegrationTest;
import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportParams;
import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.exporting.DefaultExcelExportPreProcessor;
import com.hybris.backoffice.excel.exporting.DefaultExcelExportService;
import com.hybris.backoffice.excel.exporting.DefaultExcelExportWorkbookPostProcessor;
import com.hybris.backoffice.excel.importing.DefaultExcelImportService;
import com.hybris.backoffice.excel.importing.DefaultExcelImportWorkbookPostProcessor;
import com.hybris.backoffice.excel.importing.ImpexConverter;
import com.hybris.backoffice.excel.importing.data.ExcelImportResult;


/**
 * Integration test for testing Excel Classification Support feature. It includes import and export scenarios.
 */
@Transactional
@IntegrationTest
public class ExcelClassificationIntegrationTest extends ExcelIntegrationTest
{

	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Resource
	private ClassificationService classificationService;

	private ClassificationClassModel classificationClass;
	private ClassificationSystemVersionModel systemVersion;

	/**
	 * Export
	 */
	@Resource
	private DefaultExcelExportService excelExportService;
	@Resource
	private DefaultExcelExportWorkbookPostProcessor excelExportWorkbookPostProcessor;
	@Resource
	private DefaultExcelExportPreProcessor excelExportPreProcessor;

	/**
	 * Import
	 */
	@Resource
	private DefaultExcelImportService excelImportService;
	@Resource
	private DefaultExcelImportWorkbookPostProcessor excelImportWorkbookPostProcessor;
	@Resource
	private ImpexConverter excelImpexConverter;


	private TimeZone defaultTimeZone;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		createCoreData();
		createDefaultCatalog();
		createHardwareCatalog();

		final ClassificationSystemTestDataCreator creator = new ClassificationSystemTestDataCreator(getModelService());
		final ClassificationSystemModel system = creator.createClassificationSystem("testClassificationSystem");
		systemVersion = creator.createClassificationSystemVersion("testVersion", system);
		classificationClass = creator.createClassificationClass("testClass", systemVersion);

		defaultTimeZone = java.util.TimeZone.getDefault();
	}

	@After
	public void tearDown()
	{
		TimeZone.setDefault(defaultTimeZone);
	}

	@Test
	public void testExportIncludingClassification() throws IOException
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		// given
		final CatalogVersionModel catalogVersion = createCatalogVersionModel("catalog", "1.0");
		final ProductModel product = prepareProduct("product", catalogVersion);

		final FeatureList featureList = FeatureListFactory.create(getModelService(), getTypeService(), classificationClass,
				catalogVersion, systemVersion);

		product.setSupercategories(Lists.newArrayList(classificationClass));

		getModelService().save(product);

		classificationService.setFeatures(product, featureList);

		final ExcelExportParams excelExportParams = excelExportPreProcessor.process(
				new ExcelExportParams(Lists.newArrayList(product), new ArrayList<>(), convertFeatureToExcelAttribute(featureList)));

		// when
		try (final Workbook workbook = excelExportService.exportData(Collections.singletonList(product), new ArrayList<>()))
		{
			final ExcelExportResult excelExportResult = new ExcelExportResult(workbook, excelExportParams.getItemsToExport(),
					excelExportParams.getSelectedAttributes(), excelExportParams.getAdditionalAttributes(),
					excelExportParams.getAdditionalAttributes());

			excelExportWorkbookPostProcessor.process(excelExportResult);

			// then
			assertThat(workbook.getSheet(PRODUCT_SHEET_NAME)).isNotNull();
			soft.assertThat(getCellAt(workbook, 2, 3)).isEqualTo("true");
			soft.assertThat(getCellAt(workbook, 3, 3)).isEqualTo("true,false,true");
			soft.assertThat(getCellAt(workbook, 4, 3)).isEqualTo("3.53");
			soft.assertThat(getCellAt(workbook, 5, 3)).isEqualTo("4.53:kg");
			soft.assertThat(getCellAt(workbook, 6, 3)).isEqualTo("4.53:g,3.276:g,3.21:g");
			soft.assertThat(getCellAt(workbook, 7, 3)).isEqualTo("RANGE[2.53;3.77]");
			soft.assertThat(getCellAt(workbook, 8, 3)).isEqualTo("RANGE[1.53:m;1.58:m],RANGE[2.01:m;2.53:m]");
			soft.assertThat(getCellAt(workbook, 9, 3)).isEqualTo("03.03.2018 10:00:00");
			soft.assertThat(getCellAt(workbook, 10, 3)).isEqualTo("RANGE[03.03.2018 10:00:00;03.03.2019 12:00:00]");
			soft.assertThat(getCellAt(workbook, 11, 3)).isEqualTo("some string");
			soft.assertThat(getCellAt(workbook, 12, 3)).isEqualTo("x1,x2,x3");
			soft.assertThat(getCellAt(workbook, 13, 3)).isEqualTo("check");
			soft.assertThat(getCellAt(workbook, 14, 3)).isEqualTo("productRef:1.0:catalog");
			soft.assertThat(getCellAt(workbook, 15, 3)).isEqualTo("danke");
			soft.assertThat(getCellAt(workbook, 16, 3)).isEqualTo("thanks");
		}
	}

	private String getCellAt(final Workbook workbook, final int column, final int row)
	{
		return workbook.getSheet(PRODUCT_SHEET_NAME).getRow(row).getCell(column).getStringCellValue();
	}

	@Test
	public void testImportIncludingClassification() throws IOException
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		// given
		final CatalogVersionModel catalogVersion = createCatalogVersionModel("catalog", "1.0");
		final ProductModel product = prepareProduct("product", catalogVersion);

		final FeatureList featureList = FeatureListFactory.create(getModelService(), getTypeService(), classificationClass,
				catalogVersion, systemVersion);

		product.setSupercategories(Lists.newArrayList(classificationClass));

		getModelService().save(product);

		classificationService.setFeatures(product, featureList);

		try (final Workbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/test/excel/importClassification.xlsx")))
		{
			// when
			final Impex impex = excelImportService.convertToImpex(workbook);
			excelImportWorkbookPostProcessor.process(new ExcelImportResult(workbook, impex));

			final ImportResult importResult = //
					importService.importData( //
							createImportConfig( //
									excelImpexConverter.convert( //
											impex)));

			// then
			assertThat(importResult).isNotNull();
			soft.assertThat(importResult.isFinished()).isTrue();
			soft.assertThat(importResult.isError()).isFalse();
			soft.assertThat(importResult.isSuccessful()).isTrue();

			final FeatureList features = classificationService.getFeatures(product);

			soft.assertThat(getFeatureValue(features, SINGLE_BOOLEAN)).isEqualToIgnoringCase("false");
			soft.assertThat(getFeatureValue(features, MULTIPLE_BOOLEAN)).isEqualToIgnoringCase("true,false,true");
			soft.assertThat(getFeatureValue(features, SINGLE_NUMBER_WITHOUT_UNIT)).isEqualToIgnoringCase("5.38");
			soft.assertThat(getFeatureValue(features, SINGLE_NUMBER_WITH_UNIT)).isEqualToIgnoringCase("4.23:kg");
			soft.assertThat(getFeatureValue(features, MULTIPLE_NUMBER_WITH_UNIT)).isEqualToIgnoringCase("4.53:g,3.276:kg,3.21:g");
			soft.assertThat(getFeatureValue(features, RANGE_SINGLE_NUMBER_WITHOUT_UNIT)).isEqualToIgnoringCase("2.07,3.77");
			soft.assertThat(getFeatureValue(features, RANGE_MULTIPLE_NUMBER_WITH_UNIT))
					.isEqualToIgnoringCase("1.53:m,1.58:m,2.01:m,2.53:m");
			soft.assertThat(getFeatureValue(features, SINGLE_DATE)).isEqualToIgnoringCase("03.03.2018 10:00:00");
			soft.assertThat(getFeatureValue(features, SINGLE_RANGE_DATE))
					.isEqualToIgnoringCase("03.03.2018 10:00:00,05.03.2019 12:00:00");
			soft.assertThat(getFeatureValue(features, SINGLE_STRING)).isEqualToIgnoringCase("some other string");
			soft.assertThat(getFeatureValue(features, MULTI_STRING)).isEqualToIgnoringCase("x1,x2,x3");
			soft.assertThat(getFeatureValue(features, SINGLE_REFERENCE)).isEqualToIgnoringCase("productRef:1.0:catalog");
			soft.assertThat(getFeatureValue(features, SINGLE_ENUM)).isEqualToIgnoringCase("check");
		}
	}

	private static String getFeatureValue(final FeatureList features, final String code)
	{
		return features.getFeatureByCode("testClassificationSystem/testVersion/testClass." + code.toLowerCase()).getValue()
				.getValue().toString();
	}

	private static List<ExcelAttribute> convertFeatureToExcelAttribute(final FeatureList features)
	{
		final List<LocalizedFeature> localizedFeatures = features.getFeatures().stream().filter(LocalizedFeature.class::isInstance)
				.map(LocalizedFeature.class::cast).collect(Collectors.toList());
		final List<Feature> unlocalizedFeatures = features.getFeatures().stream()
				.filter(feature -> !(feature instanceof LocalizedFeature)).collect(Collectors.toList());

		final List<ExcelAttribute> attributes = new ArrayList<>();
		final List<ExcelAttribute> localizedAttributes = new ArrayList<>();
		for (final LocalizedFeature f : localizedFeatures)
		{
			for (final Map.Entry<Locale, List<FeatureValue>> entry : f.getValuesForAllLocales().entrySet())
			{
				final ExcelClassificationAttribute excelClassificationAttribute = new ExcelClassificationAttribute();
				excelClassificationAttribute.setIsoCode(entry.getKey().toLanguageTag());
				excelClassificationAttribute.setAttributeAssignment(f.getClassAttributeAssignment());
				localizedAttributes.add(excelClassificationAttribute);
			}
		}
		final List<ExcelAttribute> unlocalizedAttributes = unlocalizedFeatures.stream().map(feature -> {
			final ExcelClassificationAttribute excelClassificationAttribute = new ExcelClassificationAttribute();
			excelClassificationAttribute.setAttributeAssignment(feature.getClassAttributeAssignment());
			return excelClassificationAttribute;
		}).collect(Collectors.toList());

		attributes.addAll(unlocalizedAttributes);
		attributes.addAll(localizedAttributes);
		return attributes;
	}

}
