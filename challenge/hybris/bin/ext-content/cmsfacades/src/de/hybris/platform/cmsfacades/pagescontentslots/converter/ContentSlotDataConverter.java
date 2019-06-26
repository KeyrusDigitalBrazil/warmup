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
package de.hybris.platform.cmsfacades.pagescontentslots.converter;

import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.data.SlotStatus;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * This populator will populate the {@link PageContentSlotData} from the {@link ContentSlotData}
 */
public class ContentSlotDataConverter implements Converter<ContentSlotData, PageContentSlotData>
{

	@Override
	public PageContentSlotData convert(final ContentSlotData source) throws ConversionException
	{
		return convert(source, new PageContentSlotData());
	}

	@Override
	public PageContentSlotData convert(final ContentSlotData source, final PageContentSlotData target) throws ConversionException
	{
		target.setPageId(source.getPageId());
		target.setSlotId(source.getContentSlot().getUid());
		target.setPosition(source.getPosition());
		target.setSlotShared(source.isFromMaster());

		if (source.isOverrideSlot())
		{
			target.setSlotStatus(SlotStatus.OVERRIDE);
		}
		else if (source.isFromMaster())
		{
			target.setSlotStatus(SlotStatus.TEMPLATE);
		}
		else
		{
			target.setSlotStatus(SlotStatus.PAGE);
		}

		return target;
	}

}
