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

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.springframework.beans.factory.annotation.Required;

/**
 * This registry stores a collection of navigation property list generators and combine their results
 */
public class NavigationPropertyListGeneratorRegistry
{
	private Collection<SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>> generators;

	public List<NavigationProperty> generate(final Collection<IntegrationObjectItemAttributeModel> attributeModels)
	{
		return getGenerators() != null ? generateInternal(attributeModels) : Collections.emptyList();
	}

	private List<NavigationProperty> generateInternal(final Collection<IntegrationObjectItemAttributeModel> attributeModels)
	{
		return getGenerators().stream()
				.map(generator -> generator.generate(attributeModels))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	protected Collection<SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>> getGenerators()
	{
		return generators;
	}

	@Required
	public void setGenerators(final Collection<SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>> generators)
	{
		this.generators = generators;
	}
}
