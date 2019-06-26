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

package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the container generator, which uses generated entity types and associations for the container
 * content.
 */
public class DefaultEntityContainerGenerator implements EntityContainerGenerator
{
	private SchemaElementGenerator<EntitySet, EntityType> entitySetGenerator;
	private SchemaElementGenerator<AssociationSet, Association> associationSetGenerator;

	@Override
	public List<EntityContainer> generate(final Collection<EntityType> entityTypes, final Collection<Association> associations)
	{
		return entityTypes.isEmpty() && associations.isEmpty()
				? Collections.emptyList()
				: Collections.singletonList(createEntityContainer(entityTypes, associations));
	}

	private EntityContainer createEntityContainer(final Collection<EntityType> entityTypes, final Collection<Association> associations)
	{
		final List<EntitySet> entitySets = toEntitySets(entityTypes);
		return new EntityContainer()
				.setName(SchemaUtils.CONTAINER_NAME)
				.setDefaultEntityContainer(true)
				.setEntitySets(entitySets)
				.setAssociationSets(toAssociationSets(associations));
	}

	private List<EntitySet> toEntitySets(final Collection<EntityType> types)
	{
		return types.stream()
				.map(entitySetGenerator::generate)
				.collect(Collectors.toList());
	}

	private List<AssociationSet> toAssociationSets(final Collection<Association> associations)
	{
		return associations.stream()
				.map(associationSetGenerator::generate)
				.collect(Collectors.toList());
	}

	@Required
	public void setEntitySetGenerator(final SchemaElementGenerator<EntitySet, EntityType> generator)
	{
		entitySetGenerator = generator;
	}

	@Required
	public void setAssociationSetGenerator(final SchemaElementGenerator<AssociationSet, Association> generator)
	{
		associationSetGenerator = generator;
	}
}