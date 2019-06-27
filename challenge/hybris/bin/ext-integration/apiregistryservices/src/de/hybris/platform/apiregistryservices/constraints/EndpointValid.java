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

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation for {@link EndpointValidator}
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = EndpointValidator.class)
@Documented
public @interface EndpointValid
{
    String message() default "de.hybris.platform.apiregistryservices.constraints.Endpoint.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
