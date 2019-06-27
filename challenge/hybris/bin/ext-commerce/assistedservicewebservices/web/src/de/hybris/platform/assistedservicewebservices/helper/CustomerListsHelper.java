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
package de.hybris.platform.assistedservicewebservices.helper;

import de.hybris.platform.assistedservicewebservices.dto.CustomerListWsDTO;
import de.hybris.platform.commercefacades.user.data.CustomerListData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


@Component
public class CustomerListsHelper
{
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;


	public UserGroupListWsDTO getCustomerListDto(final List<UserGroupData> customerLists)
	{
		final UserGroupDataList userGroupDataList = new UserGroupDataList();
		userGroupDataList.setUserGroups(customerLists);
		return dataMapper.map(userGroupDataList, UserGroupListWsDTO.class);
	}

	public CustomerListWsDTO getSingleCustomerListDto(final CustomerListData customerList)
	{
		return dataMapper.map(customerList, CustomerListWsDTO.class);
	}
}
