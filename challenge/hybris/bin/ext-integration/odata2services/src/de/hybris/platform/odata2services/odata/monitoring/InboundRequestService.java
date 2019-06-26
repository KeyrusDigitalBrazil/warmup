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
package de.hybris.platform.odata2services.odata.monitoring;

import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;

import java.util.List;

/**
 * A service to create InboundRequests. An InboundRequest relates to an invocation
 * into the integration API.
 */
public interface InboundRequestService
{

	/**
	 * Collects entities contained in the incoming request and the corresponding response, turns them into {@code InboundRequest}
	 * and persists them.
	 * @param requests information pertaining the inbound requests extracted from the requests.
	 * @param responses information pertaining the inbound requests extracted from the responses.
	 * @param medias request bodies for all entities contained in a request.
	 */
	void register(List<RequestBatchEntity> requests, List<ResponseChangeSetEntity> responses, final List<InboundRequestMediaModel> medias);
}
