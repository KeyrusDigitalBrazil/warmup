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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.List;


/**
 * A data access to {@link B2BUnitModel}
 * 
 * 
 * @spring.bean b2bOrderDao
 */
public interface B2BUnitDao extends GenericDao<B2BUnitModel>
{

	/**
	 * Returns all member of the B2BUnit who are also member of the group with the userGroupId passed in
	 * 
	 * @param unit
	 *           - The B2BUnitModel whose member we are going to select
	 * @param userGroupId
	 *           - The uid of the UserGroup
	 * @return List<B2BCustomerModel> B2BCustomers in B2BUnit who are members of the group with uid userGroupId
	 */
	List<B2BCustomerModel> findB2BUnitMembersByGroup(final B2BUnitModel unit, final String userGroupId);



}
