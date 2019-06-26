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

import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * {@inheritDoc}
 * <p>This implementation is effectively immutable and therefore is thread safe</p>
 * <p>Reuse this implementation through composition not inheritance</p>
 */
public class PrimitiveTypeDescriptor implements TypeDescriptor
{
	private final AtomicTypeModel typeModel;

	PrimitiveTypeDescriptor(final AtomicTypeModel type)
	{
		Preconditions.checkArgument(type != null, "Non-null atomic type model must be provided");
		typeModel = type;
	}

	/**
	 * Creates an instance of this type descriptor
	 * @param model a model to get a {@code PrimitiveTypeDescriptor} for
	 * @return a descriptor for the given model.
	 * <p>Note: the implementation does not guarantee creation of a new instance for every invocation. The implementation may
	 * change to cache the values for efficiency.</p>
	 */
	public static TypeDescriptor create(final AtomicTypeModel model)
	{
		return new PrimitiveTypeDescriptor(model);
	}

	@Override
	public String getTypeCode()
	{
		return typeModel.getCode();
	}

	@Override
	public Optional<TypeAttributeDescriptor> getAttribute(final String attrName)
	{
		return Optional.empty();
	}

	@Override
	public Collection<TypeAttributeDescriptor> getAttributes()
	{
		return new ArrayList<>(0);
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && getClass() == o.getClass())
		{
			final PrimitiveTypeDescriptor that = (PrimitiveTypeDescriptor) o;
			return Objects.equals(typeModel.getCode(), that.typeModel.getCode());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return typeModel.getCode().hashCode();
	}

	@Override
	public String toString()
	{
		return "PrimitiveTypeDescriptor{" +
				typeModel.getCode() +
				'}';
	}
}
