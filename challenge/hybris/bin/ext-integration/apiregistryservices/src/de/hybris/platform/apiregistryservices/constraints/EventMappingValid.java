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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;


/**
 * The value of EventConfiguration.exportFlag should be set to "false" OR In case of mappingType = GENERIC values of the
 * EventConfiguration.mappingConfiguration should reference to existing Event Classes and theirs attributes; In case of
 * mappingType = BEAN value of converterBean should refer to spring bean with Converter interface. Supported type is
 * {@link de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel} and its sub types.
 */
@Target(
{ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = EventMappingValidValidator.class)
@Documented
public @interface EventMappingValid
{
	String keyRegexp() default "";

	Pattern.Flag[] keyFlags() default {Pattern.Flag.CASE_INSENSITIVE};

	String valueRegexp() default "";

	Pattern.Flag[] valueFlags() default {Pattern.Flag.CASE_INSENSITIVE};

	String message() default "{de.hybris.platform.apiregistryservices.constraints.EventMappingValid.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
