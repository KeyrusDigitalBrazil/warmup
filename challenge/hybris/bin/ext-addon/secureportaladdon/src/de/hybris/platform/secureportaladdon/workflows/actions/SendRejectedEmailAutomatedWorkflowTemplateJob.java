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
package de.hybris.platform.secureportaladdon.workflows.actions;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationRejectedProcessModel;


/**
 * Workflow action responsible for sending the "rejected" email
 */
public class SendRejectedEmailAutomatedWorkflowTemplateJob extends
		SendEmailAutomatedWorkflowTemplateJob<B2BRegistrationRejectedProcessModel>
{

	@Override
	protected B2BRegistrationRejectedProcessModel createProcessModel(final CustomerModel customerModel,
			final B2BRegistrationModel registrationModel)
	{
		final B2BRegistrationRejectedProcessModel process = super.createProcessModel(customerModel, registrationModel);
		process.setRejectReason(registrationModel.getRejectReason());
		return process;
	}

}