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
package de.hybris.platform.marketplaceservices.vendor.impl;

import de.hybris.platform.marketplaceservices.vendor.VendorPromotionRuleEngineContextService;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;


/**
 * A default implementation of {@link VendorPromotionRuleEngineContextService}
 */
public class DefaultVendorPromotionRuleEngineContextService implements VendorPromotionRuleEngineContextService
{

	private RuleEngineContextDao ruleEngineContextDao;

	@Override
	public AbstractRuleEngineContextModel findVendorRuleEngineContextByName(final String contextName)
	{
		return getRuleEngineContextDao().findRuleEngineContextByName(contextName);
	}

	protected RuleEngineContextDao getRuleEngineContextDao()
	{
		return ruleEngineContextDao;
	}

	public void setRuleEngineContextDao(final RuleEngineContextDao ruleEngineContextDao)
	{
		this.ruleEngineContextDao = ruleEngineContextDao;
	}

}
