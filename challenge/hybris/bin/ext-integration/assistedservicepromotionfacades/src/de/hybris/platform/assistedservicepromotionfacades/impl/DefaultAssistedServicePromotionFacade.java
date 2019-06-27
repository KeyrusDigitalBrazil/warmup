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
package de.hybris.platform.assistedservicepromotionfacades.impl;


import de.hybris.platform.assistedservicepromotionfacades.AssistedServicePromotionFacade;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AssistedServicePromotionFacade} that queries promotion data model to get customer
 * support related coupons and promotions
 */
public class DefaultAssistedServicePromotionFacade implements AssistedServicePromotionFacade
{
	private FlexibleSearchService flexibleSearchService;
	private static int COUPON_ACTIVE_ID = 1;

	@Override
	public List<AbstractRuleModel> getCSAPromotions(final String promotionCodeLike)
	{
		List<AbstractRuleModel> matchedPromotions = new ArrayList<>();
	
		if (StringUtils.isNotBlank(promotionCodeLike))
		{
			final StringBuilder buf = new StringBuilder();

			buf.append("SELECT  {p:" + AbstractRuleModel.PK + "} ");
			buf.append("FROM {" + AbstractRuleModel._TYPECODE + " as p } ");
			buf.append("WHERE {p:" + AbstractRuleModel.CODE + "} like '" + promotionCodeLike + "%'"); 

			final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
			query.addQueryParameter("promotionCodeLike", promotionCodeLike);
			matchedPromotions = getFlexibleSearchService().<AbstractRuleModel> search(query).getResult();
		}
		return matchedPromotions;
	}

	@Override
	public List<AbstractCouponModel> getCSACoupons(final String couponCodeLike)
	{
		List<AbstractCouponModel> matchedCoupons = new ArrayList<>();
		
		if (StringUtils.isNotBlank(couponCodeLike))
		{
			final StringBuilder buf = new StringBuilder();

			buf.append("SELECT  {p:" + SingleCodeCouponModel.PK + "} ");
			buf.append("FROM {" + SingleCodeCouponModel._TYPECODE + " as p } ");
			buf.append("WHERE {p:" + AbstractCouponModel.COUPONID + "} like '" + couponCodeLike + "%' AND {p:"+AbstractCouponModel.ACTIVE + "} = " + COUPON_ACTIVE_ID);

			final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
			query.addQueryParameter("couponCodeLike", couponCodeLike);
			matchedCoupons = getFlexibleSearchService().<AbstractCouponModel> search(query).getResult();
		}
		
		return matchedCoupons;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public List<AbstractRuleModel> getCustomerPromotions(String promotionCodeLike)
	{
		List<AbstractRuleModel> matchedPromotions = new ArrayList<>();

		if (StringUtils.isNotBlank(promotionCodeLike))
		{
			final StringBuilder buf = new StringBuilder();

			buf.append("SELECT  {p:" + AbstractRuleModel.PK + "} ");
			buf.append("FROM {" + AbstractRuleModel._TYPECODE + " as p } ");
			buf.append("WHERE {p:" + AbstractRuleModel.CODE + "} NOT like '" + promotionCodeLike + "%'"); 

			final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
			query.addQueryParameter("promotionCodeLike", promotionCodeLike);
			matchedPromotions = getFlexibleSearchService().<AbstractRuleModel> search(query).getResult();
		}
		
		return matchedPromotions;
	}
}
