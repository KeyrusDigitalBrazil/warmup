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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;

import java.io.Serializable;


/**
 * Represents the PartnerBase object. <br>
 *
 */
public interface PartnerBase extends BusinessObject, Cloneable, Serializable
{

	/**
	 * Sets the fully qualified address for the partner.<br>
	 *
	 * @param address
	 *           The address to be set
	 */
	void setAddress(Address address);

	/**
	 * Sets the short address for the partner.<br>
	 *
	 * @param address
	 *           The short address for the partner
	 */
	void setShortAddress(String address);

	/**
	 * Returns the short address of the partner.<br>
	 *
	 * @return shortAddress The short address of the partner
	 */
	String getShortAddress();

	/**
	 * Returns the address of the partner.<br>
	 *
	 * @return Address of the partner
	 */
	Address getAddress();

	/**
	 * Sets the id of the partner.<br>
	 *
	 * @param id
	 *           The Id of the partner
	 */
	void setId(String id);

	/**
	 * Returns the Id of the partner.<br>
	 *
	 * @return Id The id of the partner
	 */
	String getId();

	/**
	 * Returns value which indicates that address was set.<br>
	 *
	 * @return true, if address was set
	 */
	boolean isIdX();

	/**
	 * Sets flag which indicates that address was set.<br>
	 *
	 * @param idX
	 *           Flag indicates whether address was set
	 */
	void setIdX(boolean idX);

	/**
	 *
	 * @see Object#clone
	 * @return clone
	 */
	@SuppressWarnings("squid:S1161")
	PartnerBase clone();

}