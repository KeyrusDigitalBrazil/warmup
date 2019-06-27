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
/**
 *
 */
package de.hybris.platform.assistedservicepromotionfacades.data.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicepromotionfacades.AssistedServicePromotionFacade;
import de.hybris.platform.assistedservicepromotionfacades.constants.AssistedservicepromotionfacadesConstants;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSAPromoData;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * @author CSAPromotionsModelProvider
 *
 */
public class CustomerPromotionsModelProvider implements FragmentModelProvider<List<CSAPromoData>>
{
	private AssistedServicePromotionFacade assistedServicePromotionFacade;
	private Converter<AbstractRuleModel, CSAPromoData> csaPromoDataConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider#getModel(java.util.Map)
	 */
	@Override
	public List<CSAPromoData> getModel(final Map<String, String> parameters)
	{
		final String searchKeyword = Config.getString(AssistedservicepromotionfacadesConstants.PROMOTION_SEARCH_PREFIX_KEY,
				AssistedservicepromotionfacadesConstants.PROMOTION_SEARCH_PREFIX);
		return getCsaPromoDataConverter().convertAll(getAssistedServicePromotionFacade().getCustomerPromotions(searchKeyword));
	}

	/**
	 * @return the assistedServicePromotionFacade
	 */
	protected AssistedServicePromotionFacade getAssistedServicePromotionFacade()
	{
		return assistedServicePromotionFacade;
	}

	/**
	 * @param assistedServicePromotionFacade
	 *           the assistedServicePromotionFacade to set
	 */
	@Required
	public void setAssistedServicePromotionFacade(final AssistedServicePromotionFacade assistedServicePromotionFacade)
	{
		this.assistedServicePromotionFacade = assistedServicePromotionFacade;
	}

	/**
	 * @return the csaPromoDataConverter
	 */
	protected Converter<AbstractRuleModel, CSAPromoData> getCsaPromoDataConverter()
	{
		return csaPromoDataConverter;
	}

	/**
	 * @param csaPromoDataConverter
	 *           the csaPromoDataConverter to set
	 */
	@Required
	public void setCsaPromoDataConverter(final Converter<AbstractRuleModel, CSAPromoData> csaPromoDataConverter)
	{
		this.csaPromoDataConverter = csaPromoDataConverter;
	}

}
