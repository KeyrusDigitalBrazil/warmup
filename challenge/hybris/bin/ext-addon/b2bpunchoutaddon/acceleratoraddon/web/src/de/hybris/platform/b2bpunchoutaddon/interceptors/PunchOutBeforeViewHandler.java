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
package de.hybris.platform.b2bpunchoutaddon.interceptors;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


public class PunchOutBeforeViewHandler implements BeforeViewHandler
{

	public static final String VIEW_NAME_MAP_KEY = "viewName";
	private Map<String, Map<String, String>> viewMap;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws PunchOutException
	{
		final String viewName = modelAndView.getViewName();
		try
		{
			if (StringUtils.isNotBlank((String) request.getSession().getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)))
			{
				modelAndView.setViewName(getPunchoutView(viewName));
				setPunchoutModeInModel(modelAndView.getModelMap());
			}
		}
		catch (final Exception e)
		{
			throw new PunchOutException(PunchOutResponseCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}
	}

	protected String getPunchoutView(String viewName)
	{
		if (viewMap.containsKey(viewName))
		{
			viewName = B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX + viewMap.get(viewName).get(VIEW_NAME_MAP_KEY);
		}
		return viewName;
	}

	protected void setPunchoutModeInModel(final ModelMap model)
	{
		model.addAttribute("punchoutMode", Boolean.TRUE);
	}

	public Map<String, Map<String, String>> getViewMap()
	{
		return viewMap;
	}

	public void setViewMap(final Map<String, Map<String, String>> viewMap)
	{
		this.viewMap = viewMap;
	}

}
