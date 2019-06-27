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
package de.hybris.platform.sap.productconfig.frontend.util;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;


/**
 * CSS class resolver for CPQ. Used for tags, were the CSS class needs to be computed dynamically, based on dynamic
 * configuration attributes.
 */
public interface CSSClassResolver
{

	/**
	 * same as calling {@link #getGroupStyleClass(UiGroupData, boolean)} with hideExpandCollapse=false.
	 *
	 * @see #getGroupStyleClass(UiGroupData)
	 * @param group
	 *           group to check
	 * @return style class string, may contain many classes
	 */
	String getGroupStyleClass(UiGroupData group);

	/**
	 * Computes the label style based on the {@link CsticStatusType} of the corresponding cstic.<br>
	 * So for example one can style cstics with an error differently from ones without an error.
	 *
	 * @param cstic
	 *           cstic the label belongs to
	 * @return style class string, may contain many classes
	 */
	String getLabelStyleClass(CsticData cstic);

	/**
	 * Computes the style for a cstic based on the {@link CsticStatusType} of the corresponding cstic. Additionally the
	 * cstic type, for example whether the cstic is multi-valued or not may influence the style.
	 *
	 * @param cstic
	 *           cstic the value blongs to
	 * @return style class string, may contain many classes
	 */
	String getValueStyleClass(CsticData cstic);

	/**
	 * Computes the style for a cstic value based on the {@link CsticStatusType} of the corresponding cstic. Additionally
	 * the cstic value will be analysed if it is promo oppotunity or promo applied for the value.
	 *
	 * @param cstic
	 *           cstic to define a style
	 * @param value
	 *           cstic value to define a style
	 * @return style class string, may contain many classes
	 */
	String getValuePromoStyleClass(CsticData cstic, CsticValueData value);

	/**
	 * Computes the style for an ordinary node in the configuration menu based on several aspects:<br>
	 * <ul>
	 * <li>the level of the node in the menu hierarchy</li>
	 * <li>collapsed/expanded state of node</li>
	 * <li>whether the node is a leaf node or not</li>
	 * <li>{@link GroupStatusType} of the ui group represented by this node</lI>
	 * </ul>
	 * If the node is representing a conflict group, please use {@link #getMenuConflictStyleClass(UiGroupData)}
	 *
	 * @param group
	 *           ui group representing the node
	 * @param level
	 *           nesting level of the node in the menu hierarchy
	 * @return style class string, may contain many classes
	 */
	String getMenuNodeStyleClass(UiGroupData group, Integer level);

	/**
	 * Computes the group style based and the {@link GroupStatusType}. Optionally the expand/collapse state of the group
	 * is considered<br>
	 * So for example one can style an group with an error different from a group which is complete and consistent.
	 *
	 * @param group
	 *           group to check
	 * @param hideExpandCollapse
	 *           only if <code>false</code> the expand/collapse state of the group is considered
	 * @return style class string, may contain many classes
	 */
	String getGroupStyleClass(UiGroupData group, boolean hideExpandCollapse);

	/**
	 * Computes the style for an conflict node in the configuration menu.<br>
	 * If the node is representing an ordinary group, please use {@link #getMenuNodeStyleClass(UiGroupData, Integer)}
	 *
	 * @param conflict
	 *           conflicting ui group represented by this node
	 * @return style class string, may contain many classes
	 */
	String getMenuConflictStyleClass(UiGroupData conflict);

	/**
	 * Computes the resource key for the tool-tip to be associated with the group status
	 *
	 * @see #getGroupStyleClass(UiGroupData, boolean)
	 * @param group
	 *           group to check
	 * @return style class string, may contain many classes
	 */
	String getGroupStatusTooltipKey(UiGroupData group);


	/**
	 * Returns the additional style class for the message text if necessary.
	 *
	 * @param message
	 *           message to check
	 * @return additional style class for message text
	 */
	String getMessageTextAdditionalStyleClass(ProductConfigMessageData message);

	/**
	 * Returns the style class for the extended message. If message does not support extended message text the style
	 * class will be empty.
	 *
	 * @param message
	 *           message to check
	 * @return style class for extended message text
	 */
	String getExtendedMessageStyleClass(ProductConfigMessageData message);

	/**
	 * Returns the style class for the message icon. If message does not support message icon the style class will be
	 * empty.
	 *
	 * @param message
	 *           message to check
	 * @return style class for message icon
	 */
	String getMessageIconStyleClass(ProductConfigMessageData message);

}
