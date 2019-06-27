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

package de.hybris.platform.kymaintegrationbackoffice.actions;

import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;


public class RetrieveCertificateAction extends AbstractComponentWidgetAdapterAware
		implements CockpitAction<ConsumedCertificateCredentialModel, String>
{

	public static final String CERTIFICATE_SOCKET_KEY = "existingCertificate";

	@Override
	public ActionResult<String> perform(ActionContext<ConsumedCertificateCredentialModel> actionContext)
	{
		sendOutput(CERTIFICATE_SOCKET_KEY, actionContext.getData());
		return new ActionResult<>(ActionResult.SUCCESS);
	}

}
