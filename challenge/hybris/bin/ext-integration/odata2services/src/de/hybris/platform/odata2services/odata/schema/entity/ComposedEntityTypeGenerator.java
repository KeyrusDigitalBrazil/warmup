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
import de.hybris.platform.odata2services.odata.schema.navigation.NavigationPropertyListGeneratorRegistry;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

public class ComposedEntityTypeGenerator extends SingleEntityTypeGenerator
{
	private NavigationPropertyListGeneratorRegistry registry;

	@Override
	protected EntityType generateEntityType(final IntegrationObjectItemModel item)
	{
		return super.generateEntityType(item)
				.setNavigationProperties(registry.generate(item.getAttributes()));
	}

	@Override
	protected boolean isApplicable(final IntegrationObjectItemModel item)
	{
		return true;
	}

	@Override
	protected String generateEntityTypeName(final IntegrationObjectItemModel item)
	{
		return item.getCode();
	}

	@Required
	public void setRegistry(final NavigationPropertyListGeneratorRegistry registry)
	{
		this.registry = registry;
	}
}


