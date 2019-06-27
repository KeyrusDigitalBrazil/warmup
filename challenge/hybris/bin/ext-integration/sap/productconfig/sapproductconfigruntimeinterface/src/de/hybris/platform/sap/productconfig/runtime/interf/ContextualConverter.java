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

import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Extends the ordinary {@link Converter} interface. Additionally provides convert methods that accept a context for the
 * convert process.
 *
 * @param <SOURCE>
 *           type of source object
 * @param <TARGET>
 *           type of target object
 * @param <CONTEXT>
 *           type of context objec
 */
public interface ContextualConverter<SOURCE, TARGET, CONTEXT> extends Converter<SOURCE, TARGET>
{
	/**
	 * Converts the source object, creating a new instance of the destination type
	 *
	 * @param source
	 *           the source object
	 * @param context
	 *           converting context
	 * @return the converted object
	 */
	TARGET convertWithContext(SOURCE source, CONTEXT context);

}
