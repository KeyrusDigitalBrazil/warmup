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
package de.hybris.platform.odata2services.odata.persistence.hook;

/**
 * An exception that is thrown inside a {@link PostPersistHook} is translated to a {@code PostPersistHookException}
 */
public class PostPersistHookException extends PersistHookException
{
	private static final String POST_PERSIST_ERROR = "post_persist_error";

	public PostPersistHookException(final String message, final Throwable cause, final String integrationKey)
	{
		super(message, POST_PERSIST_ERROR, cause, integrationKey);
	}
}
