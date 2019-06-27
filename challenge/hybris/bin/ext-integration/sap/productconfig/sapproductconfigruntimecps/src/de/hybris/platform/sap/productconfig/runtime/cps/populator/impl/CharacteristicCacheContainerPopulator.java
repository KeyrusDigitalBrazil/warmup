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
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Responsible to fill attributes of {@link CPSMasterDataCharacteristicContainer} (which is used for caching) according
 * to the characteristics master data representation
 */
public class CharacteristicCacheContainerPopulator
		implements Populator<CPSMasterDataCharacteristic, CPSMasterDataCharacteristicContainer>
{

	@Override
	public void populate(final CPSMasterDataCharacteristic source, final CPSMasterDataCharacteristicContainer target)
	{
		populateCoreAttributes(source, target);
		populatePossibleValuesGlobals(source, target);
	}

	protected void populatePossibleValuesGlobals(final CPSMasterDataCharacteristic source,
			final CPSMasterDataCharacteristicContainer target)
	{
		if (source.getPossibleValues() == null || source.getPossibleValues().isEmpty())
		{
			target.setPossibleValueGlobals(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataPossibleValue> possibleValues = new HashMap<>();
			for (final CPSMasterDataPossibleValue value : source.getPossibleValues())
			{
				possibleValues.put(value.getId(), value);
			}
			target.setPossibleValueGlobals(possibleValues);
		}
	}

	protected void populateCoreAttributes(final CPSMasterDataCharacteristic source,
			final CPSMasterDataCharacteristicContainer target)
	{
		target.setId(source.getId());
		target.setLength(source.getLength());
		target.setMultiValued(source.isMultiValued());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setNumberDecimals(source.getNumberDecimals());
		target.setType(source.getType());
		target.setCaseSensitive(source.isCaseSensitive());
		target.setEntryFieldMask(source.getEntryFieldMask());
		target.setAdditionalValues(source.isAdditionalValues());
		target.setUnitOfMeasure(source.getUnitOfMeasure());
	}

}
