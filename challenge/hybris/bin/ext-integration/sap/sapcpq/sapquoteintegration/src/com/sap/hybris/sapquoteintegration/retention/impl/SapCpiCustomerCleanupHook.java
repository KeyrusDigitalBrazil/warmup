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
package com.sap.hybris.sapquoteintegration.retention.impl;

import java.util.Collection;
import java.util.Iterator;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.retention.hook.ItemCleanupHook;

/**
 * @author I503090
 * Removes proposal document PDF saved as media object against individual quotes
 *
 */
public class SapCpiCustomerCleanupHook implements ItemCleanupHook<CustomerModel> {
	
	@Override
	public void cleanupRelatedObjects(final CustomerModel customerModel)
	{
		final Collection<QuoteModel> quoteModels = customerModel.getQuotes();
		if (quoteModels != null && quoteModels.size() > 0)
		{
			final Iterator<QuoteModel> itr = quoteModels.iterator();
			while (itr.hasNext())
			{
				final QuoteModel quoteModel = itr.next();
				final MediaModel proposalDocument = quoteModel.getProposalDocument();
				if (proposalDocument != null)
				{
					MediaManager.getInstance().deleteMedia(proposalDocument.getFolder().getQualifier(),
							proposalDocument.getLocation());
				}
			}
		}
	}
	
}
