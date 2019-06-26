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
package com.sap.hybris.sec.eventpublisher.websocket.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.WebUtils;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.handler.impl.AfterOrderSaveEventHandler;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

public class WebSocketSecureFilter extends GenericFilterBean {
	
	private static final Logger LOGGER = LogManager.getLogger(WebSocketSecureFilter.class);

	private UserService userService;
	private ConfigurationService configurationService;

	@Autowired
	ApplicationContext applicationContext;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
			throws IOException, ServletException {

		boolean validRequest = false;
		boolean isLocalRequest = isLocalRequest(request);

		if (!isLocalRequest && getSamlCookie((HttpServletRequest) request) != null) {

			CookieBasedLoginToken token = new CookieBasedLoginToken(
					getSamlCookie((HttpServletRequest) request));
			
			validRequest = isVaildUserToken(response, token);
			if(!validRequest){
				((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}

		} else {
			((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}

		if (validRequest || isLocalRequest) {
			filterchain.doFilter(request, response);
		}

	}

	private Cookie getSamlCookie(HttpServletRequest request) {
		final String cookieName = Config.getParameter(EventpublisherConstants.SSO_COOKIE_NAME);
		return cookieName != null ? WebUtils.getCookie(request, cookieName) : null;
	}

	private synchronized boolean isLocalRequest(final ServletRequest request) {
		boolean isLocalRequest = false;
		
		Configuration configuration = configurationService.getConfiguration();
		
		final String serverPort = configuration.getString(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_PORT);
		final String host = configuration.getString(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_HOST_INTERNAL);

		AfterOrderSaveEventHandler afterOrderSaveBean = (AfterOrderSaveEventHandler) applicationContext
				.getBean(EventpublisherConstants.AFTER_ORDER_SAVE_EVENT_HANDLER);
		
		String code = afterOrderSaveBean.getCode();
		String queryParamerter = request.getParameter(EventpublisherConstants.CODE_CONSTANT);
		
		if (request.getServerName().equals(host) 
				&& serverPort.equals(Integer.toString(request.getServerPort()))
				&& code != null && code.equals(queryParamerter)) {
			
			isLocalRequest = true;
		}

		if(isLocalRequest){
			LOGGER.debug("local requested is verified:" + request.getServerName()+". returning "+isLocalRequest);
		}
		
		return isLocalRequest;
	}

	protected boolean isVaildUserToken(ServletResponse response, CookieBasedLoginToken token) {
		boolean vaildRequest = false;
		
		if (token != null && !token.getPassword().isEmpty() && token.getUser() != null) {
			final UserModel agent = getUserService().getUserForUID(token.getUser().getUid(), EmployeeModel.class);
			vaildRequest = isValidAgent(response, token, agent);
		}else{
			LOGGER.debug("token is invalid.");
		}
		return vaildRequest;
	}

	protected boolean isValidAgent(ServletResponse response, CookieBasedLoginToken token, final UserModel agent) {
		boolean validAgent = false;
		if (agent != null && !agent.isLoginDisabled() && agent.getEncodedPassword().equals(token.getPassword())) {
			validAgent = true;
		}else{
			if(agent == null){
				LOGGER.debug("Agent passed is null");
			}else if(agent.isLoginDisabled()){
				LOGGER.debug("Agent login is disabled");
			}
			LOGGER.info("Agent is not valid.");
		}
		return validAgent;
	}

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
