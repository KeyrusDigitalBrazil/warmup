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
package de.hybris.platform.cms2.cmsitems.service.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StringSortStatementFormatterTest
{
	@InjectMocks
	private StringSortStatementFormatter stringFormatter;

	@Mock
	AttributeDescriptorModel attributeDescriptor;
	@Mock
	private TypeModel attributeType;

	@Test
	public void shouldBeApplicableWithFullClassName()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn("java.lang.String");

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertThat(result, is(true));
	}

	@Test
	public void shouldNotBeApplicableWithClassSimpleName()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn("String");

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertThat(result, is(false));
	}

	@Test
	public void shouldNotBeApplicableWithInvalidClassName()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn("INVALID");

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertThat(result, is(false));
	}

}
