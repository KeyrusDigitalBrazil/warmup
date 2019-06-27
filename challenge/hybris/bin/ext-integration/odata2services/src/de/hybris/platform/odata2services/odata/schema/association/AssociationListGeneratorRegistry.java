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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class AssociationListGeneratorRegistry
{
	private List<SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>> associationGenerators;

	public List<Association> generate(final Collection<IntegrationObjectItemModel> itemModels)
	{
		Preconditions.checkArgument(itemModels != null, "An association generator cannot be created for null IntegrationObjectItemModels.");
		
		return getGenerators() != null ? getGenerators().stream()
				.map(generator -> generator.generate(itemModels))
				.flatMap(Collection::stream)
				.collect(Collectors.toList()) : Collections.emptyList();
	}

	protected List<SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>> getGenerators()
	{
		return associationGenerators;
	}

	@Required
	public void setAssociationListGenerators(final List<SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>> associationListGenerators)
	{
		this.associationGenerators = associationListGenerators;
	}
}
