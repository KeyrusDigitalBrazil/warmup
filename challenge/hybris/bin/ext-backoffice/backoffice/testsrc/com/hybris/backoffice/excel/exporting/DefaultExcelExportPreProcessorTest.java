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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelExportParams;
import com.hybris.backoffice.excel.data.SelectedAttribute;


public class DefaultExcelExportPreProcessorTest
{
	DefaultExcelExportPreProcessor excelExportPreProcessor;

	@Before
	public void setUp()
	{
		excelExportPreProcessor = new DefaultExcelExportPreProcessor();
	}

	@Test
	public void shouldAccumulateProcessedExcelExportParams()
	{
		// given
		final ExcelExportParamsDecorator selectedAttributesAppendingDecorator = createADecorator(params -> {
			params.getSelectedAttributes().add(mock(SelectedAttribute.class));
			return params;
		});
		final ExcelExportParamsDecorator additionalAttributesAppendingDecorator = createADecorator(params -> {
			params.getAdditionalAttributes().add(mock(ExcelAttribute.class));
			return params;
		});
		excelExportPreProcessor
				.setDecorators(Arrays.asList(selectedAttributesAppendingDecorator, additionalAttributesAppendingDecorator));

		// when
		final ExcelExportParams result = excelExportPreProcessor
				.process(new ExcelExportParams(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSelectedAttributes()).hasSize(1);
		assertThat(result.getAdditionalAttributes()).hasSize(1);
	}

	@Test
	public void shouldRespectOrderOfInjectedDecorators()
	{
		// given
		final ExcelExportParamsDecorator leastImportantDecorator = createADecorator(0);
		final ExcelExportParamsDecorator mostImportantDecorator = createADecorator(100);
		final ExcelExportParamsDecorator notSoImportantDecorator = createADecorator(50);
		final List<ExcelExportParamsDecorator> decorators = Arrays.asList(leastImportantDecorator, mostImportantDecorator,
				notSoImportantDecorator);

		// when
		excelExportPreProcessor.setDecorators(decorators);

		// then
		assertThat(excelExportPreProcessor.getDecorators()) //
				.containsExactly(leastImportantDecorator, notSoImportantDecorator, mostImportantDecorator);
	}

	ExcelExportParamsDecorator createADecorator(final UnaryOperator<ExcelExportParams> decorationFunc)
	{
		final int defaultOrder = 0;
		return createADecorator(decorationFunc, defaultOrder);
	}

	ExcelExportParamsDecorator createADecorator(final int order)
	{
		final UnaryOperator<ExcelExportParams> exceptionThrowingDecorationFunc = params -> {
			throw new AssertionError("expected decorate method not to be called");
		};
		return createADecorator(exceptionThrowingDecorationFunc, order);
	}

	ExcelExportParamsDecorator createADecorator(final UnaryOperator<ExcelExportParams> decorationFunc, final int order)
	{
		return new ExcelExportParamsDecorator()
		{
			@Override
			public @Nonnull ExcelExportParams decorate(@Nonnull final ExcelExportParams excelExportParams)
			{
				return decorationFunc.apply(excelExportParams);
			}

			@Override
			public int getOrder()
			{
				return order;
			}

			@Override
			public String toString()
			{
				return "decorator{order=" + getOrder() + "}";
			}
		};
	}
}
