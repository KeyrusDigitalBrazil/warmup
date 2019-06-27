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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;



/**
 * Business object representation of an Order.<br>
 * The order extends the <code>SalesDocument</code> business object.<br>
 * There can be several orders per session existent. All back ends require a login to access an order.
 * 
 * @see SalesDocument
 */
public interface Order extends SalesDocument
{

	/**
	 * Returns the TechKey of the basket used for creating this order.
	 * 
	 * @return TechKey of Basket
	 */
	TechKey getBasketId();

	/**
	 * Sets the TechKey of basket used for creating this order. The TechKey is stored as a reference to the basket.
	 * 
	 * @param basketId
	 *           TechKey of the basket
	 */
	void setBasketId(TechKey basketId);

	/**
	 * Destroys content in BusinessObject layer and application memory of the back end. Only the already persisted
	 * content remains. For example: An order which is not yet submitted and for which destroyContent is called, will be
	 * deleted from application memory. An Order which is already submitted (e.g. via checkout), destroyContent will only
	 * invalidate the BusinessObject but not the back end representation.
	 */
	@Override
	void destroyContent() throws CommunicationException;

	/**
	 * Saves the order in the back end and commits. Changes will be persisted.
	 * 
	 * @return true if the save and commit were successful
	 * @throws CommunicationException
	 *            in case of a back-end error
	 */
	boolean saveOrderAndCommit() throws CommunicationException;

	/**
	 * Sets the creation mode of LO-API. It is only used in ERP scenario.
	 * 
	 * @throws BusinessObjectException
	 *            in case of a back-end error
	 */
	void setLoadStateCreate() throws BusinessObjectException;

	/**
	 * Checks if the order is cancelable
	 * 
	 * @return true if order is cancelable
	 * @throws CommunicationException
	 *            in case of a back-end error
	 */
	boolean isCancelable() throws CommunicationException;
}