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
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

public class PropertyProcessorTestUtils
{
	static TypeAttributeDescriptor typeAttributeDescriptor(final boolean isCollection, final boolean hasPrimitiveElements)
	{
		final TypeAttributeDescriptor descriptor = collectionDescriptor(isCollection);

		when(descriptor.isPrimitive()).thenReturn(hasPrimitiveElements);
		return descriptor;
	}

	private static TypeAttributeDescriptor collectionDescriptor(final boolean isCollection)
	{
		final TypeAttributeDescriptor descriptor = mock(TypeAttributeDescriptor.class);
		when(descriptor.isCollection()).thenReturn(isCollection);
		return descriptor;
	}
}
