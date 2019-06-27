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
package de.hybris.platform.smarteditwebservices.configuration.facade;

/**
 * Constants that define keys and UID's for standard components
 */
public enum DefaultConfigurationKey {

    /**
     * The default tooling language used by Smartedit
     */
    DEFAULT_LANGUAGE("defaultToolingLanguage");

    private final String key;

    /**
     * Constructor that allows for the creation with a key/uid.
     * @param key
     */
    DefaultConfigurationKey(String key) {
        this.key = key;
    }

    /**
     * Gets the key value that this instance was intialized with.
     *
     * @return the key value for the enum
     */
    public String getKey() {
        return key;
    }

    /**
     * The uid is an alias for the key.
     *
     * @return the uid value for the enum
     */
    public String getUid() {
        return getKey();
    }
}
