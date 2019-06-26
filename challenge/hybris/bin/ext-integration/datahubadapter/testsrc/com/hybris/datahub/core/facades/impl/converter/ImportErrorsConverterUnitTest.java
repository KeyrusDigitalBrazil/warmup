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

package com.hybris.datahub.core.facades.impl.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.dataimportcommons.facades.impl.converter.ErrorLimitExceededException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.impex.ImpExHeaderError;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import com.hybris.datahub.core.facades.ItemImportResult;

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
public class ImportErrorsConverterUnitTest
{
	private static final int ERROR_LIMIT = 1;

	@InjectMocks
	private final ImportErrorsConverter converter = new ImportErrorsConverter();
	@Mock
	private ImportService importService;

	@Before
	public void setup()
	{
		converter.setErrorLimit(ERROR_LIMIT);
	}

	@Test
	public void testConvertedResultIsErrorWhenImportServiceReturnsErrors()
	{
		final ItemImportResult result = (ItemImportResult) converter.convert(importResultWithErrors());

		verify(importService).collectImportErrors(any(ImportResult.class));
		assertThat(result.isSuccessful()).isFalse();
		assertThat(result.getErrors()).hasSize(1);
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
}
