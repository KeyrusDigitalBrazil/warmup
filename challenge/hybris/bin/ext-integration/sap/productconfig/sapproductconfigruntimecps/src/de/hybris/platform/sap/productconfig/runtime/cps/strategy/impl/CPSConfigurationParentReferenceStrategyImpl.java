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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CPSConfigurationParentReferenceStrategy;

import java.util.List;


/**
 * Default implementation of {@link CPSConfigurationParentReferenceStrategy}
 */
public class CPSConfigurationParentReferenceStrategyImpl implements CPSConfigurationParentReferenceStrategy
{
	@Override
	public void addParentReferences(final CPSConfiguration cpsConfig)
	{
		final CPSItem rootItem = cpsConfig.getRootItem();
		rootItem.setParentConfiguration(cpsConfig);
		addParentReferencesForSubItems(rootItem);
		addParentReferencesForCharacteristics(rootItem);
		addParentReferencesForCharacteristicGroups(rootItem);
	}

	protected void addParentReferencesForSubItems(final CPSItem item)
	{
		final List<CPSItem> subItems = item.getSubItems();
		if (subItems != null)
		{
			for (final CPSItem subItem : subItems)
			{
				subItem.setParentConfiguration(item.getParentConfiguration());
				subItem.setParentItem(item);
				addParentReferencesForCharacteristics(subItem);
				addParentReferencesForCharacteristicGroups(subItem);
				addParentReferencesForSubItems(subItem);
			}
		}
	}

	protected void addParentReferencesForCharacteristics(final CPSItem item)
	{
		final List<CPSCharacteristic> characteristics = item.getCharacteristics();
		if (characteristics != null)
		{
			for (final CPSCharacteristic characteristic : characteristics)
			{
				characteristic.setParentItem(item);
				addParentReferencesForCharacteristicValues(characteristic);
			}
		}
	}

	protected void addParentReferencesForCharacteristicGroups(final CPSItem item)
	{
		final List<CPSCharacteristicGroup> characteristicGroups = item.getCharacteristicGroups();
		if (characteristicGroups != null)
		{
			for (final CPSCharacteristicGroup characteristicGroup : characteristicGroups)
			{
				characteristicGroup.setParentItem(item);
			}
		}
	}

	protected void addParentReferencesForCharacteristicValues(final CPSCharacteristic characteristic)
	{
		final List<CPSValue> values = characteristic.getValues();
		final List<CPSPossibleValue> possibleValues = characteristic.getPossibleValues();
		if (values != null)
		{
			for (final CPSValue value : values)
			{
				value.setParentCharacteristic(characteristic);
			}
		}
		if (possibleValues != null)
		{
			for (final CPSPossibleValue possibleValue : possibleValues)
			{
				possibleValue.setParentCharacteristic(characteristic);
			}
		}
	}
}
