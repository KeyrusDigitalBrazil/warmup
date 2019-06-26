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
package com.hybris.backoffice.excel.validators.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.parser.RangeParserUtils;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelRangeClassificationFieldValidatorTest
{

	public static final HashMap<String, Object> ANY_CONTEXT = new HashMap<>();
	@Mock
	private ExcelNumberClassificationFieldValidator excelNumberClassificationFieldValidator;

	private final ExcelRangeClassificationFieldValidator validator = new ExcelRangeClassificationFieldValidator();

	@Before
	public void setUp()
	{
		validator.setValidators(Lists.newArrayList(excelNumberClassificationFieldValidator));
	}

	@Test
	public void shouldValidatorBeInvokedWhenInputIsRange()
	{
		// given
		final Map<String, String> param1 = new LinkedHashMap<>();
		param1.put(RangeParserUtils.prependFromPrefix(ImportParameters.RAW_VALUE), "2");
		final Map<String, String> param2 = new LinkedHashMap<>();
		param2.put(RangeParserUtils.prependToPrefix(ImportParameters.RAW_VALUE), "3");

		final List<Map<String, String>> params = Lists.newArrayList(param1, param2);

		final ImportParameters importParameters = new ImportParameters(null, null, "RANGE[2;3]", null, params);

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertTrue(output);
	}

	@Test
	public void shouldValidatorNotBeHandledWhenInputIsMultivaluedRange()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "RANGE[2;3];RANGE[2;4]", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidatorNotBeHandledWhenInputIsNotRange()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "XXX", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidatorDelegateToOtherValidators()
	{
		// given
		final Map<String, String> param1 = new LinkedHashMap<>();
		param1.put(RangeParserUtils.prependFromPrefix(ImportParameters.RAW_VALUE), "2");
		final Map<String, String> param2 = new LinkedHashMap<>();
		param2.put(RangeParserUtils.prependToPrefix(ImportParameters.RAW_VALUE), "3");

		final List<Map<String, String>> params = Lists.newArrayList(param1, param2);

		final ImportParameters importParameters = new ImportParameters(null, null, "RANGE[2;3]", null, params);

		given(excelNumberClassificationFieldValidator.canHandle(any(), any())).willReturn(true);
		given(excelNumberClassificationFieldValidator.validate(any(), any(), any())).willReturn(ExcelValidationResult.SUCCESS);

		// when
		validator.validate(mockExcelAttribute(), importParameters, ANY_CONTEXT);

		// then
		then(excelNumberClassificationFieldValidator).should(times(2)).validate(any(), any(), any());
	}

	private ExcelClassificationAttribute mockExcelAttribute()
	{
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);
		given(assignmentModel.getRange()).willReturn(true);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);
		return attribute;
	}

}
