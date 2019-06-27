/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimwebservices.v2.controllers;

import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.user.ScimUserFacade;
import de.hybris.platform.scimfacades.utils.ScimUtils;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/Users")
@Api(value = "/Users")
public class ScimUsersController
{

	private static final Logger LOG = Logger.getLogger(ScimUsersController.class);

	@Resource(name = "scimUserValidator")
	private Validator scimUserValidator;

	@Resource(name = "scimUserNameValidator")
	private Validator scimUserNameValidator;

	@Resource
	private ScimUserFacade scimUserFacade;

	@Resource(name = "scimUserEmailValidator")
	private Validator scimUserEmailValidator;

	@ApiOperation(value = "Create user in the system", response = ScimUser.class)
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	public ScimUser createUser(
			@ApiParam(value = "The ScimUser that contains information about the user") @RequestBody final ScimUser scimUser)
	{
		LOG.info("ScimUsersController.createUser entry. UserID=" + sanitize(scimUser.getId()));
		validate(scimUser, "user", scimUserValidator);
		validate(scimUser.getName(), "userName", scimUserNameValidator);
		validate(ScimUtils.getPrimaryEmail(scimUser.getEmails()), "emails", scimUserEmailValidator);

		return scimUserFacade.createUser(scimUser);
	}

	@ApiOperation(value = "Update user in the system", response = ScimUser.class)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	public ScimUser updateUser(@ApiParam(value = "User ID of the User") @PathVariable final String userId,
			@ApiParam(value = "The ScimUser that contains information about the usder") @RequestBody final ScimUser scimUser)
	{
		LOG.info("ScimUsersController.updateUser entry. UserID=" + scimUser.getId());

		validate(scimUser, "user", scimUserValidator);
		validate(scimUser.getName(), "userName", scimUserNameValidator);
		validate(ScimUtils.getPrimaryEmail(scimUser.getEmails()), "emails", scimUserEmailValidator);

		if (!StringUtils.equals(userId, scimUser.getId()))
		{
			throw new ScimException("Mismatch in user ids supplied for update " + userId);
		}

		return scimUserFacade.updateUser(userId, scimUser);
	}

	@ApiOperation(value = "Get user from the system")
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public ScimUser getUser(@ApiParam(value = "User ID of the User") @PathVariable final String userId)
	{
		LOG.info("ScimUsersController.getUser entry. UserID=" + sanitize(userId));

		return scimUserFacade.getUser(sanitize(userId));
	}

	@ApiOperation(value = "Get users from the system", response = ScimUser.class)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET)
	public ScimUser getUsers(final HttpServletRequest request, final HttpServletResponse response)
	{
		throw new ScimException("Fetching all users is currently not supported.");
	}

	@ApiOperation(value = "Delete user from the system")
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	public boolean deleteUser(@ApiParam(value = "User ID of the User") @PathVariable final String userId)
	{
		LOG.info("ScimUsersController.deleteUser entry. " + sanitize(userId));

		return scimUserFacade.deleteUser(sanitize(userId));
	}

	@ApiOperation(value = "Patch update user in the system", response = ScimUser.class)
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
	public ScimUser patchUser(@ApiParam(value = "User ID of the User") @PathVariable final String userId,
			@ApiParam(value = "The ScimUser that contains information about the usder") @RequestBody final ScimUser scimUser)
	{
		LOG.info("ScimUsersController.patchUser entry.");

		return scimUserFacade.updateUser(userId, scimUser);
	}

	/**
	 * Validates the object by using the passed validator
	 *
	 * @param object
	 *           the object to be validated
	 * @param objectName
	 *           the object name
	 * @param validator
	 *           validator which will validate the object
	 */
	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	/**
	 * Method to sanitize the input string
	 *
	 * @param input
	 *           the input string
	 * @return String sanitized string
	 */
	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}
}
