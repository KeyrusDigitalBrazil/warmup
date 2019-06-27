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
package de.hybris.platform.commercefacades.product.strategies.merge;

import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;

import java.util.List;

/**
 * An interface representing a strategy for merging two product configuration lists.
 */
public interface ProductConfigurationMergeStrategy {

    /**
     * Merges two product configuration lists
     *
     * @param firstConfiguration
     * 			the first list configuration to merge
     * @param secondConfiguration
     * 			the second list configuration to merge
     * @return the result of merging two product configuration item lists
     */
    List<ConfigurationInfoData> merge(final List<ConfigurationInfoData> firstConfiguration,
        final List<ConfigurationInfoData> secondConfiguration);
}
