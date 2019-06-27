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

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Strategy is a simple POJO representing a Merchandising Strategy.
 */
public class Strategy
{
	@JsonProperty(value = "id")
	private String id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "description")
	private String description;

	@JsonProperty(value = "live")
	private Boolean live;

	@JsonProperty(value = "mix_associations")
	@Valid
	private List<MixAssociation> mixAssociations = new ArrayList<>(0);

	/**
	 * @return the id. This is the ID of the Strategy.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set. This is the ID of the Strategy.
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the name. This is the name of the Strategy.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set. This is the name of the Strategy.
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the description. This is a free text description of the Strategy.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set. This is a free text description of the Strategy.
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * @return live - whether the Strategy is configured to be live or not.
	 */
	public Boolean getLive()
	{
		return live;
	}

	/**
	 * @param live
	 *           the live to set - whether the Strategy is configured to be live or not.
	 */
	public void setLive(final Boolean live)
	{
		this.live = live;
	}

	/**
	 * @return the mixAssociations. This is a list of associations that the Strategy has.
	 */
	public List<MixAssociation> getMixAssociations()
	{
		return mixAssociations;
	}

	/**
	 * @return the mixAssociations. This is a list of associations that the Strategy has.
	 */
	public void setMixAssociations(final List<MixAssociation> associations) {
		this.mixAssociations = associations;
	}
}
