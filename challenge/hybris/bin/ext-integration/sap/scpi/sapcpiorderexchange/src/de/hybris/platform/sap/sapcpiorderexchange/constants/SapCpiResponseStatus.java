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
package de.hybris.platform.sap.sapcpiorderexchange.constants;

public enum SapCpiResponseStatus {

    SCPI_ERROR("error"),
    SCPI_SUCCESS("success");

    private final String status;

    SapCpiResponseStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
