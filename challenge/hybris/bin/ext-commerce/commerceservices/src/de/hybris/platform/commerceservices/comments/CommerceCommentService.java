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
package de.hybris.platform.commerceservices.comments;

import de.hybris.platform.commerceservices.service.data.CommerceCommentParameter;


/**
 * Commerce comment service.
 */
public interface CommerceCommentService
{

	/**
	 * Adds a comment to an item.
	 *
	 * @param parameter The comment parameter with comment data to be added.
	 */
	void addComment(final CommerceCommentParameter parameter);
}
