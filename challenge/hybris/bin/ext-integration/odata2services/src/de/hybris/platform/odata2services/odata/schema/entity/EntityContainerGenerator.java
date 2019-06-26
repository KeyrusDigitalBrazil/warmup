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

import java.util.Collection;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityType;

/**
 * Generator of the {@link EntityContainer} for an EDMX schema.
 */
public interface EntityContainerGenerator
{
	/**
	 * Generates the entity container element.
	 *
	 * @param entityTypes all entity types included in the schema.
	 * @param associations all associations between the entity types included in the schema.
	 * @return the generated entity containers or an empty list, if there are no entity types and associations to include in the
	 * schema.
	 */
	List<EntityContainer> generate(Collection<EntityType> entityTypes, Collection<Association> associations);
}
