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
package de.hybris.platform.commerceservices.spring.config;

import java.util.ArrayList;
import java.util.List;


public class MultipleListMergeBean
{
	private List<MergeTestBean> propertyList;
	private List<MergeTestBean> fieldList;

	public MultipleListMergeBean()
	{
		this.propertyList = new ArrayList<MergeTestBean>();
		this.fieldList = new ArrayList<MergeTestBean>();
	}

	public List<MergeTestBean> getPropertyList()
	{
		return propertyList;
	}

	public void setPropertyList(final List<MergeTestBean> propertyList)
	{
		this.propertyList = propertyList;
	}

	public void setFieldList(final List<MergeTestBean> fieldList)
	{
		this.fieldList = fieldList;
	}
}