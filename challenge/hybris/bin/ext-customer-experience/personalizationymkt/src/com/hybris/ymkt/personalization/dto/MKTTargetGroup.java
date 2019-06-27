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
/**
 * 
 */
package com.hybris.ymkt.personalization.dto;

import java.util.UUID;


/**
 * 
 */
public class MKTTargetGroup
{
	protected UUID targetGroupUUID;
	protected String targetGroup;
	protected String targetGroupName;
	protected String targetGroupDescription;
	protected int targetGroupMemberCount;
	protected String marketingArea;

	/**
	 * @return the targetGroupMemberUUID
	 */
	public UUID getTargetGroupUUID()
	{
		return targetGroupUUID;
	}

	/**
	 * @param targetGroupUUID
	 *           the targetGroupUUID to set
	 */
	public void setTargetGroupUUID(UUID targetGroupUUID)
	{
		this.targetGroupUUID = targetGroupUUID;
	}

	/**
	 * @return the targetGroup
	 */
	public String getTargetGroup()
	{
		return targetGroup;
	}

	/**
	 * @param targetGroup
	 *           the targetGroup to set
	 */
	public void setTargetGroup(String targetGroup)
	{
		this.targetGroup = targetGroup;
	}

	/**
	 * @return the targetGroupName
	 */
	public String getTargetGroupName()
	{
		return targetGroupName;
	}

	/**
	 * @param targetGroupName
	 *           the targetGroupName to set
	 */
	public void setTargetGroupName(String targetGroupName)
	{
		this.targetGroupName = targetGroupName;
	}

	/**
	 * @return the targetGroupDescription
	 */
	public String getTargetGroupDescription()
	{
		return targetGroupDescription;
	}

	/**
	 * @param targetGroupDescription
	 *           the targetGroupDescription to set
	 */
	public void setTargetGroupDescription(String targetGroupDescription)
	{
		this.targetGroupDescription = targetGroupDescription;
	}

	/**
	 * @return the targetGroupMemberCount
	 */
	public int getTargetGroupMemberCount()
	{
		return targetGroupMemberCount;
	}

	/**
	 * @param targetGroupMemberCount
	 *           the targetGroupMemberCount to set
	 */
	public void setTargetGroupMemberCount(int targetGroupMemberCount)
	{
		this.targetGroupMemberCount = targetGroupMemberCount;
	}

	/**
	 * @return the marketingArea
	 */
	public String getMarketingArea()
	{
		return marketingArea;
	}

	/**
	 * @param marketingArea
	 *           the marketingArea to set
	 */
	public void setMarketingArea(String marketingArea)
	{
		this.marketingArea = marketingArea;
	}

}
