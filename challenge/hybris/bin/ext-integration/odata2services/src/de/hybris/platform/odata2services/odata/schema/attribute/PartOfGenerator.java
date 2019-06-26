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
 * A generator responsible for including {@code s:IsPartOf} annotation on the {@code Property} and {@code NavigationProperty} EDMX elements.
 */
public class PartOfGenerator implements AnnotationGenerator<IntegrationObjectItemAttributeModel>
{
	private static final String IS_PART_OF = "s:IsPartOf";

	private static final AnnotationAttribute ANNOTATION_ATTRIBUTE = new ImmutableAnnotationAttribute()
			.setName(IS_PART_OF)
			.setText("true");
	
	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel model)
	{
		return model != null && toDescriptor(model).isPartOf();
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

	@Override
	public AnnotationAttribute generate(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return ANNOTATION_ATTRIBUTE;
	}
}
