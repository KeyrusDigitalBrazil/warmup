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

import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Methods to handle classification features
 */
public class FeatureProvider
{

	public List<FeatureData> getListOfFeatures(final ProductData productData)
	{
		ArrayList<FeatureData> features = null;
		final ArrayList<ClassificationData> classifications = (ArrayList) productData.getClassifications();
		if (CollectionUtils.isNotEmpty(classifications) && classifications.iterator().hasNext())
		{
			features = (ArrayList) classifications.iterator().next().getFeatures();
		}
		return features != null ? features : new ArrayList<>();
	}

}
