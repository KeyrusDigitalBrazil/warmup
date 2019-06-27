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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;

/**
 * Predicate that returns true if the provided {@link AttributeDescriptorModel} qualifier match the given qualifier.
 */
public class QualifierPredicate implements Predicate<AttributeDescriptorModel>
{
    private String qualifier;

    @Override
    public boolean test(final AttributeDescriptorModel attributeDescriptor)
    {
        return attributeDescriptor.getQualifier().equals(this.getQualifier());
    }

    protected String getQualifier() { return qualifier; }

    @Required
    public void setQualifier(String qualifier) { this.qualifier = qualifier; }
}