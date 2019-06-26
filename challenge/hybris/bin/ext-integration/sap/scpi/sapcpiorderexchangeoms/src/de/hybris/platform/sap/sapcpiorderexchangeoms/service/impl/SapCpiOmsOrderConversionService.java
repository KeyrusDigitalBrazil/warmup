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
package de.hybris.platform.sap.sapcpiorderexchangeoms.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.sapcpiadapter.data.*;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchangeoms.exceptions.SapCpiOmsOrderConversionServiceException;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SapCpiOmsOrderConversionService
 */
public class SapCpiOmsOrderConversionService extends SapCpiOmmOrderConversionService {

    private SapPlantLogSysOrgService sapPlantLogSysOrgService;
    private FlexibleSearchService flexibleSearchService;


    private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsOrderConversionService.class);

    @Override
    public SapCpiOrder convertOrderToSapCpiOrder(OrderModel orderModel) {

        SapCpiOrder sapCpiOmsOrder = new SapCpiOrder();

        getSapOrderContributor().createRows(orderModel).stream().findFirst().ifPresent(row -> {

            sapCpiOmsOrder.setSapCpiConfig(mapOrderConfigInfo(orderModel));

            sapCpiOmsOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiOmsOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
            sapCpiOmsOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
            sapCpiOmsOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
            sapCpiOmsOrder.setPaymentMode(mapAttribute(OrderCsvColumns.PAYMENT_MODE, row));
            sapCpiOmsOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));

            sapCpiOmsOrder.setSalesOrganization(mapAttribute(OrderCsvColumns.SALES_ORGANIZATION, row));
            sapCpiOmsOrder.setDistributionChannel(mapAttribute(OrderCsvColumns.DISTRIBUTION_CHANNEL, row));
            sapCpiOmsOrder.setChannel(mapAttribute(OrderCsvColumns.CHANNEL, row));
            sapCpiOmsOrder.setDivision(mapAttribute(OrderCsvColumns.DIVISION, row));

            sapCpiOmsOrder.setPurchaseOrderNumber(mapAttribute(OrderCsvColumns.PURCHASE_ORDER_NUMBER, row));
            sapCpiOmsOrder.setTransactionType(orderModel.getStore().getSAPConfiguration().getSapcommon_transactionType());

            orderModel.getStore()
                    .getSAPConfiguration()
                    .getSapDeliveryModes()
                    .stream()
                    .filter(entry -> entry.getDeliveryMode().getCode().contentEquals(orderModel.getDeliveryMode().getCode()))
                    .findFirst()
                    .ifPresent(entry -> sapCpiOmsOrder.setShippingCondition(entry.getDeliveryValue()));

            sapCpiOmsOrder.setSapCpiOrderItems(mapOrderItems(orderModel));
            sapCpiOmsOrder.setSapCpiPartnerRoles(mapOrderPartners(orderModel));
            sapCpiOmsOrder.setSapCpiOrderAddresses(mapOrderAddresses(orderModel));
            sapCpiOmsOrder.setSapCpiOrderPriceComponents(mapOrderPrices(orderModel));
            sapCpiOmsOrder.setSapCpiCreditCardPayments(mapCreditCards(orderModel));

        });

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("SCPI OMS order object: %n %s", ReflectionToStringBuilder.toString(sapCpiOmsOrder, new RecursiveToStringStyle())));
        }

        return sapCpiOmsOrder;
    }

    @Override
    protected SapCpiConfig mapOrderConfigInfo(OrderModel orderModel) {

        String plantCode = orderModel.getConsignments().stream().findFirst().get().getWarehouse().getCode();
        SAPLogicalSystemModel sapLogicalSystem = getSapPlantLogSysOrgService().getSapLogicalSystemForPlant(orderModel.getStore(), plantCode);

        SapCpiTargetSystem sapCpiTargetSystem = new SapCpiTargetSystem();

        sapCpiTargetSystem.setSenderName(sapLogicalSystem.getSenderName());
        sapCpiTargetSystem.setSenderPort(sapLogicalSystem.getSenderPort());

        sapCpiTargetSystem.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
        sapCpiTargetSystem.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());


        if (sapLogicalSystem.getSapHTTPDestination() != null) {

            String targetUrl = sapLogicalSystem.getSapHTTPDestination().getTargetURL();

            sapCpiTargetSystem.setUrl(targetUrl);
            sapCpiTargetSystem.setClient(targetUrl.split("sap-client=")[1].substring(0, 3));
            sapCpiTargetSystem.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());

        } else {

            logError(sapLogicalSystem.getSapLogicalSystemName());
        }

        SapCpiConfig sapCpiConfig = new SapCpiConfig();
        sapCpiConfig.setSapCpiTargetSystem(sapCpiTargetSystem);

        return sapCpiConfig;

    }

    @Override
    protected List<SapCpiOrderItem> mapOrderItems(OrderModel orderModel) {


        final List<SapCpiOrderItem> sapCpiOrderItems = new ArrayList<>();

        getSapOrderEntryContributor().createRows(orderModel).forEach(row -> {

            final SapCpiOrderItem sapCpiOrderItem = new SapCpiOrderItem();

            sapCpiOrderItem.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiOrderItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));
            sapCpiOrderItem.setQuantity(mapAttribute(OrderEntryCsvColumns.QUANTITY, row));
            sapCpiOrderItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE, row));
            sapCpiOrderItem.setUnit(mapAttribute(OrderEntryCsvColumns.ENTRY_UNIT_CODE, row));
            sapCpiOrderItem.setProductName(mapAttribute(OrderEntryCsvColumns.PRODUCT_NAME, row));

            sapCpiOrderItem.setNamedDeliveryDate(mapDateAttribute(OrderEntryCsvColumns.EXPECTED_SHIPPING_DATE, row));
            sapCpiOrderItem.setPlant(mapAttribute(OrderEntryCsvColumns.WAREHOUSE, row));
            sapCpiOrderItem.setItemCategory(mapAttribute(OrderEntryCsvColumns.ITEM_CATEGORY, row));

            sapCpiOrderItems.add(sapCpiOrderItem);

        });

        return sapCpiOrderItems;

    }

    @Override
    protected List<SapCpiPartnerRole> mapOrderPartners(OrderModel orderModel) {

        final List<SapCpiPartnerRole> sapCpiPartnerRoles = new ArrayList<>();

        getSapPartnerContributor().createRows(orderModel).forEach(row -> {

            SapCpiPartnerRole sapCpiPartnerRole = new SapCpiPartnerRole();

            sapCpiPartnerRole.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiPartnerRole.setDocumentAddressId(mapAttribute(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, row));
            sapCpiPartnerRole.setPartnerId(mapAttribute(PartnerCsvColumns.PARTNER_CODE, row));
            sapCpiPartnerRole.setPartnerRoleCode(mapAttribute(PartnerCsvColumns.PARTNER_ROLE_CODE, row));

            sapCpiPartnerRole.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));

            sapCpiPartnerRoles.add(sapCpiPartnerRole);

        });

        return sapCpiPartnerRoles;
    }

    @Override
    public List<SapCpiOrderCancellation> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel) {

        List<SapCpiOrderCancellation> sapCpiOrderCancellations = new ArrayList<>();

        getSapOrderCancelRequestContributor().createRows(orderCancelRecordEntryModel)
                .stream()
                .collect(Collectors.groupingBy(row -> row.get(OrderCsvColumns.ORDER_ID))).entrySet().forEach(

                rowSet -> {

                    final SapCpiOrderCancellation sapCpiOrderCancellation = new SapCpiOrderCancellation();
                    final List<SapCpiOrderCancellationItem> sapCpiOrderCancellationItems = new ArrayList<>();

                    rowSet.getValue().stream().findFirst().ifPresent(row -> {
                                sapCpiOrderCancellation.setRejectionReason(row.get(OrderEntryCsvColumns.REJECTION_REASON).toString());
                                sapCpiOrderCancellation.setOrderId(row.get(OrderCsvColumns.ORDER_ID).toString());
                                sapCpiOrderCancellation.setSapCpiConfig(mapOrderCancellationConfigInfo(row.get(OrderCsvColumns.LOGICAL_SYSTEM).toString()));
                            }
                    );

                    rowSet.getValue().stream().forEach(row -> {

                        SapCpiOrderCancellationItem sapCpiOrderCancellationItem = new SapCpiOrderCancellationItem();
                        sapCpiOrderCancellationItem.setEntryNumber(row.get(OrderEntryCsvColumns.ENTRY_NUMBER).toString());
                        sapCpiOrderCancellationItem.setProductCode(row.get(OrderEntryCsvColumns.PRODUCT_CODE).toString());
                        sapCpiOrderCancellationItems.add(sapCpiOrderCancellationItem);

                    });

                    sapCpiOrderCancellation.setSapCpiOrderCancellationItems(sapCpiOrderCancellationItems);

                    sapCpiOrderCancellations.add(sapCpiOrderCancellation);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(String.format("SCPI cancel order object: %n %s", ReflectionToStringBuilder.toString(sapCpiOrderCancellation, new RecursiveToStringStyle())));
                    }
                }
        );

        return sapCpiOrderCancellations;
    }


    protected SapCpiConfig mapOrderCancellationConfigInfo(String sapLogicalSystemName) {


        SAPLogicalSystemModel sapLogicalSystem = readSapLogicalSystem(sapLogicalSystemName);

        SapCpiTargetSystem sapCpiTargetSystem = new SapCpiTargetSystem();

        sapCpiTargetSystem.setSenderName(sapLogicalSystem.getSenderName());
        sapCpiTargetSystem.setSenderPort(sapLogicalSystem.getSenderPort());

        sapCpiTargetSystem.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
        sapCpiTargetSystem.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());


        if (sapLogicalSystem.getSapHTTPDestination() != null) {

            String targetUrl = sapLogicalSystem.getSapHTTPDestination().getTargetURL();

            sapCpiTargetSystem.setUrl(targetUrl);
            sapCpiTargetSystem.setClient(targetUrl.split("sap-client=")[1].substring(0, 3));
            sapCpiTargetSystem.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());

        } else {

            logError(sapLogicalSystemName);
        }

        SapCpiConfig sapCpiConfig = new SapCpiConfig();
        sapCpiConfig.setSapCpiTargetSystem(sapCpiTargetSystem);

        return sapCpiConfig;

    }


    protected SAPLogicalSystemModel readSapLogicalSystem(final String sapLogicalSystemName) {

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                "SELECT {o:pk} FROM {SAPLogicalSystem AS o} WHERE { o.sapLogicalSystemName} = ?sapLogicalSystemName");

        flexibleSearchQuery.addQueryParameter("sapLogicalSystemName", sapLogicalSystemName);

        final SAPLogicalSystemModel sapLogicalSystem = getFlexibleSearchService().searchUnique(flexibleSearchQuery);

        if (sapLogicalSystem == null) {
            logError(sapLogicalSystemName);
        }

        return sapLogicalSystem;
    }

    protected void logError(String sapLogicalSystemName) {
        String msg = String.format("Failed while retrieving the target system information for the logical system [%s]!", sapLogicalSystemName);
        LOG.error(msg);
        throw new SapCpiOmsOrderConversionServiceException(msg);
    }

    protected SapPlantLogSysOrgService getSapPlantLogSysOrgService() {
        return sapPlantLogSysOrgService;
    }

    @Required
    public void setSapPlantLogSysOrgService(SapPlantLogSysOrgService sapPlantLogSysOrgService) {
        this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
