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

import java.math.BigDecimal;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.modulemgmt.interf.BasketOrderConsistency;


/**
 * Business object representation of a basket.<br>
 * The basket extends the <code>SalesDocument</code> business object.<br>
 * There will be always exactly one basket per session. Depending on the back end a login may be required to access the
 * basket.
 * 
 * @see SalesDocument
 */

public interface Basket extends SalesDocument
{

	/**
	 * Injects the basketOrderConsisteny into the basket
	 * 
	 * @param basketOrderConsistency
	 *           consistency manager
	 */
	void setBasketOrderConsistency(BasketOrderConsistency basketOrderConsistency);

	/**
	 * Reads the sales document. The back-end call happens if considered as necessary or if forced. The method also
	 * checks the {@link BasketOrderConsistency} and can trigger an update before the read happens.
	 * 
	 * @param force
	 *           If true, then read even if not considered as necessary
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	@Override
	void read(boolean force) throws CommunicationException;

	/**
	 * @return the total quantity of the products in basket
	 */
	BigDecimal calculateTotalQuantity();

	/**
	 * Releases cart object. Afterwards, cart is fully initialized
	 * 
	 * @throws CommunicationException
	 */
	void release() throws CommunicationException;

}