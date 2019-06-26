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
package com.sap.hybris.sapcustomerb2b.inbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.sap.sapmodel.model.ReferenceDistributionChannelMappingModel;
import de.hybris.platform.sap.sapmodel.model.ReferenceDivisionMappingModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.Set;


/**
 * Enhancement of DefaultB2BUnitService to determine the sales area dependent B2BUnit
 */
public class DefaultSAPB2BUnitService extends DefaultB2BUnitService {

    private static final Logger LOGGER = Logger.getLogger(DefaultSAPB2BUnitService.class);
    private BaseStoreService baseStoreService;
    private FlexibleSearchService flexibleSearchService;

    /*
     * Gets recursively all members of type B2BUnitModel for given B2BUnit and all their sub B2BUnits; is the given
     * B2BUnit a sales area dependent B2BUnit this method gets all members of type B2BUnit for current B2BUnit's root
     * B2BUnit and their sub B2BUnit
     *
     * @see de.hybris.platform.b2b.services.impl.DefaultB2BUnitService#getBranch(
     * de.hybris.platform.b2b.model.B2BUnitModel)
     */
    @Override
    public Set<B2BUnitModel> getBranch(final B2BUnitModel unit) {

        final Set<B2BUnitModel> organizationSet = new HashSet<>();

        // check whether B2BUnit is a sales area dependent B2BUnit
        if (isSalesAreaDependentB2BUnit(unit)) {
            // determine branch (set of B2BUnit members) of sales area dependent B2BUnit's root B2BUnit
            this.getBranch(getRootUnit(unit), organizationSet);
        } else {
            this.getBranch(unit, organizationSet);
        }
        return organizationSet;
    }

    /**
     * Checks whether the current B2BUnit is a sales area dependent B2BUnit
     *
     * @param unit
     * @return true = B2BUnit is a sales area dependent B2BUnit; false B2BUnit is a sales area independent B2BUnit
     */
    protected boolean isSalesAreaDependentB2BUnit(final B2BUnitModel unit) {
        if (unit != null) {
            final String salesAreaSuffix = getSalesAreaSuffix();
            if (!salesAreaSuffix.isEmpty() && unit.getUid().endsWith(salesAreaSuffix)) {
                return true;
            }
        }

        return false;
    }

    /*
     * Gets the parent B2BUnit of B2BCustomer; if the base store has assigned a SAP base store configuration with a sales
     * area definition and the parent B2BUnit has a sub B2BUnit for this sales area as member the sales area dependent
     * sub B2BUnit will returned
     *
     * @see de.hybris.platform.b2b.services.impl.DefaultB2BUnitService#getParent(
     * de.hybris.platform.b2b.model.B2BCustomerModel)
     */
    @Override
    public B2BUnitModel getParent(final B2BCustomerModel b2bCustomer) {

        // retrieve parent B2BUnit (sales area independent)
        final B2BUnitModel parentB2BUnit = super.getParent(b2bCustomer);
        if (parentB2BUnit != null) {

            final String parentB2BUnitUID = parentB2BUnit.getUid();
            final String salesAreaSuffix = getSalesAreaSuffix();
            if (salesAreaSuffix.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No SAP Base Store Configuration with sale area definition assigned to current Base Store! Returns sales area independent B2B Unit with UID: ["
                            + parentB2BUnitUID + "]");
                }
                return parentB2BUnit;
            } else {

                // search for sales area dependent B2BUnit in members of parent B2BUnit
                final B2BUnitModel parentB2BUnitMember = getB2BUnitModel(parentB2BUnit, parentB2BUnitUID, salesAreaSuffix);

                if (parentB2BUnitMember != null) {
                    return parentB2BUnitMember;
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No sales area dependent B2B Unit with UID: [" + parentB2BUnitUID + salesAreaSuffix
                            + "] found! Returns sales area independent B2B Unit with UID: [" + parentB2BUnitUID + "]");
                }
            }

        }

        return parentB2BUnit;
    }

    private B2BUnitModel getB2BUnitModel(B2BUnitModel parentB2BUnit, String parentB2BUnitUID, String salesAreaSuffix) {
        final Set<PrincipalModel> parentB2BUnitMembers = parentB2BUnit.getMembers();
        if (parentB2BUnitMembers != null) {
            final String salesAreaDependentB2BUnitUID = parentB2BUnitUID + salesAreaSuffix;
            for (final PrincipalModel parentB2BUnitMember : parentB2BUnitMembers) {
                // check whether the member is a B2BUnitModel and the UID is equal to sales area dependent B2BUnit UID
                if (parentB2BUnitMember instanceof B2BUnitModel
                        && parentB2BUnitMember.getUid().equalsIgnoreCase(salesAreaDependentB2BUnitUID)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Returns sales area dependent B2B Unit with UID: [" + parentB2BUnitMember.getUid() + "]");
                    }
                    return (B2BUnitModel) parentB2BUnitMember;
                }
            }
        }
        return null;
    }

    /**
     * Gets the sales area suffix
     *
     * @return String sales area suffix
     */
    protected String getSalesAreaSuffix() {
        String salesAreaSuffix = "";

        // retrieve current base store
        final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
        if (currentBaseStore != null && currentBaseStore.getSAPConfiguration() != null) {
            // SALES ORG
            final String salesOrganization = currentBaseStore.getSAPConfiguration().getSapcommon_salesOrganization();
            // DISTRIBUTION CHANNEL
            String distributionChannel = currentBaseStore.getSAPConfiguration().getSapcommon_distributionChannel();
            // DIVISION
            String division = currentBaseStore.getSAPConfiguration().getSapcommon_division();
            // check whether no part of sales area is blank
            if (!StringUtils.isBlank(salesOrganization) && !StringUtils.isBlank(distributionChannel)
                    && !StringUtils.isBlank(division)) {
                // retrieve common distribution channel
                distributionChannel = getCommonDistributionChannel(salesOrganization, distributionChannel);
                // retrieve common division
                division = getCommonDivision(salesOrganization, division);
                // concatenate sales area suffix
                salesAreaSuffix = "_" + salesOrganization + "_" + distributionChannel + "_" + division;
            }
        }
        return salesAreaSuffix;
    }

    /**
     * Gets the common distribution channel
     *
     * @param salesOrganization
     * @param distributionChannel
     * @return String common distribution channel
     */
    protected String getCommonDistributionChannel(final String salesOrganization, final String distributionChannel) {
        final ReferenceDistributionChannelMappingModel example = new ReferenceDistributionChannelMappingModel();
        example.setSalesOrganization(salesOrganization);
        example.setDistChannel(distributionChannel);
        final ReferenceDistributionChannelMappingModel referenceDistributionChannel = flexibleSearchService
                .getModelByExample(example);
        if (referenceDistributionChannel != null) {
            return referenceDistributionChannel.getRefDistChannelCustMat();
        } else {
            return null;
        }
    }

    /**
     * Gets the common division
     *
     * @param salesOrganization
     * @param division
     * @return String common division
     */
    protected String getCommonDivision(final String salesOrganization, final String division) {
        final ReferenceDivisionMappingModel example = new ReferenceDivisionMappingModel();
        example.setSalesOrganization(salesOrganization);
        example.setDivision(division);
        final ReferenceDivisionMappingModel referenceDivision = flexibleSearchService.getModelByExample(example);
        if (referenceDivision != null) {
            return referenceDivision.getRefDivisionCustomer();
        } else {
            LOGGER.error(String.format("Missing configuration for sales organization [%s] and division [%s]!", salesOrganization, division));
            LOGGER.error("Check the item types SAPSalesOrganization, ReferenceDistributionChannelMapping, and ReferenceDivisionMapping for the missing sales organization and division!");
            return null;
        }
    }


    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }


}
