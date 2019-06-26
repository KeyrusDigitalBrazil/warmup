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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;

import java.util.ArrayList;
import java.util.List;


/**
 * Takes care of transforming a {@link ProductModel} object of a variant into a {@link ConfigurationOverviewData}
 * object, which is used to display the pre-selected values of the product variant.<br>
 * We want to avoid to create a runtime configuration for a variant, just to display the overview page. This would be an
 * in-performant solution. However the variant data is also available in the hybris classification data, hence we
 * obtained it from there.<br>
 * Will use the {@link VariantOverviewValuePopulator} to populate indivdual values.
 */
public class VariantOverviewPopulator implements Populator<ProductModel, ConfigurationOverviewData>
{
	private Populator<ProductModel, ProductData> classificationPopulator;
	private Populator<FeatureData, List<CharacteristicValue>> variantOverviewValuePopulator;
	private FeatureProvider featureProvider;
	private static final String GROUP_ID = "_GEN";
	private static final String GROUP_DESCRIPTION = "[_GEN]";

	/**
	 * @return the featureProvider
	 */
	public FeatureProvider getFeatureProvider()
	{
		return featureProvider;
	}

	/**
	 * @param featureProvider
	 *           the featureProvider to set
	 */
	public void setFeatureProvider(final FeatureProvider featureProvider)
	{
		this.featureProvider = featureProvider;
	}

	/**
	 * @return the classificationPopulator
	 */
	public Populator<ProductModel, ProductData> getClassificationPopulator()
	{
		return classificationPopulator;
	}

	/**
	 * @param classificationPopulator
	 *           the classificationPopulator to set
	 */
	public void setClassificationPopulator(final Populator<ProductModel, ProductData> classificationPopulator)
	{
		this.classificationPopulator = classificationPopulator;
	}

	/**
	 * @return the variantOverviewValuePopulator
	 */
	public Populator<FeatureData, List<CharacteristicValue>> getVariantOverviewValuePopulator()
	{
		return variantOverviewValuePopulator;
	}

	/**
	 * @param variantOverviewValuePopulator
	 *           the variantOverviewValuePopulator to set
	 */
	public void setVariantOverviewValuePopulator(
			final Populator<FeatureData, List<CharacteristicValue>> variantOverviewValuePopulator)
	{
		this.variantOverviewValuePopulator = variantOverviewValuePopulator;
	}

	@Override
	public void populate(final ProductModel productModel, final ConfigurationOverviewData overviewData)
	{
		final ProductData productData = new ProductData();
		getClassificationPopulator().populate(productModel, productData);
		final List<FeatureData> features = getFeatureProvider().getListOfFeatures(productData);
		final List<CharacteristicValue> values = new ArrayList<>();
		processFeatureList(features, values);
		final CharacteristicGroup group = addGeneralGroup(overviewData);
		group.setCharacteristicValues(values);
	}

	/**
	 * Calls the VariantOverviewValuePopulator for each FeatureData object.
	 *
	 * @param features
	 *           list of FeatureData objects
	 * @param values
	 *           list of resulting CharacteristicValue objects
	 */
	protected void processFeatureList(final List<FeatureData> features, final List<CharacteristicValue> values)
	{
		for (final FeatureData feature : features)
		{
			getVariantOverviewValuePopulator().populate(feature, values);
		}
	}

	/**
	 * Creates a general group to fulfill object hierarchy of ConfigurationOverviewData as product variants don't
	 * organize their features in groups.
	 *
	 * @param overviewData
	 * @return general group
	 */
	protected CharacteristicGroup addGeneralGroup(final ConfigurationOverviewData overviewData)
	{
		final List<CharacteristicGroup> groups = new ArrayList<>();
		final CharacteristicGroup group = new CharacteristicGroup();
		group.setId(GROUP_ID);
		group.setGroupDescription(GROUP_DESCRIPTION);
		groups.add(group);
		overviewData.setGroups(groups);
		return group;
	}

}
