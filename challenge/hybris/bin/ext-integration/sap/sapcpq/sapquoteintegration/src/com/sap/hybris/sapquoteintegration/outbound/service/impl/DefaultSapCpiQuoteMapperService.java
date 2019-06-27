/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapquoteintegration.constants.SapquoteintegrationConstants;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteCustomerModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteMapperService;

/**
 *
 */
public class DefaultSapCpiQuoteMapperService implements SapCpiQuoteMapperService<QuoteModel, SAPCpiOutboundQuoteModel> {

    protected static final Logger LOG = Logger.getLogger(DefaultSapCpiQuoteMapperService.class);
    private static final String MESSAGE_FIRED = "messageFired";
    protected static final String PROMOTION_PREFX = "Commerce promotion description : ";
    private ConfigurationService configurationService;
    private CustomerNameStrategy customerNameStrategy;
    private PromotionsService promotionsService;
    private QuoteService quoteService;

    @Override
    public void map(final QuoteModel quoteModel, final SAPCpiOutboundQuoteModel scpiQuoteModel) {
        mapQuoteToSapCpiOutboundQuote(quoteModel, scpiQuoteModel);
    }

    /**
     *
     */
    protected SAPCpiOutboundQuoteModel mapQuoteToSapCpiOutboundQuote(final QuoteModel quoteModel,
            final SAPCpiOutboundQuoteModel scpiQuoteModel) {
        scpiQuoteModel.setBaseStoreUid(quoteModel.getStore().getUid());
        scpiQuoteModel.setQuoteStatus(quoteModel.getState().toString());
        scpiQuoteModel.setCreationDate(quoteModel.getCreationtime().toString());
        scpiQuoteModel.setCurrencyIsoCode(quoteModel.getCurrency().getSapCode());
        scpiQuoteModel.setQuoteId(quoteModel.getCode());
        scpiQuoteModel.setVersion(quoteModel.getVersion().toString());
        scpiQuoteModel
                .setPromotionSummary(createPromotionSummary(getPromotionsService().getPromotionResults(quoteModel)));
        scpiQuoteModel.setSapCpiOutboundQuoteCustomers(mapQuoteCustomers((B2BCustomerModel) quoteModel.getUser()));
        SAPConfigurationModel sapConfiguration = quoteModel.getStore().getSAPConfiguration();
        scpiQuoteModel.setSalesOrganization(sapConfiguration.getSapcommon_salesOrganization());
        scpiQuoteModel.setDistributionChannel(sapConfiguration.getSapcommon_distributionChannel());
        scpiQuoteModel.setDivison(sapConfiguration.getSapcommon_division());
        
        return scpiQuoteModel;
    }

    /**
     *
     */
    protected String createPromotionSummary(final PromotionOrderResults promotionResults) {
        if (promotionResults != null) {
            String promotions = appendPromotions(promotionResults.getAppliedOrderPromotions());
            promotions = promotions + appendPromotions(promotionResults.getAppliedProductPromotions());

            return promotions.length() == 0 ? PROMOTION_PREFX
                    : PROMOTION_PREFX + promotions.substring(0, promotions.length() - 1);
        }
        return null;
    }

    protected String appendPromotions(final List<PromotionResult> promotions) {
        if (promotions != null) {
            final StringBuilder promotionSummary = new StringBuilder();
            promotions.stream().forEach(promotion -> {
                if(promotion.getLocalizedProperty(MESSAGE_FIRED) != null) {
                    promotionSummary.append(promotion.getLocalizedProperty(MESSAGE_FIRED).toString()).append(";");
                }
            });
            return promotionSummary.toString();
        }
        return "";
    }

    /**
     *
     */
    protected Set<SAPCpiOutboundQuoteCustomerModel> mapQuoteCustomers(final B2BCustomerModel customerModel) {
        final Set<SAPCpiOutboundQuoteCustomerModel> customers = new HashSet<>();
        customers.add(mapCustomer(customerModel));

        return customers;
    }

    /**
     *
     */
    protected SAPCpiOutboundQuoteCustomerModel mapCustomer(final B2BCustomerModel customerModel) {

        Collection<AddressModel> addresses = customerModel.getAddresses();
        AddressModel address = addresses != null && !addresses.isEmpty() ? getValidAddress(addresses) : null;

        if (address == null) {
            addresses = customerModel.getDefaultB2BUnit().getAddresses();
            address = getValidAddress(addresses);
        }

        validateParameterNotNullStandardMessage(CustomerModel.NAME, customerModel.getName());

        final SAPCpiOutboundQuoteCustomerModel customer = new SAPCpiOutboundQuoteCustomerModel();

        final String[] name = getCustomerNameStrategy().splitName(customerModel.getName());
        customer.setFirstName(name[0]);
        customer.setLastName(name[1]);
        customer.setCustomerId(customerModel.getCustomerID());
        customer.setEmail(customerModel.getUid());
        customer.setCustomerRoleCode(getConfigurationService().getConfiguration()
                .getString(SapquoteintegrationConstants.CUSTOMER_ROLE_CODE));

        if (customerModel.getDefaultB2BUnit() != null) {
            customer.setB2bUnitName(customerModel.getDefaultB2BUnit().getName());
        }
        
        if(address!=null) {
            populateAddress(customer,address);
        }
        return customer;
    }

    protected void populateAddress(SAPCpiOutboundQuoteCustomerModel customer, AddressModel address) {
        
        if (address.getLine1() != null) {
            customer.setLine1(address.getLine1());
        }
        if (address.getTown() != null) {
            customer.setCity(address.getTown());
        }
        if (address.getCountry() != null) {
            customer.setCountry(address.getCountry().getIsocode());
        }
        if (address.getPostalcode() != null) {
            customer.setPostalcode(address.getPostalcode());
        }

        if (address.getLine2() != null) {
            customer.setLine2(address.getLine2());
        }

        if (address.getFax() != null) {
            customer.setFax(address.getFax());
        }

        if (address.getCellphone() != null || address.getPhone1() != null) {
            customer.setPhoneNumber(address.getCellphone() != null ? address.getCellphone() : address.getPhone1());
        }
    }

    protected AddressModel getValidAddress(final Collection<AddressModel> addresses) {

        AddressModel address = null;
        for (final AddressModel a : addresses) {
            if (a != null) {
                address = a;
                break;
            }
        }
        return address;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CustomerNameStrategy getCustomerNameStrategy() {
        return customerNameStrategy;
    }

    @Required
    public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy) {
        this.customerNameStrategy = customerNameStrategy;
    }

    public PromotionsService getPromotionsService() {
        return promotionsService;
    }

    @Required
    public void setPromotionsService(final PromotionsService promotionsService) {
        this.promotionsService = promotionsService;
    }

    public QuoteService getQuoteService() {
        return quoteService;
    }

    @Required
    public void setQuoteService(final QuoteService quoteService) {
        this.quoteService = quoteService;
    }

}
