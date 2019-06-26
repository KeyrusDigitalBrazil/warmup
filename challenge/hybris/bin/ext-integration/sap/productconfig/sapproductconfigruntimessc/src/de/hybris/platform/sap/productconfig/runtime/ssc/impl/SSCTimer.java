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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.GenericTimer;

import org.apache.log4j.Logger;


/**
 * Helper class to enable to simple performance measurements of configuration engine calls. It will simple trace the
 * duration of every configuration engine call and log it with DEBUG severity.<br>
 * Set the LogLevel for this class to debug to enable the measurement.
 */
public class SSCTimer extends GenericTimer
{
	private static final String SSC = "SSC";
	private static final Logger LOG = Logger.getLogger(SSCTimer.class);

	@Override
	protected Logger getLogger()
	{
		return LOG;
	}

	@Override
	protected String getMeasuredDomainName()
	{
		return SSC;
	}
}
