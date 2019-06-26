/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * MixAssociation is a POJO representing an association between a Strategy and a Mixcard.
 */
public class MixAssociation
{
	@JsonProperty(value = "id")
	private String id;

	@JsonProperty(value = "mixcard_id")
	private String mixcardId;

	@JsonProperty(value = "categories")
	private List<Category> categories = new ArrayList<>(0);

	@JsonProperty(value = "time_start")
	private String timeStart;

	@JsonProperty(value = "time_end")
	private String timeEnd;

	/**
	 * @return the id. This is the ID of the association.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set. This is the ID of the association.
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the mixcardId. This is the ID of the Mixcard being used.
	 */
	public String getMixcardId()
	{
		return mixcardId;
	}

	/**
	 * @param mixcardId
	 *           the mixcardId to set. This is the ID of the Mixcard being used.
	 */
	public void setMixcardId(final String mixcardId)
	{
		this.mixcardId = mixcardId;
	}

	/**
	 * @return the timeStart. This is an ISO-8601 timestamp representing the time that the association is valid for.
	 */
	public String getTimeStart()
	{
		return timeStart;
	}

	/**
	 * @param timeStart
	 *           the timeStart to set. This is an ISO-8601 timestamp representing the time that the association is valid
	 *           for.
	 */
	public void setTimeStart(final String timeStart)
	{
		this.timeStart = timeStart;
	}

	/**
	 * @return the timeEnd. This is an ISO-8601 timestamp representing the time that the association is valid for.
	 */
	public String getTimeEnd()
	{
		return timeEnd;
	}

	/**
	 * @param timeEnd
	 *           the timeEnd to set. . This is an ISO-8601 timestamp representing the time that the association is valid
	 *           for.
	 */
	public void setTimeEnd(final String timeEnd)
	{
		this.timeEnd = timeEnd;
	}

	/**
	 * @return the categories. This is a list of Categories that the association has.
	 */
	public List<Category> getCategories()
	{
		return categories;
	}

	/**
	 * Sets the categories.
	 * 
	 * @param categories.
	 *           This is a list of Categories that the association has.
	 */
	public void setCategories(final List<Category> categories)
	{
		this.categories = categories;
	}
}
