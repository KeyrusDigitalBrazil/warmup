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

import java.util.function.Predicate;


/**
 * Interface responsible for verification of an item visibility.
 * @param <T> the object that extends {@link ItemModel}
 */
public interface RenderingVisibilityRule<T extends ItemModel>
{
	/**
	 * The predicate to verify that the provided itemModel is the one for which the visibility is being verified.
	 * @return the {@link Predicate}
	 */
	Predicate<ItemModel> restrictedBy();

	/**
	 * Verifies the visibility of itemModel
	 * @param itemModel
	 * @return
	 */
	boolean isVisible(T itemModel);
}
