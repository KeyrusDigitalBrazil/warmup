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
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataClass;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Responsible for translating the master data representation of a class (generalization of product) into its cache
 * representation
 */
public class ClassCacheContainerPopulator implements Populator<CPSMasterDataClass, CPSMasterDataClassContainer>
{
	private Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecificConverter;

	@Override
	public void populate(final CPSMasterDataClass source, final CPSMasterDataClassContainer target)
	{
		populateCoreAttributes(source, target);
		populateCharacteristicsSpecific(source, target);
	}

	protected void populateCharacteristicsSpecific(final CPSMasterDataClass source, final CPSMasterDataClassContainer target)
	{
		if (source.getCharacteristicSpecifics() == null || source.getCharacteristicSpecifics().isEmpty())
		{
			target.setCharacteristicSpecifics(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataCharacteristicSpecificContainer> characteristics = new HashMap<>();
			for (final CPSMasterDataCharacteristicSpecific characteristic : source.getCharacteristicSpecifics())
			{
				final CPSMasterDataCharacteristicSpecificContainer convertedCharacteristic = getCharacteristicSpecificConverter()
						.convert(characteristic);
				characteristics.put(convertedCharacteristic.getId(), convertedCharacteristic);
			}
			target.setCharacteristicSpecifics(characteristics);
		}
	}

	protected void populateCoreAttributes(final CPSMasterDataClass source, final CPSMasterDataClassContainer target)
	{
		target.setId(source.getId());
		target.setName(source.getName());
	}

	protected Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> getCharacteristicSpecificConverter()
	{
		return characteristicSpecificConverter;
	}

	/**
	 * @param characteristicSpecificConverter
	 *           Converter characteristic master data on product or class level -> cache representation
	 */
	public void setCharacteristicSpecificConverter(
			final Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecificConverter)
	{
		this.characteristicSpecificConverter = characteristicSpecificConverter;
	}

}
