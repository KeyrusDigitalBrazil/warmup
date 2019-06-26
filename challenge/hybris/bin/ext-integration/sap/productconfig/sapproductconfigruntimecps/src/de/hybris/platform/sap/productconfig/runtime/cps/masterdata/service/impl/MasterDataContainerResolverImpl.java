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
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Utility class to extract specific data from {@link CPSMasterDataKnowledgeBaseContainer}.
 */
public class MasterDataContainerResolverImpl implements MasterDataContainerResolver
{

	private static final String CSTIC_TYPE_STRING = "string";
	protected static final String CSTIC_TYPE_FLOAT = "float";
	protected static final String CSTIC_TYPE_INTEGER = "integer";

	@Override
	public CPSMasterDataCharacteristicContainer getCharacteristic(final CPSMasterDataKnowledgeBaseContainer kBContainer,
			final String characteristicId)
	{
		final CPSMasterDataCharacteristicContainer csticContainer = kBContainer.getCharacteristics().get(characteristicId);
		if (csticContainer == null)
		{
			throw new IllegalStateException("Could not find characteristic for: " + characteristicId);
		}
		return csticContainer;
	}

	@Override
	public Set<String> getPossibleValueIds(final CPSMasterDataKnowledgeBaseContainer kbContainer, final String characteristicId)
	{
		final CPSMasterDataCharacteristicContainer cstic = kbContainer.getCharacteristics().get(characteristicId);
		if (cstic == null)
		{
			return Collections.emptySet();
		}
		return cstic.getPossibleValueGlobals().keySet();
	}

	@Override
	public String getValueName(final CPSMasterDataKnowledgeBaseContainer kBContainer, final String characteristicId,
			final String valueId)
	{
		final CPSMasterDataCharacteristicContainer cstic = getCharacteristic(kBContainer, characteristicId);
		if (!this.isCsticStringType(cstic))
		{
			// For cstics with a type different from "string", the value names are formatted in facade-layer.
			return null;
		}
		final CPSMasterDataPossibleValue value = cstic.getPossibleValueGlobals().get(valueId);
		if (value == null)
		{
			return valueId;
		}
		return value.getName();
	}

	protected boolean isCsticNumericType(final CPSMasterDataCharacteristicContainer csticContainer)
	{
		final String type = csticContainer.getType();
		return CSTIC_TYPE_FLOAT.equals(type) || CSTIC_TYPE_INTEGER.equals(type);
	}

	protected boolean isCsticStringType(final CPSMasterDataCharacteristicContainer cstic)
	{
		return CSTIC_TYPE_STRING.equals(cstic.getType());
	}

	@Override
	public boolean isCharacteristicNumeric(final CPSMasterDataKnowledgeBaseContainer kbContainer, final String csticId)
	{
		final CPSMasterDataCharacteristicContainer cstic = kbContainer.getCharacteristics().get(csticId);
		return isCsticNumericType(cstic);
	}

	@Override
	public List<String> getGroupCharacteristicIDs(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String itemKey,
			final String itemType, final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return getGroup(kbMasterData, itemKey, groupId).getCharacteristicIDs();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return getClass(kbMasterData, itemKey).getCharacteristicSpecifics().entrySet().stream().map(entry -> entry.getKey())
					.collect(Collectors.toList());
		}
		else
		{
			throw new IllegalArgumentException("Item type not allowed: " + itemType);
		}
	}

	protected CPSMasterDataCharacteristicGroup getGroup(final CPSMasterDataKnowledgeBaseContainer kbMasterData,
			final String itemKey, final String groupId)
	{
		final CPSMasterDataProductContainer product = getProduct(kbMasterData, itemKey);
		final CPSMasterDataCharacteristicGroup group = product.getGroups().get(groupId);
		if (group == null)
		{
			throw new IllegalStateException("Could not find group for: " + groupId);
		}
		return group;
	}

	protected CPSMasterDataClassContainer getClass(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		final CPSMasterDataClassContainer classContainer = kbMasterData.getClasses().get(id);
		if (classContainer == null)
		{
			throw new IllegalStateException("Could not find class for: " + id);
		}
		return classContainer;
	}

	protected String getClassName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		return getClass(kbMasterData, id).getName();
	}

	protected CPSMasterDataProductContainer getProduct(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		final CPSMasterDataProductContainer productContainer = kbMasterData.getProducts().get(id);
		if (productContainer == null)
		{
			throw new IllegalStateException("Could not find product for: " + id);
		}
		return productContainer;
	}

	protected String getProductName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		return getProduct(kbMasterData, id).getName();
	}

	@Override
	public String getItemName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id, final String type)
	{
		if (type == null)
		{
			throw new IllegalStateException("Type is null.");
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(type))
		{
			return getProductName(kbMasterData, id);
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(type))
		{
			return getClassName(kbMasterData, id);
		}
		throw new IllegalStateException("Invalide type: " + type);
	}

	@Override
	public String getGroupName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String itemKey, final String itemType,
			final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return getGroup(kbMasterData, itemKey, groupId).getName();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return getClassName(kbMasterData, itemKey);
		}
		else
		{
			throw new IllegalArgumentException("Item type not allowed: " + itemType);
		}
	}

	@Override
	public String getValuePricingKey(final CPSMasterDataKnowledgeBaseContainer masterData, final String productId,
			final String characteristicId, final String valueId)
	{
		final CPSMasterDataCharacteristicSpecificContainer csticSpecific = masterData.getProducts().get(productId).getCstics()
				.get(characteristicId);
		if (csticSpecific == null)
		{
			return null;
		}
		final CPSMasterDataPossibleValueSpecific value = csticSpecific.getPossibleValueSpecifics().get(valueId);
		if (value == null)
		{
			return null;
		}
		return value.getVariantConditionKey();
	}

	@Override
	public Set<String> getSpecificPossibleValueIds(final CPSMasterDataKnowledgeBaseContainer masterData, final String productId,
			final String itemType, final String characteristicId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			final CPSMasterDataProductContainer cpsMasterDataProductContainer = masterData.getProducts().get(productId);
			final CPSMasterDataCharacteristicSpecificContainer csticSpecific = cpsMasterDataProductContainer.getCstics()
					.get(characteristicId);
			if (csticSpecific == null)
			{
				return Collections.emptySet();
			}
			return retrieveIdsForPossibleValueSpecificsWithVariantCondition(csticSpecific);
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return Collections.emptySet();
		}
		else
		{
			throw new IllegalArgumentException("Unknown item type: " + itemType);
		}
	}

	protected Set<String> retrieveIdsForPossibleValueSpecificsWithVariantCondition(
			final CPSMasterDataCharacteristicSpecificContainer csticSpecific)
	{
		final Set<String> possibleValueIds = new HashSet<>();
		final Map<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecifics = csticSpecific.getPossibleValueSpecifics();
		if (MapUtils.isNotEmpty(possibleValueSpecifics))
		{
			for (final Map.Entry<String, CPSMasterDataPossibleValueSpecific> entry : possibleValueSpecifics.entrySet())
			{
				if (StringUtils.isNotEmpty(entry.getValue().getVariantConditionKey()))
				{
					possibleValueIds.add(entry.getKey());
				}
			}
		}
		return possibleValueIds;
	}

	@Override
	public boolean isProductMultilevel(final CPSMasterDataKnowledgeBaseContainer masterData, final String id)
	{
		return getProduct(masterData, id).isMultilevel();
	}

	@Override
	public Integer getKbBuildNumber(final CPSMasterDataKnowledgeBaseContainer kbContainer)
	{
		return kbContainer.getHeaderInfo().getBuild();
	}
}
