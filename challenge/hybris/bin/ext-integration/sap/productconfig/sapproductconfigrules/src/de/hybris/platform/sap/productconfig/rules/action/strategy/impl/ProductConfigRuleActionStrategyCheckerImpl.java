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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategyChecker;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * default implementation of {@link ProductConfigRuleActionStrategyChecker}
 */
public class ProductConfigRuleActionStrategyCheckerImpl implements ProductConfigRuleActionStrategyChecker
{

	private static final Logger LOG = Logger.getLogger(ProductConfigRuleActionStrategyCheckerImpl.class);

	private ConfigModelFactory configModelFactory;

	private static final String MSG_CSTIC_NOT_FOUND = "[configId=%s] %s is not possible - Characteristic '%s' is not part of the model (anymore).";
	private static final String MSG_VALUE_ASSIGNED = "[configId=%s] %s is not possible - Value '%s' is already assigned to characteristic '%s'.";
	private static final String MSG_CSTIC_IS_READONLY = "[configId=%s] %s is not possible - Value '%s' of characteristic '%s' is readonly.";
	private static final String MSG_VALUE_NOT_IN_DOMAIN = "[configId=%s] %s is not possible - Value '%s' of characteristic '%s' is not part of assignable values list.";
	private static final String MSG_CSTIC_NOT_NUMERIC = "[configId=%s] %s is not possible - Value '%s' of characteristic '%s' is not a parseable number and the characteristic is flagged as numeric.";

	@Override
	public CsticModel getCstic(final ConfigModel model, final AbstractRuleActionRAO action, final Map<String, CsticModel> csticMap)
	{
		final CsticRAO appliedToObject = (CsticRAO) action.getAppliedToObject();
		return csticMap.get(appliedToObject.getCsticName());
	}

	@Override
	public boolean checkCsticPartOfModel(final ConfigModel model, final AbstractRuleActionRAO action, final String prefix,
			final Map<String, CsticModel> csticMap)
	{
		if (getCstic(model, action, csticMap) == null)
		{
			final CsticRAO csticRao = (CsticRAO) action.getAppliedToObject();
			LOG.error(String.format(MSG_CSTIC_NOT_FOUND, model.getId(), prefix, csticRao.getCsticName()));
			return false;
		}
		return true;
	}


	@Override
	public boolean checkValueUnassigned(final ConfigModel model, final AbstractRuleActionRAO action, final String value,
			final String prefix, final Map<String, CsticModel> csticMap)
	{
		final CsticModel cstic = getCstic(model, action, csticMap);
		final List<CsticValueModel> assignedValues = cstic.getAssignedValues();
		if (CollectionUtils.isNotEmpty(assignedValues))
		{
			for (final CsticValueModel assignedValue : assignedValues)
			{
				if (assignedValue.getName().equals(value))
				{
					LOG.error(String.format(MSG_VALUE_ASSIGNED, model.getId(), prefix, value, cstic.getName()));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean checkValueAssignable(final ConfigModel model, final AbstractRuleActionRAO action, final String valueToSet,
			final String prefix, final Map<String, CsticModel> csticMap)
	{
		final CsticModel cstic = getCstic(model, action, csticMap);
		final CsticValueModel value = getConfigModelFactory().createInstanceOfCsticValueModel(cstic.getValueType());
		value.setName(valueToSet);

		if (cstic.isConstrained() && !cstic.getAssignableValues().contains(value))
		{
			LOG.error(String.format(MSG_VALUE_NOT_IN_DOMAIN, model.getId(), prefix, valueToSet, cstic.getName()));
			return false;
		}
		if (cstic.isReadonly())
		{
			LOG.error(String.format(MSG_CSTIC_IS_READONLY, model.getId(), prefix, valueToSet, cstic.getName()));
			return false;
		}

		return true;
	}

	@Override
	public boolean checkValueForamtable(final ConfigModel model, final AbstractRuleActionRAO action, final String valueToSet,
			final ProductConfigRuleFormatTranslator rulesFormator, final String prefix, final Map<String, CsticModel> csticMap)
	{
		final CsticModel cstic = getCstic(model, action, csticMap);
		if (!rulesFormator.canBeFormattedForService(cstic, valueToSet))
		{
			LOG.error(String.format(MSG_CSTIC_NOT_NUMERIC, model.getId(), prefix, valueToSet, cstic.getName()));
			return false;
		}
		return true;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return this.configModelFactory;
	}

	/**
	 * @param configModelFactory
	 *           factory for configuration model objects
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

}
