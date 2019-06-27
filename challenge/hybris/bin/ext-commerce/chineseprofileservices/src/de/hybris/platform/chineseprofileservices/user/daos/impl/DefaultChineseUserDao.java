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

package de.hybris.platform.chineseprofileservices.user.daos.impl;


import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultUserDao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultChineseUserDao extends DefaultUserDao
{
	private static final Pattern MOBILE_NO_PATTERN = Pattern.compile("^(\\+)?(\\d{2,3})?(\\s)?(\\d{11})$");
	private static final String FIND_USER_BY_UID = "SELECT {" + UserModel.PK + "} FROM {" + UserModel._TYPECODE + "} WHERE {"
			+ UserModel.UID + "}=?" + UserModel.UID;
	private static final String FIND_USER_BY_MOBILE = "SELECT {" + CustomerModel.PK + "} FROM {" + CustomerModel._TYPECODE
			+ "} WHERE {" + CustomerModel.MOBILENUMBER + "}=?" + CustomerModel.MOBILENUMBER;

	@Override
	public UserModel findUserByUID(final String loginName)
	{
		final Matcher matcher = MOBILE_NO_PATTERN.matcher(loginName);
		final FlexibleSearchQuery query;
		if (matcher.matches())
		{
			query = new FlexibleSearchQuery(FIND_USER_BY_MOBILE);
			query.addQueryParameter(CustomerModel.MOBILENUMBER, loginName);
		}
		else
		{
			query = new FlexibleSearchQuery(FIND_USER_BY_UID);
			query.addQueryParameter(UserModel.UID, loginName);
		}

		final List<UserModel> resList = getFlexibleSearchService().<UserModel> search(query).getResult();
		if (resList.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					"Found " + resList.size() + " users with the unique uid or mobile No. '" + loginName + "'");
		}
		else
		{
			return resList.isEmpty() ? null : resList.get(0);
		}
	}

}
