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
package de.hybris.platform.cmswebservices.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Annotation for securing rest endpoints. <br>
 * Users who are part of <b>cmsmanagergroup</b> are able to access all resources.
 */
@Target(
		{ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_BASECMSMANAGERGROUP') or hasRole('ROLE_ADMINGROUP')")
public @interface IsAuthorizedCmsManager
{
	//empty
}