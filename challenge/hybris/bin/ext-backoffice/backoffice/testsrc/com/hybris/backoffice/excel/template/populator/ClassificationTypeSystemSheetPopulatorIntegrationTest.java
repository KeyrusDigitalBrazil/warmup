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
package com.hybris.backoffice.excel.template.populator;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.testframework.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.ExcelTemplateService;


@IntegrationTest
@Transactional
public class ClassificationTypeSystemSheetPopulatorIntegrationTest extends ServicelayerTest
{
	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();
	@Resource
	ExcelTemplateService excelTemplateService;
	@Resource
	I18NService i18NService;
	@Resource
	FlexibleSearchService flexibleSearchService;
	@Resource(name = "defaultClassificationTypeSystemSheetPopulator")
	ClassificationTypeSystemSheetPopulator populator;

	@Test
	public void shouldExportClassificationTypeSystemSheetOnTemplate() throws Exception
	{
		loadClassificationImpex();

		// given
		final String queryForAllClassificationAssignments = "SELECT {ClassAttributeAssignment:PK} FROM {ClassAttributeAssignment}";
		final SearchResult<ClassAttributeAssignmentModel> searchResult = flexibleSearchService
				.search(queryForAllClassificationAssignments);
		final Collection<ExcelAttribute> attributes = new LinkedList<>();
		for (final ClassAttributeAssignmentModel assignment : searchResult.getResult())
		{
			for (final Locale locale : i18NService.getSupportedLocales())
			{
				final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
				attribute.setAttributeAssignment(assignment);
				attribute.setIsoCode(String.valueOf(locale));
				attributes.add(attribute);
			}
		}

		try (final Workbook workbook = excelTemplateService.createWorkbook(loadResource("/excel/excelImpExMasterTemplate.xlsx")))
		{
			// when
			populator.populate(createExcelExportResultWithAvailableAttributes(workbook, attributes));

			// then
			final Sheet classificationTypeSystemSheet = workbook
					.getSheet(ExcelTemplateConstants.UtilitySheet.CLASSIFICATION_TYPE_SYSTEM.getSheetName());
			assertThat(classificationTypeSystemSheet).isNotNull();
			final Row firstRow = classificationTypeSystemSheet.getRow(0);
			assertThat(firstRow).isNotNull();
			soft.assertThat(getCellValue(firstRow, 0)).isEqualTo("FullName");
			soft.assertThat(getCellValue(firstRow, 1)).isEqualTo("ClassificationSystem");
			soft.assertThat(getCellValue(firstRow, 2)).isEqualTo("ClassificationVersion");
			soft.assertThat(getCellValue(firstRow, 3)).isEqualTo("ClassificationClass");
			soft.assertThat(getCellValue(firstRow, 4)).isEqualTo("ClassificationAttribute");
			soft.assertThat(getCellValue(firstRow, 5)).isEqualTo("AttrLocalized");
			soft.assertThat(getCellValue(firstRow, 6)).isEqualTo("AttrLocLang");
			soft.assertThat(getCellValue(firstRow, 7)).isEqualTo("IsMandatory");
			final Row secondRow = classificationTypeSystemSheet.getRow(1);
			assertThat(secondRow).isNotNull();
			soft.assertThat(getCellValue(secondRow, 0)).isEqualTo(
					"{software.manufacturerURL[de] - SampleClassification/1.0},{software.manufacturerURL[en] - SampleClassification/1.0}");
			soft.assertThat(getCellValue(secondRow, 1)).isEqualTo("SampleClassification");
			soft.assertThat(getCellValue(secondRow, 2)).isEqualTo("1.0");
			soft.assertThat(getCellValue(secondRow, 3)).isEqualTo("software");
			soft.assertThat(getCellValue(secondRow, 4)).isEqualTo("manufacturerURL");
			soft.assertThat(getCellValue(secondRow, 5)).isEqualTo("true");
			soft.assertThat(getCellValue(secondRow, 6)).isEqualTo("{de},{en}");
			soft.assertThat(getCellValue(secondRow, 7)).isEqualTo("false");
		}
	}

	private void loadClassificationImpex() throws ImpExException
	{
		importStream(loadResource("/test/excel/classificationSystem.csv"), StandardCharsets.UTF_8.name(),
				"classificationSystem.csv");
	}

	private InputStream loadResource(final String path)
	{
		return Optional.ofNullable(getClass().getResourceAsStream(path))
				.orElseThrow(() -> new AssertionError("Could not load resource: " + path));
	}

	private ExcelExportResult createExcelExportResultWithAvailableAttributes(final Workbook workbook,
			final Collection<ExcelAttribute> attributes)
	{
		return new ExcelExportResult(workbook, null, null, null, attributes);
	}

	private String getCellValue(final Row row, final int i)
	{
		return row.getCell(i).getStringCellValue();
	}
}
