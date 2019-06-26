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
package com.sap.hybris.sapquoteintegrationaddon.facade;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;

/**
 *
 */
public class DefaultSapQuoteFacadeImpl implements SapQuoteFacade {

	static final private Logger logger = Logger.getLogger(DefaultSapQuoteFacadeImpl.class.getName());

	private QuoteService quoteService;

	private MediaService mediaService;

	private UserService userService;

	/**
	 * @return the quoteService
	 */
	public QuoteService getQuoteService() {
		return quoteService;
	}

	/**
	 * @return the mediaService
	 */
	public MediaService getMediaService() {
		return mediaService;
	}

	/**
	 * @param mediaService
	 *            the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService) {
		this.mediaService = mediaService;
	}

	/**
	 * @param quoteService
	 *            the quoteService to set
	 */
	public void setQuoteService(final QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	@Override
	public byte[] downloadQuoteProposalDocument(final String quoteCode) {
		byte[] dataFromMedia = null;
		try {
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(quoteCode);
			String loggedInUid = userService.getCurrentUser().getUid();
			if (loggedInUid.equals(quote.getUser().getUid())) {
				final MediaModel proposalDocument = quote.getProposalDocument();
				dataFromMedia = mediaService.getDataFromMedia(proposalDocument);
			} else {
				throw new Exception("Access Denied to open Proposal Document");
			}
		} catch (final Exception e) {
			logger.error("Unable to Retrieve the PDF from backend", e);

		}
		return dataFromMedia;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
