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
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author CSAPromotionsModelProvider
 *
 */
public class CSAPromotionsModelProvider implements FragmentModelProvider<List<CSAPromoData>>
{
	private AssistedServicePromotionFacade assistedServicePromotionFacade;
	private Converter<AbstractRuleModel, CSAPromoData> csaPromoDataConverter;
	private CartFacade cartFacade;

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
		final List<CSAPromoData> csaAllPromos = getCsaPromoDataConverter()
				.convertAll(getAssistedServicePromotionFacade().getCSAPromotions(searchKeyword));
		final List<CSAPromoData> csaPotentialAndFiredPromos = new ArrayList<>();
		final List<PromotionResultData> firedPromos = getCartFacade().getSessionCart().getAppliedOrderPromotions();
		final List<PromotionResultData> potentialPromos = getCartFacade().getSessionCart().getPotentialOrderPromotions();

		if (CollectionUtils.isNotEmpty(csaAllPromos))
		{
			//Populating Fired promos
			if (CollectionUtils.isNotEmpty(firedPromos))
			{
				populateFiredOrPotentialPromosFromCart(Boolean.TRUE, csaAllPromos, csaPotentialAndFiredPromos,
						firedPromos.stream().filter(firedPromo -> firedPromo.getPromotionData().getCode().startsWith(searchKeyword))
								.collect(Collectors.toList()));
			}
			//Populating Potential promos
			if (CollectionUtils.isNotEmpty(potentialPromos))
			{
				populateFiredOrPotentialPromosFromCart(Boolean.FALSE, csaAllPromos, csaPotentialAndFiredPromos,
						potentialPromos.stream()
								.filter(potentialPromo -> potentialPromo.getPromotionData().getCode().startsWith(searchKeyword))
								.collect(Collectors.toList()));
			}
			Collections.sort(csaPotentialAndFiredPromos, new Comparator<CSAPromoData>()
			{
				@Override
				public int compare(final CSAPromoData promo1, final CSAPromoData promo2)
				{
					return Integer.compare(promo2.getPriority().intValue(), promo1.getPriority().intValue());
				}
			});
		}
		return csaPotentialAndFiredPromos;
	}

	protected void populateFiredOrPotentialPromosFromCart(final Boolean fired, final List<CSAPromoData> csaAllPromos,
			final List<CSAPromoData> csaPotentialAndFiredPromos, final List<PromotionResultData> cartPromos)
	{

		cartPromos.stream().forEach(firedPromo -> {

			final CSAPromoData cartPromo = csaAllPromos.stream()
					.filter(csaPromo -> csaPromo.getCode().equalsIgnoreCase(firedPromo.getPromotionData().getCode())).findFirst()
					.get();
			cartPromo.setFired(fired);
			cartPromo.setFiredMessage(firedPromo.getDescription());
			csaPotentialAndFiredPromos.add(cartPromo);

		});
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

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

}
