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
package de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ConfigurationOrderEntryMapper;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSFlatListContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigConditionModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHierarchyModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigInstanceModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigValueModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;


public class CPSConfigurationOrderEntryMapper implements ConfigurationOrderEntryMapper
{
	private ObjectMapper objectMapper;

	@Override
	public boolean isMapperApplicable(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderItemModel outboundItem)
	{
		return outboundItem != null && entry.getExternalConfiguration() != null;
	}


	@Override
	public int mapConfiguration(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderModel orderModel,
			final String entryNumber)
	{
		final CPSCommerceExternalConfiguration externalCommerceConfiguration = readExternalConfigFromEntry(entry);
		final CPSExternalConfiguration externalConfiguration = externalCommerceConfiguration.getExternalConfiguration();
		final CPSFlatListContainer flatListContainer = createFlatListContainer(externalConfiguration);
		mapConfigurationHeader(externalConfiguration, orderModel, entryNumber);
		mapConfigInstances(orderModel, flatListContainer.getItems(), externalCommerceConfiguration.getUnitCodes(), entryNumber);
		mapConfigHierarchies(orderModel, flatListContainer.getSubItems(), entryNumber);
		mapConfigValues(orderModel, flatListContainer.getValues(), entryNumber);
		mapConfigConditions(orderModel, flatListContainer.getConditions(), entryNumber);
		return flatListContainer.getItems().size();
	}

	protected void mapConfigurationHeader(final CPSExternalConfiguration externalConfiguration,
			final SAPCpiOutboundOrderModel orderModel, final String entryNumber)
	{
		final SAPCpiOutboundOrderItemConfigHeaderModel configHeader = new SAPCpiOutboundOrderItemConfigHeaderModel();
		configHeader.setKbName(externalConfiguration.getKbKey().getName());
		configHeader.setKbVersion(externalConfiguration.getKbKey().getVersion());
		configHeader.setComplete(externalConfiguration.getRootItem().isComplete());
		configHeader.setConsistent(externalConfiguration.getRootItem().isConsistent());
		configHeader.setRootInstanceId(externalConfiguration.getRootItem().getId());
		configHeader.setCommerceLeading(true);
		configHeader.setConfigurationId(entryNumber);
		configHeader.setExternalItemId(entryNumber);
		configHeader.setSapCpiOutboundOrder(orderModel);
		orderModel.getProductConfigHeaders().add(configHeader);
	}

	protected void mapConfigValues(final SAPCpiOutboundOrderModel orderModel, final List<CPSExternalValue> sourceValues,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigValueModel> targetValues = new HashSet<>();

		for (final CPSExternalValue sourceValue : sourceValues)
		{
			final SAPCpiOutboundOrderItemConfigValueModel targetValue = new SAPCpiOutboundOrderItemConfigValueModel();
			targetValue.setAuthor(sourceValue.getAuthor());
			targetValue.setCharacteristicId(sourceValue.getParentCharacteristic().getId());
			targetValue.setInstanceId(sourceValue.getParentCharacteristic().getParentItem().getId());
			targetValue.setValueId(sourceValue.getValue());
			targetValue.setConfigurationId(entryNumber);
			targetValue.setSapCpiOutboundOrder(orderModel);
			targetValues.add(targetValue);
		}
		orderModel.getProductConfigValues().addAll(targetValues);
	}

	protected void mapConfigHierarchies(final SAPCpiOutboundOrderModel orderModel, final List<CPSExternalItem> sourceItems,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigHierarchyModel> targetItems = new HashSet<>();

		for (final CPSExternalItem sourceItem : sourceItems)
		{
			final SAPCpiOutboundOrderItemConfigHierarchyModel targetItem = new SAPCpiOutboundOrderItemConfigHierarchyModel();

			targetItem.setAuthor(sourceItem.getBomPositionAuthor());
			targetItem.setClassType(sourceItem.getBomPositionObjectKey().getClassType());
			targetItem.setInstanceId(sourceItem.getId());
			targetItem.setObjectKey(sourceItem.getObjectKey().getId());
			targetItem.setObjectType(sourceItem.getObjectKey().getType());
			targetItem.setParentId(sourceItem.getParentItem().getId());
			targetItem.setBomNumber(sourceItem.getBomPosition());
			targetItem.setSalesRelevant(sourceItem.isSalesRelevant());
			targetItem.setConfigurationId(entryNumber);
			targetItem.setSapCpiOutboundOrder(orderModel);
			targetItems.add(targetItem);
		}
		orderModel.getProductConfigHierarchies().addAll(targetItems);
	}

	protected void mapConfigInstances(final SAPCpiOutboundOrderModel orderModel, final List<CPSExternalItem> sourceItems,
			final Map<String, String> unitcodes, final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigInstanceModel> targetItems = new HashSet<>();

		for (final CPSExternalItem sourceItem : sourceItems)
		{
			final SAPCpiOutboundOrderItemConfigInstanceModel targetItem = new SAPCpiOutboundOrderItemConfigInstanceModel();
			targetItem.setAuthor(sourceItem.getBomPositionAuthor());
			targetItem.setClassType(sourceItem.getObjectKey().getClassType());
			targetItem.setInstanceId(sourceItem.getId());
			targetItem.setObjectKey(sourceItem.getObjectKey().getId());
			targetItem.setObjectType(sourceItem.getObjectKey().getType());
			targetItem.setQuantity(sourceItem.getQuantity().getValue().toString());
			targetItem.setQuantityUnit(unitcodes.get(sourceItem.getQuantity().getUnit()));
			targetItem.setComplete(sourceItem.isComplete());
			targetItem.setConsistent(sourceItem.isConsistent());
			targetItem.setConfigurationId(entryNumber);
			targetItem.setSapCpiOutboundOrder(orderModel);
			targetItems.add(targetItem);
		}
		orderModel.getProductConfigInstances().addAll(targetItems);
	}

	protected void mapConfigConditions(final SAPCpiOutboundOrderModel orderModel, final List<CPSVariantCondition> sourceConditions,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigConditionModel> targetConditions = new HashSet<>();

		for (final CPSVariantCondition sourceCondition : sourceConditions)
		{
			final SAPCpiOutboundOrderItemConfigConditionModel targetCondition = new SAPCpiOutboundOrderItemConfigConditionModel();
			targetCondition.setConditionKey(sourceCondition.getKey());
			targetCondition.setConditionFactor(sourceCondition.getFactor());
			targetCondition.setInstanceId(sourceCondition.getParentItemId());
			targetCondition.setConfigurationId(entryNumber);
			targetCondition.setSapCpiOutboundOrder(orderModel);
			targetConditions.add(targetCondition);
		}
		orderModel.getProductConfigConditions().addAll(targetConditions);
	}

	protected CPSCommerceExternalConfiguration readExternalConfigFromEntry(final AbstractOrderEntryModel entry)
	{
		final String externalConfiguration = entry.getExternalConfiguration();
		try
		{
			return getObjectMapper().readValue(externalConfiguration, CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing external configuration failed: expected JSON of CPSExternalConfiguration", e);
		}
	}

	protected CPSFlatListContainer createFlatListContainer(final CPSExternalConfiguration configuration)
	{
		final CPSFlatListContainer result = initializeFlatListContainer();
		fillListContainerForInstance(configuration.getRootItem(), result);
		return result;
	}

	protected CPSFlatListContainer initializeFlatListContainer()
	{
		final CPSFlatListContainer result = new CPSFlatListContainer();
		result.setItems(new ArrayList<>());
		result.setSubItems(new ArrayList<>());
		result.setValues(new ArrayList<>());
		result.setConditions(new ArrayList<>());
		return result;
	}

	protected void fillListContainerForInstance(final CPSExternalItem item, final CPSFlatListContainer listContainer)
	{
		listContainer.getItems().add(item);
		if (item.getParentItem() != null)
		{
			listContainer.getSubItems().add(item);
		}
		if (item.getVariantConditions() != null)
		{
			fillListContainerForConditions(item, listContainer);
		}
		for (final CPSExternalCharacteristic characteristic : item.getCharacteristics())
		{
			characteristic.setParentItem(item);
			fillListContainerForCharacteristic(characteristic, listContainer);
		}
		for (final CPSExternalItem subItem : item.getSubItems())
		{
			subItem.setParentItem(item);
			fillListContainerForInstance(subItem, listContainer);
		}
	}

	protected void fillListContainerForCharacteristic(final CPSExternalCharacteristic characteristic,
			final CPSFlatListContainer listContainer)
	{
		for (final CPSExternalValue value : characteristic.getValues())
		{
			value.setParentCharacteristic(characteristic);
			listContainer.getValues().add(value);
		}

	}

	protected void fillListContainerForConditions(final CPSExternalItem item, final CPSFlatListContainer listContainer)
	{
		final List<CPSVariantCondition> conditions = listContainer.getConditions();
		for (final CPSVariantCondition sourceCondition : item.getVariantConditions())
		{
			sourceCondition.setParentItemId(item.getId());
			conditions.add(sourceCondition);
		}
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	public void setObjectMapper(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}
}
