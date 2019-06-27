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
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Populates the (product or class) specific attributes of a characteristics from the result of the master data call to
 * the respective container we use for caching
 */
public class CharacteristicSpecificCacheContainerPopulator
		implements Populator<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer>
{

	@Override
	public void populate(final CPSMasterDataCharacteristicSpecific source,
			final CPSMasterDataCharacteristicSpecificContainer target)
	{
		populateCoreAttributes(source, target);
		populatePossibleValues(source, target);
	}

	protected void populatePossibleValues(final CPSMasterDataCharacteristicSpecific source,
			final CPSMasterDataCharacteristicSpecificContainer target)
	{
		if (source.getPossibleValueSpecifics() == null || source.getPossibleValueSpecifics().isEmpty())
		{
			target.setPossibleValueSpecifics(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataPossibleValueSpecific> possibleValues = new HashMap<>();
			for (final CPSMasterDataPossibleValueSpecific value : source.getPossibleValueSpecifics())
			{
				possibleValues.put(value.getId(), value);
			}
			target.setPossibleValueSpecifics(possibleValues);
		}
	}

	protected void populateCoreAttributes(final CPSMasterDataCharacteristicSpecific source,
			final CPSMasterDataCharacteristicSpecificContainer target)
	{
		target.setId(source.getId());
	}

}
