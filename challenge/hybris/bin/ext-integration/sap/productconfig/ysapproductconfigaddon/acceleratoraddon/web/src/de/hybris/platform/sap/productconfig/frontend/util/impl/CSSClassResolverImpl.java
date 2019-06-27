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


import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.frontend.util.CSSClassResolver;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


/**
 * Default implementation of {@link CSSClassResolver}
 */
public class CSSClassResolverImpl implements CSSClassResolver
{
	static final String STYLE_CLASS_SEPERATOR = " ";
	static final String STYLE_CLASS_CSTIC_LABEL = "cpq-csticlabel";
	static final String STYLE_CLASS_CSTIC_LABEL_ERROR = "cpq-csticlabel-error";
	static final String STYLE_CLASS_CSTIC_LABEL_SUCCESS = "cpq-csticlabel-success";
	static final String STYLE_CLASS_CSTIC_LABEL_WARNING = "cpq-csticlabel-warning";
	static final String STYLE_CLASS_CSTIC_LABEL_LONGTEXT = "cpq-csticlabel-longtext-icon";

	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE = "cpq-csticValue";
	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_ERROR = "cpq-csticValue-error";
	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_MULTI_LIST = "cpq-csticValue-multi";
	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY = "cpq-promo-opportunity";
	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY_EXTENDED = "cpq-promo-extended-opportunity-message";
	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED = "cpq-promo-applied";
	static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED_EXTENDED = "cpq-promo-extended-applied-message";
	static final String STYLE_CLASS_MESSAGE_SIGN_QUESTION = "cpq-message-question-sign";
	static final String STYLE_CLASS_MESSAGE_SIGN_INFO = "cpq-message-info-sign";

	static final String STYLE_CLASS_GROUP = "cpq-group-header";
	static final String STYLE_CLASS_GROUP_FINISHED = "cpq-group-completed";
	static final String STYLE_CLASS_GROUP_ERROR = "cpq-group-error";
	static final String STYLE_CLASS_GROUP_WARNING = "cpq-group-warning";
	static final String STYLE_CLASS_GROUP_CLOSE = "cpq-group-title-close";
	static final String STYLE_CLASS_GROUP_OPEN = "cpq-group-title-open";

	static final String STYLE_CLASS_MENU_NODE_EXPANDED = "cpq-menu-expanded";
	static final String STYLE_CLASS_MENU_NODE_COLLAPSED = "cpq-menu-collapsed";
	static final String STYLE_CLASS_MENU_NODE_ERROR = "cpq-menu-error";
	static final String STYLE_CLASS_MENU_NODE_WARNING = "cpq-menu-warning";
	static final String STYLE_CLASS_MENU_NODE_COMPLETED = "cpq-menu-completed";

	static final String STYLE_CLASS_MENU_LEVEL = "cpq-menu-level-";
	static final String STYLE_CLASS_MENU_NODE = "cpq-menu-node";
	static final String STYLE_CLASS_MENU_LEAF = "cpq-menu-leaf";
	static final String STYLE_CLASS_MENU_NON_CONF_LEAF = "cpq-menu-nonConfLeaf";

	static final String STYLE_MENU_CONFLICT_HEADER = "cpq-menu-conflict-header";
	static final String STYLE_MENU_CONFLICT_NODE = "cpq-menu-conflict-node";

	static final String STYLE_CLASS_MENU_NODE_CONFLICT = "cpq-menu-conflict";
	static final String STYLE_CLASS_CSTIC_LABEL_CONFLICT = "cpq-csticlabel-conflict";
	static final String STYLE_CLASS_GROUP_CONFLICT = "cpq-group-conflict";
	static final String STYLE_CLASS_CONFLICTGROUP = "cpq-conflictgroup";

	static final String RESOURCE_KEY_GROUP_ERROR_TOOLTIP = "sapproductconfig.group.tooltip.error";
	static final String RESOURCE_KEY_GROUP_FINISHED_TOOLTIP = "sapproductconfig.group.tooltip.finished";



	@Override
	public String getLabelStyleClass(final CsticData cstic)
	{
		String styleClassString = STYLE_CLASS_CSTIC_LABEL;
		switch (cstic.getCsticStatus())
		{
			case ERROR:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_ERROR);
				break;
			case WARNING:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_WARNING);
				break;
			case CONFLICT:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_CONFLICT);
				break;
			case FINISHED:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_SUCCESS);
				break;
			default:
				break;
		}

		return styleClassString;
	}


	@Override
	public String getValueStyleClass(final CsticData cstic)
	{
		String styleClassString = STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE;
		final CsticStatusType csticStatus = cstic.getCsticStatus();
		if (CsticStatusType.ERROR.equals(csticStatus) || CsticStatusType.WARNING.equals(csticStatus)
				|| CsticStatusType.CONFLICT.equals(csticStatus))
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_ERROR);
		}
		if (isMultiUiElementsType(cstic))
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_MULTI_LIST);
		}

		return styleClassString;
	}

	@Override
	public String getValuePromoStyleClass(final CsticData cstic, final CsticValueData value)
	{
		// style class used for the dropDownType.tag
		String styleClassString = "";

		if (value != null && value.getMessages() != null && !value.getMessages().isEmpty())
		{
			if (ProductConfigMessagePromoType.PROMO_OPPORTUNITY.equals(value.getMessages().get(0).getPromoType()))
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY);
			}
			else if (ProductConfigMessagePromoType.PROMO_APPLIED.equals(value.getMessages().get(0).getPromoType()))
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED);
			}
		}
		return styleClassString;
	}

	protected boolean isMultiUiElementsType(final CsticData cstic)
	{
		return cstic.getType() == UiType.CHECK_BOX_LIST || cstic.getType() == UiType.RADIO_BUTTON;
	}

	protected String appendStyleClass(final String styleClassString, final String styleClass)
	{
		if (styleClassString.isEmpty())
		{
			return styleClass;
		}

		return styleClassString + STYLE_CLASS_SEPERATOR + styleClass;
	}

	protected boolean isNullorEmptyList(final List<?> list)
	{
		return null == list || list.isEmpty();
	}

	@Override
	public String getGroupStyleClass(final UiGroupData group)
	{
		return getGroupStyleClass(group, false);
	}

	@Override
	public String getGroupStyleClass(final UiGroupData group, final boolean hideExpandCollapse)
	{
		String styleClassString = STYLE_CLASS_GROUP;
		if (GroupType.CONFLICT == group.getGroupType())
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CONFLICTGROUP);
		}
		else
		{
			switch (group.getGroupStatus())
			{
				case ERROR:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_ERROR);
					break;
				case WARNING:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_WARNING);
					break;
				case CONFLICT:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_CONFLICT);
					break;
				case FINISHED:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_FINISHED);
					break;
				default:
					break;
			}
		}
		if (!hideExpandCollapse)
		{
			if (group.isCollapsed())
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_CLOSE);
			}
			else
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_OPEN);
			}
		}

		return styleClassString;
	}

	@Override
	public String getMenuNodeStyleClass(final UiGroupData group, final Integer level)
	{
		String styleClassString = STYLE_CLASS_MENU_LEVEL + level.toString();
		final boolean showAsNode = !isNullorEmptyList(group.getSubGroups());
		final boolean showAsLeaf = GroupType.CSTIC_GROUP == group.getGroupType();

		if (showAsNode)
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE);
			if (group.isCollapsedInSpecificationTree())
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_COLLAPSED);
			}
			else
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_EXPANDED);
			}
		}
		else if (showAsLeaf)
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_LEAF);
		}
		else
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NON_CONF_LEAF);
		}

		if (group.isConfigurable())
		{
			switch (group.getGroupStatus())
			{
				case ERROR:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_ERROR);
					break;
				case WARNING:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_WARNING);
					break;
				case CONFLICT:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_CONFLICT);
					break;
				case FINISHED:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_COMPLETED);
					break;
				default:
					break;
			}
		}
		return styleClassString;
	}

	@Override
	public String getMenuConflictStyleClass(final UiGroupData conflict)
	{
		final boolean showAsConflictHeader = GroupType.CONFLICT_HEADER == conflict.getGroupType();
		String styleClassString = "";
		if (showAsConflictHeader)
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_MENU_CONFLICT_HEADER);
			if (conflict.isCollapsedInSpecificationTree())
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_COLLAPSED);
			}
			else
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_MENU_NODE_EXPANDED);
			}
		}
		else
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_MENU_CONFLICT_NODE);
		}
		return styleClassString;
	}

	@Override
	public String getGroupStatusTooltipKey(final UiGroupData group)
	{
		String groupStatusTooltipKey = "";
		switch (group.getGroupStatus())
		{
			case WARNING:
			case ERROR:
				groupStatusTooltipKey = RESOURCE_KEY_GROUP_ERROR_TOOLTIP;
				break;
			case FINISHED:
				groupStatusTooltipKey = RESOURCE_KEY_GROUP_FINISHED_TOOLTIP;
				break;
			default:
				break;
		}

		return groupStatusTooltipKey;
	}


	@Override
	public String getMessageTextAdditionalStyleClass(final ProductConfigMessageData message)
	{
		final ProductConfigMessagePromoType promoType = message.getPromoType();
		if (ProductConfigMessagePromoType.PROMO_APPLIED.equals(promoType))
		{
			return STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED;
		}
		else if (ProductConfigMessagePromoType.PROMO_OPPORTUNITY.equals(promoType))
		{
			return STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY;
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}


	@Override
	public String getExtendedMessageStyleClass(final ProductConfigMessageData message)
	{
		final ProductConfigMessagePromoType promoType = message.getPromoType();
		if (ProductConfigMessagePromoType.PROMO_APPLIED.equals(promoType))
		{
			return STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED_EXTENDED;
		}
		else if (ProductConfigMessagePromoType.PROMO_OPPORTUNITY.equals(promoType))
		{
			return STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY_EXTENDED;
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}


	@Override
	public String getMessageIconStyleClass(final ProductConfigMessageData message)
	{
		final ProductConfigMessagePromoType promoType = message.getPromoType();
		if (ProductConfigMessagePromoType.PROMO_APPLIED.equals(promoType)
				|| ProductConfigMessagePromoType.PROMO_OPPORTUNITY.equals(promoType))
		{
			return StringUtils.EMPTY;
		}
		else
		{
			if (ProductConfigMessageUISeverity.INFO.equals(message.getSeverity()))
			{
				return STYLE_CLASS_MESSAGE_SIGN_QUESTION;
			}
			else
			{
				return STYLE_CLASS_MESSAGE_SIGN_INFO;
			}
		}

	}
}
