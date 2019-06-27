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

import static de.hybris.platform.integrationservices.model.ModelUtils.isUnique;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

public class UniqueAttributeGenerator implements AnnotationGenerator<IntegrationObjectItemAttributeModel>
{
	static final String IS_UNIQUE = "s:IsUnique";
	
	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return itemAttributeModel != null && isUnique(itemAttributeModel);
	}

	@Override
	public AnnotationAttribute generate(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return new AnnotationAttribute()
				.setName(IS_UNIQUE)
				.setText("true");
	}
}
