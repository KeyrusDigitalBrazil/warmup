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
package de.hybris.platform.sap.c4c.quote.inbound.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;
import de.hybris.platform.util.localization.Localization;


public class DefaultInboundQuoteHelper implements InboundQuoteHelper
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteHelper.class);
	private static final String TAX = "TAX";

	private QuoteService quoteService;
	private ModelService modelService;
	private CommerceQuoteService commerceQuoteService;
	private ProductService productService;
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	private EventService eventService;
	private UserService userService;

	@Override
	public QuoteModel createQuoteSnapshot(String code, String state)
	{
		QuoteModel quote = quoteService.getCurrentQuoteForCode(code);
		QuoteModel quoteSnapshot = null;
		if (quote != null)
		{
			quoteSnapshot = getQuoteService().createQuoteSnapshot(quote, QuoteState.valueOf(state));
			getModelService().save(quoteSnapshot);
			getModelService().refresh(quoteSnapshot);
		}
		else
		{
			LOG.info("Quote does not exist with code", code);
		}
		return quoteSnapshot;
	}

	

	@Override
	public String getNameForQuote(String code)
	{
		QuoteModel quote = null;
		try
		{
			quote = getQuoteService().getCurrentQuoteForCode(code);
		}
		catch (ModelNotFoundException e)
		{
			LOG.info("No existing quote found with this code in getNameForQuote() - " + code + " Creating new quote", e);
			return String.format("%s %s", getLocalizedTypeName(), code);
		}
		return quote.getName();
	}

	protected String getLocalizedTypeName()
	{
		return Localization.getLocalizedString(C4cquoteConstants.QUOTE_NAME_STRING);
	}


	@Override
	public String getPreviousEstimatedTotal(String code)
	{
		QuoteModel quote = null;
		try
		{
			quote = getQuoteService().getCurrentQuoteForCode(code);
		}
		catch (ModelNotFoundException e)
		{
			LOG.info("No existing quote found with this code in getPreviousEstimatedTotal() - " + code + " Creating new quote", e);
			return Double.toString(0);
		}
		Double previousEstimatedTotal = quote.getTotalPrice();
		return Double.toString(previousEstimatedTotal);
	}

	@Override
	public String getGuid(String code)
	{
		QuoteModel quote = null;
		try
		{
			quote = getQuoteService().getCurrentQuoteForCode(code);
		}
		catch (ModelNotFoundException e)
		{
			LOG.info("No existing quote found with this code in getGuid() - " + code, e);
			return C4cquoteConstants.IGNORE;
		}
		return quote.getGuid();
	}

	@Override
        public Double applyQuoteDiscountAndTax(String quoteId, Double discountedPrice, Double taxValue, String userUid)
        {
                LOG.info("Applying discount on current quote version");
                QuoteModel currentQuote = getQuoteService().getCurrentQuoteForCode(quoteId);
                Double discount = currentQuote.getSubtotal() - discountedPrice;
                currentQuote.setState(QuoteState.SELLER_DRAFT);
                getModelService().save(currentQuote);
                if (discount > Double.valueOf(0))
                {
                        getCommerceQuoteService().applyQuoteDiscount(currentQuote, getUserService().getUserForUID(userUid), 
                                discount, DiscountType.ABSOLUTE);
                }
                
                final String taxCode = TAX + currentQuote.getCode();
                final TaxValue tax = new TaxValue(taxCode, taxValue, true, currentQuote.getCurrency().toString());
                final List<TaxValue> taxValues = new ArrayList<>();
                taxValues.add(tax);
                        
                currentQuote.setTotalTax(taxValue);
                currentQuote.setTotalTaxValues(taxValues);

                currentQuote.setState(QuoteState.BUYER_OFFER);
                getModelService().save(currentQuote);
                return discount;
        }	

	@Override
	public String createQuoteEntryProduct(String code, String productId)
	{
		QuoteModel quote = null;
		ProductModel product = null;
		String response = null;
		try
		{
			quote = getQuoteService().getQuoteForCodeAndVersion(code, 1);
			if (quote.getEntries() != null && !quote.getEntries().isEmpty())
			{
				ProductModel firstProduct = quote.getEntries().get(0).getProduct();
				product = productService.getProductForCode(firstProduct.getCatalogVersion(), productId);
				response = product.getPk().toString();
			}
		}
		catch (ModelNotFoundException e)
		{
			LOG.info("No existing quote found with this code and version in createQuoteEntryProduct() - ", e);
			return C4cquoteConstants.IGNORE;
		}
		return response;
	}

	/**
	 * Method converts entry number format from C4C to Hybris
	 *
	 * @param code
	 *           contains the numeric String
	 * @return String with conversion done
	 *
	 */
	@Override
	public String convertEntryNumber(String code)
	{
		return Integer.toString(Integer.parseInt(code) / 10 - 1);
	}

	public CommerceQuoteService getCommerceQuoteService()
	{
		return commerceQuoteService;
	}

	public void setCommerceQuoteService(CommerceQuoteService commerceQuoteService)
	{
		this.commerceQuoteService = commerceQuoteService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(ProductService productService)
	{
		this.productService = productService;
	}
	
	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	public QuoteUserTypeIdentificationStrategy getQuoteUserTypeIdentificationStrategy()
	{
		return quoteUserTypeIdentificationStrategy;
	}

	public void setQuoteUserTypeIdentificationStrategy(QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy)
	{
		this.quoteUserTypeIdentificationStrategy = quoteUserTypeIdentificationStrategy;
	}

	public EventService getEventService()
	{
		return eventService;
	}

	public void setEventService(EventService eventService)
	{
		this.eventService = eventService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}
