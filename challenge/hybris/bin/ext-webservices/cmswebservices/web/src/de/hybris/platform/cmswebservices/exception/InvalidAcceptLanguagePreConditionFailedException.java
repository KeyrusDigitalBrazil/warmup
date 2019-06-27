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
package de.hybris.platform.cmswebservices.exception;

/**
 * Thrown when a PUT or POST request has been placed with multiple ACCEPT-LANGUAGES.
 */
public class InvalidAcceptLanguagePreConditionFailedException extends RuntimeException {

	private static final long serialVersionUID = -3729553926413250731L;

	public InvalidAcceptLanguagePreConditionFailedException(final String message)
	{
		super(message);
	}

	public InvalidAcceptLanguagePreConditionFailedException(final Throwable cause) {
		super(cause);
	}

	public InvalidAcceptLanguagePreConditionFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}
}