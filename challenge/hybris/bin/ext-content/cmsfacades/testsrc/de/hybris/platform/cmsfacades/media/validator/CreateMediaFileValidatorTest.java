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
import de.hybris.platform.cmsfacades.dto.MediaFileDto;

import java.io.IOException;
import java.io.InputStream;
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
public class CreateMediaFileValidatorTest
{
	private static final String MEDIA_MIME = "image/jpeg";
	private static final String INVALID = "invalid";

	@Mock
	private Predicate<String> validFileTypePredicate;
	@InjectMocks
	private CreateMediaFileValidator validator;

	private MediaFileDto mediaFileDto;
	private Errors errors;
	private InputStream inputStream;

	@Before
	public void setUp()
	{
		inputStream = new InputStreamStub();
		mediaFileDto = new MediaFileDto();
		mediaFileDto.setInputStream(inputStream);
		mediaFileDto.setMime(MEDIA_MIME);

		errors = new BeanPropertyBindingResult(mediaFileDto, mediaFileDto.getClass().getSimpleName());

		when(validFileTypePredicate.test(MEDIA_MIME)).thenReturn(Boolean.TRUE);
		when(validFileTypePredicate.test(INVALID)).thenReturn(Boolean.FALSE);
	}

	@Test
	public void shouldPassAllValidations()
	{
		validator.validate(mediaFileDto, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void shouldFailFileClosed() throws IOException
	{
		inputStream.close();

		validator.validate(mediaFileDto, errors);

		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.MEDIA_INPUT_STREAM_CLOSED, CreateMediaFileValidator.INPUT_STREAM);
	}

	@Test
	public void shouldFailFileNull()
	{
		mediaFileDto.setInputStream(null);

		validator.validate(mediaFileDto, errors);

		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_REQUIRED, CreateMediaFileValidator.INPUT_STREAM);
	}

	@Test
	public void shouldFailAllValidations()
	{
		mediaFileDto.setInputStream(null);
		mediaFileDto.setMime(INVALID);

		validator.validate(mediaFileDto, errors);

		assertTrue(errors.hasErrors());
		assertEquals(2, errors.getFieldErrorCount());
		assertFieldError(CmsfacadesConstants.FIELD_REQUIRED, CreateMediaFileValidator.INPUT_STREAM);
		assertFieldError(CmsfacadesConstants.FIELD_FORMAT_INVALID, CreateMediaFileValidator.MIME);
	}

	protected void assertFieldError(final String expectedError, final String errorCode)
	{
		assertEquals(expectedError, errors.getFieldError(errorCode).getCode());
	}

	private class InputStreamStub extends InputStream
	{
		boolean isClosed = false;

		@Override
		public int read() throws IOException
		{
			return 0;
		}

		@Override
		public int available() throws IOException
		{
			if (isClosed)
			{
				throw new IOException();
			}
			return 0;
		}

		@Override
		public void close() throws IOException
		{
			isClosed = true;
		}
	}
}