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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;




/**
 * Responsible to populate characteristics
 */
public class PossibleValuePopulator implements Populator<CPSPossibleValue, CsticValueModel>
{

	@Override
	public void populate(final CPSPossibleValue source, final CsticValueModel target)
	{
		populateCoreAttributes(source, target);

	}

	protected void populateCoreAttributes(final CPSPossibleValue source, final CsticValueModel target)
	{
		final String intervalType = source.getIntervalType();
		if (!CPSIntervalType.isInterval(intervalType))
		{
			target.setName(source.getValueLow());
		}
		else
		{
			populateAttributesForIntervalCstics(source, target);
		}
		target.setSelectable(source.isSelectable());
		target.setDomainValue(source.isSelectable());
	}

	protected void populateAttributesForIntervalCstics(final CPSPossibleValue source, final CsticValueModel target)
	{
		final String valueLow = source.getValueLow();
		final String valueHigh = source.getValueHigh();

		String interval;

		switch (CPSIntervalType.fromString(source.getIntervalType()))
		{
			case HALF_OPEN_RIGHT_INTERVAL:
			case CLOSED_INTERVAL:
			case HALF_OPEN_LEFT_INTERVAL:
			case OPEN_INTERVAL:
				interval = new StringBuilder().append(valueLow).append(" - ").append(valueHigh).toString();
				break;
			case INFINITY_TO_HIGH_OPEN_INTERVAL:
				interval = new StringBuilder().append("< ").append(valueHigh).toString();
				break;
			case INFINITY_TO_HIGH_CLOSED_INTERVAL:
				interval = new StringBuilder().append("≤ ").append(valueHigh).toString();
				break;
			case LOW_TO_INFINITY_OPEN_INTERVAL:
				interval = new StringBuilder().append("> ").append(valueLow).toString();
				break;
			case LOW_TO_INFINITY_CLOSED_INTERVAL:
				interval = new StringBuilder().append("≥ ").append(valueLow).toString();
				break;
			default:
				throw new IllegalStateException("");
		}

		target.setName(interval);
		target.setLanguageDependentName(interval);
	}
}
