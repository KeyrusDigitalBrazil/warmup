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

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;

public final class CreateIntegrationObjectModalData {

    private String name;
    private ComposedTypeModel composedTypeModel;
    private IntegrationType type;

    public CreateIntegrationObjectModalData(final String name, final ComposedTypeModel composedTypeModel, final IntegrationType type) {
        this.name = name;
        this.composedTypeModel = composedTypeModel;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ComposedTypeModel getComposedTypeModel() {
        return composedTypeModel;
    }

    public IntegrationType getType() {
        return type;
    }

}
