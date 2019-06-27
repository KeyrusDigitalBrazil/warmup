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
package de.hybris.platform.sap.sapcpireturnsexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundPartnerRoleModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundPriceComponentModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderItemModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderPriceComponentModel;
import de.hybris.platform.sap.sapcpireturnsexchange.service.SapCpiReturnOutboundConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;

/**
 *
 */
public class SapCpiOmmReturnsOutboundConversionService implements SapCpiReturnOutboundConversionService {

    private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmmReturnsOutboundConversionService.class);

    private RawItemContributor<ReturnRequestModel> returnOrderSalesConditionsContributor;
    private RawItemContributor<ReturnRequestModel> precedingDocContributor;
    private RawItemContributor<ReturnRequestModel> returnOrderContributor;
    private RawItemContributor<ReturnRequestModel> returnOrderEntryContributor;
    private RawItemContributor<ReturnRequestModel> returnOrderPartnerContributor;
    private SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO;

    @Override
    public SAPCpiOutboundReturnOrderModel convertReturnOrderToSapCpiOutboundReturnOrder(
            final ReturnRequestModel returnRequest) {

        final SAPCpiOutboundReturnOrderModel sapCpiOutboundReturnOrder = new SAPCpiOutboundReturnOrderModel();

        returnOrderContributor.createRows(returnRequest).stream().findFirst().ifPresent(row -> {

            sapCpiOutboundReturnOrder.setSapCpiConfig(mapReturnOrderConfigInfo(returnRequest));
            precedingDocContributor.createRows(returnRequest).stream().findFirst().ifPresent(docRow -> {
                sapCpiOutboundReturnOrder.setPreceedingDocumentId(
                        mapAttribute(ReturnOrderEntryCsvColumns.PRECEDING_DOCUMENT_ID, docRow));
            });
            sapCpiOutboundReturnOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiOutboundReturnOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
            sapCpiOutboundReturnOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
            sapCpiOutboundReturnOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
            sapCpiOutboundReturnOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));

            sapCpiOutboundReturnOrder.setSapCpiOutboundPriceComponents(mapReturnOrderPrices(returnRequest));
            sapCpiOutboundReturnOrder.setSapCpiOutboundOrderItems(mapReturnOrderItems(returnRequest));
            sapCpiOutboundReturnOrder.setSapCpiOutboundPartnerRoles(mapReturnOrderPartners(returnRequest));

        });

        final SAPConfigurationModel storeConfigurationModel = getSAPConfig(returnRequest.getOrder());
        if (storeConfigurationModel != null) {
            sapCpiOutboundReturnOrder.setDivision(storeConfigurationModel.getSapcommon_division());
            sapCpiOutboundReturnOrder
                    .setDistributionChannel(storeConfigurationModel.getSapcommon_distributionChannel());
            sapCpiOutboundReturnOrder.setSalesOrganization(storeConfigurationModel.getSapcommon_salesOrganization());
        }
        return sapCpiOutboundReturnOrder;
    }

    private SAPConfigurationModel getSAPConfig(final OrderModel order) {
        if (order != null && order.getStore() != null) {
            return order.getStore().getSAPConfiguration();
        }
        return null;
    }

    protected Set<SAPCpiOutboundPriceComponentModel> mapReturnOrderPrices(final ReturnRequestModel requestRequest) {

        final List<SAPCpiOutboundPriceComponentModel> sapCpiReturnOrderPriceComponents = new ArrayList<>();

        returnOrderSalesConditionsContributor.createRows(requestRequest).forEach(row -> {

            final SAPCpiOutboundReturnOrderPriceComponentModel sapCpiOrderPriceComponent = new SAPCpiOutboundReturnOrderPriceComponentModel();

            sapCpiOrderPriceComponent.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiOrderPriceComponent
                    .setEntryNumber(mapAttribute(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, row));
            sapCpiOrderPriceComponent.setConditionCode(mapAttribute(SalesConditionCsvColumns.CONDITION_CODE, row));
            sapCpiOrderPriceComponent
                    .setConditionCounter(mapAttribute(SalesConditionCsvColumns.CONDITION_COUNTER, row));
            sapCpiOrderPriceComponent
                    .setCurrencyIsoCode(mapAttribute(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, row));
            sapCpiOrderPriceComponent
                    .setPriceQuantity(mapAttribute(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, row));
            sapCpiOrderPriceComponent.setUnit(mapAttribute(SalesConditionCsvColumns.CONDITION_UNIT_CODE, row));
            sapCpiOrderPriceComponent.setValue(mapAttribute(SalesConditionCsvColumns.CONDITION_VALUE, row));
            sapCpiOrderPriceComponent.setAbsolute(mapAttribute(SalesConditionCsvColumns.ABSOLUTE, row));

            sapCpiReturnOrderPriceComponents.add(sapCpiOrderPriceComponent);

        });

        return new HashSet<>(sapCpiReturnOrderPriceComponents);

    }

    protected Set<SAPCpiOutboundOrderItemModel> mapReturnOrderItems(final ReturnRequestModel returnRequest) {

        final List<SAPCpiOutboundOrderItemModel> sapCpiOrderItems = new ArrayList<>();

        returnOrderEntryContributor.createRows(returnRequest).forEach(row -> {

            final SAPCpiOutboundReturnOrderItemModel sapCpiOrderItem = new SAPCpiOutboundReturnOrderItemModel();

            sapCpiOrderItem.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiOrderItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));
            sapCpiOrderItem.setQuantity(mapAttribute(OrderEntryCsvColumns.QUANTITY, row));
            sapCpiOrderItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE, row));
            sapCpiOrderItem.setUnit(mapAttribute(OrderEntryCsvColumns.ENTRY_UNIT_CODE, row));
            sapCpiOrderItem.setWarehouse(mapAttribute(ReturnOrderEntryCsvColumns.WAREHOUSE, row));
            sapCpiOrderItem.setRejectionReason(mapAttribute(OrderEntryCsvColumns.REJECTION_REASON, row));
            sapCpiOrderItem.setProductName(mapAttribute(OrderEntryCsvColumns.PRODUCT_NAME, row));
            sapCpiOrderItem.setCancellationCode(
                    mapAttribute(ReturnOrderEntryCsvColumns.REASON_CODE_FOR_RETURN_CANCELLATION, row));
            sapCpiOrderItems.add(sapCpiOrderItem);

        });

        return new HashSet<>(sapCpiOrderItems);

    }

    protected Set<SAPCpiOutboundPartnerRoleModel> mapReturnOrderPartners(final ReturnRequestModel returnRequest) {

        final List<SAPCpiOutboundPartnerRoleModel> sapCpiPartnerRoles = new ArrayList<>();

        returnOrderPartnerContributor.createRows(returnRequest).forEach(row -> {

            final SAPCpiOutboundPartnerRoleModel sapCpiPartnerRole = new SAPCpiOutboundPartnerRoleModel();

            sapCpiPartnerRole.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            sapCpiPartnerRole.setDocumentAddressId(mapAttribute(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, row));
            sapCpiPartnerRole.setPartnerId(mapAttribute(PartnerCsvColumns.PARTNER_CODE, row));
            sapCpiPartnerRole.setPartnerRoleCode(mapAttribute(PartnerCsvColumns.PARTNER_ROLE_CODE, row));
            sapCpiPartnerRole.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));

            sapCpiPartnerRoles.add(sapCpiPartnerRole);

        });

        return new HashSet<>(sapCpiPartnerRoles);
    }

    protected SAPCpiOutboundConfigModel mapReturnOrderConfigInfo(final ReturnRequestModel returnRequest) {

        final SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();

        final Optional<SAPLogicalSystemModel> sapLogicalSystemOptional = getSapCoreSAPGlobalConfigurationDAO()
                .getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig().stream()
                .filter(logSys -> logSys.isDefaultLogicalSystem()).findFirst();

        if (sapLogicalSystemOptional.isPresent()) {

            final SAPLogicalSystemModel sapLogicalSystem = sapLogicalSystemOptional.get();

            sapCpiOutboundConfig.setSenderName(sapLogicalSystem.getSenderName());
            sapCpiOutboundConfig.setSenderPort(sapLogicalSystem.getSenderPort());

            sapCpiOutboundConfig.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
            sapCpiOutboundConfig.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());

            if (sapLogicalSystem.getSapHTTPDestination() != null) {

                final String targetUrl = sapLogicalSystem.getSapHTTPDestination().getTargetURL();

                sapCpiOutboundConfig.setUrl(targetUrl);
                sapCpiOutboundConfig.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());
                sapCpiOutboundConfig.setClient(targetUrl.split("sap-client=")[1].substring(0, 3));

            } else {

                final String msg = String.format(
                        "Error occurs while reading the http destination system information for the return request [%s]!",
                        returnRequest.getCode());
                LOG.error(msg);
                throw new RuntimeException(msg);
            }

        } else {

            final String msg = String.format(
                    "Error occurs while reading the default logical system information for the order [%s]!",
                    returnRequest.getCode());
            LOG.error(msg);
            throw new RuntimeException(msg);

        }

        return sapCpiOutboundConfig;

    }

    protected String mapAttribute(final String attribute, final Map<String, Object> row) {
        return row.get(attribute) != null ? row.get(attribute).toString() : null;
    }

    protected String mapDateAttribute(final String attribute, final Map<String, Object> row) {

        if (row.get(attribute) != null && row.get(attribute) instanceof Date) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format((Date) row.get(attribute));
        }

        return null;
    }

    protected RawItemContributor<ReturnRequestModel> getReturnOrderSalesConditionsContributor() {
        return returnOrderSalesConditionsContributor;
    }

    @Required
    public void setReturnOrderSalesConditionsContributor(
            final RawItemContributor<ReturnRequestModel> returnOrderSalesConditionsContributor) {
        this.returnOrderSalesConditionsContributor = returnOrderSalesConditionsContributor;
    }

    protected RawItemContributor<ReturnRequestModel> getPrecedingDocContributor() {
        return precedingDocContributor;
    }

    @Required
    public void setPrecedingDocContributor(final RawItemContributor<ReturnRequestModel> precedingDocContributor) {
        this.precedingDocContributor = precedingDocContributor;
    }

    protected RawItemContributor<ReturnRequestModel> getReturnOrderContributor() {
        return returnOrderContributor;
    }

    @Required
    public void setReturnOrderContributor(final RawItemContributor<ReturnRequestModel> returnOrderContributor) {
        this.returnOrderContributor = returnOrderContributor;
    }

    protected RawItemContributor<ReturnRequestModel> getReturnOrderEntryContributor() {
        return returnOrderEntryContributor;
    }

    @Required
    public void setReturnOrderEntryContributor(
            final RawItemContributor<ReturnRequestModel> returnOrderEntryContributor) {
        this.returnOrderEntryContributor = returnOrderEntryContributor;
    }

    protected RawItemContributor<ReturnRequestModel> getReturnOrderPartnerContributor() {
        return returnOrderPartnerContributor;
    }

    @Required
    public void setReturnOrderPartnerContributor(
            final RawItemContributor<ReturnRequestModel> returnOrderPartnerContributor) {
        this.returnOrderPartnerContributor = returnOrderPartnerContributor;
    }

    protected SAPGlobalConfigurationDAO getSapCoreSAPGlobalConfigurationDAO() {
        return sapCoreSAPGlobalConfigurationDAO;
    }

    @Required
    public void setSapCoreSAPGlobalConfigurationDAO(final SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO) {
        this.sapCoreSAPGlobalConfigurationDAO = sapCoreSAPGlobalConfigurationDAO;
    }

}
