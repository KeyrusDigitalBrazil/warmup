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

package de.hybris.platform.integrationservices.model;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

/**
 * Provides calculation of the dynamic {@code partOf} attribute on the {@link IntegrationObjectItemAttributeModel}
 */
public class PartOfAttributeHandler extends AbstractDynamicAttributeHandler<Boolean, IntegrationObjectItemAttributeModel>
{
	/**
	 * Reads value of the {@code partOf} attribute
	 * @param model a model object to read the value from.
	 * @return a boolean specifying whether the referenced integration object item is part of this integration object item and
	 * cannot exist by itself. Although the return type is {@code Boolean}, it's actually never {@code null}.
	 */
	@Override
	@NotNull
	public Boolean get(final IntegrationObjectItemAttributeModel model)
	{
		assert model != null : "Platform does not pass null models into the dynamic attribute handlers";

		final AttributeDescriptorModel descriptor = model.getAttributeDescriptor();
		return isCollectionPartOfThisType(descriptor) || isRelatedTypeIsPartOfThisType(descriptor);
	}

	protected boolean isCollectionPartOfThisType(final AttributeDescriptorModel descriptor)
	{
		return descriptor != null && falseIfNull(descriptor::getPartOf);
	}

	protected boolean isRelatedTypeIsPartOfThisType(final AttributeDescriptorModel descriptor)
	{
		return descriptor instanceof RelationDescriptorModel
				&& falseIfNull(() -> getRelationAttribute(descriptor).getPartOf());
	}

	private RelationDescriptorModel getRelationAttribute(final AttributeDescriptorModel descriptor)
	{
		final RelationMetaTypeModel relationType = ((RelationDescriptorModel) descriptor).getRelationType();
		final RelationDescriptorModel model = relationType.getTargetAttribute();
		return model == null
				? relationType.getSourceAttribute()
				: model;
	}

	private static boolean falseIfNull(final Supplier<Boolean> function)
	{
		return Boolean.TRUE.equals(function.get());
	}
}
