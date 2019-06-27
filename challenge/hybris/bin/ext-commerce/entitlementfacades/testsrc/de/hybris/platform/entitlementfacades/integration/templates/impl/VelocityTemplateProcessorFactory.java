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

package de.hybris.platform.entitlementfacades.integration.templates.impl;

import de.hybris.platform.entitlementfacades.integration.templates.TemplateProcessor;
import de.hybris.platform.entitlementfacades.integration.templates.TemplateProcessorFactory;


public class VelocityTemplateProcessorFactory implements TemplateProcessorFactory
{
	@Override
	public TemplateProcessor createTemplateProcessor()
	{
		return new VelocityTemplateProcessor();
	}
}
