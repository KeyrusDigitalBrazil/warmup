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
package de.hybris.platform.assistedservicestorefront.restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Path restriction implementation for all Assisted Service Agents. Paths from provided list are restricted.
 */
public class AssitedServiceAllAgentsPathRestriction extends AssistedServicePathRestriction
{
	@Override
	public boolean evaluate(final HttpServletRequest httpservletrequest, final HttpServletResponse httpservletresponse)
	{
		if (pathMatches(httpservletrequest) && getAssistedServiceFacade().isAssistedServiceAgentLoggedIn())
		{
			sendRedirectToPreviousPage(httpservletrequest, httpservletresponse);
			return false;
		}
		return true;
	}
}
