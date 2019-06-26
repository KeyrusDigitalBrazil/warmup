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

package de.hybris.platform.stocknotificationaddon.forms;

/**
 *
 */
public class NotificationChannelForm
{

	private String channel;
	private String value;
	private boolean enabled;
	private boolean visible;


	public String getChannel()
	{
		return channel;
	}

	public void setChannel(final String channel)
	{
		this.channel = channel;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(final boolean visible)
	{
		this.visible = visible;
	}

}
