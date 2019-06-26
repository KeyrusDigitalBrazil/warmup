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
package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.data.*;
import de.hybris.platform.sap.sapcpiadapter.model.*;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SAP CPI OMM Order Mapper Service
 */
public class SapCpiOmmOrderMapperService implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel> {

  private SapCpiOrderConversionService sapCpiOrderConversionService;

  @Override
  public void map(OrderModel orderModel, SAPCpiOutboundOrderModel sapCpiOutboundOrderModel) {

    mapSapCpiOrderToSAPCpiOrderOutbound(getSapCpiOrderConversionService().convertOrderToSapCpiOrder(orderModel), sapCpiOutboundOrderModel);

  }

  protected void mapSapCpiOrderToSAPCpiOrderOutbound(SapCpiOrder sapCpiOrder, SAPCpiOutboundOrderModel sapCpiOutboundOrder) {

    sapCpiOutboundOrder.setOrderId(sapCpiOrder.getOrderId());
    sapCpiOutboundOrder.setBaseStoreUid(sapCpiOrder.getBaseStoreUid());
    sapCpiOutboundOrder.setCreationDate(sapCpiOrder.getCreationDate());
    sapCpiOutboundOrder.setCurrencyIsoCode(sapCpiOrder.getCurrencyIsoCode());
    sapCpiOutboundOrder.setPaymentMode(sapCpiOrder.getPaymentMode());
    sapCpiOutboundOrder.setDeliveryMode(sapCpiOrder.getDeliveryMode());
    sapCpiOutboundOrder.setChannel(sapCpiOrder.getChannel());
    sapCpiOutboundOrder.setPurchaseOrderNumber(sapCpiOrder.getPurchaseOrderNumber());
    sapCpiOutboundOrder.setTransactionType(sapCpiOrder.getTransactionType());
    sapCpiOutboundOrder.setSalesOrganization(sapCpiOrder.getSalesOrganization());
    sapCpiOutboundOrder.setDistributionChannel(sapCpiOrder.getDistributionChannel());
    sapCpiOutboundOrder.setDivision(sapCpiOrder.getDivision());
    sapCpiOutboundOrder.setShippingCondition(sapCpiOrder.getShippingCondition());

    sapCpiOutboundOrder.setSapCpiConfig(mapOrderConfigInfo(sapCpiOrder.getSapCpiConfig()));
    sapCpiOutboundOrder.setSapCpiOutboundOrderItems(mapOrderItems(sapCpiOrder.getSapCpiOrderItems()));
    sapCpiOutboundOrder.setSapCpiOutboundPartnerRoles(mapOrderPartners(sapCpiOrder.getSapCpiPartnerRoles()));
    sapCpiOutboundOrder.setSapCpiOutboundAddresses(mapOrderAddresses(sapCpiOrder.getSapCpiOrderAddresses()));
    sapCpiOutboundOrder.setSapCpiOutboundPriceComponents(mapOrderPrices(sapCpiOrder.getSapCpiOrderPriceComponents()));
    sapCpiOutboundOrder.setSapCpiOutboundCardPayments(mapCreditCards(sapCpiOrder.getSapCpiCreditCardPayments()));

  }


  protected SAPCpiOutboundConfigModel mapOrderConfigInfo(SapCpiConfig sapCpiConfig) {

    SapCpiTargetSystem sapCpiTargetSystem = sapCpiConfig.getSapCpiTargetSystem();

    SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();

    sapCpiOutboundConfig.setUrl(sapCpiTargetSystem.getUrl());
    sapCpiOutboundConfig.setUsername(sapCpiTargetSystem.getUsername());
    sapCpiOutboundConfig.setClient(sapCpiTargetSystem.getClient());

    sapCpiOutboundConfig.setSenderName(sapCpiTargetSystem.getSenderName());
    sapCpiOutboundConfig.setSenderPort(sapCpiTargetSystem.getSenderPort());

    sapCpiOutboundConfig.setReceiverName(sapCpiTargetSystem.getReceiverName());
    sapCpiOutboundConfig.setReceiverPort(sapCpiTargetSystem.getReceiverPort());

    return sapCpiOutboundConfig;

  }

  protected Set<SAPCpiOutboundOrderItemModel> mapOrderItems(List<SapCpiOrderItem> sapCpiOrderItems) {

    List<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItems = new ArrayList<>();

    sapCpiOrderItems.forEach(item -> {

      SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItem = new SAPCpiOutboundOrderItemModel();
      sapCpiOutboundOrderItem.setOrderId(item.getOrderId());
      sapCpiOutboundOrderItem.setEntryNumber(item.getEntryNumber());
      sapCpiOutboundOrderItem.setQuantity(item.getQuantity());
      sapCpiOutboundOrderItem.setCurrencyIsoCode(item.getCurrencyIsoCode());
      sapCpiOutboundOrderItem.setUnit(item.getUnit());
      sapCpiOutboundOrderItem.setProductCode(item.getProductCode());
      sapCpiOutboundOrderItem.setProductName(item.getProductName());
      sapCpiOutboundOrderItem.setPlant(item.getPlant());
      sapCpiOutboundOrderItem.setNamedDeliveryDate(item.getNamedDeliveryDate());
      sapCpiOutboundOrderItem.setItemCategory(item.getItemCategory());

      sapCpiOutboundOrderItems.add(sapCpiOutboundOrderItem);

    });

    return new HashSet<>(sapCpiOutboundOrderItems);

  }


  protected Set<SAPCpiOutboundPartnerRoleModel> mapOrderPartners(List<SapCpiPartnerRole> sapCpiPartnerRoles) {

    List<SAPCpiOutboundPartnerRoleModel> sapCpiOutboundPartnerRoles = new ArrayList<>();

    sapCpiPartnerRoles.forEach(partner -> {

      SAPCpiOutboundPartnerRoleModel sapCpiOutboundPartnerRole = new SAPCpiOutboundPartnerRoleModel();
      sapCpiOutboundPartnerRole.setOrderId(partner.getOrderId());
      sapCpiOutboundPartnerRole.setPartnerRoleCode(partner.getPartnerRoleCode());
      sapCpiOutboundPartnerRole.setPartnerId(partner.getPartnerId());
      sapCpiOutboundPartnerRole.setDocumentAddressId(partner.getDocumentAddressId());
      sapCpiOutboundPartnerRole.setEntryNumber(partner.getEntryNumber());

      sapCpiOutboundPartnerRoles.add(sapCpiOutboundPartnerRole);

    });

    return new HashSet<>(sapCpiOutboundPartnerRoles);

  }

  protected Set<SAPCpiOutboundAddressModel> mapOrderAddresses(List<SapCpiOrderAddress> sapCpiOrderAddresses) {

    List<SAPCpiOutboundAddressModel> sapCpiOutboundAddresses = new ArrayList<>();

    sapCpiOrderAddresses.forEach(address -> {

      SAPCpiOutboundAddressModel sapCpiOutboundAddress = new SAPCpiOutboundAddressModel();
      sapCpiOutboundAddress.setOrderId(address.getOrderId());
      sapCpiOutboundAddress.setDocumentAddressId(address.getDocumentAddressId());
      sapCpiOutboundAddress.setFirstName(address.getFirstName());
      sapCpiOutboundAddress.setLastName(address.getLastName());
      sapCpiOutboundAddress.setMiddleName(address.getMiddleName());
      sapCpiOutboundAddress.setMiddleName2(address.getMiddleName2());
      sapCpiOutboundAddress.setStreet(address.getStreet());
      sapCpiOutboundAddress.setCity(address.getCity());
      sapCpiOutboundAddress.setDistrict(address.getDistrict());
      sapCpiOutboundAddress.setBuilding(address.getBuilding());
      sapCpiOutboundAddress.setApartment(address.getApartment());
      sapCpiOutboundAddress.setPobox(address.getPobox());
      sapCpiOutboundAddress.setFaxNumber(address.getFaxNumber());
      sapCpiOutboundAddress.setTitleCode(address.getTitleCode());
      sapCpiOutboundAddress.setTelNumber(address.getTelNumber());
      sapCpiOutboundAddress.setHouseNumber(address.getHouseNumber());
      sapCpiOutboundAddress.setPostalCode(address.getPostalCode());
      sapCpiOutboundAddress.setRegionIsoCode(address.getRegionIsoCode());
      sapCpiOutboundAddress.setCountryIsoCode(address.getCountryIsoCode());
      sapCpiOutboundAddress.setEmail(address.getEmail());
      sapCpiOutboundAddress.setLanguageIsoCode(address.getLanguageIsoCode());

      sapCpiOutboundAddresses.add(sapCpiOutboundAddress);

    });

    return new HashSet<>(sapCpiOutboundAddresses);

  }

  protected Set<SAPCpiOutboundPriceComponentModel> mapOrderPrices(List<SapCpiOrderPriceComponent> sapCpiOrderPriceComponents) {

    List<SAPCpiOutboundPriceComponentModel> sapCpiOutboundPriceComponents = new ArrayList<>();

    sapCpiOrderPriceComponents.forEach(price -> {

      SAPCpiOutboundPriceComponentModel sapCpiOutboundPriceComponent = new SAPCpiOutboundPriceComponentModel();
      sapCpiOutboundPriceComponent.setOrderId(price.getOrderId());
      sapCpiOutboundPriceComponent.setEntryNumber(price.getEntryNumber());
      sapCpiOutboundPriceComponent.setValue(price.getValue());
      sapCpiOutboundPriceComponent.setUnit(price.getUnit());
      sapCpiOutboundPriceComponent.setAbsolute(price.getAbsolute());
      sapCpiOutboundPriceComponent.setConditionCode(price.getConditionCode());
      sapCpiOutboundPriceComponent.setConditionCounter(price.getConditionCounter());
      sapCpiOutboundPriceComponent.setCurrencyIsoCode(price.getCurrencyIsoCode());
      sapCpiOutboundPriceComponent.setPriceQuantity(price.getPriceQuantity());

      sapCpiOutboundPriceComponents.add(sapCpiOutboundPriceComponent);

    });

    return new HashSet<>(sapCpiOutboundPriceComponents);

  }

  protected Set<SAPCpiOutboundCardPaymentModel> mapCreditCards(List<SapCpiCreditCardPayment> sapCpiCreditCardPayments) {

    List<SAPCpiOutboundCardPaymentModel> sapCpiOutboundCardPayments = new ArrayList<>();

    sapCpiCreditCardPayments.forEach(payment -> {

      SAPCpiOutboundCardPaymentModel sapCpiOutboundCardPayment = new SAPCpiOutboundCardPaymentModel();
      sapCpiOutboundCardPayment.setOrderId(payment.getOrderId());
      sapCpiOutboundCardPayment.setRequestId(payment.getRequestId());
      sapCpiOutboundCardPayment.setCcOwner(payment.getCcOwner());
      sapCpiOutboundCardPayment.setValidToMonth(payment.getValidToMonth());
      sapCpiOutboundCardPayment.setValidToYear(payment.getValidToYear());
      sapCpiOutboundCardPayment.setSubscriptionId(payment.getSubscriptionId());
      sapCpiOutboundCardPayment.setPaymentProvider(payment.getPaymentProvider());

      sapCpiOutboundCardPayments.add(sapCpiOutboundCardPayment);

    });

    return new HashSet<>(sapCpiOutboundCardPayments);

  }

  protected SapCpiOrderConversionService getSapCpiOrderConversionService() {
    return sapCpiOrderConversionService;
  }

  @Required
  public void setSapCpiOrderConversionService(SapCpiOrderConversionService sapCpiOrderConversionService) {
    this.sapCpiOrderConversionService = sapCpiOrderConversionService;
  }

}
