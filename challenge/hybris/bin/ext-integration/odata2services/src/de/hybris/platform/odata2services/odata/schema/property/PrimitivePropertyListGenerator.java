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
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.provider.Property;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class PrimitivePropertyListGenerator extends AbstractPropertyListGenerator
{
	private SchemaElementGenerator<Optional<Property>, IntegrationObjectItemModel> integrationKeyPropertyGenerator;
	
	@Override
	public List<Property> generate(final IntegrationObjectItemModel itemModel)
	{
		Preconditions.checkArgument(itemModel != null,
				"A Property list cannot be generated from a null parameter");

		final List<Property> properties = new ArrayList<>(generateProperties(itemModel.getAttributes()));
		integrationKeyPropertyGenerator.generate(itemModel).ifPresent(properties::add);
		return properties;
	}

	@Required
	public void setIntegrationKeyPropertyGenerator(final SchemaElementGenerator<Optional<Property>, IntegrationObjectItemModel> integrationKeyPropertyGenerator)
	{
		this.integrationKeyPropertyGenerator = integrationKeyPropertyGenerator;
	}
}
