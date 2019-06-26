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
package de.hybris.platform.commerceservices.spring.config;




/**
 * Processes all List Merge Directives in the visible application context. This bean is a bean post processor to ensure
 * it is initialised by the container prior to bean
 *
 * @deprecated since 6.0, use {@link de.hybris.platform.spring.config.ListMergeDirectiveBeanPostProcessor} instead
 */
@Deprecated
public class ListMergeDirectiveBeanPostProcessor extends de.hybris.platform.spring.config.ListMergeDirectiveBeanPostProcessor
{
	//deprecated
}
