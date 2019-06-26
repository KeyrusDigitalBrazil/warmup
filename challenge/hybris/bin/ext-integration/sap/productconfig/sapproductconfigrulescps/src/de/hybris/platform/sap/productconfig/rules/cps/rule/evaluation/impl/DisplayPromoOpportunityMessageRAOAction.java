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
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;


/**
 * Encapsulates display promo opportunity message logic as rule action.
 */
public class DisplayPromoOpportunityMessageRAOAction extends DisplayPromoMessageRAOAction
{
	@Override
	protected ProductConfigMessagePromoType getPromoType()
	{
		return ProductConfigMessagePromoType.PROMO_OPPORTUNITY;
	}
}
