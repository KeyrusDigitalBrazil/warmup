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
package com.hybris.backoffice.excel.importing;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import com.hybris.backoffice.excel.ExcelIntegrationTest;


public class ExcelImportServiceIntegrationTest extends ExcelIntegrationTest
{
	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Resource
	ExcelImportService excelImportService;

	@Resource(name = "excelImpexConverter")
	ImpexConverter impexConverter;

	@Resource
	ImportService importService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	@Test
	public void shouldImportExcelFile() throws IOException
	{
		// given
		// Excel sheet with the following structure:
		// catalogVersion*^	code*^	order
		// catalog:1.0		product	123
		saveItem(createCatalogVersionModel("catalog", "1.0"));
		final String queryForImportedProducts = "SELECT {product:PK} FROM {Product AS product} WHERE {product:code} like 'product'";
		try (final Workbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/test/excel/import.xlsx")))
		{
			// when
			final ImportResult importResult = //
					importService.importData( //
							createImportConfig( //
									impexConverter.convert( //
											excelImportService.convertToImpex(workbook))));

			// then
			assertThat(importResult).isNotNull();
			soft.assertThat(importResult.isFinished()).isTrue();
			soft.assertThat(importResult.isError()).isFalse();
			soft.assertThat(importResult.isSuccessful()).isTrue();
			final Collection<ProductModel> importedProducts = getResult(queryForImportedProducts);
			soft.assertThat(importedProducts).extracting("code").containsOnly("product");
			soft.assertThat(importedProducts).extracting("order").containsOnly(123);
		}
	}
	
	private <T> Collection<T> getResult(final String query)
	{
		return flexibleSearchService.<T> search(query).getResult();
	}
}
