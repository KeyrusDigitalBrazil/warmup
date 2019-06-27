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
package de.hybris.platform.cmsfacades.navigations.service.functions;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.function.Function;

/**
 * Default implementation for conversion of {@link ItemModel} into {@code CMSItemModel#getUid()}
 * @deprecated since 1811 - no longer needed
 */
@Deprecated
public class DefaultNavigationEntryCmsItemModelIdConversionFunction implements Function<ItemModel, String> 
{
	@Override
	public String apply(final ItemModel itemModel) 
	{
		if (!(CMSItemModel.class.isAssignableFrom(itemModel.getClass()))) 
		{
			throw new ConversionException("Invalid CMS Component: " + itemModel);
		}
		return ((CMSItemModel) itemModel).getUid();
	}
}
