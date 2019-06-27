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

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class EntityTypeListGenerator implements SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>>
{
	private List<SchemaElementGenerator<Collection<EntityType>, IntegrationObjectItemModel>> entityTypeGenerators;

	@Override
	public List<EntityType> generate(final Collection<IntegrationObjectItemModel> models)
	{
		Preconditions.checkArgument(models != null,
				"An EntityType list cannot be generated from a null parameter");

		final List<EntityType> entityTypeList = new ArrayList<>();
		models.forEach(model -> entityTypeGenerators.forEach(generator -> entityTypeList.addAll(generator.generate(model))));
		return SchemaUtils.removeDuplicates(entityTypeList, EntityType::getName);
	}

	@Required
	public void setEntityTypeGenerators(final List<SchemaElementGenerator<Collection<EntityType>, IntegrationObjectItemModel>> entityTypeGenerators)
	{
		this.entityTypeGenerators = entityTypeGenerators;
	}
}
