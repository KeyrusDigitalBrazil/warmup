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
package de.hybris.platform.cmsfacades.media.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.MediaData;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreateMediaValidatorTest
{
	private static final String MEDIA_CODE = "mouse123";
	private static final String MEDIA_DESC = "Wireless Mouse";
	private static final String MEDIA_ALT_TEXT = "Apple Wireless Mouse";
	private static final String INVALID = "invalid";

	@Mock
	private Predicate<String> validStringLengthPredicate;
	@Mock
	private Predicate<String> mediaCodeExistsPredicate;
	@InjectMocks
	private CreateMediaValidator validator;

	private MediaData mediaData;
	private Errors errors;

	@Before
	public void setUp()
	{
		mediaData = new MediaData();
		mediaData.setAltText(MEDIA_ALT_TEXT);
		mediaData.setCode(MEDIA_CODE);
		mediaData.setDescription(MEDIA_DESC);

		errors = new BeanPropertyBindingResult(mediaData, mediaData.getClass().getSimpleName());

		when(validStringLengthPredicate.test(MEDIA_CODE)).thenReturn(Boolean.TRUE);
		when(validStringLengthPredicate.test(MEDIA_DESC)).thenReturn(Boolean.TRUE);
		when(validStringLengthPredicate.test(MEDIA_ALT_TEXT)).thenReturn(Boolean.TRUE);
		when(validStringLengthPredicate.test(INVALID)).thenReturn(Boolean.FALSE);

		when(mediaCodeExistsPredicate.test(MEDIA_CODE)).thenReturn(Boolean.FALSE);
	}

	@Test
	public void shouldSupportClass()
	{
		final boolean result = validator.supports(MediaData.class);
		assertTrue(result);
	}

	@Test
	public void shouldNotSupportClass()
	{
		final boolean result = validator.supports(Object.class);
		assertFalse(result);
	}

	@Test
	public void shouldPassAllValidations()
	{
		validator.validate(mediaData, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void shouldFailCodeNotUnique()
	{
		when(mediaCodeExistsPredicate.test(MEDIA_CODE)).thenReturn(Boolean.TRUE);

		validator.validate(mediaData, errors);

		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_ALREADY_EXIST, CreateMediaValidator.CODE);
	}

	@Test
	public void shouldFailCodeTooLongAndNotUnique()
	{
		when(validStringLengthPredicate.test(MEDIA_CODE)).thenReturn(Boolean.FALSE);
		when(mediaCodeExistsPredicate.test(MEDIA_CODE)).thenReturn(Boolean.TRUE);

		validator.validate(mediaData, errors);

		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_LENGTH_EXCEEDED, CreateMediaValidator.CODE);
	}

	@Test
	public void shouldFailDescriptionTooLong()
	{
		when(validStringLengthPredicate.test(MEDIA_DESC)).thenReturn(Boolean.FALSE);

		validator.validate(mediaData, errors);

		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_LENGTH_EXCEEDED, CreateMediaValidator.DESCRIPTION);
	}

	@Test
	public void shouldFailNullCode()
	{
		mediaData.setCode(null);

		validator.validate(mediaData, errors);

		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_REQUIRED, CreateMediaValidator.CODE);
	}

	@Test
	public void shouldFailAllNullFieldsValidations()
	{
		mediaData.setAltText(null);
		mediaData.setCode(null);
		mediaData.setDescription(null);

		validator.validate(mediaData, errors);

		assertTrue(errors.hasErrors());
		assertEquals(3, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_REQUIRED, CreateMediaValidator.CODE);
		assertFieldError(CmsfacadesConstants.FIELD_REQUIRED, CreateMediaValidator.DESCRIPTION);
		assertFieldError(CmsfacadesConstants.FIELD_REQUIRED, CreateMediaValidator.ALT_TEXT);
	}

	protected void assertFieldError(final String expectedError, final String errorCode)
	{
		assertEquals(expectedError, errors.getFieldError(errorCode).getCode());
	}

}
