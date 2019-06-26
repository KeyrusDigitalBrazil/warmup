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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;


/**
 * Mapper class for mapping {@link ProductConfigMessage} to {@link ProductConfigMessageData}
 */
public interface ConfigurationMessageMapper
{
	/**
	 * maps messages from cstic model to cstic data to display on UI
	 * 
	 * @param cstciData
	 * @param csticModel
	 */
	void mapMessagesFromModelToData(CsticData cstciData, CsticModel csticModel);

	/**
	 * maps messages from cstic value model to cstic value data to display on UI
	 * 
	 * @param cstciValueData
	 * @param csticValueModel
	 */
	void mapMessagesFromModelToData(CsticValueData cstciValueData, CsticValueModel csticValueModel);

	/**
	 * maps product configuration messages from model to data ( on product level)
	 * 
	 * @param configData
	 * @param configModel
	 */
	void mapMessagesFromModelToData(ConfigurationData configData, ConfigModel configModel);
}
