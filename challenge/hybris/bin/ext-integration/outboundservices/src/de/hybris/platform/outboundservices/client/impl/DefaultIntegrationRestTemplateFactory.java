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
package de.hybris.platform.outboundservices.client.impl;

import java.util.List;

import com.google.common.base.Preconditions;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateCreator;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestOperations;


/**
 * The default implementation of the factory to create rest template instance.
 */
public class DefaultIntegrationRestTemplateFactory implements IntegrationRestTemplateFactory
{
	private List<IntegrationRestTemplateCreator> restTemplateCreators;

	@Override
	public RestOperations create(final ConsumedDestinationModel destination)
	{
		Preconditions.checkArgument(destination != null, "Consumed destination model cannot be null.");

		final IntegrationRestTemplateCreator creator = getRestTemplateCreators().stream()
				.filter(s -> s.isApplicable(destination)).findFirst()
				.orElseThrow(UnsupportedRestTemplateException::new);

		return creator.create(destination);
	}

	protected List<IntegrationRestTemplateCreator> getRestTemplateCreators()
	{
		return restTemplateCreators;
	}

	@Required
	public void setRestTemplateCreators(final List<IntegrationRestTemplateCreator> restTemplateCreators)
	{
		this.restTemplateCreators = restTemplateCreators;
	}
}
