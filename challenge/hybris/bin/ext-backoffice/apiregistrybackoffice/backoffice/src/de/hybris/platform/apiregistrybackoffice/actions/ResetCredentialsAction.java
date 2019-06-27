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
package de.hybris.platform.apiregistrybackoffice.actions;

import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;

import java.util.HashMap;
import java.util.Map;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowContextParameterNames;


/**
 * Action responsible for Credentials Reset
 */

public class ResetCredentialsAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<Object, Object>
{

	public ResetCredentialsAction()
	{
		//nothing to do
	}

	public ActionResult<Object> perform(ActionContext<Object> ctx)
	{
		final Map<String, Object> parameters = new HashMap();
		parameters.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), ExposedOAuthCredentialModel._TYPECODE);
		parameters.put("configurableFlowConfigCtx", "reset-credentials-wizard");
		this.sendOutput("openWizard", parameters);
		return new ActionResult("success", null);
	}

	@Override
	public boolean canPerform(ActionContext<Object> ctx)
	{
		return true;
	}

	@Override
	public boolean needsConfirmation(ActionContext<Object> ctx)
	{
		return false;
	}

	@Override
	public String getConfirmationMessage(ActionContext<Object> ctx)
	{
		return null;
	}
}
