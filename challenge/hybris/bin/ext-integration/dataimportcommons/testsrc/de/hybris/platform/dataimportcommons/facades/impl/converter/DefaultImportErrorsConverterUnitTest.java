/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.dataimportcommons.facades.impl.converter;

import static de.hybris.platform.dataimportcommons.facades.DataImportTestUtils.successfulImportResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.platform.dataimportcommons.facades.DataItemImportResult;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.impex.ImpExHeaderError;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests how converter builds the import result and handles the import errors. The error parsing of the error log and
 * the unresolved lines reported in the import result is simulated for more comprehensive case coverage.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultImportErrorsConverterUnitTest
{
	private static final int ERROR_LIMIT = 1;
	private static final int UNLIMITED_ERROR = -1;

	@InjectMocks
	private final DefaultImportErrorsConverter converter = new DefaultImportErrorsConverter();
	@Mock
	private ImportService importService;

	@Before
	public void setup()
	{
		converter.setErrorLimit(ERROR_LIMIT);
	}

	@Test
	public void testConvertsNullImportResultToNullItemsImportResult()
	{
		final DataItemImportResult result = converter.convert(null);
		assertThat(result).isNull();
	}

	@Test
	public void testConvertsSuccessfulImportResult()
	{
		final DataItemImportResult result = converter.convert(successfulImportResult());

		assertThat(result).isNotNull();
		assertThat(result.getExportErrorDatas()).isEmpty();
	}

	@Test
	public void testDoesNotAttemptToCollectErrorsWhenResultIsSuccessful()
	{
		converter.convert(successfulImportResult());

		verify(importService, never()).collectImportErrors(any(ImportResult.class));
	}

	@Test
	public void testConvertedResultIsErrorWhenImportServiceReturnsErrors()
	{
		final DataItemImportResult result = converter.convert(importResultWithErrors());

		verify(importService).collectImportErrors(any(ImportResult.class));
		assertThat(result.getExportErrorDatas()).hasSize(1);
	}

	private ImportResult importResultWithErrors()
	{
		final ImportResult importResult = mock(ImportResult.class);
		doReturn(true).when(importResult).isError();
		doReturn(Stream.of(mock(ImpExHeaderError.class))).when(importService).collectImportErrors(importResult);
		return importResult;
	}

	@Test(expected = ErrorLimitExceededException.class)
	public void testThrowsExceptionWhenErrorLimitIsExceeded()
	{
		converter.convert(importResultWithErrorsExceedingLimit());
	}

	private ImportResult importResultWithErrorsExceedingLimit()
	{
		final ImportResult importResult = mock(ImportResult.class);
		doReturn(true).when(importResult).isError();
		doReturn(Stream.of(mock(ImpExHeaderError.class), mock(ImpExHeaderError.class))).when(importService).collectImportErrors(importResult);
		return importResult;
	}

	@Test
	public void testConverterForUnlimitedErrors()
	{
		converter.setErrorLimit(UNLIMITED_ERROR);
		final ImportResult importResult = mock(ImportResult.class);
		doReturn(true).when(importResult).isError();
		doReturn(Stream.of(mock(ImpExHeaderError.class), mock(ImpExHeaderError.class), mock(ImpExHeaderError.class)))
				.when(importService).collectImportErrors(importResult);

		final DataItemImportResult result = converter.convert(importResult);
		verify(importService).collectImportErrors(any(ImportResult.class));
		assertThat(result.getExportErrorDatas()).hasSize(3);
	}

	@Test
	public void testConverterIsLimitedErrorResultsWhenLimitEqualToZero()
	{
		converter.setErrorLimit(0);
		assertThat(converter.isLimited()).isTrue();
	}

	@Test
	public void testConverterIsLimitedErrorResultsWhenLimitGreaterThanZero()
	{
		converter.setErrorLimit(1);
		assertThat(converter.isLimited()).isTrue();
	}

	@Test
	public void testConverterIsUnlimitedErrorResults()
	{
		converter.setErrorLimit(UNLIMITED_ERROR);
		assertThat(converter.isLimited()).isFalse();
	}
}
