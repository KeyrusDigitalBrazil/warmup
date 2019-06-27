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
package com.hybris.merchandising.dto;

/**
 * An individual element of a SmartEdit drop down. SmartEdit requires it to have
 * both an ID (the value) and a label (the displayed text).
 *
 */
public class DropdownElement
{

	String id;
	String label;

	/**
	 * Default constructor.
	 */
	public DropdownElement()
	{
	}

	/**
	 * Constructor taking an ID and label.
	 * 
	 * @param pId
	 *           The ID for the new element
	 * @param pLabel
	 *           The label for the new element
	 */
	public DropdownElement(final String pId, final String pLabel)
	{
		id = pId;
		label = pLabel;
	}

	/**
	 * @return Id of the dropdown element
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           Accepts id as String and sets to the dropdown element
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return Label as String
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *           Accepts Label as String and sets to the dropdown element
	 */
	public void setLabel(final String label)
	{
		this.label = label;
	}

}
