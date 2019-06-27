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
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;


/**
 * Compares build number of knowledgebases to those of the returned runtime configurations. Thereby it can detect
 * outdated data on the hybris side.
 */
public interface KnowledgebaseKeyComparator
{
	/**
	 * Compares build number of the provided runtime configuration with those of the associated knowledgebase.
	 * 
	 * @param runtimeConfiguration
	 *           runtime configuration
	 * @return status of the build number synchronization
	 */
	KnowledgebaseBuildSyncStatus retrieveKnowledgebaseBuildSyncStatus(final CPSConfiguration runtimeConfiguration);
}
