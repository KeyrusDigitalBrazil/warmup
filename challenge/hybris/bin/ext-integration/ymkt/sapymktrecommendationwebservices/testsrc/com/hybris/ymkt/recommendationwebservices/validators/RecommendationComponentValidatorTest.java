/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.recommendationwebservices.validators;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrorsProvider;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.hybris.ymkt.recommendation.model.CMSSAPRecommendationComponentModel;


@UnitTest
public class RecommendationComponentValidatorTest
{
	@Mock
	private AbstractCMSComponentModel model;

	@Spy
	private RecommendationComponentValidator validator;

	@Mock
	private DefaultValidationErrorsProvider validationErrorsProvider;

	@Before
	public void setup() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		validator.setValidationErrorsProvider(validationErrorsProvider);

		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.RECOTYPE)).thenReturn("1");
		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.LEADINGITEMTYPE)).thenReturn("1");
		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.LEADINGITEMDSTYPE)).thenReturn("1");
		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.CARTITEMDSTYPE)).thenReturn("1");
	}

	@Test
	public void testAddValidatorRule() throws IOException
	{
		//Test that a new rule is added
		final ValidationErrors validationErrors = new DefaultValidationErrors();

		Mockito.when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);

		validator.addValidatorRule("someField", "type.some.error.code");

		Assert.assertEquals(1, validationErrors.getValidationErrors().size());
		Assert.assertEquals("someField", validationErrors.getValidationErrors().get(0).getField());
	}

	@Test
	public void testValidate() throws IOException
	{
		Mockito.doNothing().when(validator).addValidatorRule(anyString(), anyString());

		//Test that validate() triggers 0 calls to addValidatorRule()
		validator.validate(model);
		Mockito.verify(validator, times(0)).addValidatorRule(anyString(), anyString());

		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.RECOTYPE)).thenReturn("");
		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.LEADINGITEMTYPE)).thenReturn("");
		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.LEADINGITEMDSTYPE)).thenReturn("");
		Mockito.when(model.getProperty(CMSSAPRecommendationComponentModel.CARTITEMDSTYPE)).thenReturn("");

		//Test that validate() triggers 4 calls to addValidatorRule()
		validator.validate(model);
		Mockito.verify(validator, times(4)).addValidatorRule(anyString(), anyString());
	}
}
