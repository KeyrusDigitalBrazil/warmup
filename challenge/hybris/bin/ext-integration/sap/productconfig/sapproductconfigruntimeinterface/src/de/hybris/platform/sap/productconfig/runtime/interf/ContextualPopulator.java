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

import de.hybris.platform.converters.Populator;


/**
 * Compared to ordinary {@link Populator}s, a ContextualPopulator is context aware, meaning that a context can be
 * provided for the populating process.
 *
 * @param <SOURCE>
 *           type of source object
 * @param <TARGET>
 *           type of target object
 * @param <CONTEXT>
 *           type of context object
 */
public interface ContextualPopulator<SOURCE, TARGET, CONTEXT>
{
	/**
	 * Populate the target instance from the source instance. The collection of options is used to control what data is
	 * populated.
	 *
	 * @param source
	 *           the source object
	 * @param target
	 *           the target to fill
	 * @param context
	 *           populating context
	 * @param options
	 *           options used to control what data is populated
	 */
	void populate(SOURCE source, TARGET target, CONTEXT context);
}
