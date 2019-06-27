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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter.mapping;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;


/**
 * Interface for converters mappings see {@link ImpexTransformerTask}.
 */
public interface ConverterMapping
{
	/**
	 * @return converter associated with this mapping
	 */
	ImpexConverter getConverter();

	/**
	 * @return mapping of associated converter
	 */
	String getMapping();

}
