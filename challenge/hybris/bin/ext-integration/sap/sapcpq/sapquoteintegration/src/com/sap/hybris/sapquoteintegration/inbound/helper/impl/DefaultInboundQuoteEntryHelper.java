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
package com.sap.hybris.sapquoteintegration.inbound.helper.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.constants.SapquoteintegrationConstants;
import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.service.SapQuoteService;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.DiscountValue;

public class DefaultInboundQuoteEntryHelper implements InboundQuoteEntryHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteEntryHelper.class);
	private QuoteService quoteService;
	private UserService userService;
	private KeyGenerator keyGenerator;
	private SapQuoteService sapQuoteService;
	private CommentService commentService;
	private CalculationService calculationService;
	private ProductService productService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.sapquoteintegration.inbound.InboundQuoteEntryHelper#
	 * processInboundQuoteEntry(de.hybris.platform.core.model.order.QuoteEntryModel)
	 */
	@Override
	public QuoteEntryModel processInboundQuoteEntry(QuoteEntryModel inboundQuoteEntry) {
		LOG.info("Entering DefaultInboundQuoteEntryHelper#processInboundQuoteEntry");
		QuoteModel quote = quoteService.getCurrentQuoteForCode(inboundQuoteEntry.getOrder().getCode());
		inboundQuoteEntry.setOrder(quote);
		BaseStoreModel store = quote.getStore();
		List<CatalogModel> catalogs = store.getCatalogs();
		ProductModel productForCode = null;
		boolean productFoundInBaseStore = false;
		for (CatalogModel catalogModel : catalogs) {
			CatalogVersionModel activeCatalogVersion = catalogModel.getActiveCatalogVersion();
			try {
				productForCode = productService.getProductForCode(activeCatalogVersion, inboundQuoteEntry.getItemId());
				productFoundInBaseStore = true;
				break;
			} catch (Exception e) {
				LOG.info("Product " + inboundQuoteEntry.getItemId() + " not found in Catalog " + activeCatalogVersion,e);
			}
		}
		if (!productFoundInBaseStore) {
			LOG.error("Product " + inboundQuoteEntry.getItemId() + " not found in Base Store -  " + store
					+ "Replication of Quote will not proceed.");
		} else {
			inboundQuoteEntry.setProduct(productForCode);
			if (inboundQuoteEntry.getEntryDiscount() == null) {
				inboundQuoteEntry.setEntryDiscount(0.0d);
			}
			Double totalPrice = (inboundQuoteEntry.getBasePrice() * inboundQuoteEntry.getQuantity())
					- inboundQuoteEntry.getEntryDiscount();
			inboundQuoteEntry.setTotalPrice(totalPrice);
			inboundQuoteEntry.setEntryNumber(Integer.parseInt(inboundQuoteEntry.getRank()));
			processDiscount(inboundQuoteEntry);
			inboundQuoteEntry.setComments(processQuoteEntryComments(inboundQuoteEntry));
		}
		LOG.info("Exiting DefaultInboundQuoteEntryHelper#processInboundQuoteEntry");
		return inboundQuoteEntry;

	}

	/**
	 * @param inboundQuoteEntry
	 */
	private void processDiscount(QuoteEntryModel inboundQuoteEntry) {
		if (inboundQuoteEntry.getEntryDiscount() != null && inboundQuoteEntry.getEntryDiscount() > 0.0d) {
			Double discountPerItem = inboundQuoteEntry.getEntryDiscount() / inboundQuoteEntry.getQuantity();
			List<DiscountValue> dvList = new ArrayList<DiscountValue>();
			// Format of dv = [<DV<QuoteDiscount#20.0#false#28.8#USD#false>VD>]
			DiscountValue dv = new DiscountValue(SapquoteintegrationConstants.QUOTE_ENTRY_DISCOUNT_CODE, discountPerItem, true, discountPerItem,
					inboundQuoteEntry.getOrder().getCurrency().getIsocode());
			dvList.add(dv);
			inboundQuoteEntry.setDiscountValues(dvList);
			inboundQuoteEntry.setEntryDiscountInternal("<" + dv.toString() + ">");
		}
	}

	/**
	 * @param inboundQuote
	 */
	protected List<CommentModel> processQuoteEntryComments(QuoteEntryModel inboundQuoteEntry) {
		List<CommentModel> comments = inboundQuoteEntry.getComments();
		if (comments != null) {
			final String domainCode = "quoteDomain";
			final DomainModel domain = getCommentService().getDomainForCode(domainCode);
			final String componentCode = "quoteComponent";
			final ComponentModel component = getCommentService().getComponentForCode(domain, componentCode);
			final String commentTypeCode = "quoteEntryComment";
			final CommentTypeModel commentType = getCommentService().getCommentTypeForCode(component, commentTypeCode);
			for (CommentModel comment : comments) {
				comment.setComponent(component);
				comment.setCommentType(commentType);
				comment.setCreationtime(new Date());
				comment.setCode(UUID.randomUUID().toString());
				final UserModel author = getUserService().getUserForUID(comment.getCommentAuthorEmail());
				comment.setAuthor(author);
			}
		}

		return comments;
	}

	protected String generateCode() {
		final Object generatedValue = getKeyGenerator().generate();
		if (generatedValue instanceof String) {
			return (String) generatedValue;
		} else {
			return String.valueOf(generatedValue);
		}
	}

	public void calculateQuote(QuoteEntryModel quoteEntryModel) {
		QuoteModel existingQuote = null;
		try {
			existingQuote = quoteService.getCurrentQuoteForCode(quoteEntryModel.getOrder().getCode());
			calculationService.calculateTotals(existingQuote, true);
		} catch (de.hybris.platform.servicelayer.exceptions.ModelNotFoundException | CalculationException e) {
			LOG.error("Exception Occurred during Quote Recalculation. " + e);
		}
	}

	public QuoteService getQuoteService() {
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

	public SapQuoteService getSapQuoteService() {
		return sapQuoteService;
	}

	public void setSapQuoteService(SapQuoteService sapQuoteService) {
		this.sapQuoteService = sapQuoteService;
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public CalculationService getCalculationService() {
		return calculationService;
	}

	public void setCalculationService(CalculationService calculationService) {
		this.calculationService = calculationService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 * @param productService
	 *            the productService to set
	 */
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

}
