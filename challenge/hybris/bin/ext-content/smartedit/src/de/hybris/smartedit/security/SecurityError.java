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
package de.hybris.smartedit.security;

public enum SecurityError {

    AUTHENTICATION_ERROR_BAD_CREDENTIALS("authentication.error.badcredentials");

    private final String code;

    SecurityError(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
