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
package de.hybris.platform.assistedserviceyprofilefacades;


import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.DeviceAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.TechnologyUsedData;

import java.util.List;


/**
 * Interface provides ability to get affinity data for a profile.
 */
public interface YProfileAffinityFacade
{
	/**
	 * Returns list of products affinities associated to the current session customer.
	 *
	 * @param productAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed.
	 *
	 * @return List<ProductAffinityData>
	 */
	List<ProductAffinityData> getProductAffinities(ProductAffinityParameterData productAffinityParameterData);

	/**
	 * Returns list of categories affinities associated to the current session customer.
	 *
	 * @param categoryAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed
	 *
	 * @return List<CategoryAffinityData>
	 */
	List<CategoryAffinityData> getCategoryAffinities(CategoryAffinityParameterData categoryAffinityParameterData);

	/**
	 * Returns list of device affinities associated to the current session customer.
	 *
	 * @param deviceAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed.
	 * @return List<TechnologyUsedData>
	 */
	List<TechnologyUsedData> getDeviceAffinities(DeviceAffinityParameterData deviceAffinityParameterData);
}