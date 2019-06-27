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

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.service.data.CommerceCommentParameter;
import de.hybris.platform.commerceservices.util.CommerceCommentUtils;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteCommentConversionHelper;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * Default implementation for InboundQuoteCommentConversionHelper
 */
public class DefaultInboundQuoteCommentConversionHelper implements InboundQuoteCommentConversionHelper {

	private ModelService modelService;
	private CommentService commentService;
	private QuoteService quoteService;
	private UserService userService;

	@Override
	public String createHeaderComment(String quoteId, String text, String userUid) {
		QuoteModel quote = getQuoteService().getCurrentQuoteForCode(quoteId);
		UserModel user = getUserService().getUserForUID(userUid);
		return createComment(CommerceCommentUtils.buildQuoteCommentParameter(quote, user, text));
	}
	
	@Override
	public String getQuoteComments(String quoteId) {
		QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteId);
		List<CommentModel> quoteComments = new ArrayList<>(quoteModel.getComments());
		StringBuilder commentCodes = new StringBuilder();
		if (CollectionUtils.isNotEmpty(quoteComments)) {
			for (CommentModel comment : quoteComments) {
				commentCodes = commentCodes.append(comment.getCode()).append(C4cquoteConstants.COMMENTS_CODE_SEPARATOR);
			}
		}
		return commentCodes.toString();
	}
	
	/**
	 * Method to create a comment model with given parameters
	 * 
	 * @param parameter
	 * @param commentType
	 * @param baseStore
	 * @return List of comment codes
	 */
	protected String createComment(final CommerceCommentParameter parameter) {
		String result;
		CommentModel commentModel = null;
		validateCommentParameter(parameter);
		commentModel = saveComment(parameter);
		result = commentModel.getCode();
		return result;
	}


	/**
	 * @param parameter
	 * @return
	 */
	private CommentModel saveComment(final CommerceCommentParameter parameter) {
		CommentModel commentModel;
		final DomainModel domainModel = getCommentService().getDomainForCode(parameter.getDomainCode());
		final ComponentModel componentModel = getCommentService().getComponentForCode(domainModel, parameter.getComponentCode());
		final CommentTypeModel commentTypeModel = getCommentService().getCommentTypeForCode(componentModel, parameter.getCommentTypeCode());
		commentModel = getModelService().create(CommentModel.class);
		commentModel.setText(parameter.getText());
		commentModel.setAuthor(parameter.getAuthor());
		commentModel.setComponent(componentModel);
		commentModel.setCommentType(commentTypeModel);
		getModelService().save(commentModel);
		return commentModel;
	}

	/**
	 * Method to get quoteEnrty model for given quote and entry number
	 * 
	 * @param order
	 * @param number
	 * @return
	 */
	protected AbstractOrderEntryModel getEntryForEntryNumber(final AbstractOrderModel order, final int number) {
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		if (entries != null && !entries.isEmpty()) {
			final Integer requestedEntryNumber = Integer.valueOf(number);
			for (final AbstractOrderEntryModel entry : entries) {
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber())) {
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * Method to validate the CommerceComment parameters to generate a quote
	 * comment
	 * 
	 * @param parameter
	 */
	protected void validateCommentParameter(final CommerceCommentParameter parameter) {
		ServicesUtil.validateParameterNotNullStandardMessage("parameter", parameter);
		ServicesUtil.validateParameterNotNullStandardMessage("author", parameter.getAuthor());
		checkArgument(isNotEmpty(parameter.getText()), "Text cannot not be empty");
		checkArgument(isNotEmpty(parameter.getDomainCode()), "Domain cannot not be empty");
		checkArgument(isNotEmpty(parameter.getComponentCode()), "Component cannot not be empty");
		checkArgument(isNotEmpty(parameter.getCommentTypeCode()), "CommentType cannot not be empty");
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public QuoteService getQuoteService() {
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

}
