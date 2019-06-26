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
package de.hybris.platform.odata2services.odata.persistence;

public class MissingLanguageException extends InvalidDataException
{
	private static final String ERROR_CODE = "missing_language";

	public MissingLanguageException(final String localizedEntityTypeName)
	{
		super( ERROR_CODE, String.format("%s language is a required attribute. Please resubmit and make sure that each %s contains the language attribute.", localizedEntityTypeName, localizedEntityTypeName));
	}
}
