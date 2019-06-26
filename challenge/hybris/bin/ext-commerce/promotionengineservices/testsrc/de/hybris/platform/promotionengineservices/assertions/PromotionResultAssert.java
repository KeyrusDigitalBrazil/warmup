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
package de.hybris.platform.promotionengineservices.assertions;


import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;

import java.util.Objects;

import org.assertj.core.api.AbstractAssert;


/**
 * Assertions applicable on {@link PromotionEngineResult}
 */
public class PromotionResultAssert extends AbstractAssert<PromotionResultAssert, PromotionEngineResult>
{
	public PromotionResultAssert(final PromotionEngineResult actual)
	{
		super(actual, PromotionResultAssert.class);
	}

	public static PromotionResultAssert assertThat(final PromotionEngineResult actual)
	{
		return new PromotionResultAssert(actual);
	}

	public PromotionResultAssert hasCode(final String code)
	{
		isNotNull();
		if (!Objects.equals(actual.getCode(), code))
		{
			failWithMessage("Expected character's code to be <%s> but was <%s>", code, actual.getCode());
		}
		return this;
	}

	public PromotionResultAssert hasName(final String name)
	{
		isNotNull();
		if (!Objects.equals(actual.getName(), name))
		{
			failWithMessage("Expected character's name to be <%s> but was <%s>", name, actual.getName());
		}
		return this;
	}

	public PromotionResultAssert hasDescription(final String description)
	{
		isNotNull();
		if (!Objects.equals(actual.getDescription(), description))
		{
			failWithMessage("Expected character's description to be <%s> but was <%s>", description, actual.getDescription());
		}
		return this;
	}

	public PromotionResultAssert isFiredPromotion()
	{
		isNotNull();
		if (!Objects.equals(Boolean.valueOf(actual.isFired()), Boolean.TRUE))
		{
			failWithMessage("Expected isFiredPromotion promotion to be <%s> but was <%s>", Boolean.TRUE,
					Boolean.valueOf(actual.isFired()));
		}
		return this;
	}

	public PromotionResultAssert isPotentialPromotion()
	{
		isNotNull();
		if (!Objects.equals(Boolean.valueOf(actual.isFired()), Boolean.FALSE))
		{
			failWithMessage("Expected isFiredPromotion promotion to be <%s> but was <%s>", Boolean.FALSE,
					Boolean.valueOf(actual.isFired()));
		}
		return this;
	}
}
