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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.util;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProduct;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class SalesDocumentUtil
{

	private SalesDocumentUtil()
	{

	}

	public static void copyAttributesOfItem(final Item source, final Item destination)
	{

		destination.setProductGuid(source.getProductGuid());
		destination.setProductId(source.getProductId());
		destination.setUnit(source.getUnit());
		destination.setQuantity(source.getQuantity());

		destination.setDeliveryPriority(source.getDeliveryPriority());
		Text text = source.getText();
		if (null != text)
		{
			text = text.clone();
			text.setHandle("");
		}
		destination.setText(text);

		// Need to be passed for Grid items
		destination.setConfigType(source.getConfigType());
		destination.setParentId(source.getParentId());

		ShipTo shipTo = source.getShipTo();
		if (shipTo != null)
		{
			shipTo = shipTo.clone();
		}
		destination.setShipTo(shipTo);

		PartnerList partnerList = source.getPartnerListData();
		if (null != partnerList)
		{
			partnerList = partnerList.clone();
		}
		destination.setPartnerListData(partnerList);

		destination.setConfigurable(source.isConfigurable());

		destination.setItmTypeUsage(source.getItmTypeUsage());
	}

	/**
	 * Temporary helper method to create a HashMap from incoming Set of key/value-pairs. Method is necessary as long as
	 * getExtensionMap is not available in the BusinessObject-interface to transfer an extension map from one object to
	 * another one.
	 *
	 * @param set
	 *           Set of keys/values
	 * @return new HashMap with all keys/values of the Set
	 */
	public static Map<String, Object> createExtensionMap(final Set<Entry<String, Object>> set)
	{
		if ((set == null) || (set.isEmpty()))
		{
			return new HashMap(0);
		}

		final Map<String, Object> newMap = new HashMap();
		final Iterator<Map.Entry<String, Object>> extIter = set.iterator();
		while (extIter.hasNext())
		{
			final Entry<String, Object> extEntry = extIter.next();

			newMap.put(extEntry.getKey(), extEntry.getValue());
		}
		return newMap;
	}

	/**
	 * keep previous messages except those for current item
	 */
	public static MessageList filterPreviousMessages(final MessageList bufferedMessges, final Item modifiedItem)
	{

		final Iterator<Message> messages = bufferedMessges.iterator();

		final MessageList result = new MessageList();

		while (messages.hasNext())
		{
			final Message message = messages.next();

			if (modifiedItem == null || !message.getTechKey().equals(modifiedItem.getTechKey()))
			{

				result.add(message);

			}

		}

		return result;
	}

	public static List<TechKey> determineItemsToDelete(final ItemList items)
	{

		final List<TechKey> techkeysToDelete = new ArrayList();

		for (final Item item : items)
		{
			if (item.isProductEmpty() && !TechKey.isEmpty(item.getTechKey()))
			{
				techkeysToDelete.add(item.getTechKey());
			}
		}
		return techkeysToDelete;

	}

	public static boolean applyAlternativeProducts(final ItemList items) throws CommunicationException
	{
		boolean updateNeeded = false;

		for (final Item item : items)
		{
			for (final AlternativeProduct prod : item.getAlternativProductList())
			{
				updateNeeded = true;
				SalesDocumentUtil.applyAlternativeProductToItem(item, prod);
				break;
			}
		}

		return updateNeeded;
	}

	public static Map<TechKey, Message> checkQuantityUOM(final ItemList items, final Converter converter)
			throws BusinessObjectException
	{
		Map<TechKey, Message> messages = null;
		for (final Item itm : items)
		{
			final String unit = itm.getUnit();
			final BigDecimal quantity = itm.getQuantity();
			if (unit != null && !unit.isEmpty() && quantity != null)
			{
				final BigDecimal roundedQuantity = quantity.setScale(converter.getUnitScale(unit), BigDecimal.ROUND_HALF_UP);
				final boolean roundingNeeded = 0 != roundedQuantity.compareTo(quantity);
				messages = isRoundingNeeded(messages, itm, unit, roundedQuantity, roundingNeeded, converter);
			}
		}
		if (messages == null)
		{
			messages = Collections.emptyMap();
		}
		return messages;
	}

	/**
	 * @param messages
	 * @param itm
	 * @param unit
	 * @param roundedQuantity
	 * @param roundingNeeded
	 * @return
	 * @throws BusinessObjectException
	 */
	private static Map<TechKey, Message> isRoundingNeeded(final Map<TechKey, Message> messages, final Item itm, final String unit,
			final BigDecimal roundedQuantity, final boolean roundingNeeded, final Converter converter) throws BusinessObjectException
	{
		Map<TechKey, Message> mes = messages;
		BigDecimal roundQuantity = roundedQuantity;
		if (roundingNeeded)
		{
			final BigDecimal minimumScaleValue = converter.getMinimumScaleValue(unit);
			if (roundQuantity.compareTo(minimumScaleValue) < 0)
			{
				roundQuantity = minimumScaleValue;
			}
			itm.setQuantity(roundQuantity);
			final String[] args = new String[1];
			args[0] = converter.convertUnitKey2UnitID(unit);
			// Quantity was rounded because unit of measure PC allows
			// fewer decimal places than unit of measure EA
			final String key = "sapsalestransactions.bo.message.item.quantityUOM";
			final Message message = new Message(Message.WARNING, key, args, "UNIT_CONVERSION");
			if (mes == null)
			{
				mes = new HashMap();
			}
			mes.put(itm.getTechKey(), message);
		}
		return mes;
	}

	public static void applyAlternativeProductToItem(final Item item, final AlternativeProduct prod) throws CommunicationException
	{

		item.applyAlternativeProduct(prod.getSystemProductGUID(), prod.getSystemProductId());
		item.setSubstitutionReasonId(prod.getSubstitutionReasonId());
	}

	public static void resetItemWithChangedProduct(final Item item)
	{
		item.setProductGuid(TechKey.EMPTY_KEY);
		item.setUnit(null);
		item.setDescription(null);
		item.setProductChanged(false);
	}

	/**
	 * enhance current item messages with current item techKey to keep reference to them in eventual basket modification
	 * and update
	 */
	public static void addTeckKeyToMessages(final Item modifiedItem, final MessageList messageList)
	{

		final Iterator<Message> messages = messageList.iterator();

		while (messages.hasNext())
		{

			if (modifiedItem != null)
			{
				messages.next().setTechKey(modifiedItem.getTechKey());
			}
			else
			{

				messages.next();
			}

		}

	}

	public static void prepareItemWithChangedProduct(final Item item)
	{
		if (item.isProductChanged())
		{
			SalesDocumentUtil.resetItemWithChangedProduct(item);
		}
	}


}
