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
package de.hybris.platform.sap.productconfig.runtime.ssc;

import java.util.Map;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


/**
 * Holds SSC sessions
 */
public interface ConfigurationSessionContainer
{

	/**
	 * @return Session map
	 */
	Map<String, IConfigSession> getSessionMap();

	/**
	 * @param sessionId
	 */
	void releaseSession(String sessionId);

	/**
	 * @param qualifiedId
	 * @return SSC configuration session
	 */
	IConfigSession retrieveConfigSession(String qualifiedId);

	/**
	 * @param qualifiedId
	 * @param configSession
	 */
	void storeConfiguration(String qualifiedId, IConfigSession configSession);

}
