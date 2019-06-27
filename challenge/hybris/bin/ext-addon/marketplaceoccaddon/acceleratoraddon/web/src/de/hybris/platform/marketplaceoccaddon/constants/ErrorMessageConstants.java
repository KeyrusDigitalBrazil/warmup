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
package de.hybris.platform.marketplaceoccaddon.constants;

/**
 * Class for error message constants.
 */
public final class ErrorMessageConstants
{

	public static final String UNSUPPORTED_FILE_TYPE = "Unsupported file type";
	public static final String SIZE_EXCEED_LIMIT = "File size limit exceeded";
	public static final String UNZIP_FILE_ERROR = "File unzipping error";
	public static final String UNZIP_FILE_NAME_FORMAT_INVALID = "File with invalid name format in the zip";
	public static final String PROCESS_FILE_ERROR = "File processing error";
	public static final String FILE_NOT_EXIST = "File does not exist";
	public static final String COMPRESS_FILE_ERROR = "Files compression error";


	private ErrorMessageConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
