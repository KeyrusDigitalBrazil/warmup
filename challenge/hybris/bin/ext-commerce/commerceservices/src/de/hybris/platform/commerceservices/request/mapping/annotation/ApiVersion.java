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
package de.hybris.platform.commerceservices.request.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation can be used for controllers. It allows restrict visibility of methods annotated with
 * {@code @RequestMapping} only to selected version of commerce web services (e.g. v1 or v2).
 */
@Target(
{ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion
{
	/**
    * Returns API version for which methods from controller should be registered (e.g. v1).
    */
	String value();
}
