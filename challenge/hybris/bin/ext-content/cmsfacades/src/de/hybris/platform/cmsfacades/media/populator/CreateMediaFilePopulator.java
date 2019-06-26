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
package de.hybris.platform.cmsfacades.media.populator;

import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * This populator will populate the {@link MediaModel} with the information provided by the {@link MediaFileDto}
 */
public class CreateMediaFilePopulator implements Populator<MediaFileDto, MediaModel>
{
	@Override
	public void populate(final MediaFileDto source, final MediaModel target) throws ConversionException
	{
		target.setMime(source.getMime());
		target.setRealFileName(source.getName());
		target.setSize(source.getSize());
	}

}
