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

import org.assertj.core.api.Assertions;

import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;


public class PromotionEngineServicesAssertions extends Assertions
{
	public static PromotionResultAssert assertThat(PromotionEngineResult actual) {
		return new PromotionResultAssert(actual);
	}
}
