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
package de.hybris.platform.sap.productconfig.facades.analytics;

import java.util.List;


/**
 * Facade for integration of analytical capbalilities with product configuration. A typical use case is show the percent
 * of customers that selected a certain option with the configuartion.
 */
public interface ConfigurationAnalyticsFacade
{

	/**
	 * Reads the analytical data for the given list of cstics.
	 *
	 * @param csticUiKeys
	 *           list of cstic ui keys for which analytical data should be obtained
	 * @param configId
	 *           the config id for which analytical data should be obtained
	 * @return analytical data
	 */
	List<AnalyticCsticData> getAnalyticData(List<String> csticUiKeys, String configId);
}
