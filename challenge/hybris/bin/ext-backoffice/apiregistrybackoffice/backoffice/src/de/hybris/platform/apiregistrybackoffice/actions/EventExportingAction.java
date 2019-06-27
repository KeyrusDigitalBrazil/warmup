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

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;

/**
 * Action in 'EventConfiguration' listview, triggers eventexportwidget
 */
public class EventExportingAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<Object, Object>
{
	public static final String OPEN_WIDGET = "openWidget";

	@Override
	public ActionResult<Object> perform(ActionContext<Object> actionContext)
	{
		sendOutput(OPEN_WIDGET, actionContext.getData());
		return new ActionResult<>(ActionResult.SUCCESS);
	}
}
