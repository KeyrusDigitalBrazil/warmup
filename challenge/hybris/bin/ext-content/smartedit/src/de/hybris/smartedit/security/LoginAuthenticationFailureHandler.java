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
package de.hybris.smartedit.security;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static de.hybris.smartedit.security.SecurityError.AUTHENTICATION_ERROR_BAD_CREDENTIALS;
import static java.lang.String.format;

public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
    private static final Logger LOG = Logger.getLogger(LoginAuthenticationFailureHandler.class);

    private final FlashMapManager flashMapManager;

    public LoginAuthenticationFailureHandler(final FlashMapManager flashMapManager) {
        this.flashMapManager = flashMapManager;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException
    {
        final FlashMap flashMap = new FlashMap();
        flashMap.put("errorcode", AUTHENTICATION_ERROR_BAD_CREDENTIALS.getCode());
        LOG.debug(
                format("Adding flash attribute with error code to the request for error code: [%s]",
                  AUTHENTICATION_ERROR_BAD_CREDENTIALS.getCode()));
        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        super.onAuthenticationFailure(request, response, exception);
    }


}