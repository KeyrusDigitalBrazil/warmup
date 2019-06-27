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
package de.hybris.platform.sap.productconfig.services.evaluator;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.sap.productconfig.services.model.CMSCartConfigurationRestrictionModel;


/**
 * This evaluator is attached to the configuration specific cart component. In our CPQ context, it always returns
 * 'true'. Dependent extensions can register other evaluators and register them instead, to offer a more dynamic
 * behaviour.
 */
public class CMSCartConfigurationRestrictionEvaluator implements CMSRestrictionEvaluator<CMSCartConfigurationRestrictionModel>
{

	@Override
	public boolean evaluate(final CMSCartConfigurationRestrictionModel arg0, final RestrictionData arg1)
	{

		return false;
	}

}
