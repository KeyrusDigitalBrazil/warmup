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
package de.hybris.platform.odata2webservices.controller;

import static org.springframework.web.servlet.support.RequestContextUtils.getLocale;

import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataHttpException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.servlet.ODataExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.common.base.Strings;

@RestControllerAdvice
public class ControllerExceptionHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	private final ODataServiceFactory serviceFactory;
	private final Converter<ODataResponse, ResponseEntity<String>> oDataResponseToResponseEntityConverter;

	public ControllerExceptionHandler(final ODataServiceFactory factory, final Converter<ODataResponse, ResponseEntity<String>> converter)
	{
		serviceFactory = factory;
		oDataResponseToResponseEntityConverter = converter;
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handleException(final HttpServletRequest request, final Exception exception)
	{
		final ODataHttpException oDataHttpException = isODataHttpException(exception.getCause())
				? (ODataHttpException) exception.getCause()
				: new ODataBadRequestException(ODataBadRequestException.COMMON, exception);

		return handleODataHttpException(request, oDataHttpException);
	}

	@ExceptionHandler(value = ODataHttpException.class)
	public ResponseEntity<String> handleODataHttpException(final HttpServletRequest request, final ODataException exception)
	{
		LOG.error("Failed to handle {}", request.getRequestURI(), exception);

		final ODataExceptionWrapper exceptionWrapper = new ODataExceptionWrapper(request, serviceFactory);
		return oDataResponseToResponseEntityConverter.convert(exceptionWrapper.wrapInExceptionResponse(exception));
	}

	@ExceptionHandler(value = InvalidODataSchemaException.class)
	public ResponseEntity<String> handleInvalidODataSchemaException(final HttpServletRequest request, final InvalidODataSchemaException exception)
	{
		return handleODataHttpException(request, new ODataApplicationException(getMessage(exception), getLocale(request), HttpStatusCodes.BAD_REQUEST, exception.getCause()));
	}

	private static String getMessage(final Exception exception)
	{
		final StringBuilder msg = new StringBuilder(exception.getLocalizedMessage());
		if (exception.getCause() != null && !Strings.isNullOrEmpty(exception.getCause().getLocalizedMessage()))
		{
			msg.append("\n").append(exception.getCause().getLocalizedMessage());
		}
		return msg.toString();
	}

	private static boolean isODataHttpException(final Throwable throwable)
	{
		return throwable instanceof ODataHttpException;
	}
}
