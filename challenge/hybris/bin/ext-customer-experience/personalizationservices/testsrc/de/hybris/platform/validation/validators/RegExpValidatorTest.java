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
package de.hybris.platform.validation.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.validation.annotations.RegExp;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

@UnitTest
public class RegExpValidatorTest
{
    private static String CORRECT_REGEXP = ".*";
    private static String INCORRECT_REGEXP = "[";

    protected RegExpValidator regExpValidator = new RegExpValidator();

    @Mock
    protected ConstraintValidatorContext validatorContext;

    @Mock
    protected RegExp regExpAnnotation;

	@Before
    public void init()
	{
        MockitoAnnotations.initMocks(this);
        when(regExpAnnotation.notEmpty()).thenReturn(false);
        regExpValidator.initialize(regExpAnnotation);
	}

    @Test
    public void testIsValid()
    {
        //when
        boolean isValid = regExpValidator.isValid(CORRECT_REGEXP,validatorContext);

        //then
        Assert.assertTrue(isValid);
    }

    @Test
    public void testIsValidForIncorrectRegexp()
    {
        //when
        boolean isValid = regExpValidator.isValid(INCORRECT_REGEXP,validatorContext);

        //then
        Assert.assertFalse(isValid);
    }

    @Test
    public void testIsValidForNull()
    {
        //when
        boolean isValid = regExpValidator.isValid(null,validatorContext);

        //then
        Assert.assertTrue(isValid);
    }

    @Test
	public void testIsValidForEmptyRegexp()
    {
        //when
        boolean isValid = regExpValidator.isValid("",validatorContext);

        //then
        Assert.assertTrue(isValid);
    }

    @Test
    public void testIsValidForBlankRegexp()
    {
        //when
        boolean isValid = regExpValidator.isValid("   ",validatorContext);

        //then
        Assert.assertTrue(isValid);
    }

    @Test
    public void testIsValidForNullWhenNotEmptyIsTrue()
    {
        //given
        when(regExpAnnotation.notEmpty()).thenReturn(true);
        regExpValidator.initialize(regExpAnnotation);

        //when
        boolean isValid = regExpValidator.isValid(null,validatorContext);

        //then
        Assert.assertFalse(isValid);
    }


    @Test
    public void testIsValidForEmptyRegexpWhenNotEmptyIsTrue()
    {
        //given
        when(regExpAnnotation.notEmpty()).thenReturn(true);
        regExpValidator.initialize(regExpAnnotation);

        //when
        boolean isValid = regExpValidator.isValid("",validatorContext);

        //then
        Assert.assertFalse(isValid);
    }

    @Test
    public void testIsValidForBlankRegexpWhenNotEmptyIsTrue()
    {
        //given
        when(regExpAnnotation.notEmpty()).thenReturn(true);
        regExpValidator.initialize(regExpAnnotation);

        //when
        boolean isValid = regExpValidator.isValid("    ",validatorContext);

        //then
        Assert.assertFalse(isValid);
    }
}
