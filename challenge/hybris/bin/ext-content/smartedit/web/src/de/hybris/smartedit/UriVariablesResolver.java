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
package de.hybris.smartedit;

import java.util.Objects;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



/**
 * Helper class to resolve URI variables to be used with spring integration configuration
 */
public class UriVariablesResolver
{
	private static final String HTTP = "http";

	private static String unsecuredPort;
	private static String securedPort;
	private static String hostname;

	private UriVariablesResolver()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Use the host name defined in {@code project.properties}.
     *
	 * @return the host address
	 */
	public static String resolveHost()
	{
		// Use the host name defined in project.properties
		if (Strings.isBlank(hostname))
		{
		    throw new IllegalStateException("unknown hostname - Make sure to set the smartedit.tomcat.hostname property");
		} else {
			return hostname;
        }
	}

	/**
	 * Find the port of the server running smartedit. If the port in the request does not match the port of the current
	 * host, then return the current host port instead.
	 *
	 * @return the host port
	 */
	public static String resolvePort()
	{
		final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		final String scheme = requestAttributes.getRequest().getScheme();
		String port = String.valueOf(requestAttributes.getRequest().getServerPort());

		if (!port.equals(unsecuredPort) && !port.equals(securedPort))
		{
			if (Objects.nonNull(scheme) && scheme.equalsIgnoreCase(HTTP))
			{
				port = unsecuredPort;
			}
			else
			{
				port = securedPort;
			}
		}
		return port;
	}

	/**
	 * Set the port values used by the host
	 *
	 * @param securedPort
	 *           the value of the secured (ssl) port
	 * @param unsecuredPort
	 *           the value of the unsecured (http) port
	 */
	public static void setHostConfiguration(final String securedPort, final String unsecuredPort, final String hostname)
	{
		UriVariablesResolver.securedPort = securedPort;
		UriVariablesResolver.unsecuredPort = unsecuredPort;
		UriVariablesResolver.hostname = hostname;
	}


}
