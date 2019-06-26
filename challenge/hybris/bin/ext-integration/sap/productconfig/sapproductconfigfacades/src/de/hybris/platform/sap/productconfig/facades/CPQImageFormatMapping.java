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
package de.hybris.platform.sap.productconfig.facades;

import java.util.Map;


/**
 * Mapping for CPQ image formats.
 *
 * There are two image formats for characteristics (192Wx96H) and characteristic values (96Wx96H).
 */
public interface CPQImageFormatMapping
{
	/**
	 *
	 * @return mapping with CPQ image formats
	 */
	Map<String, CPQImageType> getCPQMediaFormatQualifiers();
}
