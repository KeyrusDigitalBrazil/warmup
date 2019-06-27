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
package de.hybris.platform.odata2services.odata.schema.attribute;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultTypeAttributeDescriptor;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

public class NullableAttributeGenerator implements AnnotationGenerator<IntegrationObjectItemAttributeModel>
{
	private static final String NULLABLE = "Nullable";

	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return itemAttributeModel != null && itemAttributeModel.getAttributeDescriptor() != null;
	}

	@Override
	public AnnotationAttribute generate(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		final TypeAttributeDescriptor descriptor = toAttributeDescriptor(itemAttributeModel);
		return nullableAttribute(descriptor.isNullable());
	}

	/**
	 * Converts the attribute model to an implementation of the {@link TypeAttributeDescriptor}
	 * @param itemAttributeModel a model to convert
	 * @return an attribute descriptor for the specified attribute model
	 */
	protected TypeAttributeDescriptor toAttributeDescriptor(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return DefaultTypeAttributeDescriptor.create(itemAttributeModel);
	}

	private static AnnotationAttribute nullableAttribute(final boolean isNullable)
	{
		return new AnnotationAttribute()
				.setName(NULLABLE)
				.setText(String.valueOf(isNullable));
	}
}
