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
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;

import java.util.List;
import java.util.Set;


/**
 * Master data service for filling config model.
 */
public interface ConfigurationMasterDataService
{
	/**
	 * Gets localised name for item
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param id
	 *           id of the item
	 * @param type
	 *           type of the item
	 * @return localised name of the item
	 *
	 */
	String getItemName(final String kbId, final String id, final String type);

	/**
	 * Gets localised name for group. In case we are requesting the group name for class nodes, the class name will be
	 * returned as the class node doesn't carry UI groups, except for the 'general' dummy group
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param itemKey
	 *           id of the product or class
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param groupId
	 *           id of the group. Not relevant for class nodes
	 * @return localised name of the group
	 */
	String getGroupName(final String kbId, final String itemKey, final String itemType, final String groupId);

	/**
	 * Gives information whether product is multilevel
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param productId
	 *           id of the product
	 * @return whether product is multilevel
	 */
	boolean isProductMultilevel(final String kbId, final String productId);

	/**
	 * Gets localised master data for characteristic
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param characteristicId
	 *           id of the characteristic
	 * @return localised master data of the characteristic
	 */
	CPSMasterDataCharacteristicContainer getCharacteristic(String kbId, String characteristicId);

	/**
	 * Gets localised name for value
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param characteristicId
	 *           id of the characteristic
	 * @param valueId
	 *           id of the value
	 * @return localised name of the value
	 */
	String getValueName(String kbId, String characteristicId, String valueId);

	/**
	 * Gets characteristic ids for group. In case we are requesting the characteristics for class nodes, all
	 * characteristic assigned to the class node will be returned as the class node does not carry any group besides the
	 * 'general' dummy group
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param itemKey
	 *           id of the product or a class node
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param groupId
	 *           id of the group. Not relevant for class nodes
	 * @return list of characteristic ids belonging to the group
	 */
	List<String> getGroupCharacteristicIDs(String kbId, String itemKey, String itemType, String groupId);


	/**
	 * Get master data by KB id
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @return knowledgebase data for the given id
	 */
	CPSMasterDataKnowledgeBaseContainer getMasterData(final String kbId);


	/**
	 * Get pricing key for value
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param productId
	 *           id of the product
	 * @param characteristicId
	 *           id of the characteristic
	 * @param valueId
	 *           id of the value
	 * @return pricing key for the given value
	 */
	String getValuePricingKey(String kbId, String productId, String characteristicId, String valueId);

	/**
	 * Get all specific (that have a variant condition) possible value ids for a given characteristic. For class nodes,
	 * which don't carry variant key assignments, an empty Set is returned
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param itemKey
	 *           id of the product of of a class node
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param characteristicId
	 *           id of the characteristic
	 * @return list of possible value ids
	 */
	Set<String> getSpecificPossibleValueIds(String kbId, String itemKey, String itemType, String characteristicId);

	/**
	 * Get all possible value ids for a given characteristic
	 *
	 * @param kbId
	 *           id of the knowledgebase
	 * @param productId
	 *           id of the product
	 * @param characteristicId
	 *           id of the characteristic
	 * @return list of possible value ids
	 */
	Set<String> getPossibleValueIds(String kbId, String characteristicId);

	/**
	 * Checks if a characteristic is numeric
	 *
	 * @param kbId
	 *           Id of the knowledgebase
	 * @param csticId
	 *           Id of the characteristic
	 * @return whether characteristic is numeric
	 */
	boolean isCharacteristicNumeric(String kbId, String csticId);

	/**
	 * Retrieves the build number for the specified knowledgebase id
	 *
	 * @param kbId
	 *           Id of the knowledgebase
	 * @return build number of the knowledgebase
	 */
	Integer getKbBuildNumber(String kbId);

	/**
	 * Removes the knowledgebase from the hybris cache in order to force a reload from service
	 *
	 * @param kbId
	 *           Id of the knowledgebase
	 */
	void removeCachedKb(String kbId);

}
