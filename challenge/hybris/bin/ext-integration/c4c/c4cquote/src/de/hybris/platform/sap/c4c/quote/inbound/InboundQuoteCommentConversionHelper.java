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
package de.hybris.platform.sap.c4c.quote.inbound;

/**
 * Helper class to convert text coming from backend into Comment Model
 */
public interface InboundQuoteCommentConversionHelper
{

	/**
	 * Method to create comment Quote Header level with given parameters
	 * 
	 * @param quoteId
	 * @param text
	 * @param userId
	 * @return String
	 */
	String createHeaderComment(final String quoteId, final String text, final String userUid);

	
	/**
	 * Method to get Quote Comments codes
	 * 
	 * @param quoteId
	 * @return String
	 */
	String getQuoteComments(final String quoteId);

}
