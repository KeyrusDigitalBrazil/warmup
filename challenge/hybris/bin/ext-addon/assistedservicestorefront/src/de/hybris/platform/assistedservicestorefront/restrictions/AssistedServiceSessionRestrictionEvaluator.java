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
package de.hybris.platform.assistedservicestorefront.restrictions;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.cms2.model.restrictions.AssistedServiceSessionRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;

import org.springframework.beans.factory.annotation.Required;


/**
 * Evaluates an ASM agent through session.
 * <p/>
 *
 */
public class AssistedServiceSessionRestrictionEvaluator implements
		CMSRestrictionEvaluator<AssistedServiceSessionRestrictionModel>
{
	private AssistedServiceFacade defaultAssistedServiceFacade;

	@Override
	public boolean evaluate(final AssistedServiceSessionRestrictionModel amsSessionRestriction, final RestrictionData context)
	{
		return defaultAssistedServiceFacade.isAssistedServiceAgentLoggedIn();
	}

	@Required
	public void setDefaultAssistedServiceFacade(final AssistedServiceFacade defaultAssistedServiceFacade)
	{
		this.defaultAssistedServiceFacade = defaultAssistedServiceFacade;
	}
}
