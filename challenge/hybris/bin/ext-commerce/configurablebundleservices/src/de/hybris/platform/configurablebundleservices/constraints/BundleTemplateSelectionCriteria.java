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

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Triggers when {@link de.hybris.platform.configurablebundleservices.model.BundleTemplateModel#getChildTemplates()}
 * is not empty AND {@link de.hybris.platform.configurablebundleservices.model.BundleTemplateModel#getBundleSelectionCriteria()}
 * is not empty.
 */
@Target(
        { FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = BundleTemplateSelectionCriteriaValidator.class)
@Documented
public @interface BundleTemplateSelectionCriteria
{
    String message() default "{de.hybris.platform.configurablebundleservices.constraints.BundleTemplateSelectionCriteria.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
