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
package de.hybris.platform.cmsfacades.pagescontentslots.service.impl;

import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.pagescontentslots.service.PageContentSlotConverterType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;


/**
 * Default implementation of <code>PageContentSlotConverterType</code>.
 */
public class DefaultPageContentSlotConverterType implements PageContentSlotConverterType
{
	private Class<? extends CMSRelationModel> classType;
	private AbstractPopulatingConverter<CMSRelationModel, PageContentSlotData> converter;

	@Override
	public Class<? extends CMSRelationModel> getClassType()
	{
		return classType;
	}

	@Override
	public void setClassType(final Class<? extends CMSRelationModel> classType)
	{
		this.classType = classType;
	}

	@Override
	public AbstractPopulatingConverter<CMSRelationModel, PageContentSlotData> getConverter()
	{
		return converter;
	}

	@Override
	public void setConverter(final AbstractPopulatingConverter<CMSRelationModel, PageContentSlotData> converter)
	{
		this.converter = converter;
	}

}
