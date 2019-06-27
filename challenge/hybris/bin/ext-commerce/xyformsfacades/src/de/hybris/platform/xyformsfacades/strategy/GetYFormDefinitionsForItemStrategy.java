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
package de.hybris.platform.xyformsfacades.strategy;

import de.hybris.platform.xyformsfacades.data.YFormDefinitionData;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;

import java.util.List;


/**
 * Strategy used to get yForm Definitions associated to an hybris item.
 */
public interface GetYFormDefinitionsForItemStrategy<T>
{
	public List<YFormDefinitionData> execute(T code) throws YFormServiceException;
}
