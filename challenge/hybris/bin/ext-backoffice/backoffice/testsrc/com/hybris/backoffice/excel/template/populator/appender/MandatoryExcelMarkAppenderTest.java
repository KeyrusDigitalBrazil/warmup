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
package com.hybris.backoffice.excel.template.populator.appender;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;


@RunWith(MockitoJUnitRunner.class)
public class MandatoryExcelMarkAppenderTest
{

	@Mock
	CommonI18NService commonI18NService;

	@InjectMocks
	MandatoryExcelMarkAppender appender = new MandatoryExcelMarkAppender();

	@Test
	public void shouldMarkNotBeAppendedWhenAttributeIsNotMandatory()
	{
		// given
		final String input = "Article Number";

		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.isMandatory()).willReturn(false);

		// when
		final String output = appender.apply(input, excelAttribute);

		// then
		assertThat(output).isEqualTo(input).doesNotContain(String.valueOf(ExcelTemplateConstants.SpecialMark.MANDATORY.getMark()));
	}

	@Test
	public void shouldMarkBeAppendedWhenAttributeIsMandatory()
	{
		// given
		final String input = "Article Number";

		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.isMandatory()).willReturn(true);

		// when
		final String output = appender.apply(input, excelAttribute);

		// then
		assertThat(output).isEqualTo("Article Number" + ExcelTemplateConstants.SpecialMark.MANDATORY.getMark());
	}

	@Test
	public void shouldMarkNotBeAppendedWhenLocaleIsDifferentFromCurrentLocale()
	{
		// given
		final String input = "Article Number";

		final String currentIsoCode = "en";
		final String attrIsoCode = "de";
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.isMandatory()).willReturn(true);
		given(excelAttribute.isLocalized()).willReturn(true);
		given(excelAttribute.getIsoCode()).willReturn(attrIsoCode);

		final LanguageModel languageModel = mock(LanguageModel.class);
		given(languageModel.getIsocode()).willReturn(currentIsoCode);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);

		// when
		final String output = appender.apply(input, excelAttribute);

		// then
		assertThat(output).isEqualTo(input).doesNotContain(String.valueOf(ExcelTemplateConstants.SpecialMark.MANDATORY.getMark()));
	}

	@Test
	public void shouldMarkBeAppendedWhenAttributeIsMandatoryAndLocalesAreTheSame()
	{
		// given
		final String input = "Article Number";

		final String currentIsoCode = "en";
		final String attrIsoCode = "en";
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.isMandatory()).willReturn(true);
		given(excelAttribute.isLocalized()).willReturn(true);
		given(excelAttribute.getIsoCode()).willReturn(attrIsoCode);

		final LanguageModel languageModel = mock(LanguageModel.class);
		given(languageModel.getIsocode()).willReturn(currentIsoCode);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);

		// when
		final String output = appender.apply(input, excelAttribute);

		// then
		assertThat(output).contains(String.valueOf(ExcelTemplateConstants.SpecialMark.MANDATORY.getMark()));
	}

	@Test
	public void shouldMarkBeNotAppendedWhenAttributeIsMandatoryAndDoesntHaveDefaultValue()
	{
		// given
		final String input = "Article Number";

		final ExcelAttributeDescriptorAttribute excelAttribute = mock(ExcelAttributeDescriptorAttribute.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(excelAttribute.isMandatory()).willReturn(true);
		given(excelAttribute.getAttributeDescriptorModel()).willReturn(attributeDescriptorModel);
		given(attributeDescriptorModel.getDefaultValue()).willReturn(null);

		// when
		final String output = appender.apply(input, excelAttribute);

		// then
		assertThat(output).contains(ExcelTemplateConstants.Mark.MANDATORY);
	}

}
