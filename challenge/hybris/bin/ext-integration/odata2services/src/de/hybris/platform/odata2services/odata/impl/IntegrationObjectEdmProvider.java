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

package de.hybris.platform.odata2services.odata.impl;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ENTITY_TYPE;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.SERVICE;

import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


public class IntegrationObjectEdmProvider extends EdmProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationObjectEdmProvider.class);

	private final IntegrationObjectService integrationObjectService;
	private final SchemaGenerator schemaGenerator;
	private final String type;
	private final String serviceName;
	private Schema schema;

	IntegrationObjectEdmProvider(final IntegrationObjectService integrationObjectService,
			final SchemaGenerator schemaGenerator, final ODataContext context)
	{
		super();
		this.integrationObjectService = integrationObjectService;
		this.schemaGenerator = schemaGenerator;
		type = getParameter(ENTITY_TYPE, context);
		serviceName = getParameter(SERVICE, context);
	}

	@Override
	public List<Schema> getSchemas() throws ODataException
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(serviceName), "Service must be provided when generating schemas");

		try
		{
			final Schema schemaForType = StringUtils.isEmpty(type)
					? generateSchemaForAllTypes()
					: generateSchemaForType();
			return Collections.singletonList(schemaForType);
		}
		catch (final RuntimeException e)
		{
			LOG.error("Error reading schema for service '{}' and type '{}'", serviceName, type, e);
			throw new ODataException(e);
		}
	}

	private Schema generateSchemaForAllTypes()
	{
		LOG.debug("Reading schema for all types for service '{}'.", serviceName);
		if (schema == null)
		{
			schema = schemaGenerator.generateSchema(integrationObjectService.findAllIntegrationObjectItems(serviceName));
		}
		return schema;
	}

	private Schema generateSchemaForType()
	{
		LOG.debug("Reading schema for service '{}' and type '{}'.", serviceName, type);
		return schemaGenerator.generateSchema(integrationObjectService.findAllDependencyTypes(type, serviceName));
	}

	private static String getParameter(final String param, final ODataContext context)
	{
		final Object value = context.getParameter(param);
		return value != null ? value.toString() : "";
	}

	@Override
	public EntitySet getEntitySet(final String entityContainer, final String entitySetName)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(entityContainer), "Requested container name should not be null");
		Preconditions.checkArgument(StringUtils.isNotEmpty(entitySetName), "Requested entity name should not be null");

		final EntityContainer container = generateSchemaForAllTypes().getEntityContainers().stream()
				.filter(con -> entityContainer.equals(con.getName())).findFirst().orElse(null);

		if (container != null)
		{
			return container.getEntitySets().stream()
					.filter(entitySet -> entitySetName.equals(entitySet.getName()))
					.findFirst()
					.orElse(null);
		}

		return null;
	}

	@Override
	public EntityType getEntityType(final FullQualifiedName entityTypeName)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(entityTypeName.getName()), "Requested entity type should not be null");

		return generateSchemaForAllTypes().getEntityTypes().stream()
				.filter(entityType -> entityType.getName().equals(entityTypeName.getName()))
				.findFirst()
				.orElse(null);
	}

	@Override
	public Association getAssociation(final FullQualifiedName associationName)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(associationName.getName()), "Requested entity type should not be null");

		if (SchemaUtils.NAMESPACE.equals(associationName.getNamespace()))
		{
			return generateSchemaForAllTypes().getAssociations().stream()
					.filter(association -> association.getName().equals(associationName.getName()))
					.findFirst()
					.orElse(null);
		}
		return null;
	}

	@Override
	public AssociationSet getAssociationSet(final String entityContainer, final FullQualifiedName associationName,
			final String sourceEntitySetName, final String sourceEntitySetRole)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(associationName.getName()),
				"Requested entity association name should not be null");

		if (SchemaUtils.CONTAINER_NAME.equals(entityContainer))
		{
			return generateSchemaForAllTypes().getEntityContainers().get(0).getAssociationSets().stream()
					.filter(associationSet -> associationName.equals(associationSet.getAssociation()))
					.findFirst()
					.orElse(null);
		}

		return null;
	}

	@Override
	public EntityContainerInfo getEntityContainerInfo(final String entityContainerName)
	{
		if (SchemaUtils.CONTAINER_NAME.equals(entityContainerName) || StringUtils.isEmpty(entityContainerName))
		{
			final EntityContainerInfo entityContainerInfo = new EntityContainerInfo();
			entityContainerInfo.setName(SchemaUtils.CONTAINER_NAME);
			entityContainerInfo.setDefaultEntityContainer(true);
			return entityContainerInfo;
		}

		return null;
	}
}
