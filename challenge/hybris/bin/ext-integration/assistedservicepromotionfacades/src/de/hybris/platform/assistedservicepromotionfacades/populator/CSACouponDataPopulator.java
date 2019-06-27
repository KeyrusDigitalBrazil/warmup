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

import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import org.apache.commons.lang.StringUtils;


/**
 * @author CSACouponDataPopulator
 *
 */
public class CSACouponDataPopulator implements Populator<AbstractCouponModel, CSACouponData>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractCouponModel source, final CSACouponData target)
	{
		target.setCode(source.getCouponId().replaceFirst("csa_coupon_", StringUtils.EMPTY));
		target.setName(source.getName());

	}
}
