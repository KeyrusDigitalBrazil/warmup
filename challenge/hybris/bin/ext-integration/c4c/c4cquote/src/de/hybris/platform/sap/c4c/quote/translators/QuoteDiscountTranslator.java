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
package de.hybris.platform.sap.c4c.quote.translators;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;

/**
 * Translator class to apply discount on current version of quote
 */
public class QuoteDiscountTranslator extends DataHubTranslator<InboundQuoteHelper> 
{

    private static final Logger LOG = LoggerFactory.getLogger(QuoteDiscountTranslator.class);
    @SuppressWarnings("javadoc")
    public static final String HELPER_BEAN = "inboundQuoteHelper";

    @SuppressWarnings("javadoc")
    public QuoteDiscountTranslator() 
    {
        super(HELPER_BEAN);
    }

    @Override
    public void performImport(final String totalDiscounts, final Item processedItem) throws ImpExException 
    {
        LOG.debug("QuoteDiscountTranslator: Invoked translator to create a new discount value for given quote");
        String quoteId = "";
        Double discountedPrice = 0d;
        Double taxValue = 0d;
        String userUid = "";
        try 
        {
            if (totalDiscounts != null && !totalDiscounts.equals(C4cquoteConstants.IGNORE)) 
            {
                final List<String> discountData = Arrays.asList(StringUtils.split(totalDiscounts, '|'));
                quoteId = discountData.get(0);
                discountedPrice = Double.parseDouble(discountData.get(1));
                userUid = discountData.get(2);
                taxValue = Double.parseDouble(discountData.get(3));
                processedItem.setAttribute("totalDiscounts",
                        getInboundHelper().applyQuoteDiscountAndTax(quoteId, discountedPrice, taxValue, userUid));
            }

        } catch (JaloInvalidParameterException | JaloBusinessException e) {
            LOG.debug("Error in translating the quote discounts.", e);
        }
    }
}