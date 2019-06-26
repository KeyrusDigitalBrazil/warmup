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
package de.hybris.platform.dataimportcommons.facades;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.util.MediaUtil;


public class DataImportTestUtils
{
	private DataImportTestUtils()
	{
		// not instantiable
	}

	/**
	 * Simulates an import error with the specified message.
	 *
	 * @param msg an error message for the error.
	 * @return an import error
	 */
	public static DataImportError error(final String msg)
	{
		final DataImportError err = mock(DataImportError.class);
		doReturn(msg).when(err).getMessage();
		return err;
	}

	/**
	 * Simulates an import error for each message submitted.
	 *
	 * @param messages an array of messages to convert to import errors
	 * @return a collection of import errors with the specified messages.
	 */
	public static Collection<DataImportError> errors(final String... messages)
	{
		final List<DataImportError> errors = new ArrayList<>(messages.length);
		for (final String msg : messages)
		{
			errors.add(error(msg));
		}
		return errors;
	}

	/**
	 * Mocks a successful import result.
	 *
	 * @return the mocked result.
	 */
	public static ImportResult successfulImportResult()
	{
		final ImportResult result = importResult(null);
		doReturn(true).when(result).isSuccessful();
		doReturn(false).when(result).isError();
		doReturn(false).when(result).hasUnresolvedLines();
		return result;
	}

	/**
	 * Mocks an import result, which is wired up with the CronJobModel and WorkMedia. The result is neither successful
	 * nor erroneous and should be further mocked for that purpose.
	 *
	 * @param impexFileLoc path to the source of the impex file being imported, which is relative to the hybris server data
	 *                     directory.
	 * @return the mocked result.
	 */
	public static ImportResult importResult(final String impexFileLoc)
	{
		final ImpExMediaModel media = mock(ImpExMediaModel.class);
		doReturn(impexFileLoc).when(media).getLocation();
		doReturn(MediaUtil.URL_HAS_DATA).when(media).getInternalURL();

		final ItemModelContext itemModelContext = mock(ItemModelContext.class);
		doReturn(itemModelContext).when(media).getItemModelContext();

		final ImpExImportCronJobModel job = mock(ImpExImportCronJobModel.class);
		doReturn(media).when(job).getWorkMedia();

		final ImportResult res = mock(ImportResult.class);
		doReturn(job).when(res).getCronJob();
		doReturn(media).when(res).getUnresolvedLines();

		final String tenantId = "master";
		doReturn(tenantId).when(job).getTenantId();

		return res;
	}
}
