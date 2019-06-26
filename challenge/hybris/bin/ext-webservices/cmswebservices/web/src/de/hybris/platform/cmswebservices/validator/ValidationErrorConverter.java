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
package de.hybris.platform.cmswebservices.validator;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.converters.AbstractLocalizedErrorConverter;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

/**
 * Validation Converter for {@link ValidationErrors} type. 
 */
public class ValidationErrorConverter extends AbstractLocalizedErrorConverter
{
	 private static final String TYPE = "ValidationError";
    private static final String SUBJECT_TYPE = "parameter";
    private static final String REASON_INVALID = "invalid";
    private static final String REASON_MISSING = "missing";
    
    private I18NService i18NService;

    public boolean supports(final Class clazz)
    {
        return ValidationErrors.class.isAssignableFrom(clazz);
    }

    public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList)
    {
        final ValidationErrors errors = (ValidationErrors) o;
        final Locale currentLocale = getI18NService().getCurrentLocale();

        webserviceErrorList.addAll(
                errors.getValidationErrors().stream().map(validationError -> {
                    ErrorWsDTO errorDto = this.createTargetElement();
                    errorDto.setType(TYPE);
                    errorDto.setSubjectType(SUBJECT_TYPE);
                    errorDto.setMessage(this.getMessage(validationError.getErrorCode(), validationError.getErrorArgs(), currentLocale, validationError.getDefaultMessage()));
                    errorDto.setSubject(validationError.getField());
                    errorDto.setReason(validationError.getRejectedValue() == null ? REASON_MISSING : REASON_INVALID);
                    errorDto.setLanguage(validationError.getLanguage());
                    errorDto.setPosition(validationError.getPosition());
                    errorDto.setExceptionMessage(validationError.getExceptionMessage());
                    errorDto.setErrorCode(validationError.getErrorCode());
                    return errorDto;
                }).collect(toList()));
    }

    protected I18NService getI18NService()
    {
        return i18NService;
    }

    @Required
    public void setI18NService(final I18NService i18NService)
    {
        this.i18NService = i18NService;
    }
}
