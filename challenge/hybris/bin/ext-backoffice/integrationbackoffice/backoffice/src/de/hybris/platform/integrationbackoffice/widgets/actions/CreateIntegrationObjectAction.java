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
package de.hybris.platform.integrationbackoffice.widgets.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;

public final class CreateIntegrationObjectAction extends AbstractComponentWidgetAdapterAware
        implements CockpitAction<String, String> {

    @Override
    public ActionResult<String> perform(final ActionContext<String> ctx) {
        sendOutput("createIntegrationObjectPerform", "");
        return new ActionResult<>(ActionResult.SUCCESS, "");
    }

}
