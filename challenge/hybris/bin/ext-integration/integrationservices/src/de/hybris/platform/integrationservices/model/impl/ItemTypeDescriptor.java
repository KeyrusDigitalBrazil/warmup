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

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * {@inheritDoc}
 * <p>This implementation is effectively immutable and therefore is thread safe</p>
 * <p>Reuse this implementation through composition not inheritance</p>
 */
public class ItemTypeDescriptor implements TypeDescriptor
{
	private final IntegrationObjectItemModel itemTypeModel;
	private final Map<String, TypeAttributeDescriptor> attributeDescriptors;

	ItemTypeDescriptor(@NotNull final IntegrationObjectItemModel model)
	{
		Preconditions.checkArgument(model != null, "Non-null integration object item model must be provided");
		itemTypeModel = model;
		attributeDescriptors = initializeAttributeDescriptors(model);
	}

	public static TypeDescriptor create(final IntegrationObjectItemModel model)
	{
		return new ItemTypeDescriptor(model);
	}

	private Map<String, TypeAttributeDescriptor> initializeAttributeDescriptors(final IntegrationObjectItemModel model)
	{
		return model.getAttributes().stream()
				.map(this::createAttributeDescriptor)
				.collect(Collectors.toMap(TypeAttributeDescriptor::getAttributeName, Function.identity()));
	}

	@Override
	public String getTypeCode()
	{
		return itemTypeModel.getCode();
	}

	@Override
	public Optional<TypeAttributeDescriptor> getAttribute(final String attrName)
	{
		return Optional.ofNullable(attributeDescriptors.get(attrName));
	}

	@Override
	public Collection<TypeAttributeDescriptor> getAttributes()
	{
		return new HashSet<>(attributeDescriptors.values());
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	private TypeAttributeDescriptor createAttributeDescriptor(final IntegrationObjectItemAttributeModel model)
	{
		return DefaultTypeAttributeDescriptor.create(model);
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
			final ItemTypeDescriptor that = (ItemTypeDescriptor) o;
			return Objects.equals(integrationObjectName(), that.integrationObjectName())
					&& Objects.equals(getTypeCode(), that.getTypeCode());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(integrationObjectName(), getTypeCode());
	}

	@Override
	public String toString()
	{
		return "ItemTypeDescriptor{" +
				"integrationObject='" + integrationObjectName() +
				"', typeCode='" + getTypeCode() +
				"'}";
	}

	private String integrationObjectName()
	{
		final IntegrationObjectModel integrationObject = itemTypeModel.getIntegrationObject();
		return integrationObject != null ? integrationObject.getCode() : "";
	}
}
