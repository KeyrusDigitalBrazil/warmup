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
package de.hybris.platform.odata2services.odata.schema.navigation;

import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class NavigationPropertyListGenerator implements SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>
{
	private SchemaElementGenerator<Optional<NavigationProperty>, IntegrationObjectItemAttributeModel> navigationPropertyGenerator;

	/**
	 * Generates the list of NavigationProperty based on the entire list of entity attributes
	 *
	 * @param entityAttributes the complete set of attributes for the given entity type and all of its associated types
	 * @return the List of NavigationProperties
	 */
	@Override
	public List<NavigationProperty> generate(final Collection<IntegrationObjectItemAttributeModel> entityAttributes)
	{
		Preconditions.checkArgument(entityAttributes != null,
				"A NavigationProperty list cannot be generated from a null parameter");

		return entityAttributes.stream()
				.filter(Objects::nonNull)
				.map(navigationPropertyGenerator::generate)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	@Required
	public void setNavigationPropertyGenerator(final SchemaElementGenerator<Optional<NavigationProperty>, IntegrationObjectItemAttributeModel> generator)
	{
		navigationPropertyGenerator = generator;
	}
}
