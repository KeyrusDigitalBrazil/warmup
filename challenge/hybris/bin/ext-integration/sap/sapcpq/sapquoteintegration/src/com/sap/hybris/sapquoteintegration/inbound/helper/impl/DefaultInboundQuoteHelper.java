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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.service.SapQuoteService;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Base64;
import de.hybris.platform.util.DiscountValue;

public class DefaultInboundQuoteHelper implements InboundQuoteHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteHelper.class);
	private QuoteService quoteService;
	private UserService userService;
	private KeyGenerator keyGenerator;
	private CommentService commentService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private CatalogService catalogService;
	private ModelService modelService;
	private MediaService mediaService;
	private SapQuoteService sapQuoteService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.sapquoteintegration.inbound.InboundQuoteHelper#
	 * processInboundQuote(de.hybris.platform.core.model.order.QuoteModel)
	 */
	@Override
	public QuoteModel processInboundQuote(QuoteModel inboundQuote) {
		LOG.info("Enter DefaultInboundQuoteHelper#processInboundQuote");

		if (inboundQuote.getCode().equals(inboundQuote.getExternalQuoteId())) {
			// process new quote
			LOG.info("DefaultInboundQuoteHelper#processInboundQuote Creating new quote");
			inboundQuote.setState(QuoteState.BUYER_OFFER);
			inboundQuote.setDate(new Date());
			String baseStoreUid = sapQuoteService.getSiteAndStoreFromSalesArea(inboundQuote.getSalesOrganization(),
					inboundQuote.getDistributionChannel(), inboundQuote.getDivision());
			inboundQuote.setStore(baseStoreService.getBaseStoreForUid(baseStoreUid));
			inboundQuote.setSite(baseSiteService.getBaseSiteForUID(baseStoreUid));
			inboundQuote.setGuid(UUID.randomUUID().toString());
			inboundQuote.setVersion(1);

		} else {
			// create a Vendor Quote of Existing Quote
			LOG.info("DefaultInboundQuoteHelper#processInboundQuote Creating Vendor Quote of Existing quote");
			replicateCurrentQuote(inboundQuote);
			inboundQuote.setSubtotal(inboundQuote.getTotalPrice() + inboundQuote.getTotalDiscounts());
		}
		inboundQuote.setExpirationTime(inboundQuote.getQuoteExpirationDate());
		processComments(inboundQuote);
		processDiscount(inboundQuote);
		saveProposalDocument(inboundQuote);
		LOG.info("Exiting DefaultInboundQuoteHelper#processInboundQuote");
		return inboundQuote;

	}

	/**
	 * @param inboundQuote
	 */
	private void replicateCurrentQuote(QuoteModel inboundQuote) {
		QuoteModel currentQuote = quoteService.getCurrentQuoteForCode(inboundQuote.getCode());
		inboundQuote.setState(QuoteState.BUYER_OFFER);
		
		inboundQuote.setAssignee(currentQuote.getAssignee());
		inboundQuote.setB2bcomments(currentQuote.getB2bcomments());
		inboundQuote.setCreationtime(currentQuote.getCreationtime());
		
		inboundQuote.setDate(currentQuote.getDate());
		inboundQuote.setDescription(currentQuote.getDescription());
		inboundQuote.setGuid(currentQuote.getGuid());
		inboundQuote.setLocale(currentQuote.getLocale());
		inboundQuote.setName(currentQuote.getName());
		inboundQuote.setOwner(currentQuote.getOwner());
		inboundQuote.setPreviousEstimatedTotal(currentQuote.getTotalPrice());
		inboundQuote.setSite(currentQuote.getSite());
		inboundQuote.setStore(currentQuote.getStore());
		inboundQuote.setUnit(currentQuote.getUnit());
		inboundQuote.setUser(currentQuote.getUser());
		inboundQuote.setVersion(currentQuote.getVersion().intValue() + 1);
		inboundQuote.setWorkflow(currentQuote.getWorkflow());
	}

	protected void processDiscount(QuoteModel quoteModel) {
		LOG.info("DefaultInboundQuoteHelper#processInboundQuote quoteModel=" + quoteModel);
		Double headerDiscount = quoteModel.getHeaderDiscount();
		if (headerDiscount != null && headerDiscount.doubleValue() > 0.0d) {
			List<DiscountValue> dvList = new ArrayList<DiscountValue>();
			DiscountValue dv = new DiscountValue(CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
					headerDiscount.doubleValue(), true, headerDiscount.doubleValue(),
					quoteModel.getCurrency().getIsocode());
			dvList.add(dv);
			quoteModel.setGlobalDiscountValues(dvList);
			quoteModel.setQuoteDiscountValuesInternal("<" + dv.toString() + ">");
		}
	}

	/**
	 * @param inboundQuote
	 */
	protected void processComments(QuoteModel inboundQuote) {
		List<CommentModel> comments = inboundQuote.getComments();
		if (comments != null) {
			final String domainCode = "quoteDomain";
			final DomainModel domain = getCommentService().getDomainForCode(domainCode);
			final String componentCode = "quoteComponent";
			final ComponentModel component = getCommentService().getComponentForCode(domain, componentCode);
			final String commentTypeCode = "quoteComment";
			final CommentTypeModel commentType = getCommentService().getCommentTypeForCode(component, commentTypeCode);
			for (CommentModel comment : comments) {
				if (comment.getAuthor() == null) {
					final UserModel author = getUserService().getUserForUID(comment.getCommentAuthorEmail());
					comment.setAuthor(author);
					comment.setComponent(component);
					comment.setCommentType(commentType);
					comment.setCreationtime(new Date());
					comment.setCode(UUID.randomUUID().toString());
				}
			}
			inboundQuote.setComments(comments);
		}
	}

	protected void saveProposalDocument(QuoteModel inboundQuote) {
		String externalProposalDocument = inboundQuote.getExternalQuoteDocument();
		MediaModel uploadFileMedia = null;

		if (externalProposalDocument != null && !externalProposalDocument.isEmpty()) {

			uploadFileMedia = modelService.create(MediaModel.class);
			String randomFileName = UUID.randomUUID().toString() + ".pdf";
			uploadFileMedia.setCode(randomFileName);
			final CatalogModel cm = catalogService.getCatalogForId("powertoolsProductCatalog");
			Set<CatalogVersionModel> catalogModelSet = cm.getCatalogVersions();

			if (catalogModelSet != null) {
				Iterator<CatalogVersionModel> iterator = catalogModelSet.iterator();
				final CatalogVersionModel catalogVersionModel = (CatalogVersionModel) iterator.next();
				uploadFileMedia.setCatalogVersion(catalogVersionModel);
			}

			uploadFileMedia.setDescription("File Description");
			modelService.save(uploadFileMedia);

			byte[] bytes = Base64.decode(externalProposalDocument);

			final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			mediaService.setStreamForMedia(uploadFileMedia, inputStream, randomFileName, "application/pdf");
		}
		inboundQuote.setProposalDocument(uploadFileMedia);
		inboundQuote.setExternalQuoteDocument(null);

	}

	protected String generateCode() {
		final Object generatedValue = getKeyGenerator().generate();
		if (generatedValue instanceof String) {
			return (String) generatedValue;
		} else {
			return String.valueOf(generatedValue);
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

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public SapQuoteService getSapQuoteService() {
		return sapQuoteService;
	}

	public void setSapQuoteService(SapQuoteService sapQuoteService) {
		this.sapQuoteService = sapQuoteService;
	}
}
