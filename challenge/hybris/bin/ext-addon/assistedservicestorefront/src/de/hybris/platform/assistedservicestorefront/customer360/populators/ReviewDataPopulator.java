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
package de.hybris.platform.assistedservicestorefront.customer360.populators;

import de.hybris.platform.assistedservicestorefront.customer360.ReviewData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import org.springframework.beans.factory.annotation.Required;

/**
 * CustomerReviewModel -> ReviewData populator
 */
public class ReviewDataPopulator implements Populator<CustomerReviewModel, ReviewData>
{
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Override
    public void populate(CustomerReviewModel customerReviewModel, ReviewData reviewData)
    {
        reviewData.setProductName(customerReviewModel.getProduct().getName());
        reviewData.setReviewStatus(customerReviewModel.getApprovalStatus().getCode());
        reviewData.setReviewText(customerReviewModel.getHeadline());
        reviewData.setCreated(customerReviewModel.getCreationtime());
        reviewData.setUpdated(customerReviewModel.getModifiedtime());
        reviewData.setRating(customerReviewModel.getRating());
        reviewData.setSKUNumber(customerReviewModel.getProduct().getCode());
        reviewData.setProductUrl(getProductModelUrlResolver().resolve(customerReviewModel.getProduct()));
    }

    protected UrlResolver<ProductModel> getProductModelUrlResolver()
    {
        return productModelUrlResolver;
    }

    @Required
    public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
    {
        this.productModelUrlResolver = productModelUrlResolver;
    }
}
