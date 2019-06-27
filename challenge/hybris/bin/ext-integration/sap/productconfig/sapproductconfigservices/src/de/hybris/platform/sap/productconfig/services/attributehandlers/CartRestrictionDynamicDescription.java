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
package de.hybris.platform.sap.productconfig.services.attributehandlers;

import de.hybris.platform.sap.productconfig.services.model.CMSCartConfigurationRestrictionModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;


/**
 * Description of Restriction we use for enabling specific cart CMS components. Based in service extension because we
 * need to have dependent modules influence them also when our frontend is not deployed.
 */
public class CartRestrictionDynamicDescription implements DynamicAttributeHandler<String, CMSCartConfigurationRestrictionModel>
{

	static final String DESCR_KEY = "type.CMSCartConfigurationRestriction.description.text";

	@Override
	public String get(final CMSCartConfigurationRestrictionModel arg0)
	{
		return getLocalizedText(DESCR_KEY);

	}

	protected String getLocalizedText(final String descrKey)
	{
		return Localization.getLocalizedString(descrKey);
	}

	@Override
	public void set(final CMSCartConfigurationRestrictionModel arg0, final String arg1)
	{
		throw new UnsupportedOperationException();
	}



}
