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
package de.hybris.platform.cmsfacades.common.predicate.attributes;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

/**
 * Predicate that returns true if the provided {@link AttributeDescriptorModel} represents a 'part-of' or a 'nested' attribute.
 */
public class NestedOrPartOfAttributePredicate implements Predicate<AttributeDescriptorModel>
{
    private Set<Predicate<AttributeDescriptorModel>> nestedAttributePredicates;

    @Override
    public boolean test(final AttributeDescriptorModel attributeDescriptor)
    {
        return attributeDescriptor.getPartOf() || getNestedAttributePredicates().stream().anyMatch(predicate -> predicate.test(attributeDescriptor));
    }

    protected Set<Predicate<AttributeDescriptorModel>> getNestedAttributePredicates()
    {
        return nestedAttributePredicates;
    }

    @Required
    public void setNestedAttributePredicates(Set<Predicate<AttributeDescriptorModel>> nestedAttributePredicates)
    {
        this.nestedAttributePredicates = nestedAttributePredicates;
    }

}