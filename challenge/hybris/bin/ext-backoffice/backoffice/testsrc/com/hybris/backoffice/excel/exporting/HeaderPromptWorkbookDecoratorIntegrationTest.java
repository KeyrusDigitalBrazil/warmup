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
package com.hybris.backoffice.excel.exporting;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.HeaderPrompt.HEADER_ATTR_DISPLAYED_NAME;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.HeaderPrompt.HEADER_REFERENCE_FORMAT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;

import com.hybris.backoffice.excel.ExcelIntegrationTest;
import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.ExcelTemplateService;


public class HeaderPromptWorkbookDecoratorIntegrationTest extends ExcelIntegrationTest
{

	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Resource
	ExcelTemplateService excelTemplateService;
	@Resource
	HeaderPromptWorkbookDecorator headerPromptWorkbookDecorator;

	@Test
	public void testDecoratorDataPreparing() throws Exception
	{
		try (final Workbook workbook = excelTemplateService.createWorkbook(loadResource("/excel/excelImpExMasterTemplate.xlsx")))
		{
			// given
			final ExcelExportResult result = prepareExcelExportResult(workbook);

			// when
			headerPromptWorkbookDecorator.decorate(result);

			// then
			final List<Row> rows = getRows(workbook);
			assertThat(rows).isNotNull();

			final String attributeWithReferenceFormat = "data_sheet";
			final String attributeLocalized = "description";
			final Function<String, List<Row>> converter = attribute -> rows.stream()
					.filter(row -> row.getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue().startsWith(attribute))//
					.collect(Collectors.toList());

			/*
			 * Product & Variant = 2
			 */
			soft.assertThat(converter.apply(attributeWithReferenceFormat).size()).isEqualTo(2);

			/*
			 * 'data_sheet' has reference format
			 */
			soft.assertThat(converter.apply(attributeWithReferenceFormat).get(0).getCell(HEADER_REFERENCE_FORMAT.getIndex())
					.getStringCellValue()).isNotEmpty();

			/*
			 * Localized pattern: name[lang]
			 */
			soft.assertThat(
					converter.apply(attributeLocalized).get(0).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.containsPattern(//
							Pattern.compile(String.format("%s\\[.*\\]", attributeLocalized))//
			);
		}
	}

	protected ExcelExportResult prepareExcelExportResult(final Workbook workbook)
	{
		final ProductModel productModel = prepareProduct("product", createCatalogVersionModel("catalog", "1.0"));
		final VariantTypeModel variantTypeModel = saveItem(prepareVariant());
		final ProductModel product = saveItem(prepareProductWithVariant(productModel, variantTypeModel));
		final ItemModel variant = saveItem(prepareVariantProductModel(product, variantTypeModel));
		workbook.createSheet(getModelService().getModelType(product));
		workbook.createSheet(getModelService().getModelType(variant));
		return new ExcelExportResult(workbook, Lists.newArrayList(product, variant), null, null, null);
	}

	protected List<Row> getRows(final Workbook workbook)
	{
		final Sheet headerPrompt = workbook.getSheet(ExcelTemplateConstants.UtilitySheet.HEADER_PROMPT.getSheetName());
		final int rowLastNum = headerPrompt.getLastRowNum();
		return IntStream.range(1, rowLastNum).mapToObj(headerPrompt::getRow).collect(Collectors.toList());
	}

	private InputStream loadResource(final String path)
	{
		return Optional.ofNullable(getClass().getResourceAsStream(path))
				.orElseThrow(() -> new AssertionError("Could not load resource: " + path));
	}

}
