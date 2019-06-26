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
package de.hybris.platform.outboundservices.client;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import org.springframework.web.client.RestOperations;


/**
 * The RestTemplate creator interface to create RestTemplate instance by given Consumed destination model.
 */
public interface IntegrationRestTemplateCreator
{

	/**
	 * Create a rest template by given consumed destination model.
	 *
	 * @param destination consumed destination model
	 * @return restTemplate
	 */
	RestOperations create(ConsumedDestinationModel destination);

	/**
	 * If the strategy applicable for giving consumed destination model
	 *
	 * @param destination consumed destination model
	 * @return applicable
	 */
	boolean isApplicable(ConsumedDestinationModel destination);
}
