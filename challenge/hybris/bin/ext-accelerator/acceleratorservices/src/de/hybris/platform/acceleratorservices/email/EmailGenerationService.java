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
package de.hybris.platform.acceleratorservices.email;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Service for generating an email.
 */
public interface EmailGenerationService
{
	/**
	 * Generates EmailMessage give business process and cms email page.
	 * 
	 * @param businessProcessModel
	 *           Business process object
	 * @param emailPageModel
	 *           Email page
	 * @return EmailMessage
	 */
	EmailMessageModel generate(BusinessProcessModel businessProcessModel, EmailPageModel emailPageModel);
}
