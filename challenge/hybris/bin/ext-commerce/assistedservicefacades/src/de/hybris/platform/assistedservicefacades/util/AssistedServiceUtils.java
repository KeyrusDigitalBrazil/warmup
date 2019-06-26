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
package de.hybris.platform.assistedservicefacades.util;

import de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.util.Config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.WebUtils;


public final class AssistedServiceUtils
{
	private AssistedServiceUtils()
	{
	}

	public static String getCardLastFourDigits(final CustomerModel customer)
	{
		final String cardNumber = customer.getDefaultPaymentInfo() instanceof CreditCardPaymentInfoModel
				? ((CreditCardPaymentInfoModel) customer.getDefaultPaymentInfo()).getNumber() : null;

		if (cardNumber == null)
		{
			return "----";
		}

		else if (cardNumber.length() >= 4)
		{
			return cardNumber.subSequence(cardNumber.length() - 4, cardNumber.length()).toString();
		}

		else
		{
			return cardNumber.subSequence(0, cardNumber.length()).toString();
		}
	}

	public static String getCreationDate(final CustomerModel customer)
	{
		return customer.getCreationtime() != null ? new SimpleDateFormat("dd/MM/YYYY").format(customer.getCreationtime())
				: "--/--/----";
	}

	/**
	 * Erase SAML sso cookie from browser.
	 *
	 * @param response
	 */
	public static void eraseSamlCookie(final HttpServletResponse response)
	{
		final String cookieName = Config.getParameter(AssistedservicefacadesConstants.SSO_COOKIE_NAME);
		if (cookieName != null)
		{
			final Cookie cookie = new Cookie(cookieName, "");
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			cookie.setSecure(true);
			response.addCookie(cookie);
		}
	}

	/**
	 * Get SAML sso cookie.
	 *
	 * @param request
	 * @return saml SSO cookie if persist
	 */
	public static Cookie getSamlCookie(final HttpServletRequest request)
	{
		final String cookieName = Config.getParameter(AssistedservicefacadesConstants.SSO_COOKIE_NAME);
		return cookieName != null ? WebUtils.getCookie(request, cookieName) : null;
	}

	/**
	 * @param abstractOrder
	 * @param currentSite
	 *
	 * @return site URL associated to order or cart.
	 */
	public static String populateCartorOrderUrl(final AbstractOrderModel abstractOrder, final BaseSiteModel currentSite)
	{
		final StringBuilder siteUrlBldr = new StringBuilder();
		final BaseSiteModel baseSiite = abstractOrder.getSite();
		if (baseSiite != null && !baseSiite.getUid().equals(currentSite.getUid()) && abstractOrder.getUser() != null)
		{
			siteUrlBldr.append(Config.getParameter("website." + baseSiite.getUid() + ".https"));
			siteUrlBldr.append(Config.getParameter(AssistedservicefacadesConstants.ASM_DEEPLINK_PARAM) + "?customerId="
					+ abstractOrder.getUser().getUid() + "&");

			if (abstractOrder instanceof CartModel && ((CartModel) abstractOrder).getSaveTime() != null)
			{
				siteUrlBldr.append("&fwd=/my-account/saved-carts/").append(abstractOrder.getCode());
			}
			else
			{
				siteUrlBldr.append(abstractOrder.getItemtype()).append("Id=").append(abstractOrder.getGuid());
			}
		}
		return siteUrlBldr.toString();
	}

	/**
	 * @param ticket
	 * @param currentSite
	 *
	 * @return customer support ticket URL.
	 */
	public static String populateTicketUrl(final CsTicketModel ticket, final BaseSiteModel currentSite)
	{
		final StringBuilder siteUrlBldr = new StringBuilder();
		if (ticket.getBaseSite() != null && !ticket.getBaseSite().getUid().equals(currentSite.getUid())
				&& ticket.getCustomer() != null)
		{
			siteUrlBldr.append(Config.getParameter("website." + ticket.getBaseSite().getUid() + ".https"));
			siteUrlBldr.append(Config.getParameter(AssistedservicefacadesConstants.ASM_DEEPLINK_PARAM)).append("?customerId=");
			siteUrlBldr.append(ticket.getCustomer().getUid()).append("&");
			siteUrlBldr.append("&fwd=/my-account/support-ticket/").append(ticket.getTicketID());
		}
		return siteUrlBldr.toString();
	}

	public static TimeSince getTimeSince(final Date date)
	{
		final List<Long> timeMillis = Arrays.asList(Long.valueOf(TimeUnit.DAYS.toMillis(365)),
				Long.valueOf(TimeUnit.DAYS.toMillis(30)), Long.valueOf(TimeUnit.DAYS.toMillis(1)),
				Long.valueOf(TimeUnit.HOURS.toMillis(1)), Long.valueOf(TimeUnit.MINUTES.toMillis(1)),
				Long.valueOf(TimeUnit.SECONDS.toMillis(1)));
		final List<TimeSince> timeUnits = Arrays.asList(TimeSince.YEARS, TimeSince.YEAR, TimeSince.MONTHS, TimeSince.MONTH,
				TimeSince.DAYS, TimeSince.DAY, TimeSince.HOURS, TimeSince.HOUR, TimeSince.MINUTES, TimeSince.MINUTE,
				TimeSince.SECONDS, TimeSince.SECOND);
		if (date == null)
		{
			return TimeSince.MOMENT;
		}
		final long diff = System.currentTimeMillis() - date.getTime();
		for (int i = 0; i < timeMillis.size(); ++i)
		{
			final long current = timeMillis.get(i);
			final long temp = diff / current;
			if (temp > 0)
			{
				if (temp == 1)
				{
					return timeUnits.get(i * 2 + 1).setValue(temp);
				}
				return timeUnits.get(i * 2).setValue(temp);
			}
		}
		return TimeSince.MOMENT;
	}
}
