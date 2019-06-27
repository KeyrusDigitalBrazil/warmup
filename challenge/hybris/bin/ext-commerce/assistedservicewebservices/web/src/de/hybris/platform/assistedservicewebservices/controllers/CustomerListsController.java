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
package de.hybris.platform.assistedservicewebservices.controllers;

import de.hybris.platform.assistedservicewebservices.dto.CustomerListWsDTO;
import de.hybris.platform.assistedservicewebservices.helper.CustomerListsHelper;
import de.hybris.platform.commercefacades.user.data.CustomerListData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
public class CustomerListsController extends AbstractAssistedServiceWebServiceController
{
	@Resource(name = "customerListsHelper")
	private CustomerListsHelper customerListsHelper;

	@ApiOperation(value = "Returns customer lists", notes = "This endpoint returns list of all customer lists. This can only be done when logged in")
	@RequestMapping(value = "/customerlists", method = RequestMethod.GET)
	@ResponseBody
	public UserGroupListWsDTO getCustomerLists(
			@ApiParam(value = "Id of the BaseSite", required = true) @RequestParam(required = true) final String baseSite)
	{
		final String currentCustomerUid = getCustomerFacade().getCurrentCustomerUid();
		final List<UserGroupData> customerLists = getCustomerListFacade().getCustomerListsForEmployee(currentCustomerUid);
		return getCustomerListsHelper().getCustomerListDto(customerLists);
	}

	@ApiOperation(value = "Returns single customer list details", notes = "This endpoint returns details of customer list with valid Id")
	@RequestMapping(value = "/customerlists/{customerlist}", method = RequestMethod.GET)
	@ResponseBody
	public CustomerListWsDTO getCustomerListDetails(
			@ApiParam(value = "Id of the customer list", required = true) @PathVariable("customerlist") final String customerlist,
			@ApiParam(value = "Id of the BaseSite", required = true) @RequestParam(required = true) final String baseSite)
	{
		final String currentCustomerUid = getCustomerFacade().getCurrentCustomerUid();
		final CustomerListData customerListData = getCustomerListFacade().getCustomerListForUid(customerlist, currentCustomerUid);
		return getCustomerListsHelper().getSingleCustomerListDto(customerListData);
	}

	public CustomerListsHelper getCustomerListsHelper()
	{
		return customerListsHelper;
	}
}
