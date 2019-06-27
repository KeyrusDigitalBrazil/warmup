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
package com.hybris.cis.client.shared.models;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;


public class AnnotationHashMapEntryType
{
	/** The key of the entry. */
	@XmlAttribute(name = "key")
	private String key;

	/** The value of the entry. */
	@XmlAttribute(name = "value")
	private String value;

	public AnnotationHashMapEntryType()
	{
		// required by jaxb
	}

	/**
	 * Instantiates a new hash map entry.
	 * 
	 * @param entry a map entry of type String, String
	 */
	public AnnotationHashMapEntryType(final Map.Entry<String, String> entry)
	{
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	public String getKey()
	{
		return this.key;
	}

	public String getValue()
	{
		return this.value;
	}
}
