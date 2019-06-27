/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata;

import java.util.Collection;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

/**
 * An EDMX schema element that may have OData annotations.
 */
public interface ODataAnnotatable
{
	Collection<String> getAnnotationNames();

	Optional<AnnotationAttribute> getAnnotation(String name);
}
