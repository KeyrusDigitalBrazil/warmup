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
package de.hybris.platform.integrationbackoffice.widgets.modals;

import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.createComboItem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.filterAttributesForAttributesMap;

public final class CreateIntegrationObjectModalController extends DefaultWidgetController {

    @WireVariable
    private transient ReadService readService;

    private Textbox integrationObjectName;
    private Combobox rootTypeComboBox;
    private Combobox integrationTypeComboBox;

    public void setReadService(final ReadService readService) {
        this.readService = readService;
    }

    @Override
    public void initialize(final Component component) {
        super.initialize(component);
        loadComposedTypes();
        loadIntegrationTypes();
    }

    @ViewEvent(componentID = "createButton", eventName = Events.ON_CLICK)
    public void createIntegrationObjectModalData() {
        final String serviceName = integrationObjectName.getValue();
        if (("").equals(serviceName)) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.emptyService"),
                    getLabel("integrationbackoffice.editMode.error.title.emptyService"), 1, Messagebox.ERROR);
        } else if (!isServiceNameValid(serviceName)) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.invalidService"),
                    getLabel("integrationbackoffice.editMode.error.title.invalidService"), 1, Messagebox.ERROR);
        } else if (!isServiceNameUnique(serviceName)) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.serviceExists"),
                    getLabel("integrationbackoffice.editMode.error.title.serviceExists"), 1, Messagebox.ERROR);
        } else if (rootTypeComboBox.getSelectedItem() == null) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.invalidRootType"),
                    getLabel("integrationbackoffice.editMode.error.title.invalidRootType"), 1, Messagebox.ERROR);
        } else {
            final ComposedTypeModel root = rootTypeComboBox.getSelectedItem().getValue();
            final IntegrationType type;

            if (integrationTypeComboBox.getSelectedItem() != null) {
                type = integrationTypeComboBox.getSelectedItem().getValue();
            } else {
                type = IntegrationType.INBOUND; //Defaults to INBOUND
            }

            sendOutput("createButtonClick", new CreateIntegrationObjectModalData(serviceName, root, type));
        }
    }

    private void loadComposedTypes() {
        readService.getAvailableTypes().forEach(type -> {
            if (!filterAttributesForAttributesMap(readService, type).isEmpty()) {
                rootTypeComboBox.appendChild(createComboItem(type.getCode(), type));
            }
        });
    }

    private void loadIntegrationTypes() {
        readService.getIntegrationTypes().forEach((type -> integrationTypeComboBox.appendChild(createComboItem(type.getCode(), type))));
    }

    boolean isServiceNameValid(final String serviceName) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        final Matcher matcher = pattern.matcher(serviceName);
        return matcher.matches();
    }

    boolean isServiceNameUnique(final String serviceName) {
        for (final IntegrationObjectModel integrationObject : readService.getIntegrationObjectModels()) {
            if (integrationObject.getCode().equalsIgnoreCase(serviceName)) {
                return false;
            }
        }
        return true;
    }

}
