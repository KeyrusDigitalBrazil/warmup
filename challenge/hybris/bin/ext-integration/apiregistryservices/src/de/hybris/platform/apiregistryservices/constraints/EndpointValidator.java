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
package de.hybris.platform.apiregistryservices.constraints;

import de.hybris.platform.apiregistryservices.model.EndpointModel;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validates if one of fields: the SpecUrl and the SpecData, is not empty of the given instance of
 * {@link de.hybris.platform.apiregistryservices.model.EndpointModel}.
 */
public class EndpointValidator implements ConstraintValidator<EndpointValid, EndpointModel>
{
    @Override
    public void initialize(final EndpointValid endpoint)
    {
        //empty
    }

    @Override
    public boolean isValid(final EndpointModel endpoint, final ConstraintValidatorContext validatorContext)
    {
        return StringUtils.isNotBlank(endpoint.getSpecUrl()) || StringUtils.isNotBlank(endpoint.getSpecData());
    }
}
