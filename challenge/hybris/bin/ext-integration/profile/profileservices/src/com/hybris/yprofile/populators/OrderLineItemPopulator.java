/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.yprofile.populators;

import com.hybris.yprofile.dto.Category;
import com.hybris.yprofile.dto.OrderLineItem;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;
import org.springframework.beans.factory.annotation.Required;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OrderLineItemPopulator implements Populator<AbstractOrderEntryModel, OrderLineItem> {

    private static final Logger LOG = Logger.getLogger(OrderLineItemPopulator.class);

    private Converter<CategoryModel, Category> profileCategoryConverter;

    @Override
    public void populate(AbstractOrderEntryModel abstractOrderEntryModel, OrderLineItem orderLineItem) {

        orderLineItem.setPos(abstractOrderEntryModel.getEntryNumber());
        orderLineItem.setRef(abstractOrderEntryModel.getProduct().getCode());
        orderLineItem.setType(abstractOrderEntryModel.getProduct().getItemtype());
        orderLineItem.setUnit(abstractOrderEntryModel.getUnit() != null ? abstractOrderEntryModel.getUnit().getCode() : "");
        orderLineItem.setPrice_list(abstractOrderEntryModel.getBasePrice());
        orderLineItem.setPrice_effective(abstractOrderEntryModel.getTotalPrice());
        orderLineItem.setCurrency(abstractOrderEntryModel.getOrder() != null ? (abstractOrderEntryModel.getOrder().getCurrency() != null ? abstractOrderEntryModel.getOrder().getCurrency().getIsocode() : "") : "");
        orderLineItem.setQuantity(abstractOrderEntryModel.getQuantity());

        final Optional<List<Category>> categories = getCategories(abstractOrderEntryModel);

        if (categories.isPresent()) {
            orderLineItem.setCategories(categories.get());
        }

        final Collection<TaxValue> taxValues = abstractOrderEntryModel.getTaxValues();
        if (taxValues != null) {
            taxValues.stream().forEach(tv -> orderLineItem.setTaxAmount(tv.getValue()));
        }
    }

    private Optional<List<Category>> getCategories(AbstractOrderEntryModel abstractOrderEntryModel) {

        try {

            final List<Category> categories = new ArrayList<>();
            abstractOrderEntryModel.getProduct().getSupercategories().stream().forEach(
                    (CategoryModel categoryModel)
                            -> categories.add(getProfileCategoryConverter().convert(categoryModel))
            );

            return Optional.ofNullable(categories);
        } catch (Exception e){
            LOG.warn("Error occurred retrieving categories. Message: " + e.getMessage());
            LOG.debug("Error occurred retrieving categories.", e);
        }

        return Optional.empty();
    }

    public Converter<CategoryModel, Category> getProfileCategoryConverter() {
        return profileCategoryConverter;
    }

    @Required
    public void setProfileCategoryConverter(Converter<CategoryModel, Category> profileCategoryConverter) {
        this.profileCategoryConverter = profileCategoryConverter;
    }
}
