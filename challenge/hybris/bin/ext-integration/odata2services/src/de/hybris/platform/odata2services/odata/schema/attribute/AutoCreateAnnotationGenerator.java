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

/**
 * A generator responsible for including {@code s:IsAutoCreate} annotation on the
 * {@code Property} and {@code NavigationProperty} EDMX elements.
 */
public class AutoCreateAnnotationGenerator implements AnnotationGenerator<IntegrationObjectItemAttributeModel>
{
	private static final String IS_AUTO_CREATE = "s:IsAutoCreate";

	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel attributeModel)
	{
		return attributeModel != null && toDescriptor(attributeModel).isAutoCreate();
	}

	@Override
	public AnnotationAttribute generate(final IntegrationObjectItemAttributeModel attributeModel)
	{
		return new AnnotationAttribute()
				.setName(IS_AUTO_CREATE)
				.setText("true");
	}

	/**
	 * Converts model to the attribute descriptor.
	 * @param model a model to convert
	 * @return an instance created by calling {@code DefaultTypeAttributeDescriptor.create(model)}
	 */
	protected TypeAttributeDescriptor toDescriptor(final IntegrationObjectItemAttributeModel model)
	{
		return DefaultTypeAttributeDescriptor.create(model);
	}
}
