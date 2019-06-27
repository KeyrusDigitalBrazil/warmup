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
package de.hybris.platform.cmsfacades.mediacontainers;

import de.hybris.platform.core.model.media.MediaContainerModel;


/**
 * Facade for managing media containers.
 */
public interface MediaContainerFacade
{

	/**
	 * Creates a new Media Container with a sequential qualifier name.
	 * @return a new {@link MediaContainerModel}
	 */
	MediaContainerModel createMediaContainer();
}
