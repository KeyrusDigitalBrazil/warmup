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
package de.hybris.platform.secaddon.controllers;

import de.hybris.platform.secaddon.data.ErrorData;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;


@ControllerAdvice
public class TicketingControllerAdvice
{
    @Resource
    private I18NService i18NService;
    @Resource
    private MessageSource messageSource;

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorData dataIntegrityViolationExceptionHandler(Exception ex)
    {
        ErrorData error = new ErrorData();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        return error;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorData handleInvalidRequest(MethodArgumentNotValidException exception)
    {
        ErrorData error = new ErrorData();
        error.setMessage(getMessage(exception));
        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        return error;
    }

    protected String getMessage(MethodArgumentNotValidException ex)
    {
        return messageSource.getMessage(getErrorCode(ex), null, i18NService.getCurrentLocale());
    }

    private String getErrorCode(MethodArgumentNotValidException ex)
    {
        return ex.getBindingResult().getAllErrors().stream().findFirst().get().getCode();
    }

}
