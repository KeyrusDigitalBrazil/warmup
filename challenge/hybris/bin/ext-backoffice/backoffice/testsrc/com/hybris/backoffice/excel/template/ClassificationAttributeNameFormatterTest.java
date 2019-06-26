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
package com.hybris.backoffice.excel.template;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.template.populator.DefaultExcelAttributeContext;
import com.hybris.backoffice.excel.template.populator.appender.ExcelMarkAppender;
import com.hybris.backoffice.excel.template.populator.extractor.ClassificationFullNameExtractor;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationAttributeNameFormatterTest
{
	@Mock
	ClassificationFullNameExtractor mockedClassificationFullNameExtractor;
	@InjectMocks
	ClassificationAttributeNameFormatter classificationAttributeNameFormatter;

	@Test
	public void shouldFormatAttributeNameWithExtractor()
	{
		// given
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		given(mockedClassificationFullNameExtractor.extract(excelClassificationAttribute)).willReturn("extractedFullName");

		// when
		final String result = classificationAttributeNameFormatter
				.format(DefaultExcelAttributeContext.ofExcelAttribute(excelClassificationAttribute));

		// then
		assertThat(result).isEqualTo("extractedFullName");
	}

	@Test
	public void shouldApplyAppenders()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		given(mockedClassificationFullNameExtractor.extract(excelAttribute)).willReturn("fullName");

		ExcelMarkAppender<ExcelClassificationAttribute> firstAppender = mock(ExcelMarkAppender.class);
		ExcelMarkAppender<ExcelClassificationAttribute> secondAppender = mock(ExcelMarkAppender.class);
		given(firstAppender.apply(any(), any())).willReturn("fullNameFirstAppender");
		given(secondAppender.apply(any(), any())).willReturn("fullNameFirstAppenderSecondAppender");
		classificationAttributeNameFormatter.setAppenders(Arrays.asList(firstAppender, secondAppender));

		// when
		final String result = classificationAttributeNameFormatter
				.format(DefaultExcelAttributeContext.ofExcelAttribute(excelAttribute));

		// then
		assertThat(result).isEqualTo("fullNameFirstAppenderSecondAppender");
		then(firstAppender).should().apply("fullName", excelAttribute);
		then(secondAppender).should().apply("fullNameFirstAppender", excelAttribute);
	}
}
