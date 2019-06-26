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
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.user.UserGroupOption;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commercewebservicescommons.dto.user.MemberListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.PrincipalWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Controller for {@link de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade}
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/customergroups")
@Api(tags = "Customer Groups")
public class CustomerGroupsController extends BaseController
{
	private static final Set<UserGroupOption> OPTIONS;

	static
	{
		String userGroupOptions = "";
		for (final UserGroupOption option : UserGroupOption.values())
		{
			userGroupOptions = userGroupOptions + option.toString() + " ";
		}
		userGroupOptions = userGroupOptions.trim().replace(" ", YcommercewebservicesConstants.OPTIONS_SEPARATOR);
		OPTIONS = getOptions(userGroupOptions);
	}

	@Resource(name = "customerGroupFacade")
	private CustomerGroupFacade customerGroupFacade;
	@Resource(name = "principalListDTOValidator")
	private Validator principalListDTOValidator;
	@Resource(name = "userGroupDTOValidator")
	private Validator userGroupDTOValidator;
	@Resource(name = "userService")
	private UserService userService;

	protected static Set<UserGroupOption> getOptions(final String options)
	{
		final String[] optionsStrings = options.split(",");

		final Set<UserGroupOption> opts = new HashSet<>();
		for (final String option : optionsStrings)
		{
			opts.add(UserGroupOption.valueOf(option));
		}
		return opts;
	}

	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ApiOperation(hidden = true, value = "Creates a new customer group.", notes = "Creates a new customer group that is a direct subgroup of a customergroup.\n\nTo try out the methods of "
			+ "the Customer Groups controller, you must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void createCustomerGroup(@ApiParam(value = "Id of new customer group.") @RequestParam final String groupId,
			@ApiParam(value = "Name in current locale.") @RequestParam(required = false) final String localizedName)
	{
		customerGroupFacade.createCustomerGroup(groupId, localizedName);
	}

	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ApiOperation(nickname = "createCustomerGroup", value = "Creates a new customer group.", notes =
			"Creates a new customer group that is a direct subgroup of a customergroup.\n\nTo try out the methods of the Customer "
					+ "Groups controller, you must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void createCustomerGroup(
			@ApiParam(value = "User group object with id and name.", required = true) @RequestBody final UserGroupWsDTO userGroup)
	{
		validate(userGroup, "userGroup", userGroupDTOValidator);

		customerGroupFacade.createCustomerGroup(userGroup.getUid(), userGroup.getName());
		if (userGroup.getMembers() != null)
		{
			for (final PrincipalWsDTO member : userGroup.getMembers())
			{
				customerGroupFacade.addUserToCustomerGroup(userGroup.getUid(), member.getUid());
			}
		}
	}

	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PATCH)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(hidden = true, value = "List of users to assign to customer group.", notes = "Assigns user(s) to a customer group.\n\nTo try out the methods of the Customer Groups "
			+ "controller, you must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void updateCustomerGroupWithUsers(
			@ApiParam(value = "Group identifier.", required = true) @PathVariable final String groupId,
			@ApiParam(value = "List of users ids to assign to customer group. List should be in form: members=uid1&members=uid2...") @RequestParam(value = "members") final List<String> members)
	{
		checkMembers(members, "You cannot add user to group :" + sanitize(groupId) + ".");
		for (final String member : members)
		{
			customerGroupFacade.addUserToCustomerGroup(groupId, member);
		}
	}

	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(nickname = "updateCustomerGroupWithUsers", value = "Assigns user(s) to a customer group.", notes =
			"Assigns user(s) to a customer group.\n\nTo try out the methods of the Customer Groups controller, you must "
					+ "authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void updateCustomerGroupWithUsers(
			@ApiParam(value = "Group identifier.", required = true) @PathVariable final String groupId,
			@ApiParam(value = "List of users to assign to customer group.", required = true) @RequestBody final MemberListWsDTO members)
	{
		validate(members.getMembers(), "members", principalListDTOValidator);

		for (final PrincipalWsDTO member : members.getMembers())
		{
			customerGroupFacade.addUserToCustomerGroup(groupId, member.getUid());
		}
	}

	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PUT)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(hidden = true, value = "List of users to set for customer group.", notes = "Sets members for a user group. The list of existing members is overwritten with a new one.\n\nTo "
			+ "try out the methods of the Customer Groups controller, you must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void replaceUserListForCustomerGroup(@ApiParam(value = "Group identifier.") @PathVariable final String groupId,
			@ApiParam(value = "List of users ids to assign to customer group. List should be in form: members=uid1&members=uid2...") @RequestParam(required = false, value = "members") final List<String> members)
	{
		setUserListForCustomerGroupInternal(groupId, members);
	}

	protected void setUserListForCustomerGroupInternal(final String groupId, final List<String> members)
	{
		final List<String> evaluatedMembers = members != null ? members : new ArrayList<>();

		final UserGroupData userGroup = customerGroupFacade.getCustomerGroup(groupId,
				Collections.singleton(UserGroupOption.MEMBERS));

		final HashSet<String> oldMembers = new HashSet();
		for (final PrincipalData member : userGroup.getMembers())
		{
			oldMembers.add(member.getUid());
		}

		final HashSet<String> newMembers = new HashSet();
		for (final String member : evaluatedMembers)
		{
			newMembers.add(member);
		}

		final Collection<String> membersToRemove = CollectionUtils.subtract(oldMembers, newMembers);
		checkMembers(membersToRemove, "You cannot remove user from group :" + sanitize(groupId) + ".");
		final Collection<String> membersToAdd = CollectionUtils.subtract(newMembers, oldMembers);
		checkMembers(membersToAdd, "You cannot add user to group :" + sanitize(groupId) + ".");

		for (final String memberToRemove : membersToRemove)
		{
			customerGroupFacade.removeUserFromCustomerGroup(groupId, memberToRemove);
		}

		for (final String memberToAdd : membersToAdd)
		{
			customerGroupFacade.addUserToCustomerGroup(groupId, memberToAdd);
		}
	}

	protected void checkMembers(final Collection<String> membersList, final String errorMessagePrefix)
	{
		for (final String member : membersList)
		{
			if (!userService.isUserExisting(member))
			{
				throw new RequestParameterException(
						errorMessagePrefix + " User '" + sanitize(member) + "' doesn't exist or you have no privileges");
			}
		}
	}

	@RequestMapping(value = "/{groupId}/members", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(nickname = "replaceUsersForCustomerGroup", value = "Sets members for a user group.", notes =
			"Sets members for a user group. The list of existing members is overwritten with a new one.\n\nTo try out the methods "
					+ "of the Customer Groups controller, you must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void replaceUsersForCustomerGroup(
			@ApiParam(value = "Group identifier.", required = true) @PathVariable final String groupId,
			@ApiParam(value = "List of users to set for customer group.", required = true) @RequestBody final MemberListWsDTO members)
	{
		final List<String> membersIds = new ArrayList();

		if (members.getMembers() != null)
		{
			if (!members.getMembers().isEmpty())
			{
				validate(members.getMembers(), "members", principalListDTOValidator);
			}

			for (final PrincipalWsDTO member : members.getMembers())
			{
				membersIds.add(member.getUid());
			}
		}

		setUserListForCustomerGroupInternal(groupId, membersIds);
	}

	@RequestMapping(value = "/{groupId}/members/{userId:.*}", method = RequestMethod.DELETE)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(nickname = "removeUsersFromCustomerGroup", value = "Deletes a user from a customer group.", notes =
			"Deletes user from a customer group.\n\nTo try out the methods of the Customer Groups controller, you must "
					+ "authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public void removeUsersFromCustomerGroup(
			@ApiParam(value = "Group identifier.", required = true) @PathVariable final String groupId,
			@ApiParam(value = "User identifier.", required = true) @PathVariable(value = "userId") final String userId)
	{
		checkMembers(Collections.singleton(userId), "You cannot remove user from group :" + sanitize(groupId) + ".");
		customerGroupFacade.removeUserFromCustomerGroup(groupId, userId);
	}

	@RequestMapping(method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiOperation(nickname = "getCustomerGroups", value = "Get all subgroups of a customergroup.", notes =
			"Returns all customer groups that are direct subgroups of a customergroup.\n\nTo try out the methods of the "
					+ "Customer Groups controller, you must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public UserGroupListWsDTO getCustomerGroups(
			@ApiParam(value = "Current page number (starts with 0).") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Number of customer group returned in one page.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "BASIC") final String fields)
	{
		final PageOption pageOption = PageOption.createForPageNumberAndPageSize(currentPage, pageSize);
		final UserGroupDataList userGroupDataList = customerGroupFacade.getAllCustomerGroups(pageOption);
		return getDataMapper().map(userGroupDataList, UserGroupListWsDTO.class, fields);
	}

	@RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiOperation(nickname = "getCustomerGroup", value = "Get a specific customer group.", notes =
			"Returns a customer group with a specific groupId.\n\nTo try out the methods of the Customer Groups controller, you "
					+ "must authorize a user who belongs to the “customermanagergroup”.")
	@ApiBaseSiteIdParam
	public UserGroupWsDTO getCustomerGroup(
			@ApiParam(value = "Group identifier.", required = true) @PathVariable final String groupId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "BASIC") final String fields)
	{
		final UserGroupData userGroupData = customerGroupFacade.getCustomerGroup(groupId, OPTIONS);
		return getDataMapper().map(userGroupData, UserGroupWsDTO.class, fields);
	}

}
