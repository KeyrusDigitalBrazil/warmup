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
package de.hybris.platform.commercefacades.coupon;

import de.hybris.platform.commercefacades.coupon.data.CouponData;

import java.util.Optional;


/**
 * Coupon Data facade interface. Manages populating {@link CouponData} from AbstractCouponModel.
 */
public interface CouponDataFacade
{
	/**
	 * Get {@link CouponData} object based on its code.
	 *
	 * @param couponCode
	 *           coupon identifier
	 * @return the {@link CouponData}
	 *
	 * @throws IllegalArgumentException
	 *            if parameter code is <code>null</code> or empty
	 */
	Optional<CouponData> getCouponDetails(String couponCode);
}
