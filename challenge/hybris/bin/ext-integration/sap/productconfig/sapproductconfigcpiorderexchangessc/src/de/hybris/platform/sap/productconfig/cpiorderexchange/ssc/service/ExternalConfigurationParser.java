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
package de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service;

import com.sap.sce.kbrt.ext_configuration;


/**
 * Parser for the SSC generated product configuration XML
 */
public interface ExternalConfigurationParser
{
	/**
	 * Create external Configuration from XML string.
	 *
	 * @param str
	 *           XML holding configuration
	 * @return external configuration
	 */
	ext_configuration readExternalConfigFromString(String str);


}
