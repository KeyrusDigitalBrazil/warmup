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
package de.hybris.platform.stocknotificationoccaddon.controllers.pages;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.stocknotificationoccaddon.constants.ErrorMessageConstants;
import de.hybris.platform.stocknotificationoccaddon.dto.stocknotification.ProductInterestWsDTO;
import de.hybris.platform.stocknotificationoccaddon.exceptions.StockNotificationException;
import de.hybris.platform.stocknotificationoccaddon.validator.StockNotificationValidator;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
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
 * Controller for Stock Notification Occ Addon.
 */
@Controller
@RequestMapping("/{baseSiteId}/stocknotifications")
@Api(tags = "Stock Notifications")
public class StockNotificationController
{
	private static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
	private static final List<String> TRUE_BOOLEAN_STRINGS = Arrays.asList("1", "true");
	private static final List<String> FALSE_BOOLEAN_STRINGS = Arrays.asList("0", "false");

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "productInterestFacade")
	private ProductInterestFacade productInterestFacade;

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	@Resource(name = "notificationPreferenceFacade")
	private NotificationPreferenceFacade notificationPreferenceFacade;

	@Resource(name = "stockNotificationValidator")
	private StockNotificationValidator stockNotificationValidator;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value = "Gets notification settings of a product", notes = "Gets notification settings of a product.")
	@ApiBaseSiteIdParam
	public ProductInterestWsDTO getStockNotification(
			@ApiParam(value = "product identifier", required = true) @RequestParam final String productCode)
			throws StockNotificationException
	{
		getStockNotificationValidator().validateProuctStockLevel(productCode);

		return getProductInterestFacade().getProductInterestDataForCurrentCustomer(productCode, NotificationType.BACK_IN_STOCK)
				.map(i -> getDataMapper().map(i, ProductInterestWsDTO.class, DEFAULT_FIELD_SET)).orElse(null);
	}

	@RequestMapping(method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value = "Adds, updates or removes notification settings of a product", notes = "Adds, updates or removes notification settings of a product.")
	@ApiBaseSiteIdParam
	public void addStockNotification(
			@ApiParam(value = "email channel", required = false) @RequestParam(required = false) final String email,
			@ApiParam(value = "sms channel", required = false) @RequestParam(required = false) final String sms,
			@ApiParam(value = "site message channel", required = false) @RequestParam(required = false) final String site_message,
			@ApiParam(value = "product identifier", required = true) @RequestParam(required = true) final String productCode,
			final HttpServletRequest request)
	{
		final Map<String, String> parameters = getParameterMapFromRequest(request);
		final String code = parameters.get("productCode");
		getStockNotificationValidator().validateProuctStockLevel(code);
		parameters.remove("productCode");

		if (parameters.isEmpty())
		{
			throw new RequestParameterException(ErrorMessageConstants.MISSING_CHANNEL_PARAMS_MESSAGE,
					RequestParameterException.MISSING);
		}

		final EnumMap<NotificationChannel, Boolean> channels = new EnumMap<>(NotificationChannel.class);
		parameters.forEach((k, v) -> channels.put(stringToChannel(k), stringToBoolean(v.toLowerCase(Locale.ROOT), k)));

		if (channels.containsKey(NotificationChannel.SMS)
				&& StringUtils.isBlank(getNotificationPreferenceFacade().getChannelValue(NotificationChannel.SMS)))
		{
			throw new RequestParameterException(ErrorMessageConstants.NO_MOBILE_BOUND_MESSAGE, RequestParameterException.INVALID,
					ErrorMessageConstants.SMS_PARAM_NAME);
		}

		final ProductInterestData productInterestData = getProductInterestFacade()
				.getProductInterestDataForCurrentCustomer(code, NotificationType.BACK_IN_STOCK)
				.orElse(new ProductInterestData());

		handleProductInterest(code, channels, productInterestData);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value = "Removes all notification settings of the product", notes = "Removes all notification settings of the product.")
	@ApiBaseSiteIdParam
	public void removeStockNotification(
			@ApiParam(value = "product identifier", required = true) @RequestParam final String productCode)
	{
		getStockNotificationValidator().validateProuctStockLevel(productCode);

		getProductInterestFacade().removeProductInterest(
				getProductInterestFacade().getProductInterestDataForCurrentCustomer(productCode, NotificationType.BACK_IN_STOCK)
						.orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NO_PRODUCT_INTEREST_MESSAGE,
								ErrorMessageConstants.NO_PRODUCT_INTEREST, productCode)));
	}

	protected void handleProductInterest(final String productCode, final EnumMap<NotificationChannel, Boolean> channels,
			final ProductInterestData productInterestData)
	{
		if (productInterestData.getProduct() == null)
		{
			if (!channels.values().contains(Boolean.TRUE))
			{
				return;
			}
			final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
					Arrays.asList(ProductOption.BASIC));
			productInterestData.setProduct(product);
			productInterestData.setNotificationType(NotificationType.BACK_IN_STOCK);
			productInterestData.setNotificationChannels(createNotificationPreferences(channels));
		}
		else
		{
			productInterestData.getNotificationChannels().forEach(c -> {
				final NotificationChannel channel = c.getChannel();
				if (channels.containsKey(channel))
				{
					c.setEnabled(channels.get(channel).booleanValue());
				}
			});
			if (productInterestData.getNotificationChannels().stream().allMatch(p -> !p.isEnabled()))
			{
				getProductInterestFacade().removeProductInterest(productInterestData);
				return;
			}
		}
		getProductInterestFacade().saveProductInterest(productInterestData);
	}

	protected List<NotificationPreferenceData> createNotificationPreferences(final EnumMap<NotificationChannel, Boolean> channels)
	{
		final List<NotificationPreferenceData> preferences = new ArrayList();
		channels.forEach((k, v) -> {
			if (BooleanUtils.isTrue(v))
			{
				final NotificationPreferenceData preference = new NotificationPreferenceData();
				preference.setChannel(k);
				preference.setEnabled(true);
				preferences.add(preference);
			}
		});

		return preferences;
	}

	protected NotificationChannel stringToChannel(final String source)
	{
		try
		{
			return NotificationChannel.valueOf(source.toUpperCase(Locale.ROOT));
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException(String.format(ErrorMessageConstants.INVALID_PREFERENCE_MESSAGE, source),
					RequestParameterException.INVALID, null, e);
		}
	}

	protected Boolean stringToBoolean(final String source, final String subject)
	{
		if (TRUE_BOOLEAN_STRINGS.contains(source))
		{
			return Boolean.TRUE;
		}
		if (FALSE_BOOLEAN_STRINGS.contains(source))
		{
			return Boolean.FALSE;
		}
		throw new RequestParameterException(String.format(ErrorMessageConstants.INVALID_PARAMETER_MESSAGE, subject),
				RequestParameterException.INVALID);
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

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected ProductInterestFacade getProductInterestFacade()
	{
		return productInterestFacade;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected StockNotificationValidator getStockNotificationValidator()
	{
		return stockNotificationValidator;
	}

	protected NotificationPreferenceFacade getNotificationPreferenceFacade()
	{
		return notificationPreferenceFacade;
	}

}
