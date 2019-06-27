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
package de.hybris.platform.integrationservices.populator;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyGenerator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

public class DefaultIntegrationKeyPopulator <S extends ItemToMapConversionContext, T extends Map<String, Object>>
		implements Populator<S, T>
{
	private IntegrationKeyGenerator<IntegrationObjectItemModel, T> integrationKeyGenerator;

	@Override
	public void populate(final S context, final T entry)
	{
		final String integrationKey = getIntegrationKeyGenerator().generate(context.getIntegrationObjectItemModel(), entry);
		entry.put(INTEGRATION_KEY_PROPERTY_NAME, integrationKey);
	}

	protected IntegrationKeyGenerator<IntegrationObjectItemModel, T> getIntegrationKeyGenerator()
	{
		return integrationKeyGenerator;
	}

	@Required
	public void setIntegrationKeyGenerator(final IntegrationKeyGenerator<IntegrationObjectItemModel, T> integrationKeyGenerator)
	{
		this.integrationKeyGenerator = integrationKeyGenerator;
	}
}

