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
package de.hybris.platform.cmsfacades.rendering.visibility;

import de.hybris.platform.core.model.ItemModel;


/**
 * Interface responsible for verifying the visibility of an {@link ItemModel}.
 */
public interface RenderingVisibilityService
{
	/**
	 * Verifies the visibility of {@link ItemModel}
	 *
	 * @param itemModel the {@link ItemModel}
	 * @return true if the {@link ItemModel} is visible and false otherwise.
	 */
	boolean isVisible(ItemModel itemModel);
}
