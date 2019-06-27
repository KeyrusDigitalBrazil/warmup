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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.List;
import java.util.Map;


/**
 * Provide required logic to access the characteristic / values names from hybris classification system
 */
public interface ClassificationSystemCPQAttributesProvider
{
	/**
	 * Get CPQ related attributes for the given cstic from the hybris classification system. It is recommended to provide
	 * a nameMap, which should be cached at least on session level, so that the results can be cached in the map and so
	 * that there is no need to query CPQ attributes on each invocation.
	 *
	 * @param name
	 *           cstic name
	 * @param nameMap
	 *           to cache results
	 * @return CPQ attribute container
	 */
	ClassificationSystemCPQAttributesContainer getCPQAttributes(String name,
			Map<String, ClassificationSystemCPQAttributesContainer> nameMap);

	/**
	 * returns the display name for the <b>cstic</b>. In case a display name is present in the CPQ attribute container it
	 * will be returned, otherwise the display name is extracted from the cstic model.
	 *
	 * @param csticModel
	 *           model of the cstic
	 * @param hybrisNames
	 *           CPQ attribute container with hybris cstic name
	 * @param isDebugEnabled
	 *           only if <code>true</code>, debug logs will be written
	 * @return cstic name to be displayed
	 */
	String getDisplayName(final CsticModel csticModel, final ClassificationSystemCPQAttributesContainer hybrisNames,
			final boolean isDebugEnabled);

	/**
	 * returns the display name for the <b>cstic value</b>. In case a display name is present in the CPQ attribute
	 * container it will be returned, otherwise the display name is extracted from the cstic value model.
	 *
	 * @param valueModel
	 *           value model of the cstic value
	 * @param csticModel
	 *           model of the cstic
	 * @param hybrisNames
	 *           CPQ attribute container with hybris cstic value name
	 * @param isDebugEnabled
	 *           only if <code>true</code>, debug logs will be written
	 * @return cstic name to be displayed
	 */
	String getDisplayValueName(final CsticValueModel valueModel, CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer hybrisNames, final boolean isDebugEnabled);

	/**
	 * returns the display name for the <b>cstic value in context of the overview page</b>. In case a display name is
	 * present in the CPQ attribute container it will be returned, otherwise the display name is extracted from the cstic
	 * value model.
	 *
	 * @param valueModel
	 *           value model of the cstic value
	 * @param csticModel
	 *           model of the cstic
	 * @param hybrisNames
	 *           CPQ attribute container with hybris cstic value name
	 * @param isDebugEnabled
	 *           only if <code>true</code>, debug logs will be written
	 * @return cstic name to be displayed on the overview page
	 */
	String getOverviewValueName(final CsticValueModel valueModel, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer hybrisNames, final boolean isDebugEnabled);

	/**
	 * returns the <b>long text</b> for the cstic. In case a long text is present in the CPQ attribute container it will
	 * be returned, otherwise the long text is extracted from the cstic model.
	 *
	 * @param model
	 *           model of the cstic
	 * @param hybrisNames
	 *           CPQ attribute container with hybris long text
	 * @param isDebugEnabled
	 *           only if <code>true</code>, debug logs will be written
	 * @return cstic name to be displayed
	 */
	String getLongText(final CsticModel model, final ClassificationSystemCPQAttributesContainer hybrisNames,
			final boolean isDebugEnabled);

	/**
	 * extracts all media assigned to this <b>cstic</b> from the CPQ hybris attribute container. The configuration engine
	 * model does not contain any media mappings, so this can only be maintained in hybris.
	 *
	 * @param cpqAttributes
	 *           CPQ attribute container with hybris media
	 * @return list of medias
	 */
	List<ImageData> getCsticMedia(final ClassificationSystemCPQAttributesContainer cpqAttributes);

	/**
	 * extracts all media assigned to this <b>cstic value</b> from the CPQ hybris attribute container. The configuration
	 * engine model does not contain any media mappings, so this can only be maintained in hybris.
	 *
	 * @param csticValueKey
	 *           key of the cstic value
	 * @param cpqAttributes
	 *           CPQ attribute container with hybris media
	 * @return list of medias
	 */
	List<ImageData> getCsticValueMedia(final String csticValueKey, final ClassificationSystemCPQAttributesContainer cpqAttributes);

	/**
	 * @return <code>true</code> only if debug log is enabled for the attribute container
	 */
	boolean isDebugEnabled();

	/**
	 * returns the <b>long text</b> for a cstic value. In case a long text is present in the CPQ attribute container it
	 * will be returned, otherwise the long text is extracted from the cstic value model.
	 *
	 * @param csticModel
	 *           model of the cstic
	 * @param csticValueModel
	 *           value model of the cstic
	 * @param hybrisNames
	 *           CPQ attribute container with hybris long text
	 * @param isDebugEnabled
	 *           only if <code>true</code>, debug logs will be written
	 * @return cstic name to be displayed
	 */
	String getValueLongText(CsticValueModel csticValueModel, CsticModel csticModel,
			ClassificationSystemCPQAttributesContainer hybrisNames, boolean isDebugEnabledNameProvider);

}
