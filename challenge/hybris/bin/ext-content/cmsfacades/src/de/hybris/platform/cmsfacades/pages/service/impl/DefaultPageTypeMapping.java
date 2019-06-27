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
package de.hybris.platform.cmsfacades.pages.service.impl;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMapping;


/**
 * Default implementation of <code>PageTypeMapping</code>.
 * 
 * @deprecated since 6.6
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
public class DefaultPageTypeMapping implements PageTypeMapping
{
	private String typecode;
	private Class<? extends AbstractPageData> typedata;

	@Override
	public String getTypecode()
	{
		return typecode;
	}

	@Override
	public void setTypecode(final String typecode)
	{
		this.typecode = typecode;
	}

	@Override
	public Class<? extends AbstractPageData> getTypedata()
	{
		return typedata;
	}

	@Override
	public void setTypedata(final Class<? extends AbstractPageData> typedata)
	{
		this.typedata = typedata;
	}

}
