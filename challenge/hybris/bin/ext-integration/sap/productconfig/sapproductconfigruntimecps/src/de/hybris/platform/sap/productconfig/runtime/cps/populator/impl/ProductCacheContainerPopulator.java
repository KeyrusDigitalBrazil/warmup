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
import de.hybris.platform.sap.productconfig.runtime.cps.cache.impl.KnowledgeBaseContainerCacheValueLoader;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataProduct;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Populates product master data cache during cache load. See also {@link KnowledgeBaseContainerCacheValueLoader}.
 */
public class ProductCacheContainerPopulator implements Populator<CPSMasterDataProduct, CPSMasterDataProductContainer>
{
	private Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecificConverter;

	@Override
	public void populate(final CPSMasterDataProduct source, final CPSMasterDataProductContainer target)
	{
		populateCoreAttributes(source, target);
		populateCharacteristicsSpecific(source, target);
		populateCharacteristicGroups(source, target);
	}



	protected void populateCharacteristicGroups(final CPSMasterDataProduct source, final CPSMasterDataProductContainer target)
	{
		if (source.getCharacteristicGroups() == null || source.getCharacteristicGroups().isEmpty())
		{
			target.setGroups(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataCharacteristicGroup> groups = new HashMap<>();
			for (final CPSMasterDataCharacteristicGroup group : source.getCharacteristicGroups())
			{
				groups.put(group.getId(), group);
			}
			target.setGroups(groups);
		}
	}

	protected void populateCharacteristicsSpecific(final CPSMasterDataProduct source, final CPSMasterDataProductContainer target)
	{
		if (source.getCharacteristicSpecifics() == null || source.getCharacteristicSpecifics().isEmpty())
		{
			target.setCstics(Collections.emptyMap());
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
			target.setCstics(characteristics);
		}
	}

	protected void populateCoreAttributes(final CPSMasterDataProduct source, final CPSMasterDataProductContainer target)
	{
		target.setId(source.getId());
		target.setName(source.getName());
		target.setMultilevel(source.isMultilevel());
	}

	protected Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> getCharacteristicSpecificConverter()
	{
		return characteristicSpecificConverter;
	}

	/**
	 * @param characteristicSpecificConverter
	 *           converter for CPS Cstic specific master data
	 */
	public void setCharacteristicSpecificConverter(
			final Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecificConverter)
	{
		this.characteristicSpecificConverter = characteristicSpecificConverter;
	}
}
