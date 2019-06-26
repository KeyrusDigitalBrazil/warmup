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
 * List Merge Directives allow an AddOn to merge additional elements into spring-bean lists AND list properties on
 * Spring Beans. The minimal property to set is the add. This will append to the end of the list and is preferable to
 * using the list merge feature of spring since you are not required to extend and re-alias the original list bean
 * meaning the AddOns changes are more isolated from the AddOns. However, the directive also supports the ability to
 * insert the bean before or after a specified list element bean definition or bean class. List Merge Directive bean
 * definitions must also include a depends-on qualifier which should be the list-bean or the bean enclosing the list
 * property.
 *
 * @deprecated since 6.0, use {@link de.hybris.platform.spring.config.ListMergeDirective} instead
 */
@Deprecated
public class ListMergeDirective extends de.hybris.platform.spring.config.ListMergeDirective
{
	//deprecated
}
