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
package de.hybris.platform.initiateyaasconfigurationsync.service;

/**
 * The YaasConfiguration Synchronize Service interface.
 */
public interface YaasConfigurationSyncService
{

	/**
	 * This method runs configuration sync job to send configuration information to configured destination
	 */
	void syncYaasConfiguration();

}
