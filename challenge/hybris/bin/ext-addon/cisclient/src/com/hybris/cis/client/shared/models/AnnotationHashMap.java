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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JAXB can not handle java.util.Map, which is an interface. Use this map instead for webservice.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnotationHashMap
{
	/** A list of annotation has map entries. */
	@XmlElement(name = "parameter")
	private List<AnnotationHashMapEntryType> parameter = new ArrayList<AnnotationHashMapEntryType>();


	public AnnotationHashMap()
	{
		// required for JAXB
	}

	/**
	 * Instantiates a new {@link AnnotationHashMap}.
	 * 
	 * @param map a map of type String, String
	 */
	public AnnotationHashMap(final Map<String, String> map)
	{
		for (final Map.Entry<String, String> e : map.entrySet())
		{
			this.parameter.add(new AnnotationHashMapEntryType(e));
		}
	}

	/**
	 * Returns a regular generic map of type String, String.
	 * 
	 * @return a map of type String, String
	 */
	public Map<String, String> convertToMap()
	{
		final HashMap<String, String> res = new HashMap<String, String>();

		for (final AnnotationHashMapEntryType entry : this.parameter)
		{
			res.put(entry.getKey(), entry.getValue());
		}

		return res;
	}

	public List<AnnotationHashMapEntryType> getParameter()
	{
		return this.parameter;
	}

	public void setParameter(final List<AnnotationHashMapEntryType> parameter)
	{
		this.parameter = parameter;
	}
}
