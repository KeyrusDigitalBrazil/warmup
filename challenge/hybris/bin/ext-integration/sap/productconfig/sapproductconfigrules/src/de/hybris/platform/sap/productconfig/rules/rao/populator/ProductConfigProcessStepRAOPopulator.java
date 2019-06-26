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
package de.hybris.platform.sap.productconfig.rules.rao.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;


/**
 * Populator for the {@link ProductConfigProcessStepRAO}
 */
public class ProductConfigProcessStepRAOPopulator implements
		Populator<ProductConfigProcessStepModel, ProductConfigProcessStepRAO>
{

	@Override
	public void populate(final ProductConfigProcessStepModel source, final ProductConfigProcessStepRAO target)
	{
		target.setProcessStep(source.getProcessStep());
	}
}
