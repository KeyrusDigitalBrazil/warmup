/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.sap.c4c.quote.order.strategies.impl;

import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Strategy answers if order requires calculation if it contains associated quote 
 */
public class C4CQuoteRequiresCalculationStrategy {

    /**
     * Returns if order needs to be calculated
     */
    public boolean shouldCalculateAllValues(AbstractOrderModel order) {

        String c4cQuoteId = null;
        QuoteModel quoteModel = null;
        boolean shouldCalculateAllValues = false;
        if (order instanceof QuoteModel) {
            quoteModel = (QuoteModel) order;
        } else if (order instanceof CartModel) {
            CartModel cartModel = (CartModel) order;
            quoteModel = cartModel.getQuoteReference();
        } else if (order instanceof OrderModel) {
            OrderModel orderModel = (OrderModel) order;
            quoteModel = orderModel.getQuoteReference();
        }

        if (quoteModel != null) {
            c4cQuoteId = quoteModel.getC4cQuoteId();
            if (QuoteState.BUYER_DRAFT.equals(quoteModel.getState())
                    || QuoteState.SELLER_DRAFT.equals(quoteModel.getState())) {
                shouldCalculateAllValues = true;
            }
        }

        return (c4cQuoteId == null || shouldCalculateAllValues);
    }

}