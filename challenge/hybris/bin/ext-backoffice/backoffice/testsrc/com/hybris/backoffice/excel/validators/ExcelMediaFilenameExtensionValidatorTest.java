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
package com.hybris.backoffice.excel.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.hybris.backoffice.excel.translators.AbstractExcelMediaImportTranslator;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


@RunWith(Parameterized.class)
public class ExcelMediaFilenameExtensionValidatorTest
{

	@Mock
	public ConfigurationService configurationService;
	@InjectMocks
	private final ExcelMediaFilenameExtensionValidator validator = new ExcelMediaFilenameExtensionValidator();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Before
	public void setUp()
	{
		final Configuration configuration = mock(Configuration.class);
		given(configuration.getString(ExcelMediaFilenameExtensionValidator.CONFIG_EXCEL_AVAILABLE_MEDIA_EXTENSIONS,
				StringUtils.EMPTY)).willReturn("jpg,PNG,gif, BMP,jpeg");
		given(configurationService.getConfiguration()).willReturn(configuration);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][]
		{ //
				{ //
						"lkjlkj.xml", //
						false //
				}, //
				{ //
						"lkj.jpg", //
						true //
				}, //
				{ //
						"rlk3ji.png", //
						true //
				}, //
				{ //
						"kjhj.bmp", //
						true //
				}, //
				{ //
						"lekjk.JPEG", //
						true //
				} //
		});
	}

	@Parameterized.Parameter(0)
	public String input;
	@Parameterized.Parameter(1)
	public boolean output;

	@Test
	public void should()
	{
		// given
		final Map<String, String> parameters = new HashMap<>();
		parameters.put(AbstractExcelMediaImportTranslator.PARAM_FILE_PATH, input);

		// when
		final List<ValidationMessage> validationMessages = validator.validateSingleValue(null, parameters);

		// then
		assertThat(validationMessages.isEmpty()).isEqualTo(output);
	}
}
