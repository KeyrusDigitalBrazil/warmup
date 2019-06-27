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
package com.hybris.backoffice.excel.validators.engine;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.validation.enums.Severity;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.model.constraints.ConstraintGroupModel;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.validation.services.impl.DefaultHybrisConstraintViolation;
import de.hybris.platform.validation.services.impl.LocalizedHybrisConstraintViolation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.daos.BackofficeValidationDao;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelBooleanValueConverter;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelMultiValueConverter;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelNullValueConverter;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelNumberValueConverter;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelStringValueConverter;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelValueConverter;
import com.hybris.backoffice.excel.validators.engine.converters.ExcelValueConverterRegistry;


@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractValidationEngineAwareStrategyTest
{

	@Mock
	protected TypeService typeService;

	@Mock
	protected ValidationService validationService;

	@Mock
	protected BackofficeValidationDao validationDao;

	@Spy
	protected ExcelValueConverterRegistry converterRegistry;

	@Before
	public void setup()
	{
		final List<ExcelValueConverter> converters = new ArrayList<>();
		converters.add(new ExcelBooleanValueConverter());
		converters.add(new ExcelMultiValueConverter());
		converters.add(new ExcelNullValueConverter());
		converters.add(new ExcelNumberValueConverter());
		converters.add(new ExcelStringValueConverter());
		converterRegistry.setConverters(converters);
		mockKnownModelClasses();
		mockConstraintGroups("default");
	}

	protected void mockKnownModelClasses()
	{
		final Class productModelClass = ProductModel.class;
		given(typeService.getModelClass("Product")).willReturn(productModelClass);
	}

	protected void mockConstraintGroups(final String... constraintGroups)
	{
		given(validationDao.getConstraintGroups(Arrays.asList(constraintGroups)))
				.willReturn(prepareConstraintsGroupModels(constraintGroups));
	}

	private Collection<ConstraintGroupModel> prepareConstraintsGroupModels(final String... constraintGroups)
	{
		final Collection<ConstraintGroupModel> models = new ArrayList<>();
		for (final String group : constraintGroups)
		{
			final ConstraintGroupModel model = new ConstraintGroupModel();
			model.setId(group);
			models.add(model);
		}
		return models;
	}

	protected void mockValidateValue(final String qualifier, final Set<HybrisConstraintViolation> violations)
	{
		given(validationService.validateValue(eq(ProductModel.class), eq(qualifier), any(), anyCollection()))
				.willReturn(violations);
	}

	protected HybrisConstraintViolation prepareConstraintViolation(final String message, final Severity severity)
	{
		final DefaultHybrisConstraintViolation violation = mock(DefaultHybrisConstraintViolation.class);
		given(violation.getLocalizedMessage()).willReturn(message);
		given(violation.getViolationSeverity()).willReturn(severity);
		return violation;
	}

	protected HybrisConstraintViolation prepareLocalizedConstraintViolation(final String message, final Severity severity,
			final Locale locale)
	{
		final LocalizedHybrisConstraintViolation violation = mock(LocalizedHybrisConstraintViolation.class);
		given(violation.getLocalizedMessage()).willReturn(message);
		given(violation.getViolationSeverity()).willReturn(severity);
		given(violation.getViolationLanguage()).willReturn(locale);
		return violation;
	}
}
