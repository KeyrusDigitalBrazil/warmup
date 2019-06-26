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

package de.hybris.platform.integrationservices.model.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;

import org.junit.Test;

@UnitTest
public class PrimitiveTypeDescriptorUnitTest
{
	@Test
	public void testCreate()
	{
		final TypeDescriptor descriptor = PrimitiveTypeDescriptor.create(atomicType());
		assertThat(descriptor).isNotNull();
	}

	@Test
	public void testCreateWithNullModel()
	{
		assertThatThrownBy(() -> PrimitiveTypeDescriptor.create(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetTypeCode()
	{
		final PrimitiveTypeDescriptor descriptor = typeDescriptor("java.lang.Integer");
		assertThat(descriptor.getTypeCode()).isEqualTo("java.lang.Integer");
	}

	@Test
	public void testIsPrimitive()
	{
		final PrimitiveTypeDescriptor descriptor = typeDescriptor();
		assertThat(descriptor.isPrimitive()).isTrue();
	}

	@Test
	public void testGetAttributes()
	{
		final PrimitiveTypeDescriptor descriptor = typeDescriptor();
		assertThat(descriptor.getAttributes()).isEmpty();
	}

	@Test
	public void testGetAttributesDoesNotLeakMutability()
	{
		final PrimitiveTypeDescriptor descriptor = typeDescriptor();
		final Collection<TypeAttributeDescriptor> attributes = descriptor.getAttributes();
		attributes.add(mock(TypeAttributeDescriptor.class));

		assertThat(descriptor.getAttributes()).isEmpty();
	}

	@Test
	public void testGetAttribute()
	{
		final PrimitiveTypeDescriptor descriptor = typeDescriptor();
		assertThat(descriptor.getAttribute("someAttribute")).isEmpty();
	}

	@Test
	public void testEquals()
	{
		final PrimitiveTypeDescriptor sample = typeDescriptor("Integer");
		assertThat(sample)
				.isNotEqualTo(null)
				.isNotEqualTo(mock(TypeDescriptor.class))
				.isNotEqualTo(typeDescriptor("Float"))
				.isEqualTo(sample)
				.isEqualTo(typeDescriptor("Integer"));
	}

	@Test
	public void testHashCode()
	{
		final PrimitiveTypeDescriptor sample = typeDescriptor("Date");
		assertThat(sample.hashCode())
				.isNotEqualTo(mock(TypeDescriptor.class).hashCode())
				.isNotEqualTo(typeDescriptor("String").hashCode())
				.isEqualTo(typeDescriptor("Date").hashCode());
	}

	private PrimitiveTypeDescriptor typeDescriptor()
	{
		return typeDescriptor(atomicType());
	}

	private PrimitiveTypeDescriptor typeDescriptor(final String type)
	{
		return typeDescriptor(atomicType(type));
	}

	private PrimitiveTypeDescriptor typeDescriptor(final AtomicTypeModel model)
	{
		return new PrimitiveTypeDescriptor(model);
	}

	private AtomicTypeModel atomicType()
	{
		return atomicType(null);
	}

	private AtomicTypeModel atomicType(final String name)
	{
		final AtomicTypeModel model = mock(AtomicTypeModel.class);
		doReturn(name).when(model).getCode();
		return model;
	}
}