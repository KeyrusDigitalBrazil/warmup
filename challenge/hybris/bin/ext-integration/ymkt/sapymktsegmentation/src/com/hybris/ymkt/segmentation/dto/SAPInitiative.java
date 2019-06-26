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
package com.hybris.ymkt.segmentation.dto;


public class SAPInitiative implements Comparable<SAPInitiative>
{
	protected String id; // With leading zeroes.
	protected String name;
	protected String memberCount;
	
	@Override
	public int compareTo(SAPInitiative o)
	{
		return this.id.compareTo(o.id);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() == obj.getClass())
		{
			final SAPInitiative other = (SAPInitiative) obj;
			return this.id.equals(other.id);
		}
		return false;
	}

	public String getId()
	{
		return id;
	}

	public String getMemberCount()
	{
		return memberCount;
	}

	public String getName()
	{
		return name;
	}

	public String getFormattedLabel()
	{
		return this.getId() + " " + this.getName() + " (" + this.getMemberCount() + ")";
	}

	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setMemberCount(final String memberCount)
	{
		this.memberCount = memberCount;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "id: " + id + " name: " + name + " Member Count: " + memberCount;
	}
}