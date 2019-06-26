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

import java.util.List;


/**
 * Represents the UserStatusList object. <br>
 *
 */
public interface UserStatusList extends Cloneable
{

	/**
	 * Adds an additional User Status to the User Status List.<br>
	 *
	 * @param userStatus
	 *           User Status to be added to the User Stattus List
	 * @return true, if status was added to the list
	 */
	boolean addUserStatus(UserStatus userStatus);

	/**
	 * Returns the List of User Statuses.<br>
	 *
	 * @return List of User Statuses
	 */
	List<UserStatus> getUserStatusList();

	/**
	 * Returns the List of Active User Statuses.<br>
	 *
	 * @return List of Active User Statuses
	 */
	UserStatusList getActiveUserStatusList();

	/**
	 * Returns the string representation of the User Status List.<br>
	 *
	 * @return String representation of the User Status List
	 */
	@Override
	String toString();

	/**
	 * Clones the Object. Because this class only contains immutable objects, there is no difference between a shallow
	 * and deep copy.
	 *
	 *
	 * @return deep-copy of this object
	 */
	@SuppressWarnings("squid:S1161")
	Object clone();

}