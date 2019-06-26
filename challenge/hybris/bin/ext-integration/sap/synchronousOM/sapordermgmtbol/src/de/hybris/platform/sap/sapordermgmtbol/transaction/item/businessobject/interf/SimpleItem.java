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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.TechKey;

import java.math.BigDecimal;
import java.util.Map;


/**
 * this interface defines the most common attributes of an item (e.g. product, quantity, ...). <br>
 *
 */
public interface SimpleItem extends BusinessObject, Cloneable, Comparable<SimpleItem>
{

	/**
	 * Returns the product (name) of this item.<br>
	 *
	 * @return product name
	 */
	String getProductId();

	/**
	 * Returns the id / TechKey / GUID of the product.<br>
	 *
	 * @return TechKey / GUID of this product
	 */
	TechKey getProductGuid();

	/**
	 * Sets the product for this item.<br>
	 *
	 * @param productId
	 *           product name
	 */
	void setProductId(String productId);

	/**
	 * Sets the product guid for this item.<br>
	 *
	 * @param productGuid
	 *           TechKey/guid of the product
	 */
	void setProductGuid(TechKey productGuid);

	/**
	 * If product of the item changes (e.g. was a notebook and is now a monitor), we reuse the item but we need to know
	 * whether the product changed
	 *
	 * @return true if product was changed
	 */
	boolean isProductChanged();

	/**
	 * If product of the item changes (e.g. was a notebook and is now a monitor), we reuse the item but we need to know
	 * whether the product changed
	 *
	 * @param productChanged
	 *           if <code>true</code>, we conside the item to be changted
	 */
	void setProductChanged(boolean productChanged);

	/**
	 * Sets parent Id for an item. For example for the sub item, free good item.<br>
	 *
	 * @param parentId
	 *           TechKey or null/TechKey.EMPTY_KEY
	 */
	void setParentId(TechKey parentId);

	/**
	 * Returns the TechKey (guid) of the parent item if this item is a sub item.<br>
	 * If there is no parent item, null or TechKey.EMPTY_KEY is returned.
	 *
	 * @return TechKey of the parent item
	 */
	TechKey getParentId();

	/**
	 * Get descriptions on the item level.
	 *
	 * @return description
	 */
	String getDescription();

	/**
	 * Returns the quantity of this item.<br>
	 *
	 * @return quantity
	 */
	BigDecimal getQuantity();

	/**
	 * Returns the quantity of this item.<br>
	 *
	 * @return quantity
	 */
	BigDecimal getLastQuantity();

	/**
	 * Returns the UOM (Unit of Measure) of this item.<br>
	 * This unit is not localised, e.g. ST
	 *
	 * @return UOM
	 */
	String getUnit();

	/**
	 * Sets Description.<br>
	 *
	 * @param description
	 *           item description
	 */
	void setDescription(String description);

	/**
	 * Sets an item quantity.<br>
	 *
	 * @param quantity
	 *           value to set
	 */
	void setQuantity(BigDecimal quantity);

	/**
	 * Set the unit (UOM) for this item.<br>
	 * The value is not localised, e.g. ST
	 *
	 * @param unit
	 *           UOM
	 */
	void setUnit(String unit);

	/**
	 * Return the position number of this item.<br>
	 * The position is determined in the back end.
	 *
	 * @return numberInt/position
	 */
	int getNumberInt();

	/**
	 * Sets the numberInt/position of the item.<br>
	 * The position is determined in the back end and set to the item. It cannot be changed.
	 *
	 * @param numberInt
	 *           position
	 */
	void setNumberInt(int numberInt);

	/**
	 * Type safe getter for the extension map<br>
	 *
	 * @return extension map attached to this item
	 */
	Map<String, Object> getTypedExtensionMap();


}
