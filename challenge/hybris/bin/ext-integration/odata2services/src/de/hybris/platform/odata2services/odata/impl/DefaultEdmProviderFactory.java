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

import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;

import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.springframework.beans.factory.annotation.Required;

public class DefaultEdmProviderFactory implements EdmProviderFactory
{
	private IntegrationObjectService integrationObjectService;
	private SchemaGenerator schemaGenerator;

	@Override
	public EdmProvider createInstance(final ODataContext context)
	{
		return new IntegrationObjectEdmProvider(integrationObjectService, schemaGenerator, context);
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		this.integrationObjectService = integrationObjectService;
	}

	@Required
	public void setSchemaGenerator(final SchemaGenerator schemaGenerator)
	{
		this.schemaGenerator = schemaGenerator;
	}
}
