/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.List;


/**
 * A service around {@link B2BCustomerModel}
 *
 * @param <U>
 *           the customer
 * @param <C>
 *           the b2b unit
 * @spring.bean b2bCustomerService
 */
public interface B2BCustomerService<U, C>
{

	/**
	 *
	 * @param member
	 *           A user to be added to a group, like {@link B2BCustomerModel} for example
	 * @param group
	 *           A user group to which a member is going to be added, like a {@link B2BUnitModel} for example
	 */
	void addMember(final PrincipalModel member, final PrincipalGroupModel group);


	/**
	 * Delegates to {@link de.hybris.platform.servicelayer.user.UserService#getUserForUID(String, Class)} if the user was
	 * not found return null rather than throwing up
	 * de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *
	 * @param userId
	 *           The unique identifier of the user
	 *
	 * @return A hybris user typed T
	 */
	U getUserForUID(String userId);


	/**
	 * Sets the parent b2b unit. If the customer does not have a default b2b unit <param>company</param> will be set as
	 * the default. The client of this method will most likely want to call
	 * {@link B2BUnitService#updateBranchInSession(de.hybris.platform.servicelayer.session.Session, de.hybris.platform.core.model.user.UserModel)}
	 * after calling this method to make sure the session breach is refreshed.
	 *
	 * @param member
	 *           the customer who will be assigned to the parent unit
	 * @param company
	 *           the parent b2b unit
	 * @deprecated Since 4.5. Use
	 *             {@link B2BCustomerService#addMember(de.hybris.platform.core.model.security.PrincipalModel, de.hybris.platform.core.model.security.PrincipalGroupModel)}
	 */
	@Deprecated
	void setParentB2BUnit(final U member, final C company);

	/**
	 * Gets the current b2b customer.
	 *
	 * @return current session user, if B2BCustomer. If not B2BCustomer it returns null
	 */
	U getCurrentB2BCustomer();

	/**
	 * Returns true is principalExists with this uid, Visibility restrictions do no apply in query.
	 *
	 * @param uid
	 * @return true if principal exists with this uid.
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.servicelayer.user.UserService#isUserExisting(String)}
	 */
	@Deprecated
	boolean principalExists(final String uid);

	/**
	 * Gets all users
	 *
	 * @return the {@link List} of users
	 */
	List<U> getAllUsers();

	/**
	 * Gets all b2b user groups
	 * 
	 * @return the {@link List} of {@link B2BUserGroupModel}
	 */
	List<B2BUserGroupModel> getAllB2BUserGroups();

}
