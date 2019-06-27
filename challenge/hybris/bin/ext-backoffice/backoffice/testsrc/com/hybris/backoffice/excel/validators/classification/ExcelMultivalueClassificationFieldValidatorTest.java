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
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMultivalueClassificationFieldValidatorTest
{

	private static final HashMap<String, Object> ANY_CONTEXT = new HashMap<>();
	@Mock
	private ExcelBooleanClassificationFieldValidator excelBooleanClassificationFieldValidator;

	private final ExcelMultivalueClassificationFieldValidator validator = new ExcelMultivalueClassificationFieldValidator();

	@Before
	public void setUp()
	{
		validator.setValidators(Lists.newArrayList(excelBooleanClassificationFieldValidator));
	}

	@Test
	public void shouldValidatorBeInvokedWhenInputIsMultivalued()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "X,X", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertTrue(output);
	}

	@Test
	public void shouldValidatorNotBeHandledWhenInputIsNotMultivalued()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "X", null, Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidatorDelegateToOtherValidators()
	{
		// given
		final Map<String, String> param = new HashMap<>();
		param.put(ImportParameters.RAW_VALUE, "TRUE");
		final List<Map<String, String>> params = Lists.newArrayList(param, param, param);

		final ImportParameters importParameters = new ImportParameters(null, null, "TRUE,FALSE,TRUE", null, params);

		given(excelBooleanClassificationFieldValidator.canHandle(any(), any())).willReturn(true);
		given(excelBooleanClassificationFieldValidator.validate(any(), any(), any())).willReturn(ExcelValidationResult.SUCCESS);

		// when
		validator.validate(mockExcelAttribute(), importParameters, ANY_CONTEXT);

		// then
		then(excelBooleanClassificationFieldValidator).should(times(3)).validate(any(), any(), any());
	}

	private ExcelClassificationAttribute mockExcelAttribute()
	{
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.BOOLEAN);
		given(assignmentModel.getMultiValued()).willReturn(true);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);
		return attribute;
	}

}
