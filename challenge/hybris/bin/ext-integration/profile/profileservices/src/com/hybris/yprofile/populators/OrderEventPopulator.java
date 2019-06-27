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
import com.hybris.yprofile.dto.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class OrderEventPopulator implements Populator<OrderModel, Order> {

    private static final String NEW_ORDER_EVENT_TYPE = "order";
    private static final String NEW_ORDER_STATUS = "new";
    private static final String NOT_DELIVERED_STATUS = "not delivered";

    private Converter<AddressModel, Address> profileAddressConverter;
    private Converter<UserModel, Consumer> profileConsumerConverter;
    private Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter;
    private DefaultSessionTokenService defaultSessionTokenService;

    @Override
    public void populate(OrderModel orderModel, Order order) {

        order.setChannelRef(orderModel.getStore().getUid());
        order.setType(NEW_ORDER_EVENT_TYPE);
        order.setConsumer(getProfileConsumerConverter().convert(orderModel.getUser()));
        order.setBody(getOrderBody(orderModel));
        order.setSessionId(getDefaultSessionTokenService().getOrCreateSessionToken());
        order.setDate(Utils.formatDate(orderModel.getCreationtime()));
    }

    protected OrderBody getOrderBody(OrderModel orderModel){
        final OrderBody orderBody = new OrderBody();
        orderBody.setOrderId(orderModel.getCode());
        orderBody.setCartId(orderModel.getCartIdReference());
        orderBody.setDate(Utils.formatDate(orderModel.getCreationtime()));
        orderBody.setOrderValue(orderModel.getTotalPrice());
        orderBody.setCurrency(orderModel.getCurrency().getIsocode());

        orderBody.setDeliveryCost(orderModel.getDeliveryCost());
        orderBody.setTotalDiscounts(orderModel.getTotalDiscounts());

        orderBody.setStatus(orderModel.getStatusDisplay() != null ? orderModel.getStatusDisplay(): NEW_ORDER_STATUS);

        final List<Promotion> promotions = new ArrayList<>();
        orderModel.getAllPromotionResults().forEach(
                promotionResultModel ->
                        promotions.add(getPromotion(promotionResultModel.getPromotion()))
        );
        orderBody.setPromotionInfo(promotions);

        orderBody.setPaymentInfo(getPaymentInfo(orderModel));

        orderBody.setShipmentInfo(getShipmentInfo(orderModel));

        final List<OrderLineItem> lineItems = new ArrayList<>();
        orderModel.getEntries().stream().forEach(
                (AbstractOrderEntryModel abstractOrderEntryModel)
                        -> lineItems.add(getProfileOrderLineItemConverter().convert(abstractOrderEntryModel))
        );

        orderBody.setLineItems(lineItems);

        return orderBody;
    }

    protected Promotion getPromotion(AbstractPromotionModel promotionResultModel){
        final Promotion promotion = new Promotion();
        promotion.setRef(promotionResultModel.getCode());
        promotion.setType(promotionResultModel.getPromotionType());

        return promotion;
    }

    protected PaymentInfo getPaymentInfo(OrderModel orderModel){
        final PaymentInfo paymentInfo = new PaymentInfo();

        final PaymentInfoModel paymentInfoModel = orderModel.getPaymentInfo();

        paymentInfo.setPaymentType(paymentInfoModel != null ? paymentInfoModel.getItemtype() : "");

        if (paymentInfoModel instanceof CreditCardPaymentInfoModel){
            paymentInfo.setPaymentType(((CreditCardPaymentInfoModel) paymentInfoModel).getType().toString());
        }

        paymentInfo.setStatus(orderModel.getPaymentStatus() != null ? orderModel.getPaymentStatus().toString() : "");
        paymentInfo.setAddress(getProfileAddressConverter().convert(orderModel.getPaymentAddress()));

        return paymentInfo;
    }


    protected ShipmentInfo getShipmentInfo(OrderModel orderModel){

        final ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setAddress(getProfileAddressConverter().convert(orderModel.getDeliveryAddress()));
        shipmentInfo.setStatus(orderModel.getDeliveryStatus() != null ? orderModel.getDeliveryStatus().getCode() : NOT_DELIVERED_STATUS);

        return shipmentInfo;
    }


    public Converter<UserModel, Consumer> getProfileConsumerConverter() {
        return profileConsumerConverter;
    }

    @Required
    public void setProfileConsumerConverter(Converter<UserModel, Consumer> profileConsumerConverter) {
        this.profileConsumerConverter = profileConsumerConverter;
    }

    public Converter<AbstractOrderEntryModel, OrderLineItem> getProfileOrderLineItemConverter() {
        return profileOrderLineItemConverter;
    }

    public Converter<AddressModel, Address> getProfileAddressConverter() {
        return profileAddressConverter;
    }

    @Required
    public void setProfileAddressConverter(Converter<AddressModel, Address> profileAddressConverter) {
        this.profileAddressConverter = profileAddressConverter;
    }

    @Required
    public void setProfileOrderLineItemConverter(Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter) {
        this.profileOrderLineItemConverter = profileOrderLineItemConverter;
    }

    public DefaultSessionTokenService getDefaultSessionTokenService() {
        return defaultSessionTokenService;
    }

    @Required
    public void setDefaultSessionTokenService(DefaultSessionTokenService defaultSessionTokenService) {
        this.defaultSessionTokenService = defaultSessionTokenService;
    }
}
