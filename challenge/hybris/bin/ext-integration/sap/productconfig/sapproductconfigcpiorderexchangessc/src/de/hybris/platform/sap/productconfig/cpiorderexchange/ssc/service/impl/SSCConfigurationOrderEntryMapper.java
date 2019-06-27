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
package de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ConfigurationOrderEntryMapper;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service.ExternalConfigurationParser;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigConditionModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHierarchyModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigInstanceModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigValueModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.sap.sce.kbrt.cfg_ext_cstic_val;
import com.sap.sce.kbrt.cfg_ext_cstic_val_seq;
import com.sap.sce.kbrt.cfg_ext_inst;
import com.sap.sce.kbrt.cfg_ext_inst_seq;
import com.sap.sce.kbrt.cfg_ext_part;
import com.sap.sce.kbrt.cfg_ext_part_seq;
import com.sap.sce.kbrt.cfg_ext_price_key;
import com.sap.sce.kbrt.cfg_ext_price_key_seq;
import com.sap.sce.kbrt.ext_configuration;


/**
 * Mapper service for mapping the external configuration to SCPI outbound format.
 */
public class SSCConfigurationOrderEntryMapper implements ConfigurationOrderEntryMapper
{

	private ExternalConfigurationParser externalConfigurationParser;

	@Override
	public boolean isMapperApplicable(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderItemModel outboundItem)
	{
		return outboundItem != null && entry.getExternalConfiguration() != null;
	}

	@Override
	public int mapConfiguration(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderModel orderModel,
			final String entryNumber)
	{
		final ext_configuration externalConfiguration = readExternalConfigFromEntry(entry);
		mapConfigurationHeader(externalConfiguration, orderModel, entryNumber);
		mapConfigInstances(orderModel, externalConfiguration.get_insts(), entryNumber);
		mapConfigHierarchies(orderModel, externalConfiguration.get_parts(), entryNumber);
		mapConfigValues(orderModel, externalConfiguration.get_cstics_values(), entryNumber);
		mapConfigConditions(orderModel, externalConfiguration.get_price_keys(), entryNumber);
		return externalConfiguration.get_insts().length();
	}

	protected void mapConfigConditions(final SAPCpiOutboundOrderModel orderModel, final cfg_ext_price_key_seq sourceConditions,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigConditionModel> targetConditions = new HashSet<>();
		for (final Enumeration<?> elements = sourceConditions.elements(); elements.hasMoreElements();)
		{
			final cfg_ext_price_key sourceCondition = (cfg_ext_price_key) elements.nextElement();
			final SAPCpiOutboundOrderItemConfigConditionModel targetCondition = new SAPCpiOutboundOrderItemConfigConditionModel();
			targetCondition.setConditionKey(sourceCondition.get_key());
			targetCondition.setConditionFactor(String.valueOf(sourceCondition.get_factor()));
			targetCondition.setInstanceId(sourceCondition.get_inst_id().toString());
			targetCondition.setConfigurationId(entryNumber);
			targetCondition.setSapCpiOutboundOrder(orderModel);
			targetConditions.add(targetCondition);
		}
		orderModel.getProductConfigConditions().addAll(targetConditions);
	}

	protected void mapConfigValues(final SAPCpiOutboundOrderModel orderModel, final cfg_ext_cstic_val_seq sourceValues,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigValueModel> targetValues = new HashSet<>();
		for (final Enumeration<?> elements = sourceValues.elements(); elements.hasMoreElements();)
		{
			final cfg_ext_cstic_val sourceValue = (cfg_ext_cstic_val) elements.nextElement();
			final SAPCpiOutboundOrderItemConfigValueModel targetValue = new SAPCpiOutboundOrderItemConfigValueModel();
			targetValue.setAuthor(sourceValue.get_author());
			targetValue.setCharacteristicId(sourceValue.get_charc());
			targetValue.setCharacteristicText(sourceValue.get_charc_txt());
			targetValue.setInstanceId(sourceValue.get_inst_id().toString());
			targetValue.setValueId(sourceValue.get_value());
			targetValue.setValueText(sourceValue.get_value_txt());
			targetValue.setConfigurationId(entryNumber);
			targetValue.setSapCpiOutboundOrder(orderModel);
			targetValues.add(targetValue);
		}
		orderModel.getProductConfigValues().addAll(targetValues);
	}

	protected void mapConfigHierarchies(final SAPCpiOutboundOrderModel orderModel, final cfg_ext_part_seq sourceItems,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigHierarchyModel> targetItems = new HashSet<>();
		for (final Enumeration<?> elements = sourceItems.elements(); elements.hasMoreElements();)
		{
			final SAPCpiOutboundOrderItemConfigHierarchyModel targetItem = new SAPCpiOutboundOrderItemConfigHierarchyModel();
			final cfg_ext_part sourceItem = (cfg_ext_part) elements.nextElement();
			targetItem.setAuthor(sourceItem.get_author());
			targetItem.setClassType(sourceItem.get_class_type());
			targetItem.setInstanceId(sourceItem.get_inst_id().toString());
			targetItem.setObjectKey(sourceItem.get_obj_key());
			targetItem.setObjectType(sourceItem.get_obj_type());
			targetItem.setParentId(sourceItem.get_parent_id().toString());
			targetItem.setBomNumber(sourceItem.get_pos_nr());
			targetItem.setSalesRelevant(sourceItem.is_sales_relevant_p());
			targetItem.setConfigurationId(entryNumber);
			targetItem.setSapCpiOutboundOrder(orderModel);
			targetItems.add(targetItem);
		}
		orderModel.getProductConfigHierarchies().addAll(targetItems);
	}

	protected void mapConfigurationHeader(final ext_configuration externalConfiguration, final SAPCpiOutboundOrderModel orderModel,
			final String entryNumber)
	{
		final SAPCpiOutboundOrderItemConfigHeaderModel configHeader = new SAPCpiOutboundOrderItemConfigHeaderModel();
		configHeader.setKbProfile(externalConfiguration.get_kb_profile_name());
		configHeader.setKbName(externalConfiguration.get_kb_name());
		configHeader.setKbVersion(externalConfiguration.get_kb_version());
		configHeader.setComplete(externalConfiguration.is_complete_p());
		configHeader.setConsistent(externalConfiguration.is_consistent_p());
		configHeader.setRootInstanceId(String.valueOf(externalConfiguration.get_root_id()));
		configHeader.setCommerceLeading(true);
		configHeader.setConfigurationId(entryNumber);
		configHeader.setExternalItemId(entryNumber);
		orderModel.getProductConfigHeaders().add(configHeader);
	}

	protected void mapConfigInstances(final SAPCpiOutboundOrderModel orderModel, final cfg_ext_inst_seq sourceItems,
			final String entryNumber)
	{
		final Set<SAPCpiOutboundOrderItemConfigInstanceModel> targetItems = new HashSet<>();
		for (final Enumeration<?> elements = sourceItems.elements(); elements.hasMoreElements();)
		{
			final cfg_ext_inst sourceItem = (cfg_ext_inst) elements.nextElement();
			final SAPCpiOutboundOrderItemConfigInstanceModel targetItem = new SAPCpiOutboundOrderItemConfigInstanceModel();
			targetItem.setAuthor(sourceItem.get_author());
			targetItem.setClassType(sourceItem.get_class_type());
			targetItem.setInstanceId(sourceItem.get_inst_id().toString());
			targetItem.setObjectKey(sourceItem.get_obj_key());
			targetItem.setObjectType(sourceItem.get_obj_type());
			targetItem.setQuantity(sourceItem.get_quantity());
			targetItem.setQuantityUnit(sourceItem.get_quantity_unit());
			targetItem.setComplete(sourceItem.is_complete_p());
			targetItem.setConsistent(sourceItem.is_consistent_p());
			targetItem.setConfigurationId(entryNumber);
			targetItem.setSapCpiOutboundOrder(orderModel);
			targetItems.add(targetItem);
		}
		orderModel.getProductConfigInstances().addAll(targetItems);
	}

	protected ext_configuration readExternalConfigFromEntry(final AbstractOrderEntryModel entry)
	{
		final String xml = entry.getExternalConfiguration();
		final String externalConfiguration = extractConfigurationFromXml(xml);
		return getExternalConfigurationParser().readExternalConfigFromString(externalConfiguration);
	}

	protected String extractConfigurationFromXml(final String xml)
	{
		final int index1 = xml.indexOf("<CONFIGURATION");
		final int index2 = xml.indexOf("</CONFIGURATION>", index1);
		return index1 == -1 || index2 == -1 ? "" : xml.substring(index1, index2 + "</CONFIGURATION>".length());
	}

	protected void initProductConfigSets(final SAPCpiOutboundOrderModel target)
	{
		if (target.getProductConfigHeaders() == null)
		{
			target.setProductConfigHeaders(new HashSet<SAPCpiOutboundOrderItemConfigHeaderModel>());
			target.setProductConfigInstances(new HashSet<SAPCpiOutboundOrderItemConfigInstanceModel>());
			target.setProductConfigHierarchies(new HashSet<SAPCpiOutboundOrderItemConfigHierarchyModel>());
			target.setProductConfigValues(new HashSet<SAPCpiOutboundOrderItemConfigValueModel>());
			target.setProductConfigConditions(new HashSet<SAPCpiOutboundOrderItemConfigConditionModel>());
		}
	}

	@Required
	public void setExternalConfigurationParser(final ExternalConfigurationParser externalConfigurationParser)
	{
		this.externalConfigurationParser = externalConfigurationParser;
	}

	protected ExternalConfigurationParser getExternalConfigurationParser()
	{
		return externalConfigurationParser;
	}
}
