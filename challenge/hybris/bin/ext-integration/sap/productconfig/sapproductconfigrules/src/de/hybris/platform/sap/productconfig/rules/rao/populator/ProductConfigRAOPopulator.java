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
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for the {@link ProductConfigRAO}
 */
public class ProductConfigRAOPopulator implements Populator<ConfigModel, ProductConfigRAO>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigRAOPopulator.class);

	private ProductConfigRuleFormatTranslator rulesFormator;
	private ProductConfigRuleUtil ruleUtil;
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;

	@Override
	public void populate(final ConfigModel source, final ProductConfigRAO target)
	{
		final String configId = source.getId();
		target.setCstics(createCsticRAOs(getRuleUtil().getCstics(source), configId));
		if (null != source.getKbKey() && StringUtils.isNotEmpty(source.getKbKey().getProductCode()))
		{
			target.setProductCode(source.getKbKey().getProductCode());
		}
		else
		{
			target.setProductCode(getAssignmentResolverStrategy().retrieveRelatedProductCode(configId));
		}
		target.setConfigId(configId);
		target.setInCart(Boolean.FALSE);
	}

	protected List<CsticRAO> createCsticRAOs(final List<CsticModel> cstics, final String configId)
	{
		final List<CsticRAO> csticRAOs = new ArrayList<>();
		if (!CollectionUtils.isEmpty(cstics))
		{
			for (final CsticModel cstic : cstics)
			{
				final CsticRAO csticRAO = new CsticRAO();
				csticRAO.setCsticName(cstic.getName());
				csticRAO.setConfigId(configId);
				final Map<String, CsticValueRAO> assignableValuesMap = new HashMap<>();
				csticRAO.setAssignableValues(createCsticValueRAOs(cstic, assignableValuesMap, configId));
				csticRAO.setAssignedValues(getCsticValueRAOs(cstic, assignableValuesMap, configId));
				csticRAOs.add(csticRAO);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("created cstic RAO: " + csticRAO);
				}
			}
		}
		return csticRAOs;
	}

	protected List<CsticValueRAO> createCsticValueRAOs(final CsticModel cstic,
			final Map<String, CsticValueRAO> assignableValuesMap, final String configId)
	{
		final List<CsticValueRAO> csticValueRaos = new ArrayList<>();
		final Collection<CsticValueModel> values = cstic.getAssignableValues();
		if (!CollectionUtils.isEmpty(values))
		{
			for (final CsticValueModel csticValue : values)
			{
				final CsticValueRAO csticValueRao = new CsticValueRAO();
				String value = csticValue.getName();
				value = getRulesFormator().formatForRules(cstic, value);
				csticValueRao.setCsticValueName(value);
				csticValueRao.setConfigId(configId);
				csticValueRao.setCsticName(cstic.getName());
				csticValueRaos.add(csticValueRao);
				assignableValuesMap.put(csticValueRao.getCsticValueName(), csticValueRao);
			}
		}
		return csticValueRaos;
	}



	/**
	 * Loops over given list of values and retrieves appropriate object from the passed assignableValuesMap.
	 *
	 */
	protected List<CsticValueRAO> getCsticValueRAOs(final CsticModel cstic, final Map<String, CsticValueRAO> assignableValuesMap,
			final String configId)
	{
		final List<CsticValueRAO> csticValueRAOs = new ArrayList<>();
		if (!CollectionUtils.isEmpty(cstic.getAssignedValues()))
		{
			for (final CsticValueModel csticValue : cstic.getAssignedValues())
			{
				CsticValueRAO csticValueRAO = assignableValuesMap.get(csticValue.getName());

				if (csticValueRAO == null)
				{
					csticValueRAO = new CsticValueRAO();
					String value = csticValue.getName();
					value = getRulesFormator().formatForRules(cstic, value);
					csticValueRAO.setCsticValueName(value);
					csticValueRAO.setConfigId(configId);
					csticValueRAO.setCsticName(cstic.getName());

				}
				csticValueRAOs.add(csticValueRAO);
			}
		}
		else
		{
			final CsticValueRAO csticValueRAO = new CsticValueRAO();
			csticValueRAO.setCsticValueName(getRulesFormator().formatForRules(cstic, null));
			csticValueRAO.setConfigId(configId);
			csticValueRAO.setCsticName(cstic.getName());
			csticValueRAOs.add(csticValueRAO);
		}

		return csticValueRAOs;
	}

	protected ProductConfigRuleFormatTranslator getRulesFormator()
	{
		return rulesFormator;
	}

	/**
	 * @param rulesFormator
	 */
	@Required
	public void setRulesFormator(final ProductConfigRuleFormatTranslator rulesFormator)
	{
		this.rulesFormator = rulesFormator;
	}

	protected ProductConfigRuleUtil getRuleUtil()
	{
		return ruleUtil;
	}

	/**
	 * @param ruleUtil
	 */
	public void setRuleUtil(final ProductConfigRuleUtil ruleUtil)
	{
		this.ruleUtil = ruleUtil;
	}

	protected ConfigurationAssignmentResolverStrategy getAssignmentResolverStrategy()
	{
		return assignmentResolverStrategy;
	}

	@Required
	public void setAssignmentResolverStrategy(final ConfigurationAssignmentResolverStrategy assignmentResolverStrategy)
	{
		this.assignmentResolverStrategy = assignmentResolverStrategy;
	}


}
