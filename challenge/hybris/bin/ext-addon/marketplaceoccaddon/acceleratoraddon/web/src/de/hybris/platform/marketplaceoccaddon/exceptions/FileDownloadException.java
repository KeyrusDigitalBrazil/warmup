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
package de.hybris.platform.marketplaceoccaddon.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


/**
 * Exception throw when process download file.
 */
public class FileDownloadException extends WebserviceException //NOSONAR
{
	private static final String TYPE = "FileDownloadError";
	private static final String SUBJECT_TYPE = "FileDownload";


	public FileDownloadException(final String message)
	{
		super(message);
	}

	public FileDownloadException(final String message, final String reason)
	{
		super(message, reason);
	}

	public FileDownloadException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public FileDownloadException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public FileDownloadException(final String message, final String reason, final String subject, final Throwable cause)
	{
		super(message, reason, subject, cause);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}

}
