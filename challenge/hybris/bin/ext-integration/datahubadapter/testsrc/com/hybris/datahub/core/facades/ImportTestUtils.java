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

package com.hybris.datahub.core.facades;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import de.hybris.platform.dataimportcommons.facades.DataImportTestUtils;
import de.hybris.platform.dataimportcommons.facades.ErrorCode;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.servicelayer.impex.ImpExError;
import de.hybris.platform.servicelayer.impex.ImpExHeaderError;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Utilities to help with testing.
 */
public class ImportTestUtils
{
	private ImportTestUtils()
	{
		// not instantiable
	}

	/**
	 * Simulates an import error with the specified message.
	 *
	 * @param msg an error message for the error.
	 * @return an import error
	 */
	public static ImportError error(final String msg)
	{
		final ImportError err = mock(ImportError.class);
		doReturn(msg).when(err).getMessage();
		return err;
	}

	/**
	 * Simulates an import error with the specified error code and the message.
	 *
	 * @param code an error code to use for the error.
	 * @param msg an error message for the error.
	 * @return an import error
	 */
	public static ImportError error(final ErrorCode code, final String msg)
	{
		final ImportError err = error(msg);
		doReturn(code).when(err).getCode();
		return err;
	}

	/**
	 * Simulates an import error for each message submitted.
	 *
	 * @param messages an array of messages to convert to import errors
	 * @return a collection of import errors with the specified messages.
	 */
	public static Collection<ImportError> errors(final String... messages)
	{
		final List<ImportError> errors = new ArrayList<>(messages.length);
		for (final String msg : messages)
		{
			errors.add(error(msg));
		}
		return errors;
	}

	/**
	 * Simulates an impex error with the specified error message.
	 *
	 * @param msg an error message for the error.
	 * @return an ImpExError
	 */
	public static ImpExError impExError(final String msg)
	{
		final ImpExError error = mock(ImpExHeaderError.class);
		doReturn(msg).when(error).getErrorMessage();
		return error;
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and finishes stubbing to simulate success import result.
	 *
	 * @param result a result mock to stub.
	 */
	public static void makeResultSuccessful(final ImportResult result)
	{
		doReturn(true).when(result).isSuccessful();
		doReturn(false).when(result).isError();
		doReturn(false).when(result).hasUnresolvedLines();
	}

	/**
	 * Mocks an import result with errors present in the error log.
	 *
	 * @param impexFileLoc location of the impex source file being imported.
	 * @param errLogText content of the error log to mock in the result.
	 * @return the mocked result
	 */
	public static ImportResult importResultWithLogErrors(final String impexFileLoc, final String errLogText)
	{
		final ImportResult res = DataImportTestUtils.importResult(impexFileLoc);
		makeResultWithErrors(res, errLogText, null, null);
		return res;
	}

	/**
	 * Mocks an import result with errors present in the unresolved lines. Whenever reference violations are reported in the
	 * import result, the error log contains a generic error about reference violation. Therefore the error log content should
	 * passed too.
	 *
	 * @param impexFileLoc location of the impex source file being imported.
	 * @param errLogText content of the error log to mock in the result.
	 * @param mediaStream content of the unresolved lines to mock in the result.
	 * @return the mocked result
	 */
	public static ImportResult importResultWithUnresolvedLineErrors(final String impexFileLoc, final String errLogText,
			final InputStream mediaStream)
	{
		final ImportResult res = DataImportTestUtils.importResult(impexFileLoc);
		makeResultWithErrors(res, errLogText, null, mediaStream);
		return res;
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and finishes stubbing to simulate errors present in the result.
	 *
	 * @param res a mock of the <code>ImportResult</code> to finish stubbing.
	 * @param errLogText content of the error log to mock in the import result.
	 */
	public static void makeResultWithErrors(final ImportResult res, final String errLogText)
	{
		makeResultWithErrors(res, errLogText, null, null);
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and finishes stubbing to simulate errors present in the result.
	 *
	 * @param res a mock of the <code>ImportResult</code> to finish stubbing.
	 * @param errLogText content of the error log to mock in the import result.
	 * @param mediaService the mediaService
	 * @param mediaStream c
	 * should be simulated.
	 */
	public static void makeResultWithErrors(final ImportResult res, final String errLogText, final MediaService mediaService, final InputStream mediaStream)
	{
		doReturn(false).when(res).isSuccessful();
		doReturn(true).when(res).isError();
		doReturn(mediaStream != null).when(res).hasUnresolvedLines();

		final ImpExImportCronJobModel job = res.getCronJob();
		doReturn(errLogText).when(job).getLogText();

		final ImpExMediaModel media = job.getWorkMedia();
		doReturn(media).when(res).getUnresolvedLines();

		if (mediaService != null)
		{
			doReturn(mediaStream).when(mediaService).getStreamFromMedia(media);
		}
	}

	/**
	 * Takes mock of an <code>ImportResult</code> and throws an IOException when calling the mediaService
	 *
	 * @param res a mock of the <code>ImportResult</code> to finish stubbing.
	 * @param mediaService a mock of the <code>ImportResult</code> to finish stubbing.
	 */
	public static void makeResultWithMediaServiceException(final ImportResult res, final MediaService mediaService)
	{
		doReturn(false).when(res).isSuccessful();
		doReturn(true).when(res).isError();
		doReturn(true).when(res).hasUnresolvedLines();

		final ImpExImportCronJobModel job = res.getCronJob();

		final ImpExMediaModel media = job.getWorkMedia();
		doReturn(media).when(res).getUnresolvedLines();

		doThrow(IOException.class).when(mediaService).getStreamFromMedia(media);
	}

	/**
	 * Converts separate strings into a multi-line text, where each string becomes a separate line.
	 *
	 * @param lines lines of text to merge together. Pass empty strings for empty (blank) lines in the resulting text.
	 * @return text consisting of the lines combined together.
	 */
	public static String toText(final String... lines)
	{
		return StringUtils.join(lines, System.lineSeparator());
	}

	public static InputStream toInputStream(final String... lines)
	{
		final String test = toText(lines);
		return IOUtils.toInputStream(test);
	}
}
