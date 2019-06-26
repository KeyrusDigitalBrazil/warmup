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
package com.hybris.ymkt.recommendationwebservices.validators;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.validator.DefaultAbstractCMSComponentValidator;

import org.apache.commons.lang.StringUtils;

import com.hybris.ymkt.recommendation.model.CMSSAPRecommendationComponentModel;


public class RecommendationComponentValidator extends DefaultAbstractCMSComponentValidator
{
	protected static final String CART_ITEM_DS_TYPE_REQUIRED_ERROR_MSG = "recommendation.cartitemdstype.required";
	protected static final String LEADING_ITEM_DS_TYPE_REQUIRED_ERROR_MSG = "recommendation.leadingitemdstype.required";
	protected static final String LEADING_ITEM_TYPE_REQUIRED_ERROR_MSG = "recommendation.leadingitemtype.required";
	protected static final String RECO_TYPE_REQUIRED_ERROR_MSG = "recommendation.type.required";

	protected void addValidatorRule(String targetField, String errorMsgCode)
	{
		this.getValidationErrorsProvider().getCurrentValidationErrors()
				.add(newValidationErrorBuilder() //
						.field(targetField) //
						.errorCode(errorMsgCode) //
						.build());
	}

	@Override
	public void validate(final AbstractCMSComponentModel itemModel)
	{
		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPRecommendationComponentModel.RECOTYPE)))
		{
			addValidatorRule(CMSSAPRecommendationComponentModel.RECOTYPE, RECO_TYPE_REQUIRED_ERROR_MSG);
		}

		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPRecommendationComponentModel.LEADINGITEMTYPE)))
		{
			addValidatorRule(CMSSAPRecommendationComponentModel.LEADINGITEMTYPE, LEADING_ITEM_TYPE_REQUIRED_ERROR_MSG);
		}

		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPRecommendationComponentModel.LEADINGITEMDSTYPE)))
		{
			addValidatorRule(CMSSAPRecommendationComponentModel.LEADINGITEMDSTYPE, LEADING_ITEM_DS_TYPE_REQUIRED_ERROR_MSG);
		}

		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPRecommendationComponentModel.CARTITEMDSTYPE)))
		{
			addValidatorRule(CMSSAPRecommendationComponentModel.CARTITEMDSTYPE, CART_ITEM_DS_TYPE_REQUIRED_ERROR_MSG);
		}
	}
}
