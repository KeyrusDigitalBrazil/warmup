/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sec.eventpublisher.websocket.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderWS;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@ServerEndpoint(value = "/ordersocket/{contextId}/{customerId}/{agentId}")
public class DefaultSECWebSocketEndpoint {
	private static final Logger LOGGER = LogManager.getLogger(DefaultSECWebSocketEndpoint.class);
	
	private static final Map<String, Session> sessions = new HashMap<String, Session>();
	private static final String MESSAGE_SEPARATOR = ";";

	@OnOpen
	public void start(final Session session, @PathParam("contextId") final String contextId,
			@PathParam("customerId") final String customerId, @PathParam("agentId") final String agentId) 
	{
		LOGGER.debug("Websocket connection for clinet : " + customerId + " has been created by agent :" + agentId);
		final String key = customerId + agentId;
		session.getUserProperties().put(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_ORDERS,
				new ArrayList<String>());
		session.getUserProperties().put(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_CLIENTCONTEXT, contextId);
		sessions.put(key, session);
	}

	@OnClose
	public void end(final Session session, @PathParam("customerId") final String customerId,
			@PathParam("agentId") final String agentId) {
		final String key = customerId + agentId;
		sessions.remove(key);
		LOGGER.debug("Websocket connection for clinet : " + customerId + " has been removed by agent :" + agentId);
	}

	@OnMessage
	public void incoming(final Session session, final String message) {

		try {

			final Map<String, String> pathParameters = session.getPathParameters();
			final String contextId = pathParameters.get("contextId");

			final OrderWS wsOrder = new ObjectMapper().readValue(message, OrderWS.class);
			final String customerId = wsOrder.getCustomerId();
			final String agentId = wsOrder.getAgentId();
			final String orderId = wsOrder.getOrderId();

			if (EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_INTERNALCONTEXT.equals(contextId)
					&& !StringUtils.isEmpty(customerId)) {
				final String key = customerId + agentId;
				final Session targetSession = sessions.get(key);
				if (targetSession != null && !isResponseAlreadySent(targetSession, orderId)) {
					final Object clientContext = targetSession.getUserProperties()
							.get(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_CLIENTCONTEXT);
					final String responseMessage = prepareWSResponse(wsOrder, clientContext);
					sentMessageToClient(responseMessage, targetSession, orderId);
					sessions.put(key, targetSession);
					LOGGER.debug("Websocket message sent to clinet for customre id : " + customerId + " to agent :"
							+ agentId);
				}

			}
		} catch (final IOException e) {
			LOGGER.error("Failed to reply websebsocket client", e);
		}

	}

	private String prepareWSResponse(final OrderWS wsOrder, final Object clientContext) {
		StringBuilder sb = new StringBuilder();
		String sessionContext = "";
		if (clientContext instanceof String) {
			sessionContext = (String) clientContext;
		}
		Configuration configuration = getConfigurationService().getConfiguration();
		final String messageSource = configuration.getString(EventpublisherConstants.WEBSOCKET_MESSAGE_SOURCE);
		final String currentMethod = configuration.getString(EventpublisherConstants.WEBSOCKET_CURRENT_METHOD);
		final String nextMethod = configuration.getString(EventpublisherConstants.WEBSOCKET_NEXT_METHOD);
		final Boolean isNewObject = Boolean.valueOf(configuration.getBoolean(EventpublisherConstants.WEBSOCKET_RESPONSE_IS_NEW_OBJECT));
		

		sb.append(messageSource).append(MESSAGE_SEPARATOR).append(sessionContext).append(MESSAGE_SEPARATOR)
				.append(wsOrder.getOrderId()).append(MESSAGE_SEPARATOR).append(currentMethod).append(MESSAGE_SEPARATOR)
				.append(EventpublisherConstants.WEBSOCKET_RESPONSE_OK_VALUE).append(MESSAGE_SEPARATOR).append(nextMethod)
				.append(MESSAGE_SEPARATOR).append(isNewObject.booleanValue())
				.append(MESSAGE_SEPARATOR).append(wsOrder.getBdtType());

		return sb.toString();
	}

	private boolean isResponseAlreadySent(Session targetSession, String orderId) {
		Object ordersList = targetSession.getUserProperties()
				.get(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_ORDERS);
		if (ordersList instanceof List<?>) {
			List<String> inseterdOrders = (List<String>) ordersList;
			if (inseterdOrders.contains(orderId)) {
				return true;
			}
		}

		return false;
	}

	protected void sentMessageToClient(final String message, final Session targetSession, final String orderId)
			throws IOException {
		
		if (!StringUtils.isEmpty(message)) {
			Object ordersList = targetSession.getUserProperties()
					.get(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_ORDERS);
			if (targetSession != null && targetSession.getBasicRemote() != null && ordersList instanceof List<?>) {
				targetSession.getBasicRemote().sendText(message);
				List<String> orderIdList = (List<String>) ordersList;
				orderIdList.add(orderId);
				targetSession.getUserProperties().put(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_ORDERS, orderIdList);

			} else {
				LOGGER.debug("targetSession object is either null or basic remote object is null. Ignoring message.");
			}
		} else {
			LOGGER.debug("Message is empty or null! Nothing to send. Ignoring.");
		}
	}
	
	protected ConfigurationService getConfigurationService()
	{
		return (ConfigurationService)Registry.getApplicationContext().getBean("configurationService");
	}

}
