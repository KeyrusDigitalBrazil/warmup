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

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ENTITY_TYPE_PREFIX;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultTypeAttributeDescriptor;

import java.util.Set;
import java.util.stream.Collectors;

public class LocalizedEntityTypeGenerator extends SingleEntityTypeGenerator
{
	@Override
	protected boolean isApplicable(final IntegrationObjectItemModel item)
	{
		return !extractLocalizedAttributes(item).isEmpty();
	}

	private Set<IntegrationObjectItemAttributeModel> extractLocalizedAttributes(final IntegrationObjectItemModel item)
	{
		return item.getAttributes().stream()
				.filter(attr -> asDescriptor(attr).isLocalized())
				.collect(Collectors.toSet());
	}

	@Override
	protected String generateEntityTypeName(final IntegrationObjectItemModel item)
	{
		return LOCALIZED_ENTITY_TYPE_PREFIX + item.getCode();
	}

	protected TypeAttributeDescriptor asDescriptor(IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return DefaultTypeAttributeDescriptor.create(itemAttributeModel);
	}
}