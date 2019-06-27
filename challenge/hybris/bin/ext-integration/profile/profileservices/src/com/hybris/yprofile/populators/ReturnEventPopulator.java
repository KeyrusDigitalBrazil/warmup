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

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.Order;
import com.hybris.yprofile.dto.OrderBody;
import com.hybris.yprofile.dto.OrderLineItem;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class ReturnEventPopulator implements Populator<ReturnRequestModel, Order> {

    private static final String COMPLETE_RETURN_ORDER_EVENT_TYPE ="return";
    private static final String PARTIAL_RETURN_ORDER_EVENT_TYPE ="partial return";
    private static final String RETURN_ORDER_ENTRY_STATUS = "returned";

    private Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter;

    @Override
    public void populate(ReturnRequestModel returnRequestModel, Order order) throws ConversionException {

        final OrderModel orderModel = returnRequestModel.getOrder();

        final List<OrderLineItem> lineItems = new ArrayList<>();
        returnRequestModel.getReturnEntries().stream().forEach( returnEntry -> {
            final OrderLineItem lineItem = getProfileOrderLineItemConverter().convert(returnEntry.getOrderEntry());
            lineItem.setStatus(RETURN_ORDER_ENTRY_STATUS);
            lineItem.setQuantity(returnEntry.getExpectedQuantity());

            if (returnEntry instanceof RefundEntryModel){
                lineItem.setReason(((RefundEntryModel)returnEntry).getReason().toString());
            }

            lineItems.add(lineItem);
        });

        order.setChannelRef(orderModel.getStore().getUid());
        order.setType(this.getReturnEventType(orderModel.getEntries(), returnRequestModel.getReturnEntries()));
        order.setDate(Utils.formatDate(returnRequestModel.getCreationtime()));

        final OrderBody orderBody = new OrderBody();
        orderBody.setLineItems(lineItems);
        orderBody.setStatus(orderModel.getStatusDisplay());
        orderBody.setDate(Utils.formatDate(returnRequestModel.getCreationtime()));
        orderBody.setOrderId(orderModel.getCode());

        order.setBody(orderBody);
    }

    protected String getReturnEventType(List<AbstractOrderEntryModel> orderEntries, List<ReturnEntryModel> returnEntries) {
        if (orderEntries.size() == returnEntries.size()){
            for (final ReturnEntryModel entry : returnEntries) {
                if (!entry.getOrderEntry().getQuantity().equals(entry.getExpectedQuantity())) {
                    return PARTIAL_RETURN_ORDER_EVENT_TYPE;
                }
            }
            return COMPLETE_RETURN_ORDER_EVENT_TYPE;
        }

        return PARTIAL_RETURN_ORDER_EVENT_TYPE;
    }

    public Converter<AbstractOrderEntryModel, OrderLineItem> getProfileOrderLineItemConverter() {
        return profileOrderLineItemConverter;
    }

    @Required
    public void setProfileOrderLineItemConverter(Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter) {
        this.profileOrderLineItemConverter = profileOrderLineItemConverter;
    }
}
