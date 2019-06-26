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
/**
 *
 */
package de.hybris.platform.assistedservicepromotionfacades.data.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicepromotionfacades.AssistedServicePromotionFacade;
import de.hybris.platform.assistedservicepromotionfacades.constants.AssistedservicepromotionfacadesConstants;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author CSACouponsModelProvider
 *
 */
public class CSACouponsModelProvider implements FragmentModelProvider<List<CSACouponData>>
{
	private AssistedServicePromotionFacade assistedServicePromotionFacade;
	private Converter<AbstractCouponModel, CSACouponData> csaCouponDataConverter;
	private CartFacade cartFacade;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider#getModel(java.util.Map)
	 */
	@Override
	public List<CSACouponData> getModel(final Map<String, String> parameters)
	{
		final String searchKeyword = Config.getString(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX_KEY,
				AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX);
		final List<CSACouponData> csaCoupons = getCsaCouponDataConverter()
				.convertAll(getAssistedServicePromotionFacade().getCSACoupons(searchKeyword));
		final List<String> appliedCoupons = getCartFacade().getSessionCart().getAppliedVouchers();

		if (CollectionUtils.isNotEmpty(csaCoupons) && CollectionUtils.isNotEmpty(appliedCoupons))

		{
			final List<String> cartAlreadyAppliedCoupons = new ArrayList<>();

			cartAlreadyAppliedCoupons.addAll(appliedCoupons);
			cartAlreadyAppliedCoupons.stream().forEach(appliedCoupon -> 
				csaCoupons.stream().filter(csaCoupon -> (searchKeyword + csaCoupon.getCode()).equalsIgnoreCase(appliedCoupon))
						.forEach(csaCoupon -> csaCoupon.setCouponApplied(Boolean.TRUE)));
		}

		return csaCoupons;
	}

	/**
	 * @return the csaCouponDataConverter
	 */
	protected Converter<AbstractCouponModel, CSACouponData> getCsaCouponDataConverter()
	{
		return csaCouponDataConverter;
	}

	/**
	 * @param csaCouponDataConverter
	 *           the csaCouponDataConverter to set
	 */
	@Required
	public void setCsaCouponDataConverter(final Converter<AbstractCouponModel, CSACouponData> csaCouponDataConverter)
	{
		this.csaCouponDataConverter = csaCouponDataConverter;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	/**
	 * @return the assistedServicePromotionFacade
	 */
	protected AssistedServicePromotionFacade getAssistedServicePromotionFacade()
	{
		return assistedServicePromotionFacade;
	}

	/**
	 * @param assistedServicePromotionFacade
	 *           the assistedServicePromotionFacade to set
	 */
	@Required
	public void setAssistedServicePromotionFacade(final AssistedServicePromotionFacade assistedServicePromotionFacade)
	{
		this.assistedServicePromotionFacade = assistedServicePromotionFacade;
	}
}
