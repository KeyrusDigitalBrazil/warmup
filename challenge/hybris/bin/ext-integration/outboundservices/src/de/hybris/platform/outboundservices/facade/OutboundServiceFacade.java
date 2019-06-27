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

package de.hybris.platform.outboundservices.facade;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import rx.Observable;

/**
 *
 * Facade which orchestrates the {@link IntegrationObjectConversionService} and {@link IntegrationRestTemplateFactory} to integrate
 * with the RESTful endpoint.
 *
 */
public interface OutboundServiceFacade
{
	/**
	 * Method orchestrates the services to build the payload and to integrate with restful endpoint.
	 *
	 * @param itemModel the model to be converted
	 * @param integrationObjectCode the name of the integration object to convert this model as payload
	 * @param destination endpoint destination information
	 *
	 * @return rx.Observable which allows the caller to subscribe the callback method
	 */
	Observable<ResponseEntity<Map>> send(ItemModel itemModel, String integrationObjectCode, String destination);
}
