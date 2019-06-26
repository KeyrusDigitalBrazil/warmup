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
package com.hybris.backoffice.excel.validators.util;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


public class ExcelValidationResultUtilTest
{

	@Test
	public void shouldCreateHeaderWhenHeaderIsNull()
	{
		// given
		final ValidationMessage validationMessage = new ValidationMessage("Value cannot be blank");
		final ExcelValidationResult excelValidationResult = new ExcelValidationResult(validationMessage);

		// when
		ExcelValidationResultUtil.insertHeaderIfNeeded(excelValidationResult, 5, ProductModel._TYPECODE, ProductModel.CODE);

		// then
		assertThat(excelValidationResult.getValidationErrors()).hasSize(1);
		assertThat(excelValidationResult.getHeader()).isNotNull();
		assertThat(excelValidationResult.getHeader().getMessageKey()).isEqualTo("excel.import.validation.header.title");
	}

	@Test
	public void shouldNotCreateHeaderWhenHeaderExists()
	{
		// given
		final ValidationMessage validationMessage = new ValidationMessage("Value cannot be blank");
		final String expectedHeader = "Fake header";
		final ValidationMessage validationHeader = new ValidationMessage(expectedHeader);
		final ExcelValidationResult excelValidationResult = new ExcelValidationResult(validationMessage);
		excelValidationResult.setHeader(validationHeader);

		// when
		ExcelValidationResultUtil.insertHeaderIfNeeded(excelValidationResult, 5, ProductModel._TYPECODE, ProductModel.CODE);

		// then
		assertThat(excelValidationResult.getValidationErrors()).hasSize(1);
		assertThat(excelValidationResult.getHeader()).isNotNull();
		assertThat(excelValidationResult.getHeader().getMessageKey()).isEqualTo(expectedHeader);
	}

	@Test
	public void shouldPopulateMetadataAboutValidationResult()
	{
		// given
		final ValidationMessage firstValidationMessage = new ValidationMessage("Value cannot be blank");
		final ValidationMessage secondValidationMessage = new ValidationMessage("Value cannot be null");
		final ExcelValidationResult excelValidationResult = new ExcelValidationResult(
				Arrays.asList(firstValidationMessage, secondValidationMessage));

		// when
		ExcelValidationResultUtil.insertHeaderIfNeeded(excelValidationResult, 5, ProductModel._TYPECODE, ProductModel.CODE);

		// then
		assertThat(excelValidationResult.getValidationErrors()).hasSize(2);
		assertThat(excelValidationResult.getValidationErrors().stream()
				.map(error -> error.getMetadata(ExcelTemplateConstants.ValidationMessageMetadata.ROW_INDEX_KEY))
				.collect(Collectors.toList())).contains(5, 5);
		assertThat(excelValidationResult.getValidationErrors().stream()
				.map(error -> error.getMetadata(ExcelTemplateConstants.ValidationMessageMetadata.SHEET_NAME_KEY))
				.collect(Collectors.toList())).contains(ProductModel._TYPECODE, ProductModel._TYPECODE);
		assertThat(excelValidationResult.getValidationErrors().stream().map(
				error -> error.getMetadata(ExcelTemplateConstants.ValidationMessageMetadata.SELECTED_ATTRIBUTE_DISPLAYED_NAME_KEY))
				.collect(Collectors.toList())).contains(ProductModel.CODE, ProductModel.CODE);
	}

	@Test
	public void shouldMergeValidationResults()
	{
		// given
		final List<ExcelValidationResult> results = new ArrayList<>();
		results.add(prepareValidationResult("firstValidationMessage", 6, ProductModel._TYPECODE));
		results.add(prepareValidationResult("another validation message", 3, ProductModel._TYPECODE));
		results.add(prepareValidationResult("secondValidationMessage", 6, ProductModel._TYPECODE));
		results.add(prepareValidationResult("thirdValidationMessage", 6, ProductModel._TYPECODE));

		// when
		final List<ExcelValidationResult> mergedResults = ExcelValidationResultUtil.mergeValidationResults(results);

		// then
		assertThat(mergedResults).hasSize(2);
		assertThat(mergedResults.get(0).getValidationErrors()).hasSize(3);
		assertThat(mergedResults.get(0).getValidationErrors()).onProperty("messageKey").contains("firstValidationMessage",
				"secondValidationMessage", "thirdValidationMessage");
		assertThat(mergedResults.get(1).getValidationErrors()).hasSize(1);
	}

	@Test
	public void shouldPutValidationErrorsWithoutMetadataAtTheTopOfList()
	{
		// given
		final List<ExcelValidationResult> results = new ArrayList<>();
		results.add(prepareValidationResult("firstValidationMessage", 6, ProductModel._TYPECODE));
		results.add(prepareValidationResult("another validation message"));
		results.add(prepareValidationResult("secondValidationMessage", 6, ProductModel._TYPECODE));
		results.add(prepareValidationResult("one another validation message"));

		// when
		final List<ExcelValidationResult> mergedResults = ExcelValidationResultUtil.mergeValidationResults(results);

		// then
		assertThat(mergedResults).hasSize(3);
		assertThat(mergedResults.get(0).getValidationErrors()).hasSize(1);
		assertThat(mergedResults.get(0).getValidationErrors()).onProperty("messageKey").contains("another validation message");
		assertThat(mergedResults.get(1).getValidationErrors()).hasSize(1);
		assertThat(mergedResults.get(1).getValidationErrors()).onProperty("messageKey").contains("one another validation message");
		assertThat(mergedResults.get(2).getValidationErrors()).hasSize(2);
		assertThat(mergedResults.get(2).getValidationErrors()).onProperty("messageKey").contains("firstValidationMessage",
				"secondValidationMessage");
	}

	private ExcelValidationResult prepareValidationResult(final String message)
	{
		final ExcelValidationResult result = new ExcelValidationResult(new ValidationMessage(message));
		final ValidationMessage headerMessage = new ValidationMessage(StringUtils.EMPTY);
		result.setHeader(headerMessage);
		return result;
	}

	private ExcelValidationResult prepareValidationResult(final String message, final int rowIndex, final String typeCode)
	{
		final ExcelValidationResult result = new ExcelValidationResult(new ValidationMessage(message));
		final ValidationMessage headerMessage = new ValidationMessage(StringUtils.EMPTY);
		headerMessage.addMetadata(ExcelTemplateConstants.ValidationMessageMetadata.SHEET_NAME_KEY, typeCode);
		headerMessage.addMetadata(ExcelTemplateConstants.ValidationMessageMetadata.ROW_INDEX_KEY, rowIndex);
		result.setHeader(headerMessage);
		return result;
	}
}
