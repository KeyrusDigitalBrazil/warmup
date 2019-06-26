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
package de.hybris.smartedit.controllers;

import java.util.Map;

import org.springframework.messaging.handler.annotation.Header;


/**
 * Gateway to relay the DELETE operation to the secured webservice responsible of executing the operation. By default,
 * {@code smarteditwebservices} is the targeted web extension.
 */
public interface HttpDELETEGateway
{
	public void delete(Map<String, String> payload, @Header("configId") String configId, @Header("Authorization") String token);
}
