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
package de.hybris.platform.commerceservices.event;


import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Event to indicate that a quote is expired
 */
public class QuoteExpiredEvent extends AbstractQuoteSubmitEvent<BaseSiteModel>
{
    /**
     * Default Constructor
     *
     * @param quote
     */
    public QuoteExpiredEvent(final QuoteModel quote)
    {
        super(quote, null, null);
    }
}