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
package de.hybris.platform.notificationoccaddon.controllers.pages;

import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceDataList;
import de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade;
import de.hybris.platform.notificationoccaddon.constants.ErrorMessageConstants;
import de.hybris.platform.notificationoccaddon.dto.conversation.NotificationPreferenceListWsDTO;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade}.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/my-account/notificationpreferences")
@Api(tags = "Notification Preference")
public class NotificationPreferenceController
{
	private static final List<String> TRUE_BOOLEAN_STRINGS = Arrays.asList("1", "true");
	private static final List<String> FALSE_BOOLEAN_STRINGS = Arrays.asList("0", "false");

	@Resource(name = "notificationPreferenceFacade")
	private NotificationPreferenceFacade notificationPreferenceFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Gets all notification preferences of current customer", notes = "Returns the notification preferences of current customer.")
	@ApiBaseSiteIdParam
	public NotificationPreferenceListWsDTO getNotificationPreferences()
	{

		final NotificationPreferenceDataList preferenceDataList = notificationPreferenceFacade
				.getNotificationPreferences((notificationPreferenceFacade.getValidNotificationPreferences()));

		return dataMapper.map(preferenceDataList, NotificationPreferenceListWsDTO.class);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Updates all notification preferences of current customer", notes = "Updates the notification preference of current customer.")
	@ApiBaseSiteIdParam
	public void updateNotificationPreferences(
			@ApiParam(value = "email channel", required = false) @RequestParam(required = false) final String email,
			@ApiParam(value = "sms channel", required = false) @RequestParam(required = false) final String sms,
			@ApiParam(value = "site message channel", required = false) @RequestParam(required = false) final String site_message,
			final HttpServletRequest request)
	{
		final Map<String, String> channels = getParameterMapFromRequest(request);
		final List<NotificationPreferenceData> preferences = notificationPreferenceFacade.getNotificationPreferences();

		final EnumMap<NotificationChannel, Boolean> channelMap = buildNotificationChannelMap(channels);
		validateNotificationPreference(channelMap);

		preferences.forEach(p -> updatePreferenceFromRequest(p, channelMap));

		notificationPreferenceFacade.updateNotificationPreference(preferences);
	}

	protected void updatePreferenceFromRequest(final NotificationPreferenceData data,
			final Map<NotificationChannel, Boolean> channelMap) {

		channelMap.forEach((k, v) -> {
			if (data.getChannel().equals(k))
			{
				data.setEnabled(v);
			}
		});
	}

	protected EnumMap<NotificationChannel, Boolean> buildNotificationChannelMap(final Map<String, String> channels)
	{
		if (channels.isEmpty())
		{
			throw new RequestParameterException(ErrorMessageConstants.MISSING_BOTH_PARAMS_MESSAGE,
					RequestParameterException.MISSING);
		}

		final EnumMap<NotificationChannel, Boolean> notifications = new EnumMap<>(NotificationChannel.class);

		channels.forEach((k, v) -> {
			try
			{
				notifications.put(NotificationChannel.valueOf(k.toUpperCase(Locale.ROOT)), stringToBoolean(v, k));
			}
			catch (final IllegalArgumentException e)
			{
				throw new RequestParameterException(String.format(ErrorMessageConstants.INVALID_CHANNEL_MESSAGE, k),
						RequestParameterException.INVALID);
			}
		});

		return notifications;
	}

	protected Boolean stringToBoolean(final String source, final String subject)
	{
		if (TRUE_BOOLEAN_STRINGS.contains(source.toLowerCase(Locale.ROOT)))
		{
			return Boolean.TRUE;
		}
		if (FALSE_BOOLEAN_STRINGS.contains(source.toLowerCase(Locale.ROOT)))
		{
			return Boolean.FALSE;
		}
			throw new RequestParameterException(String.format(ErrorMessageConstants.INVALID_VALUE_MESSAGE, subject),
					RequestParameterException.INVALID);
	}

	protected void validateNotificationPreference(final Map<NotificationChannel, Boolean> channels)
	{
		if (channels.keySet().contains(NotificationChannel.SMS)
				&& StringUtils.isBlank(notificationPreferenceFacade.getChannelValue(NotificationChannel.SMS)))
		{
			throw new RequestParameterException(ErrorMessageConstants.NO_MOBILE_BOUND_MESSAGE, RequestParameterException.INVALID,
					ErrorMessageConstants.SMS_PARAM_NAME);
		}
	}

	protected Map getParameterMapFromRequest(final HttpServletRequest request)
	{
		final Map<String, String[]> parameterMap = request.getParameterMap();
		final Map<String, String> result = new LinkedHashMap<String, String>();
		if (MapUtils.isEmpty(parameterMap))
		{
			return result;
		}
		for (final Map.Entry<String, String[]> entry : parameterMap.entrySet())
		{
			if (entry.getValue().length > 0)
			{
				result.put(entry.getKey(), entry.getValue()[0]);
			}
		}
		return result;
	}

}

