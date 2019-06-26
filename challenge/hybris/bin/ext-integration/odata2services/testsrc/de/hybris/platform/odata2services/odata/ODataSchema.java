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
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;

/**
 * A helper class for exploring and evaluating content of an OData {@code Schema} object.
 */
public class ODataSchema
{
	private final Schema contextSchema;

	public ODataSchema(final Schema schema)
	{
		contextSchema = schema;
	}

	/**
	 * Checks whether content of this schema is empty.
	 * @return {@code true}, if this schema has empty content; {@code false}, if at least one element (XML element) is found in
	 * the schema content.
	 */
	public boolean isEmpty()
	{
		return CollectionUtils.isEmpty(contextSchema.getAssociations())
				&& CollectionUtils.isEmpty(contextSchema.getComplexTypes())
				&& CollectionUtils.isEmpty(contextSchema.getEntityContainers())
				&& CollectionUtils.isEmpty(contextSchema.getEntityTypes());
	}

	/**
	 * Retrieves names of all entity types defined in this schema.
	 * @return a collection of all entity type names present in this schema or an empty collection, if this schema does not
	 * contain a single entity type.
	 */
	public Collection<String> getEntityTypeNames()
	{
		return contextSchema.getEntityTypes().stream().map(EntityType::getName).collect(Collectors.toList());
	}

	/**
	 * Retrieves an entity type defined in this schema.
	 * @param type name of the type to retrieve
	 * @return entity type matching the type name.
	 * @throws IllegalArgumentException if the specified type does not exist in this schema.
	 */
	public ODataEntityType getEntityType(final String type)
	{
		final EntityType entityType = contextSchema.getEntityTypes().stream()
				.filter(t -> Objects.equals(type, t.getName()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("EntityType '" + type + "'" + isNotPresentInSchema()));
		return new ODataEntityType(entityType);
	}

	public boolean containsAssociationBetween(final String type1, final String type2)
	{
		return getAssociations().stream()
				.anyMatch(a -> isAssociationBetweenTypes(a, type1, type2));
	}

	boolean isAssociationBetweenTypes(final Association a, final String type1, final String type2)
	{
		return a.getEnd1().getType().equals(qualified(type1))
				&& a.getEnd2().getType().equals(qualified(type2));
	}

	public Association getAssociation(final String name)
	{
		return getAssociations().stream()
				.filter(a -> Objects.equals(a.getName(), name))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Association '" + name + "'" + isNotPresentInSchema()));
	}

	public Collection<Association> getAssociations()
	{
		return contextSchema.getAssociations();
	}

	private FullQualifiedName qualified(final String name)
	{
		return new FullQualifiedName(schemaName(), name);
	}

	/**
	 * Retrieves default entity container in this schema content.
	 * @return the default entity container or {@code null}, if default entity container is not present.
	 */
	public ODataEntityContainer getDefaultEntityContainer()
	{
		final EntityContainer container = contextSchema.getEntityContainers().stream()
				.filter(EntityContainer::isDefaultEntityContainer)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Default EntityContainer" + isNotPresentInSchema()));
		return new ODataEntityContainer(this, container);
	}

	private String isNotPresentInSchema()
	{
		return " is not present in '" + schemaName() + "' schema";
	}

	private String schemaName()
	{
		return contextSchema.getNamespace();
	}
}
