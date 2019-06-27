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
package de.hybris.platform.integrationbackoffice.widgets.editor;

import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

public final class IntegrationObjectButtonPanelController extends DefaultWidgetController {

    private Button saveDefinitionsButton;

    @Override
    public void initialize(final Component component) {
        super.initialize(component);
        saveDefinitionsButton.setDisabled(true);
    }

    @GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
    public void handleIntegrationObjectCreatedEvent(final CockpitEvent event) {
        if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance)) {
            saveDefinitionsButton.setDisabled(false);
        }
    }

    @GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
    public void handleIntegrationObjectDeletedEvent(final CockpitEvent event) {
        if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance)) {
            saveDefinitionsButton.setDisabled(true);
        }
    }

    @SocketEvent(socketId = "integrationObject")
    public void loadIntegrationObject(final IntegrationObjectModel integrationObjectModel) {
        saveDefinitionsButton.setDisabled(integrationObjectModel == null);
    }

    @ViewEvent(componentID = "saveDefinitionsButton", eventName = Events.ON_CLICK)
    public void saveDefinitionsOnClick() {
        sendOutput("saveButtonClick", "");
    }

    @ViewEvent(componentID = "refreshButton", eventName = Events.ON_CLICK)
    public void refreshButtonOnClick() {
        saveDefinitionsButton.setDisabled(true);
        sendOutput("refreshButtonClick", "");
    }

}
