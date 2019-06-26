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
package de.hybris.platform.sap.core.configuration.rfc.event;



/**
 * Event is triggered in case of change JCoTrace level.
 */
public class SAPRFCDestinationJCoTraceEvent extends SAPRFCDestinationEvent
{

	private static final long serialVersionUID = -405191119466683723L;

	private String jcoTracePath;

	/**
	 * Default Constructor.
	 */
	public SAPRFCDestinationJCoTraceEvent()
	{
	}

	/**
	 * Constructor.
	 * 
	 * @param traceLevel
	 *           trace level
	 */
	public SAPRFCDestinationJCoTraceEvent(final String traceLevel)
	{
		super(traceLevel);
	}

	/**
	 * Returns the path where the JCo trace will be written.
	 * 
	 * @return the jcoTracePath
	 */
	public String getJCoTracePath()
	{
		return jcoTracePath;
	}

	/**
	 * Sets the path for the JCo trace.
	 * 
	 * @param jcoTracePath
	 *           the jcoTracePath to set
	 */
	public void setJCoTracePath(final String jcoTracePath)
	{
		this.jcoTracePath = jcoTracePath;
	}

}
