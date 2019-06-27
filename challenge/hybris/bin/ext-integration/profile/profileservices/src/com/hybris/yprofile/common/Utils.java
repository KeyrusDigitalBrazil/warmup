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
package com.hybris.yprofile.common;

import com.hybris.yprofile.consent.cookie.EnhancedCookieGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.WebUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by d064321 on 01.12.15.
 *
 * Formatting utility to create correct data format for yaas.
 */
public class Utils {

    private static final Map<String, String> SITE_ID_MAP = new HashMap<String, String>();

    static {
        SITE_ID_MAP.put("1", "electronics");
        SITE_ID_MAP.put("2", "apparel-de");
        SITE_ID_MAP.put("3", "apparel-uk");
    }

    private Utils() {
        //Default private constructor
    }

    public static String formatDouble(Double d){
        if(d == null){
            return "";
        }
        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
        return decimalFormat.format(d);
    }

    public static String formatDate(Date d){
        final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return df2.format(d);
    }

    public static String remapSiteId(String siteId) {
       final  String result = SITE_ID_MAP.get(siteId);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        return siteId;
    }


    public static Optional<Cookie> getCookie(final HttpServletRequest request, final String cookieName) {
        if (request == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(WebUtils.getCookie(request, cookieName));
    }


    public static void setCookie(final EnhancedCookieGenerator enhancedCookieGenerator,
                                 final HttpServletResponse response,
                                 final String cookieName,
                                 final String cookieValue) {

        enhancedCookieGenerator.setCookieName(cookieName);
        enhancedCookieGenerator.addCookie(response, cookieValue);
    }

    public static void removeCookie(final EnhancedCookieGenerator enhancedCookieGenerator,
                                 final HttpServletResponse response,
                                 final String cookieName) {

        enhancedCookieGenerator.setCookieName(cookieName);
        enhancedCookieGenerator.removeCookie(response);
    }

}
