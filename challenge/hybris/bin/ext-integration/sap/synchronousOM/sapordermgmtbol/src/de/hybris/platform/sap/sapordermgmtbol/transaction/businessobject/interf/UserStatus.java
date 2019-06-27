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

/**
 * Represents the OverallStatusOrder object. <br>
 *
 */
public interface UserStatus extends Cloneable
{

	/**
	 * Returns the User Status Key.<br>
	 *
	 * @return User Status Key
	 */
	String getKey();

	/**
	 * Sets the User Status Key.<br>
	 *
	 * @param key
	 *           User Status Key
	 */
	void setKey(String key);

	/**
	 * Returns the User Status Description.<br>
	 *
	 * @return User Status Description
	 */
	String getDescription();

	/**
	 * Sets the User Status Description.<br>
	 *
	 * @param description
	 *           User Status Description
	 */
	void setDescription(String description);

	/**
	 * Returns true or false if the user status is respectively active or not .<br>
	 *
	 * @return Returns boolean value which is true, if the user status is active, and false otherwise.
	 */
	boolean isActive();

	/**
	 * Set user status active / inactive.<br>
	 *
	 * @param active
	 *           If true, the user status will be set to active
	 */
	void setActive(boolean active);

	/**
	 * Clones the Object. Because this class only contains immutable objects, there is no difference between a shallow
	 * and deep copy.
	 *
	 * @return deep-copy of this object
	 */
	@SuppressWarnings("squid:S1161")
	Object clone();

}