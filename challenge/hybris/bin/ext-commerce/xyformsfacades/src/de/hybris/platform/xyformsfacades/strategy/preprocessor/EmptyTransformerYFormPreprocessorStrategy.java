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
package de.hybris.platform.xyformsfacades.strategy.preprocessor;

import java.util.Map;



/**
 * No transformation is applied to the formData content
 */
public class EmptyTransformerYFormPreprocessorStrategy extends TransformerYFormPreprocessorStrategy
{
	@Override
	protected String transform(final String xmlContent, final Map<String, Object> params) throws YFormProcessorException
	{
		return xmlContent;
	}
}
