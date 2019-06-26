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
package de.hybris.platform.assistedservicepromotionfacades.populator;

import de.hybris.platform.assistedservicepromotionfacades.customer360.CSAPromoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;


/**
 * @author CSAPromoDataPopulator
 *
 */
public class CSAPromoDataPopulator implements Populator<AbstractRuleModel, CSAPromoData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractRuleModel source, final CSAPromoData target)
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());

	}



}
