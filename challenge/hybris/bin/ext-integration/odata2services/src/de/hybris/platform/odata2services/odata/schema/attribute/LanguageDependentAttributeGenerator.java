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

public class LanguageDependentAttributeGenerator implements AnnotationGenerator<IntegrationObjectItemAttributeModel>
{
	private static final String IS_LANGUAGE_DEPENDENT = "s:IsLanguageDependent";

	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return itemAttributeModel != null && asDescriptor(itemAttributeModel).isLocalized();
	}

	@Override
	public AnnotationAttribute generate(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return new AnnotationAttribute()
				.setName(IS_LANGUAGE_DEPENDENT)
				.setText("true");
	}

	protected TypeAttributeDescriptor asDescriptor(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return DefaultTypeAttributeDescriptor.create(itemAttributeModel);
	}
}
