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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Responsible to populate characteristics
 */
public class PossibleValueMasterDataPopulator implements ContextualPopulator<CPSPossibleValue, CsticValueModel, MasterDataContext>
{
	private MasterDataContainerResolver masterDataResolver;

	@Override
	public void populate(final CPSPossibleValue source, final CsticValueModel target, final MasterDataContext ctxt)
	{
		final String valueString = source.getValueLow();
		if (valueString != null)
		{
			final CPSMasterDataKnowledgeBaseContainer kbCacheContainer = ctxt.getKbCacheContainer();
			final String characteristicId = source.getParentCharacteristic().getId();
			target.setLanguageDependentName(getMasterDataResolver().getValueName(kbCacheContainer, characteristicId, valueString));
			target.setNumeric(getMasterDataResolver().isCharacteristicNumeric(kbCacheContainer, characteristicId));
		}

	}

	protected MasterDataContainerResolver getMasterDataResolver()
	{
		return masterDataResolver;
	}

	@Required
	public void setMasterDataResolver(final MasterDataContainerResolver masterDataResolver)
	{
		this.masterDataResolver = masterDataResolver;
	}

}
