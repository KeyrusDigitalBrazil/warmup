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
package de.hybris.platform.marketplaceaddon.forms.validation;

import de.hybris.platform.marketplaceaddon.forms.OrderReviewForm;
import de.hybris.platform.marketplaceaddon.forms.ProductReviewForm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("customerReviewValidator")
public class CustomerReviewValidator implements Validator
{

	@Override
	public boolean supports(final Class<?> paramClass)
	{
		return OrderReviewForm.class == paramClass;
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final OrderReviewForm form = (OrderReviewForm) object;
		final Double satisfaction = form.getSatisfaction();
		final Double delivery = form.getDelivery();
		final Double communication = form.getCommunication();
		final String comment = form.getComment();


		validateRating(satisfaction, "satisfaction", "order.review.satisfaction.invalid", errors);
		validateRating(delivery, "delivery", "order.review.delivery.invalid", errors);
		validateRating(communication, "communication", "order.review.communication.invalid", errors);

		if (StringUtils.isNotEmpty(comment) && comment.length() > 4000)
		{
			errors.rejectValue("orderReviewForm.comment", "order.review.comment.invalid");
		}

		final List<ProductReviewForm> productReviewForms = form.getProductReviewForms();
		for (int i = 0; i < productReviewForms.size(); i++)
		{
			final ProductReviewForm productReviewForm = productReviewForms.get(i);
			validateProductReview(productReviewForm, i, errors);
		}

	}

	protected void validateProductReview(final ProductReviewForm productReviewForm, final int index, final Errors errors)
	{
		final Double rating = productReviewForm.getRating();
		validateRating(rating, "productReviewForms[" + index + "].rating", "review.rating.invalid", errors);

		final String comment = productReviewForm.getComment();
		if (StringUtils.isNotEmpty(comment) && comment.length() > 4000)
		{
			errors.rejectValue("productReviewForms[" + index + "].comment", "order.review.comment.invalid");
		}
	}

	protected void validateRating(final Double rating, final String fieldName, final String msgKey, final Errors errors)
	{
		if (rating == null || rating.doubleValue() < 0.5 || rating.doubleValue() > 5)
		{
			errors.rejectValue(fieldName, msgKey);
		}
	}

}
