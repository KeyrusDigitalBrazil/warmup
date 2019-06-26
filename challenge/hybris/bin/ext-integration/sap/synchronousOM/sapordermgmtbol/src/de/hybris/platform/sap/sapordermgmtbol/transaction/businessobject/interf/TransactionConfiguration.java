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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;

import java.util.Map;


/**
 * The TransactionConfiguration interface provides sales-relevant settings. (e.g. sales organisation)
 * 
 */
public interface TransactionConfiguration extends SalesDocumentConfiguration
{

	/**
	 * Constant to identify the ID field in the Result Set
	 */
	String ID = "ID";

	/**
	 * Constant to identify the Description field in the Result Set
	 */
	String DESCRIPTION = "DESCRIPTION";


	/**
	 * Constants for Pricing Subtotals - Subtotal1
	 */
	String SUBTOTAL1 = "SUBTOTAL1";

	/**
	 * Constants for Pricing Subtotals - Subtotal2
	 */
	String SUBTOTAL2 = "SUBTOTAL2";

	/**
	 * Constants for Pricing Subtotals - Subtotal3
	 */
	String SUBTOTAL3 = "SUBTOTAL3";

	/**
	 * Constants for Pricing Subtotals - Subtotal4
	 */
	String SUBTOTAL4 = "SUBTOTAL4";

	/**
	 * Constants for Pricing Subtotals - Subtotal5
	 */
	String SUBTOTAL5 = "SUBTOTAL5";

	/**
	 * Constants for Pricing Subtotals - Subtotal6
	 */
	String SUBTOTAL6 = "SUBTOTAL6";


	/**
	 * Delivery Types
	 * <ul>
	 * <li>CRM table crmc_ship_cond, valuehelp crm_ship_cond</li>
	 * <li>ERP table tvsb, valuehelp h_tvsb</li>
	 * </ul>
	 * 
	 * @param consideringWECCustomizing
	 *           <ul>
	 *           <li>false = return all values from the corresponding backend customizing table
	 *           <li>true = restrict the values from the backend, to those which are explicitly allowed in the Webchannel
	 *           Customizing</li>
	 *           </ul>
	 * @return a new Map with the allowed DeliverTypes and description
	 * @throws CommunicationException
	 *            in case backend-error
	 */
	Map<String, String> getAllowedDeliveryTypes(boolean consideringWECCustomizing) throws CommunicationException;

	/**
	 * Gets language dependent short ID of customer purchase order type. Also see
	 * {@link TransactionConfiguration#setCustomerPurchOrderType(String)}
	 * 
	 * @return ID of purchase order type
	 */
	String getCustomerPurchOrderType();

	/**
	 * Sets language dependent short ID of customer purchase order type. This is sent to ERP when creating an order. See
	 * header->Purchase Order Data <br>
	 * Currently not used in CRM
	 * 
	 * @param customerPurchOrderType
	 *           customerPurchOrderType short ID
	 */
	void setCustomerPurchOrderType(String customerPurchOrderType);

	/**
	 * Gets delivery block ID. Also see {@link TransactionConfiguration#setDeliveryBlock(String)}
	 * 
	 * @return delivery block ID
	 */
	String getDeliveryBlock();

	/**
	 * Sets delivery block ID which is sent to ERP when creating an order. Currently not used in CRM. <br>
	 * If a non-initial delivery block is maintained, orders need to be released in ERP before follow-up processes can
	 * start.
	 * 
	 * @param deliveryBlock
	 *           delivery block ID
	 */
	void setDeliveryBlock(String deliveryBlock);

	/**
	 * Returns the ISO 639 language Code (2 chars) in lower case.
	 * 
	 * @return isoLanguage
	 */
	String getLanguageIso();

	/**
	 * @return the text ID under which header texts are stored in the backend
	 */
	String getHeaderTextID();

	/**
	 * @return the text ID under which item texts are stored in the backend
	 */
	String getItemTextID();

	/**
	 * @param headerTextId
	 *           the text ID under which header texts are stored in the backend
	 */
	void setHeaderTextID(String headerTextId);

	/**
	 * @param itemTextId
	 *           the text ID under which item texts are stored in the backend
	 */
	void setItemTextID(String itemTextId);

	/**
	 * Returns the property mergeIdenticalProducts, which is a WCB setting. True if identical products (e.g. same
	 * product-id, same GUID, same unit) shall be merged. This only applies to the Basket, but not to the Order.
	 * 
	 * @return mergeIdenticalProducts
	 */
	boolean isMergeIdenticalProducts();

	/**
	 * Sets the mergeIdenticalProducts, which is a WCB setting. True if identical products (e.g. same product-id, same
	 * GUID, same unit) shall be merged
	 * 
	 * @param mergeIdenticalProducts
	 *           if <code>true</code> products will be merged in basket
	 */
	void setMergeIdenticalProducts(boolean mergeIdenticalProducts);


	/**
	 * Provides information which source is used to determine the net value without freight (e.g. any subtotal)
	 * 
	 * @return sourceForNetValueWithoutFreight source for net value without freight
	 */
	String getSourceForNetValueWithoutFreight();

	/**
	 * Provides information which source is used to determine the freight value (e.g. any subtotal)
	 * 
	 * @return sourceForFreightItem
	 */
	String getSourceForFreightItem();








}
