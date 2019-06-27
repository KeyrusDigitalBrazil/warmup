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
package de.hybris.platform.gigya.gigyaloginaddon.renderers;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.gigya.gigyaloginaddon.model.GigyaRaasComponentModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;


public class GigyaRaasComponentRenderer extends DefaultAddOnCMSComponentRenderer<GigyaRaasComponentModel>
{

	private static final Logger LOG = Logger.getLogger(GigyaRaasComponentRenderer.class);

	private UserService userService;

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final GigyaRaasComponentModel component)
	{
		final Map<String, Object> variables = super.getVariablesToExpose(pageContext, component);
		final HashMap<String, Object> raasConfig = new HashMap<>();
		final ObjectMapper mapper = new ObjectMapper();

		raasConfig.put("screenSet", component.getScreenSet());
		raasConfig.put("startScreen", component.getStartScreen());
		raasConfig.put("profileEdit", component.getProfileEdit());
		raasConfig.put("sessionExpiration", Config.getInt("default.session.timeout", 3600));

		if (component.getEmbed() == Boolean.TRUE)
		{
			raasConfig.put("containerID", component.getContainerID());
			variables.put("containerID", component.getContainerID());
		}

		if (StringUtils.isNotBlank(component.getAdvancedConfiguration()))
		{
			HashMap<String, Object> advConfig = new HashMap<>();
			try
			{
				advConfig = mapper.readValue(component.getAdvancedConfiguration(), HashMap.class);
				raasConfig.putAll(advConfig);
			}
			catch (final IOException e)
			{
				LOG.error("Exception in converting json string to map" + e);
			}
		}

		final boolean isAnonymousUser = userService.isAnonymousUser(userService.getCurrentUser());
		try
		{
			variables.put("id", component.getUid().replaceAll("[^A-Za-z0-9]", ""));
			variables.put("gigyaRaas", mapper.writeValueAsString(raasConfig));

			final Boolean show;
			if (isAnonymousUser)
			{
				show = component.getShowAnonymous();
			}
			else
			{
				show = component.getShowLoggedIn();
			}
			variables.put("show", show);
			variables.put("profileEdit", component.getProfileEdit());
		}
		catch (final IOException e)
		{
			LOG.error("Exception in converting map to json string" + e);
		}

		variables.put("authenticated", !isAnonymousUser);
		return variables;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
