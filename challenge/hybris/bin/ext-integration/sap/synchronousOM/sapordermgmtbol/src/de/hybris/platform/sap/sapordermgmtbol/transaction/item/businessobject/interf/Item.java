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

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.StatusObject;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Represents the backend's view of the items of a shopping basket.
 *
 */
public interface Item extends ItemBase, StatusObject
{

	/**
	 * Return value for back-end Delivery meaning product can be delivered completely.
	 */
	String DELIVERY_IN_STOCK = "instock";

	/**
	 * Return value for back-end Delivery meaning product can be delivered partly.
	 */
	String DELIVERY_PARTLY = "limited";

	/**
	 * Return value for back-end Delivery meaning product delivery is delayed
	 */
	String DELIVERY_DELAYED = "delayed";

	/**
	 * Return value for back-end Delivery meaning product can not be delivered
	 */
	String DELIVERY_OUT_OF_STOCK = "notinstock";

	/**
	 * Creates a new AlternativProductListData.
	 *
	 * @return AlternativProductListData
	 */
	AlternativeProductList createAlternativProductList();

	/**
	 * Returns the alternativProductList.
	 *
	 * @return AlternativProductListData
	 */
	AlternativeProductList getAlternativProductList();

	/**
	 * returns the billing status of the item
	 *
	 * @return the actual billing statuts of the item
	 */
	BillingStatus getBillingStatus();

	/**
	 * Returns the overall status
	 *
	 * @return the overall item status
	 */
	OverallStatus getOverallStatus();

	/**
	 * Returns the parentHandle, that is the handle of the parent, if the position is a sub position
	 *
	 * @return String the parentHandle
	 */
	String getParentHandle();

	/**
	 * Get the payment terms.
	 *
	 * @return paymentTerms the payment terms set.
	 */
	String getPaymentTerms();

	/**
	 * Get the date which is used to calculate prices in IPC
	 *
	 * @return date relevant for pricing
	 */
	Date getPricingDate();

	/**
	 * Get The shipping status
	 *
	 * @return the shipping status of the item
	 */
	ShippingStatus getShippingStatus();

	/**
	 * Returns the shipTo associated with this item
	 *
	 * @return the shipTo to which this item will be shipped to
	 */
	ShipTo getShipTo();

	/**
	 * Returns the substitutionReasonId.
	 *
	 * @return String
	 */
	String getSubstitutionReasonId();

	/**
	 * Returns the systemProductId.
	 *
	 * @return String
	 */
	String getSystemProductId();

	/**
	 * @return true if the item can be changed
	 */
	boolean isChangeAllowed();

	/**
	 * This method returns a flag, that indicates if the item is copied from another item, e.g. when an order is created
	 * from an order template If so, this flag might be used, to suppress things like campaign determination, etc. for
	 * the copied item.
	 *
	 * @return true if this item is copied from another item false else
	 */
	boolean isCopiedFromOtherItem();

	/**
	 * Determines whether the item is a free good by checking the item usage.
	 *
	 * @return <code>true</code>, only if this item is a FreeGood
	 */
	boolean isFreeGood();

	/**
	 * Indicates whether the item is originated from catalog.
	 *
	 * @return <code>true</code> if the item is from catalog; otherwise <code>false</code>.
	 */
	boolean isFromCatalog();

	/**
	 * Checks whether this item can be merged with the given item.
	 *
	 * @param toMerge
	 *           the item this item should be merged with
	 * @return <code>true</code>, only if this item can be merged with the given item
	 */
	boolean isMergeSupported(Item toMerge);

	/**
	 * Sets the alternativProductListData.
	 *
	 * @param alternativProductList
	 *           The alternativProductList to set
	 */
	void setAlternativProductList(AlternativeProductList alternativProductList);

	/**
	 * Indicate whether the item is originated from catalog.
	 *
	 * @param fromCatalog
	 *           should be set to <code>true</code> if the item originated from catalog
	 */
	void setFromCatalog(boolean fromCatalog);

	/**
	 * Sets the parentHandle, that is the handle of the parent, if the position is a sub position
	 *
	 * @param parentHandle
	 *           the new value for the parentHandle
	 */
	void setParentHandle(String parentHandle);

	/**
	 * Set the payment terms.
	 *
	 * @param paymentTerms
	 *           the payment terms to be set.
	 */
	void setPaymentTerms(String paymentTerms);

	/**
	 * set the date which is used to calculate prices in IPC
	 *
	 * @param pricingDate
	 *           date which should be used for pricing
	 */
	void setPricingDate(Date pricingDate);

	/**
	 * Sets the shiptTo for this item.
	 *
	 * @param shipTo
	 *           shipTo to which the item will be shipped
	 */
	void setShipTo(ShipTo shipTo);

	/**
	 * Sets the substitutionReasonId.
	 *
	 * @param substitutionReasonId
	 *           The substitutionReasonId to set
	 */
	void setSubstitutionReasonId(String substitutionReasonId);

	/**
	 * Sets the systemProductId.
	 *
	 * @param systemProductId
	 *           The systemProductId to set
	 */
	void setSystemProductId(String systemProductId);

	/**
	 * Sets rejection/cancellation reason for the order.<br>
	 *
	 * @param rejection
	 *           cancellation reason (key)
	 */
	void setRejectionCode(String rejection);

	/**
	 * Gets rejection/cancellation reason for the order.<br>
	 *
	 * @return cancellation reason (key)
	 */
	String getRejectionCode();

	/**
	 * This getter is dependent on the quantity and the free quantity of this product.<br>
	 *
	 * @return the quantity that must be paid
	 */
	BigDecimal getQuantityToPay();

	/**
	 * This method sets a flag, that indicates if the item is copied from another item, e.g. when an order is created
	 * from an order template If so, this flag might be used, to suppress things like campaign determination, etc. for
	 * the copied item.
	 *
	 * @param isCopiedFromOtherItem
	 *           true when item is copied
	 */
	void setCopiedFromOtherItem(boolean isCopiedFromOtherItem);

	/**
	 * Get the processing status of the sales document item. The processing status equates GBSTA field in ERP back end
	 * <br>
	 * Used for the definition of possible cancellation
	 *
	 * @return BusinessStatus
	 */
	ProcessingStatus getProcessingStatus();

	/**
	 * Get the processing status of the sales document item. The processing status equates GBSTA field in ERP back end
	 * <br>
	 *
	 * @param processingStatus
	 *           ProcessingStatus
	 */
	void setProcessingStatus(ProcessingStatus processingStatus);


	/**
	 * Apply alternative product, e.g. product to be substituted with .<br>
	 *
	 * @param productGUID
	 *           GUID of the applied product
	 * @param productID
	 *           ID of the applied product
	 * @throws CommunicationException
	 *            in case of an back-end error
	 */
	void applyAlternativeProduct(TechKey productGUID, String productID) throws CommunicationException;

	/**
	 * Indicates whether the item is a subitem (has a parent) or not <br>
	 *
	 * @return true if subitem
	 */
	boolean isSubItem();


}
