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
package de.hybris.platform.sap.productconfig.rules.interceptor;

import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.springframework.beans.factory.annotation.Required;



/**
 * CPQ adds own attributes to the rules source model related to message handling. Those attributes are mapped from the
 * DTO into the model using this interceptor.
 */
public class ProductConfigRulesPrepareInterceptor implements PrepareInterceptor<AbstractRuleEngineRuleModel>
{
	private RuleDao ruleDao;

	@Override
	public void onPrepare(final AbstractRuleEngineRuleModel model, final InterceptorContext context) throws InterceptorException
	{
		if (!RuleType.PRODUCTCONFIG.equals(model.getRuleType()))
		{
			return;
		}
		final ProductConfigSourceRuleModel rule = ruleDao.findRuleByCode(model.getCode());

		if (null != rule)
		{
			mapSeverity(model, rule);
			mapMessageForCstic(model, rule);
		}
	}

	protected void mapSeverity(final AbstractRuleEngineRuleModel runtimeRule, final ProductConfigSourceRuleModel sourceRule)
	{
		runtimeRule.setMessageSeverity(sourceRule.getMessageSeverity());
	}

	protected void mapMessageForCstic(final AbstractRuleEngineRuleModel runtimeRule, final ProductConfigSourceRuleModel sourceRule)
	{
		runtimeRule.setMessageForCstic(sourceRule.getMessageForCstic());
	}

	protected RuleDao getRuleDao()
	{
		return ruleDao;
	}

	/**
	 * @param ruleDao
	 */
	@Required
	public void setRuleDao(final RuleDao ruleDao)
	{
		this.ruleDao = ruleDao;
	}
}
