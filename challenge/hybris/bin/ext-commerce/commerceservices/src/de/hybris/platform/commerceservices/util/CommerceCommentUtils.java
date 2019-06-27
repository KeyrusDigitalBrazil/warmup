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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.commerceservices.comments.builder.CommerceCommentParameterBuilder;
import de.hybris.platform.commerceservices.service.data.CommerceCommentParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.util.Config;


/**
 * Utility class for comment functionality.
 */
public final class CommerceCommentUtils
{

	private CommerceCommentUtils()
	{
		throw new IllegalAccessError("Utility class may not be instantiated");
	}

	/**
	 * Creates a new {@link CommerceCommentParameterBuilder} instance.
	 *
	 * @return the new {@link CommerceCommentParameterBuilder}
	 */
	public static CommerceCommentParameterBuilder parameterBuilder()
	{
		return new CommerceCommentParameterBuilder();
	}

	/**
	 * Build a {@link CommerceCommentParameter} for {@link QuoteModel} comments.
	 *
	 * @param order
	 *           the {@link AbstractOrderModel} that will hold the comment
	 * @param author
	 *           the author of the comment
	 * @param text
	 *           text of the comment
	 * @return a {@link CommerceCommentParameter} built based on the input.
	 */
	public static CommerceCommentParameter buildQuoteCommentParameter(final AbstractOrderModel order, final UserModel author,
			final String text)
	{
		return parameterBuilder().item(order).author(author).text(text)
				.domainCode(Config.getString("commerceservices.quote.comment.domain", "quoteDomain"))
				.componentCode(Config.getString("commerceservices.quote.comment.component", "quoteComponent"))
				.commentTypeCode(Config.getString("commerceservices.quote.comment.commentType", "quoteComment")).build();
	}

	/**
	 * Build a {@link CommerceCommentParameter} for {@link QuoteModel} comments.
	 *
	 * @param orderEntry
	 *           the {@link AbstractOrderEntryModel} that will hold the comment
	 * @param author
	 *           the author of the comment
	 * @param text
	 *           text of the comment
	 * @return a {@link CommerceCommentParameter} built based on the input.
	 */
	public static CommerceCommentParameter buildQuoteEntryCommentParameter(final AbstractOrderEntryModel orderEntry,
			final UserModel author, final String text)
	{
		return parameterBuilder().item(orderEntry).author(author).text(text)
				.domainCode(Config.getString("commerceservices.quote.comment.domain", "quoteDomain"))
				.componentCode(Config.getString("commerceservices.quote.comment.component", "quoteComponent"))
				.commentTypeCode(Config.getString("commerceservices.quote.comment.commentType", "quoteComment")).build();
	}
}
