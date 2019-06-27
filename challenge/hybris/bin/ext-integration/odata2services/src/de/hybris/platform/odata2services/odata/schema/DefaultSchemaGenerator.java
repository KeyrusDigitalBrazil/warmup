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
package de.hybris.platform.odata2services.odata.schema;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2services.odata.schema.association.AssociationListGeneratorRegistry;
import de.hybris.platform.odata2services.odata.schema.entity.EntityContainerGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class DefaultSchemaGenerator implements SchemaGenerator
{
	private AssociationListGeneratorRegistry associationListGeneratorRegistry;
	private SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>> entityTypesGenerator;
	private EntityContainerGenerator entityContainerGenerator;

	public Schema generateSchema(final Collection<IntegrationObjectItemModel> allModelsForType)
	{
		try
		{
			Preconditions.checkArgument(allModelsForType != null, "Unable to generate schema for null");

			final List<EntityType> entityTypes = entityTypesGenerator.generate(allModelsForType);
			final List<Association> associations = associationListGeneratorRegistry.generate(allModelsForType);
			return new Schema()
					.setNamespace(SchemaUtils.NAMESPACE)
					.setAnnotationAttributes(SchemaUtils.createNamespaceAnnotations())
					.setEntityTypes(entityTypes)
					.setAssociations(associations)
					.setEntityContainers(entityContainerGenerator.generate(entityTypes, associations));
		}
		catch(final RuntimeException e)
		{
			throw new InvalidODataSchemaException(e);
		}
	}

	@Required
	public void setEntityTypesGenerator(final SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>> entityTypesGenerator)
	{
		this.entityTypesGenerator = entityTypesGenerator;
	}

	@Required
	public void setAssociationListGeneratorRegistry(final AssociationListGeneratorRegistry associationListGeneratorRegistry)
	{
		this.associationListGeneratorRegistry = associationListGeneratorRegistry;
	}

	@Required
	public void setEntityContainerGenerator(final EntityContainerGenerator generator)
	{
		this.entityContainerGenerator = generator;
	}
}
