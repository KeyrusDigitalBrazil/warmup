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
package de.hybris.platform.sap.productconfig.runtime.interf;

import java.util.List;


/**
 * Interface for a list of populators.
 *
 * @param <SOURCE>
 *           the type of the source object
 * @param <TARGET>
 *           the type of the destination object
 * @param <OPTIONS>
 *           the type of the options/context object
 */
public interface ContextualPopulaterList<SOURCE, TARGET, OPTIONS>
{


	/**
	 * Get the list of populators.
	 *
	 * @return the populators.
	 */
	List<ContextualPopulator<SOURCE, TARGET, OPTIONS>> getContextualPopulators();

	/**
	 * Set the list of populators.
	 *
	 * @param populators
	 *           the populators
	 */
	void setContextualPopulators(List<ContextualPopulator<SOURCE, TARGET, OPTIONS>> populators);
}
