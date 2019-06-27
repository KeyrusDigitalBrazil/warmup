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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSChoice;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;


public class ConflictAssumptionPopulator implements Populator<CPSChoice, ConflictingAssumptionModel>
{

	@Override
	public void populate(final CPSChoice source, final ConflictingAssumptionModel target)
	{
		target.setInstanceId(source.getItemId());
		target.setCsticName(source.getCharacteristicId());
		target.setValueName(source.getValue().getValue());
	}
}
