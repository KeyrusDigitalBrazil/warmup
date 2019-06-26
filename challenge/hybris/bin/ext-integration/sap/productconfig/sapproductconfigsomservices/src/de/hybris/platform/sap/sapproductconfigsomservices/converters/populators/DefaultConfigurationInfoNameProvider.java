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



public class DefaultConfigurationInfoNameProvider implements ConfigurationInfoNameProvider
{


	@Override
	public String getCharacteristicDisplayName(final CsticModel cstic)
	{
		return cstic.getLanguageDependentName();
	}


	@Override
	public String getValueDisplayName(final CsticModel cstic, final CsticValueModel value)
	{
		return value.getLanguageDependentName();
	}

}
