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
package de.hybris.platform.cmsfacades.cmsitems;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;


/**
 * @deprecated since 6.7. Please use {@link de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider} instead.
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.7")
@FunctionalInterface
public interface AttributeStrategyConverterProvider extends de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider
{
}
