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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.VariantConfigurationInfoProvider;
import de.hybris.platform.sap.productconfig.facades.populator.FeatureProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class VariantConfigurationInfoProviderImpl implements VariantConfigurationInfoProvider
{
	private int maxNumberOfDisplayedCsticsInCart = 4;
	private static final String VALUE_SEPARATOR = "; ";
	private Populator<ProductModel, ProductData> classificationPopulator;
	private FeatureProvider featureProvider;

	@Override
	public List<ConfigurationInfoData> retrieveVariantConfigurationInfo(final ProductModel product)
	{
		final ProductData productData = new ProductData();
		getClassificationPopulator().populate(product, productData);
		final List<FeatureData> features = getFeatureProvider().getListOfFeatures(productData);
		final List<ConfigurationInfoData> configurationInfos = new ArrayList<>();
		for (int i = 0; i < features.size() && i < maxNumberOfDisplayedCsticsInCart; i++)
		{
			final ConfigurationInfoData configInfoInline = new ConfigurationInfoData();
			populateConfigInfoData(features.get(i), configInfoInline);
			configurationInfos.add(configInfoInline);
		}
		return configurationInfos;
	}

	protected void populateConfigInfoData(final FeatureData source, final ConfigurationInfoData target)
	{
		target.setConfigurationLabel(source.getName());
		target.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		target.setConfigurationValue(generateConfigurationValueLine(source));
	}

	/**
	 * Generates a string of feature values separated with the value separator for configuration display in order
	 *
	 * @param source
	 *           FeatureData
	 */
	protected String generateConfigurationValueLine(final FeatureData source)
	{
		final List<FeatureValueData> featureValues = (List) source.getFeatureValues();
		final StringBuilder builder = new StringBuilder();
		if (!CollectionUtils.isEmpty(featureValues))
		{
			for (int i = 0; i < featureValues.size(); i++)
			{
				if (i > 0)
				{
					builder.append(VALUE_SEPARATOR);
				}
				builder.append(featureValues.get(i).getValue());
			}
		}
		return builder.toString();
	}

	/**
	 * @return the featureProvider
	 */
	protected FeatureProvider getFeatureProvider()
	{
		return featureProvider;
	}

	/**
	 * @param featureProvider
	 *           the featureProvider to set
	 */
	@Required
	public void setFeatureProvider(final FeatureProvider featureProvider)
	{
		this.featureProvider = featureProvider;
	}

	/**
	 * @return the classificationPopulator
	 */
	protected Populator<ProductModel, ProductData> getClassificationPopulator()
	{
		return classificationPopulator;
	}

	/**
	 * @param classificationPopulator
	 *           the classificationPopulator to set
	 */
	@Required
	public void setClassificationPopulator(final Populator<ProductModel, ProductData> classificationPopulator)
	{
		this.classificationPopulator = classificationPopulator;
	}

	/**
	 * @param maxNumberOfDisplayedCsticsInCart
	 *           the maxNumberOfDisplayedCsticsInCart to set
	 */
	public void setMaxNumberOfDisplayedCsticsInCart(final int maxNumberOfDisplayedCsticsInCart)
	{
		this.maxNumberOfDisplayedCsticsInCart = maxNumberOfDisplayedCsticsInCart;
	}

}
