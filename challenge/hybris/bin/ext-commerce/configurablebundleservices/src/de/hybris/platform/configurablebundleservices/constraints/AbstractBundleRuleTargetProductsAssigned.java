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

package de.hybris.platform.configurablebundleservices.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * Validates that at least 1 target product is assigned to an {@link AbstractBundleRuleModel}.
 */
@Target(
{ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AbstractBundleRuleTargetProductsAssignedValidator.class)
@Documented
public @interface AbstractBundleRuleTargetProductsAssigned
{
	String message() default
			"{de.hybris.platform.configurablebundleservices.constraints.AbstractBundleRuleTargetProductsAssigned.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
