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

import java.util.Collection;

import org.apache.olingo.odata2.api.edm.provider.Schema;

public interface SchemaGenerator
{
	/**
	 * Generates a {@link org.apache.olingo.odata2.api.edm.provider.Schema} for an {@link IntegrationObjectItemModel} and
	 * all of its dependencies.
	 * @param allModelsForType a model and its dependencies
	 * @return a schema representing those types
	 */
	Schema generateSchema(final Collection<IntegrationObjectItemModel> allModelsForType);
}
