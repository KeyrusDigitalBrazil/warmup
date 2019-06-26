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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.util.CSSClassResolver;


/**
 * Factory to get {@link CSSClassResolver} for CPQ.<br>
 * <i>beanName=sapProductConfigDefaultCssClassResolver</i><br>
 * Allows to dynamically exchange the implementation, if desired.
 */
public final class CSSClassResolverFactory

{
	private static CSSClassResolver resolver;

	private CSSClassResolverFactory()
	{
		// do not instantiate
	}

	/**
	 * setter to inject a resolver for testing
	 *
	 * @param resolver
	 */
	static void setResolver(final CSSClassResolver resolver)
	{
		CSSClassResolverFactory.resolver = resolver;
	}

	protected static CSSClassResolver getCSSClassResolver()
	{
		if (resolver == null)
		{
			resolver = (CSSClassResolver) Registry.getApplicationContext().getBean("sapProductConfigDefaultCssClassResolver");
		}
		return resolver;
	}

	/**
	 * @see CSSClassResolver#getGroupStyleClass(UiGroupData, boolean)
	 */
	public static String getStyleClassForGroup(final UiGroupData group, final Boolean hideExpandCollapse)
	{
		return getCSSClassResolver().getGroupStyleClass(group, hideExpandCollapse.booleanValue());
	}

	/**
	 * @see CSSClassResolver#getLabelStyleClass(CsticData)
	 */
	public static String getLabelStyleClassForCstic(final CsticData cstic)
	{
		return getCSSClassResolver().getLabelStyleClass(cstic);
	}

	/**
	 * @see CSSClassResolver#getValueStyleClass(CsticData)
	 */
	public static String getValueStyleClassForCstic(final CsticData cstic)
	{
		return getCSSClassResolver().getValueStyleClass(cstic);
	}

	/**
	 * @see CSSClassResolver#getValuePromoStyleClass(CsticData, CsticValueData)
	 */
	public static String getValuePromoStyleClass(final CsticData cstic, final CsticValueData value)
	{
		return getCSSClassResolver().getValuePromoStyleClass(cstic, value);
	}

	/**
	 * @see CSSClassResolver#getMenuNodeStyleClass(UiGroupData, Integer)
	 */
	public static String getMenuNodeStyleClass(final UiGroupData group, final Integer level)
	{
		return getCSSClassResolver().getMenuNodeStyleClass(group, level);
	}

	/**
	 * @see CSSClassResolver#getMenuConflictStyleClass(UiGroupData)
	 */
	public static String getMenuConflictStyleClass(final UiGroupData conflict)
	{
		return getCSSClassResolver().getMenuConflictStyleClass(conflict);
	}

	/**
	 * @see CSSClassResolver#getGroupStatusTooltipKey(UiGroupData)
	 */
	public static String getGroupStatusTooltipKey(final UiGroupData group)
	{
		return getCSSClassResolver().getGroupStatusTooltipKey(group);
	}

	/**
	 * @see CSSClassResolver#getMessageTextAdditionalStyleClass(ProductConfigMessageData)
	 */
	public static String getMessageTextAdditionalStyleClass(final ProductConfigMessageData message)
	{
		return getCSSClassResolver().getMessageTextAdditionalStyleClass(message);
	}

	/**
	 * @see CSSClassResolver#getExtendedMessageStyleClass(ProductConfigMessageData)
	 */
	public static String getExtendedMessageStyleClass(final ProductConfigMessageData message)
	{
		return getCSSClassResolver().getExtendedMessageStyleClass(message);
	}

	/**
	 * @see CSSClassResolver#getMessageIconStyleClass(ProductConfigMessageData)
	 */
	public static String getMessageIconStyleClass(final ProductConfigMessageData message)
	{
		return getCSSClassResolver().getMessageIconStyleClass(message);
	}
}
