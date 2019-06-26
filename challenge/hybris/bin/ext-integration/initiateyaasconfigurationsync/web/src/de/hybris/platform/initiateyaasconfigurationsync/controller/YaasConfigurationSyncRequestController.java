/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.initiateyaasconfigurationsync.controller;

import de.hybris.platform.initiateyaasconfigurationsync.service.YaasConfigurationSyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping(value = "configuration-request")
public class YaasConfigurationSyncRequestController
{

	@Autowired
	private YaasConfigurationSyncService yaasConfigurationSyncService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void request()
	{
		yaasConfigurationSyncService.syncYaasConfiguration();
	}
}
