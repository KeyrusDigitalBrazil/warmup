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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import org.apache.log4j.Logger;


/**
 * geneic class for call time measurements
 */
public abstract class GenericTimer
{


	private long startTime;
	private long endTime;


	private String step;

	/**
	 * Default Constructor, will initiate the measurement.
	 */
	public GenericTimer()
	{
		super();
		startTime = System.nanoTime();
	}

	protected abstract Logger getLogger();

	/**
	 * Stops the measurement and logs the result.
	 */
	public void stop()
	{
		endTime = System.nanoTime();
		logTime();
	}

	protected void logTime()
	{
		if (!getLogger().isDebugEnabled())
		{
			return;
		}
		final long timeInNanos = endTime - startTime;
		final long timeInMs = Math.round(timeInNanos / (1000.0 * 1000.0));

		getLogger().debug("Call to " + getMeasuredDomainName() + " (" + step + ") took " + timeInMs + "ms");
	}

	/**
	 * @return name of the framework/domain for which the class are measured
	 */
	protected abstract String getMeasuredDomainName();

	/**
	 * Will re-initiate the measurement.
	 *
	 * @param step
	 *           name of the configuration engine call, will be logged together with the result.
	 */
	public void start(final String step)
	{
		this.step = step;
		startTime = System.nanoTime();
	}
}
