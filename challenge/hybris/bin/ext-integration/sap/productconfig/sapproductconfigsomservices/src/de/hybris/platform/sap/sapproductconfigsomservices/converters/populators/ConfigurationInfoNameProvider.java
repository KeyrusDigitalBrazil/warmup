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
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;


public interface ConfigurationInfoNameProvider
{
	/**
	 * @param cstic
	 *           Characteristic Model
	 * @return display name for the characteristic
	 */
	String getCharacteristicDisplayName(CsticModel cstic);

	/**
	 * @param cstic
	 *           Characteristic Model
	 * @param value
	 *           Value Model
	 * @return display name for the characteristic value
	 */
	String getValueDisplayName(CsticModel cstic, CsticValueModel value);
}
