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
package de.hybris.platform.commerceservices.order.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceQuoteAssignmentException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteAssignmentValidationStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of {@link QuoteActionValidationStrategy}.
 */
public class DefaultQuoteAssignmentValidationStrategy implements QuoteAssignmentValidationStrategy
{
	@Override
	public void validateQuoteAssignment(final QuoteModel quote, final UserModel assignee, final UserModel assigner)
	{
		if (quote.getAssignee() != null && !StringUtils.equals(quote.getAssignee().getUid(), assignee.getUid()))
		{
			throw new CommerceQuoteAssignmentException(
					String.format("Assigner:%s is not authorized to assign Quote code:%s to assignee:%s. "
							+ "Reasons: quote is already assigned", assigner.getUid(), quote.getCode(), assignee.getUid()),
					quote.getAssignee().getDisplayName());
		}

		if (!StringUtils.equals(assignee.getUid(), assigner.getUid()))
		{
			throw new CommerceQuoteAssignmentException(
					String.format(
							"Assigner:%s is not authorized to assign Quote code:%s to assignee:%s. "
									+ "Reasons: assignee & assigner are not the same",
							assigner.getUid(), quote.getCode(), assignee.getUid()),
					assignee.getDisplayName());
		}

	}

	@Override
	public void validateQuoteUnassignment(final QuoteModel quote, final UserModel assigner)
	{
		if (quote.getAssignee() != null && !StringUtils.equals(quote.getAssignee().getUid(), assigner.getUid()))
		{
			throw new CommerceQuoteAssignmentException(String.format(
					"Assigner:%s is not authorized to unassign Quote code:%s. Reason: assignee & assigner are not the same",
					assigner.getUid(), quote.getCode()), quote.getAssignee().getDisplayName());
		}
	}
}
