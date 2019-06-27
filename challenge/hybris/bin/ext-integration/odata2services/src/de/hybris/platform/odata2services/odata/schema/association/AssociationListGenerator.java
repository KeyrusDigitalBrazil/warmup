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
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class AssociationListGenerator implements SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>
{
	private AssociationGeneratorRegistry associationGeneratorRegistry;

	@Override
	public List<Association> generate(final Collection<IntegrationObjectItemModel> allIntegrationObjectItemModelsForType)
	{
		Preconditions.checkArgument(allIntegrationObjectItemModelsForType != null,
				"An Association list cannot be generated from a null parameter");

		final List<Association> associationList = new ArrayList<>();

		allIntegrationObjectItemModelsForType.stream()
				.flatMap(type -> type.getAttributes().stream())
				.forEach(attribute -> associationGeneratorRegistry.getAssociationGenerator(attribute)
						.ifPresent(associationGenerator -> associationList.add(associationGenerator.generate(attribute))));

		return associationList;
	}

	@Required
	public void setAssociationGeneratorRegistry(final AssociationGeneratorRegistry associationGeneratorRegistry)
	{
		this.associationGeneratorRegistry = associationGeneratorRegistry;
	}
}
