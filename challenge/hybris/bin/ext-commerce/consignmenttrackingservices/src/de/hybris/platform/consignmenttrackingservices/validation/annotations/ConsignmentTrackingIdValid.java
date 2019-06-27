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
package de.hybris.platform.consignmenttrackingservices.validation.annotations;


import de.hybris.platform.consignmenttrackingservices.validation.validators.ConsignmentTrackingIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Target(
{ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy =
{ ConsignmentTrackingIdValidator.class })
@Documented
public @interface ConsignmentTrackingIdValid
{
	String message() default "{de.hybris.platform.consignmenttrackingservices.validation.annotations.ConsignmentTrackingIdValid.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
