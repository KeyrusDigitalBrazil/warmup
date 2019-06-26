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
package de.hybris.platform.acceleratorfacades.device;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;

import java.util.List;


/**
 * Defines an API for interacting with the media container
 */
public interface ResponsiveMediaFacade
{
	/**
	 * Gets a {@link List} of {@link ImageData} from the media container based on the container model
	 * 
	 * @param mediaContainerModel
	 *           the media container model
	 * @return a {@link List} of {@link ImageData}
	 */
	List<ImageData> getImagesFromMediaContainer(final MediaContainerModel mediaContainerModel);
}
