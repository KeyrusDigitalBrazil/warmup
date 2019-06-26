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

import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocumentItem;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Represents the back-end's view of the items of a sales document. <br>
 *
 */
public interface ItemBase extends SimpleItem
{

	/**
	 * this enumeration defines the different kinds of item usages. <br>
	 *
	 */
	public enum ItemUsage
	{
		/**
		 * This item has been substituted.
		 */
		SUBST_1_TO_N("13"),
		/**
		 * ATP usage.
		 */
		ATP("ATP"),
		/**
		 * Return usage.
		 */
		RETURN("07"),
		/**
		 * Kit usage.
		 */
		KIT("14"),
		/**
		 * Inclusive free good (Buy 10, pay 9).
		 */
		FREE_GOOD_INCL("09"),
		/**
		 * Exclusive free good (Buy 10 X, get 1 Y).
		 */
		FREE_GOOD_EXCL("10"),
		/**
		 * A configurable product.
		 */
		CONFIGURATION("01"),
		/**
		 * Bill of material.
		 */
		BOM("08"),
		/**
		 * No item usage.
		 */
		NONE("");

		private final String value;

		ItemUsage(final String string)
		{
			value = string;
		}

		/**
		 * This method parses an ItemUsage out of the given String. NONE if it is an invalid value.<br>
		 *
		 * @param string
		 *           value which should be translated to an ItemUsage
		 * @return ItemUsage
		 */
		public static ItemUsage fromString(final String string)
		{
			for (final ItemUsage usage : ItemUsage.values())
			{
				if (usage.value.equalsIgnoreCase(string))
				{
					return usage;
				}
			}
			return NONE;
		}
	}

	/**
	 * Constant for Item configuration type <i>Product Variant</i> used to identify product variants
	 */
	String ITEM_CONFIGTYPE_VARIANT = "B";

	/**
	 * Adds a <code>ConnectedDocumentItem</code> to the predecessor list. A <code>null</code> reference passed to this
	 * function will be ignored, in this case nothing will happen.
	 *
	 * @param predecessorData
	 *           ConnectedDocumentItem to be added to the predecessor list
	 */
	void addPredecessor(ConnectedDocumentItem predecessorData);

	/**
	 * Adds a <code>ConnectedDocumentItem</code> to the successor list. A <code>null</code> reference passed to this
	 * function will be ignored, in this case nothing will happen.
	 *
	 * @param successorData
	 *           ConnectedDocument to be added to the successor list
	 */
	void addSuccessor(ConnectedDocumentItem successorData);

	/**
	 * Performs a shallow copy of this object. Because of the fact that nearly all fields of this object consist of
	 * immutable objects like <code>String</code> and <code>TechKey</code> or primitive types the shallow copy is nearly
	 * identical with a deep copy. The only difference is that the property <code>itemDelivery</code> (a list) is backed
	 * by the same data.
	 *
	 *
	 * @return shallow copy of this object
	 */
	@SuppressWarnings("squid:S1161")
	Object clone();

	/**
	 * Create a <code>ConnectedDocumentItemData</code> object
	 *
	 * @return ConnectedDocumentItemData object
	 * @see ConnectedDocumentItem
	 */
	ConnectedDocumentItem createConnectedDocumentItemData();

	/**
	 * Create a new ScheduleLine object.
	 *
	 * @return newly created schedule line object
	 */
	Schedline createScheduleLine();

	/**
	 * Creates a text object. This method is used by back end objects to get instances of the text object.
	 *
	 * @return newly created text object
	 */
	Text createText();

	/**
	 * Get the confirmed delivery date for the item.
	 *
	 * @return confirmed delivery date
	 */
	Date getConfirmedDeliveryDate();

	/**
	 * Get the confirmed quantity for the item.
	 *
	 * @return confirmed quantity
	 */
	BigDecimal getConfirmedQuantity();

	/**
	 * Gets the currency for the item.
	 *
	 * @return the currency
	 */
	String getCurrency();

	/**
	 * Get the delivered quantity for this item.
	 *
	 * @return delivered quantity as BigDecimal
	 */
	BigDecimal getDeliveredQuantity();

	/**
	 * Get the delivered quantity unit for this item.
	 *
	 * @return delivered quantity unit of measure
	 */
	String getDeliveredQuantityUnit();

	/**
	 * Returns the delivery priority key
	 *
	 * @return String
	 */
	String getDeliveryPriority();



	/**
	 * Retrieves the free quantity. Only relevant for inclusive free goods.
	 *
	 * @return the free quantity in local specific format.
	 */
	BigDecimal getFreeQuantity();

	/**
	 * Return the freight value / shipping costs.<br>
	 *
	 * @return BigDecimal of freight value
	 */
	BigDecimal getFreightValue();

	/**
	 * Returns the price of this item inclusive taxes.<br>
	 *
	 * @return gross value
	 */
	BigDecimal getGrossValue();

	/**
	 * Returns the item type usage of the back end.<br>
	 *
	 * @return ItemtypeUsage
	 */
	String getItmTypeUsage();

	/**
	 * Returns the item usage of this item.<br>
	 *
	 * @return ItemUsage
	 * @see ItemUsage
	 */
	ItemUsage getItemUsage();

	/**
	 * Price of one base unit (unit price) of this item without taxes.<br>
	 *
	 * @return Price of 1 unit without taxes
	 */
	BigDecimal getNetPrice();

	/**
	 * Returns the unit that is used for the unit price.<br>
	 * e.g. PC if the price is calculated per 2 PC
	 *
	 * @return unit of the net price
	 */
	String getNetPriceUnit();

	/**
	 * Returns the quantity of the unit that is used for the unit price.<br>
	 * e.g. 2 if the price is calculated per 2 PC
	 *
	 * @return quantity of the unit for which the unit price is calculated
	 */
	BigDecimal getNetQuantPriceUnit();

	/**
	 * Get the net costs for this item.<br>
	 * Whereas NetPrice gives the price per base unit, the NetValue is the price for the whole quantity. This includes
	 * the Shipping costs/freight.
	 *
	 * @return price of this item without taxes
	 */
	BigDecimal getNetValue();

	/**
	 * Get the net costs for this item without freight.<br>
	 * Whereas NetPrice gives the price per base unit, the NetValue is the price for the whole quantity. This is
	 * excluding the Shipping costs/freight.
	 *
	 * @return price of this item without taxes and freight
	 */
	BigDecimal getNetValueWOFreight();

	/**
	 * Returns the old quantity of this item.<br>
	 * If the quantity is changed for a item, this quantity shows the value before. This can e.g. be used for some
	 * business events.
	 *
	 * @return quantity before the change
	 */
	BigDecimal getOldQuantity();

	/**
	 * Get the business partner list
	 *
	 * @return PartnerListData list of business partners
	 */
	PartnerList getPartnerListData();

	/**
	 * returns a list of all possible units of this product.<br>
	 *
	 * @return list of units
	 */
	List<String> getPossibleUnits();



	/**
	 * Get the predecessor list.
	 *
	 * @return list of all predecessor documents
	 * @see ConnectedDocumentItem
	 */
	List<ConnectedDocumentItem> getPredecessorList();

	/**
	 * Gets the still to deliver quantity. Difference between ordered quantity and already delivered quantity.
	 *
	 * @return the quantity
	 */
	BigDecimal getQuantityToDeliver();

	/**
	 * Return the requested delivery date of this item.<br>
	 * The date that the delivery was requested for, NOT the confirmed delivery date.
	 *
	 * @return RequestedDeliveryDate
	 */
	Date getReqDeliveryDate();

	/**
	 * Returns all schedule lines.<br>
	 *
	 * @return list of schedule lines
	 * @see Schedline
	 */
	List<Schedline> getScheduleLines();

	/**
	 * Gets the successor list.<br>
	 *
	 * @return list of the successor items
	 */
	List<ConnectedDocumentItem> getSuccessorList();


	/**
	 * Gets the tax value of this item.<br>
	 *
	 * @return tax value
	 */
	BigDecimal getTaxValue();

	/**
	 * Returns the text on item level for this item.<br>
	 *
	 * @return text object
	 * @see Text
	 */
	Text getText();

	/**
	 * @return total discount, which is used for strike through prices
	 */
	BigDecimal getTotalDiscount();

	/**
	 * @return whether this item can be cancelled
	 */
	boolean isCancelable();

	/**
	 * Determine, whether or no the item is configurable.
	 *
	 * @return <code>true</code> if the item can be configured, otherwise <code>false</code>.
	 */
	boolean isConfigurable();

	/**
	 * Specifies whether the item should be configurable.
	 *
	 * @param configurable
	 *           if <code>true</code> item is configurable; otherwise not.
	 */
	void setConfigurable(boolean configurable);

	/**
	 * Sets the config flag <code> X, G(for grid),...</code> if the item is configurable; otherwise <code> blank </code>.
	 *
	 * @param configType
	 *           either X,G or <code> blank </code>
	 */
	void setConfigType(String configType);

	/**
	 * Gets the config flag
	 *
	 * @return <code> X, G(for grid),...</code> if the item is configurable; otherwise <code> blank </code>.
	 */
	String getConfigType();


	/**
	 * Do we need to sync the configuration with the backend?<br>
	 *
	 * @return Dirty?
	 */
	boolean isConfigurationDirty();



	/**
	 * Indicates whether the item is deletable.
	 *
	 * @return <code>true</code> if the item is deletable; otherwise <code>false</code>.
	 */
	boolean isDeletable();

	/**
	 * @return true if the item is invalid
	 */
	boolean isErroneous();

	/**
	 * Sets Item's Business Object Type
	 *
	 * @param busType
	 *           item type which is used for the item classification in the back end
	 */
	void setBusinessObjectType(String busType);

	/**
	 * Sets if item can be cancelled.<br>
	 *
	 * @param cancelable
	 *           if <code>true</code> an item can be cancelled
	 */
	void setCancelable(boolean cancelable);

	/**
	 * Sets Confirmed Delivery Date.<br>
	 *
	 * @param confirmedDeliveryDate
	 *           the already confirmed delivery date
	 */
	void setConfirmedDeliveryDate(Date confirmedDeliveryDate);

	/**
	 * Sets confirmed quantity.<br>
	 *
	 * @param quantity
	 *           item quantity
	 */
	void setConfirmedQuantity(BigDecimal quantity);

	/**
	 * Sets the currency for this item.<br>
	 * e.g. USD or EUR
	 *
	 * @param currency
	 *           currency
	 */
	void setCurrency(String currency);

	/**
	 * Sets if item can be deleted.<br>
	 *
	 * @param deletable
	 *           if <code>true</code> an item can be deleted
	 */
	void setDeletable(boolean deletable);

	/**
	 * Sets delivered quantity.<br>
	 *
	 * @param quantity
	 *           the already delivered quantity
	 */
	void setDeliverdQuantity(BigDecimal quantity);

	/**
	 * Sets delivered quantity unit.<br>
	 *
	 * @param unit
	 *           the unit belonging to the delivered quantity
	 */
	void setDeliverdQuantityUnit(String unit);

	/**
	 * Sets the delivery priority key.
	 *
	 * @param deliveryPriority
	 *           priority for delivery
	 */
	void setDeliveryPriority(String deliveryPriority);

	/**
	 * Sets this item as erroneous or not.<br>
	 *
	 * @param erroneous
	 *           true if the item has errors
	 */
	void setErroneous(boolean erroneous);



	/**
	 * Sets the free quantity. Only relevant for inclusive free goods.
	 *
	 * @param arg
	 *           the free quantity in local specific format.
	 */
	void setFreeQuantity(BigDecimal arg);

	/**
	 * Sets a freight value.<br>
	 *
	 * @param freightValue
	 *           freight costs for this item
	 */
	void setFreightValue(BigDecimal freightValue);

	/**
	 * Sets a gross value.<br>
	 *
	 * @param grossValue
	 *           gross value for this item
	 */
	void setGrossValue(BigDecimal grossValue);

	/**
	 * Sets an item type usage .<br>
	 *
	 * @param itmTypeUsage
	 *           type of the item as string
	 */
	void setItmTypeUsage(String itmTypeUsage);

	/**
	 * Sets the item usage for this item.<br>
	 * e.g free good
	 *
	 * @param itemUsage
	 *           an item usage
	 * @see ItemUsage
	 */
	void setItemUsage(ItemUsage itemUsage);

	/**
	 * Sets an item net price.<br>
	 *
	 * @param netPrice
	 *           value to set
	 */
	void setNetPrice(BigDecimal netPrice);

	/**
	 * Sets an item net price unit.<br>
	 * This is the unit of the unit price.
	 *
	 * @param netPriceUnit
	 *           value to set
	 */
	void setNetPriceUnit(String netPriceUnit);

	/**
	 * Sets an item net quantity price unit.<br>
	 * This is the quantity of the base unit of the unit price.
	 *
	 * @param netQuantPriceUnit
	 *           value to set
	 */
	void setNetQuantPriceUnit(BigDecimal netQuantPriceUnit);

	/**
	 * Sets net value.<br>
	 * Price without tax. For the overall quantity.
	 *
	 * @param netValue
	 *           value to set
	 */
	void setNetValue(BigDecimal netValue);

	/**
	 * Sets net value with out freight.<br>
	 * Price without tax or freight. For the overall quantity.
	 *
	 * @param netValueWOFreight
	 *           value to set
	 */
	void setNetValueWOFreight(BigDecimal netValueWOFreight);

	/**
	 * Set the property oldQuantity. Replaces the method with String parameter.
	 *
	 * @param quantity
	 *           The value of oldQuantity property as BigDecimal.
	 */
	void setOldQuantity(BigDecimal quantity);

	/**
	 * Set the business partner list Analogy to the header set business partner list
	 *
	 * @param list
	 *           PartnerListData list of business partners
	 */
	void setPartnerListData(PartnerList list);

	/**
	 * Sets all units that are possible for that product.<br>
	 *
	 * @param possibleUnits
	 *           list of all units
	 */
	void setPossibleUnits(List<String> possibleUnits);



	/**
	 * Sets the flag, that shows, if the position is price relevant
	 *
	 * @param isPriceRelevant
	 *           true or false
	 */
	void setPriceRelevant(boolean isPriceRelevant);

	/**
	 * Sets an item quantity to deliver.<br>
	 *
	 * @param quantity
	 *           value to set
	 */
	void setQuantityToDeliver(BigDecimal quantity);

	/**
	 * Sets required delivery date.<br>
	 * This is the date the delivery is requested, not the confirmed delivery date.
	 *
	 * @param reqDeliveryDate
	 *           value to set
	 */
	void setReqDeliveryDate(Date reqDeliveryDate);

	/**
	 * Sets schedule lines for the item .<br>
	 *
	 * @param scheduleLines
	 *           list of Schedlines
	 * @see Schedline
	 */
	void setScheduleLines(List<Schedline> scheduleLines);



	/**
	 * Sets the tax value of this item.<br>
	 *
	 * @param taxValue
	 *           value to set
	 */
	void setTaxValue(BigDecimal taxValue);

	/**
	 * Sets the text on item level.<br>
	 *
	 * @param text
	 *           text on item level
	 * @see Text
	 */
	void setText(Text text);

	/**
	 * Sets the total discount, which is used for strike through prices. The number must be positive (e.g. 10 and not
	 * -10, this is how it comes from IPC).
	 *
	 * @param totalDiscount
	 *           value to set
	 */
	void setTotalDiscount(BigDecimal totalDiscount);

	/**
	 * Sets the information, whether the product exists in the back end<br>
	 *
	 * @param productExists
	 *           false if the product does not exist in the back end
	 */
	void setProductExists(boolean productExists);

	/**
	 * Returns true if the product exists in the back end<br>
	 *
	 * @return true if the product exists in the back end
	 */
	boolean isProductExists();

	/**
	 * Returns true if the productId is null or ""<br>
	 *
	 * @return true if there is no product maintained
	 */
	boolean isProductEmpty();

	/**
	 * Returns the property grossValueWOFreight
	 *
	 * @return grossValueWOFreight
	 */
	BigDecimal getGrossValueWOFreight();

	/**
	 * Set the property grossValueWOFreight
	 *
	 * @param grossValueWOFreight
	 *           value to set
	 */
	void setGrossValueWOFreight(BigDecimal grossValueWOFreight);


	/**
	 * The latest delivery date is the date of the latest schedule line
	 *
	 * @param latestDeliveryDate
	 *           the latestDeliveryDate to set
	 */
	void setLatestDeliveryDate(Date latestDeliveryDate);



	/**
	 * @param isDirty
	 *           <code>true</code>, if there is the need to sync the configuration with the back-end
	 */
	void setConfigurableDirty(boolean isDirty);


	/**
	 * @return true if this item is a variant of a configurable product
	 */
	boolean isVariant();

	/**
	 * Setter if this item is a variant of a configurable product or not
	 *
	 * @param variant
	 *           true id this item is a variant
	 */
	void setVariant(boolean variant);

	/**
	 * @return created at date of this item
	 */
	Date getCreatedAt();

	/**
	 * @param createdAt
	 *           createdAt date of this item
	 */
	void setCreatedAt(Date createdAt);

	/**
	 * @return item category
	 */
	String getItemCategory();

	/**
	 * Sets item category
	 *
	 * @param itemCategory
	 *           item category
	 */
	void setItemCategory(String itemCategory);

	/**
	 * @return statistical
	 */
	boolean isStatistical();

	/**
	 * Sets statistical
	 *
	 * @param statistical
	 */
	void setStatistical(boolean statistical);


	/**
	 * @param netPriceWOFreight
	 */
	void setNetPriceWOFreight(BigDecimal netPriceWOFreight);

	/**
	 * @return netPriceWOFreight
	 */
	BigDecimal getNetPriceWOFreight();
}
