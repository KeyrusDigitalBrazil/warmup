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
package de.hybris.platform.sap.productconfig.rules.compiler.processors;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrProcessor;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.rao.BaseStoreRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * {@link RuleIrProcessor} for product configuration. Ensures that the {@link ProcessStep} is propagated to each product
 * config rule implicitly.
 */
public class ProductConfigRuleIrProcessor implements RuleIrProcessor
{
	static final String BASE_STORE_RAO_ID_ATTRIBUTE = "uid";

	@Override
	public void process(final RuleCompilerContext context, final RuleIr ruleIr)
	{
		final AbstractRuleModel sourceRule = context.getRule();

		if (sourceRule instanceof ProductConfigSourceRuleModel)
		{
			// add condition for rule engine result
			final String resultRaoVariable = context.generateVariable(RuleEngineResultRAO.class);

			final RuleIrTypeCondition irResultCondition = new RuleIrTypeCondition();
			irResultCondition.setVariable(resultRaoVariable);

			ruleIr.getConditions().add(irResultCondition);

			// add condition for ProductConfigProcessStep
			final String processStepRaoVariable = context.generateVariable(ProductConfigProcessStepRAO.class);

			final RuleIrTypeCondition irProcessStepCondition = new RuleIrTypeCondition();
			irProcessStepCondition.setVariable(processStepRaoVariable);

			ruleIr.getConditions().add(irProcessStepCondition);

			// add condition for base store
			final Collection<BaseStoreModel> baseStores = ((ProductConfigSourceRuleModel) sourceRule).getBaseStores();

			if (CollectionUtils.isNotEmpty(baseStores))
			{
				final List<String> baseStoreUids = new ArrayList<String>();
				for (final BaseStoreModel baseStore : baseStores)
				{
					baseStoreUids.add(baseStore.getUid());
				}

				final String baseStoreRaoVariable = context.generateVariable(BaseStoreRAO.class);

				final RuleIrAttributeCondition irBaseStoreCondition = new RuleIrAttributeCondition();
				irBaseStoreCondition.setVariable(baseStoreRaoVariable);
				irBaseStoreCondition.setAttribute(BASE_STORE_RAO_ID_ATTRIBUTE);
				irBaseStoreCondition.setOperator(RuleIrAttributeOperator.IN);
				irBaseStoreCondition.setValue(baseStoreUids);

				ruleIr.getConditions().add(irBaseStoreCondition);
			}
		}
	}
}
