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

package de.hybris.platform.odata2services.odata;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;

/**
 * A helper class for exploring and evaluating content of an OData {@code Schema} object.
 */
public class ODataEntityContainer
{
	private final ODataSchema contextSchema;
	private final EntityContainer contextEntityContainer;

	ODataEntityContainer(final ODataSchema schema, final EntityContainer container)
	{
		assert schema != null: "ODataSchema should have passed itself";
		assert container != null : "ODataSchema should have thrown an exception, if container is null";
		contextSchema = schema;
		contextEntityContainer = container;
	}

	/**
	 * Checks whether this container contains an {@code AssociationSet} between the specified types.
	 * @param type1 simple (not fully qualified) type name for the one end of the association.
	 * @param type2 simple (not fully qualified) type name for the other end of the association.
	 * @return {@code true}, if such {@code AssociationSet} is present in the content of this container; {@code false}, otherwise.
	 */
	public boolean containsAssociationSetBetween(final String type1, final String type2)
	{
		return contextEntityContainer.getAssociationSets().stream()
				.map(AssociationSet::getAssociation)
				.map(FullQualifiedName::getName)
				.map(contextSchema::getAssociation)
				.anyMatch(a -> contextSchema.isAssociationBetweenTypes(a, type1, type2));
	}

	/**
	 * Retrieves all entity types referred from the EntitySet elements present in this container.
	 * @return a collection of all entity types. The types are stripped of the schema name, so if EntitySet type is
	 * {@code "MyNamespace.MyType"}, then only {@code "MyType"} is returned. If there are no entity sets defined in this container,
	 * then an empty collection is returned.
	 */
	public Collection<String> getEntitySetTypes()
	{
		return contextEntityContainer.getEntitySets().stream()
				.map(EntitySet::getEntityType)
				.map(FullQualifiedName::getName)
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves names of all AssociationSets present in the content of this container.
	 * @return a collection of all association set names or an empty collection, if this container does not have a single
	 * {@link AssociationSet}.
	 */
	public Collection<String> getAssociationSetNames()
	{
		return contextEntityContainer.getAssociationSets().stream()
				.map(AssociationSet::getName)
				.collect(Collectors.toList());
	}
}
