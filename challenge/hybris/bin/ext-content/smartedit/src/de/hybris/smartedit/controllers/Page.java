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
package de.hybris.smartedit.controllers;


public enum Page {

    LOGIN_PAGE("loginPage"),
    SMART_EDIT_ROOT_PAGE("index");

    private final String viewName;

    Page(final String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }
}
