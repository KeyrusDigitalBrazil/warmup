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
package com.sap.hybris.sapquoteintegration.outbound.service;

import de.hybris.platform.comments.model.CommentModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteCommentModel;


/**
 * Provides mapping from {@link CommentModel} to {@link SAPCpiOutboundQuoteCommentModel}.
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public interface SapCpiQuoteCommentMapperService<SOURCE extends CommentModel, TARGET extends SAPCpiOutboundQuoteCommentModel>
{
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Comment Model
	 * @param target
	 *           SAP CPI Outbound Quote Comment Model
	 */
	void map(SOURCE source, TARGET target);

}