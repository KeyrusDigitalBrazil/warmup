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
package de.hybris.platform.sap.c4c.quote.translators;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;

/**
 * 
 * Abstract base class for Datahub Translators
 *
 * @param <T>
 *            Type of helper class
 */
public abstract class DataHubTranslator<T> extends AbstractSpecialValueTranslator {
    private final String helperBeanName;
    private T inboundHelper;

    public DataHubTranslator(final String beanName) {
        super();
        this.helperBeanName = beanName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException {
        if (getInboundHelper() == null) {
            setInboundHelper((T) Registry.getApplicationContext().getBean(helperBeanName));
        }
    }

    @Override
    public void validate(final String paramString) throws HeaderValidationException {
        // Nothing to do
    }

    @Override
    public String performExport(final Item paramItem) throws ImpExException {
        return null;
    }

    @Override
    public boolean isEmpty(final String paramString) {
        return false;
    }

    public void setInboundHelper(final T service) {
        this.inboundHelper = service;
    }

    protected T getInboundHelper() {
        return inboundHelper;
    }
}
