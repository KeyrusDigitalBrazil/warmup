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

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

/**
 * A default implementation of the {@link EntitySet} generator.
 */
public class EntitySetGenerator implements SchemaElementGenerator<EntitySet, EntityType>
{
	private EntitySetNameGenerator entitySetNameGenerator;

	@Override
	public EntitySet generate(final EntityType type)
	{
		Preconditions.checkArgument(type != null, "An EntitySet cannot be generated from a null EntityType");

		return new EntitySet()
				.setName(entitySetNameGenerator.generate(type.getName()))
				.setEntityType(toFullQualifiedName(type.getName()));
	}

	@Required
	public void setNameGenerator(final EntitySetNameGenerator generator)
	{
		this.entitySetNameGenerator = generator;
	}
}
