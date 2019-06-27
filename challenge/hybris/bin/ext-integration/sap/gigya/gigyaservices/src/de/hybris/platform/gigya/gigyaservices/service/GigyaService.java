/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaservices.service;

import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;

import java.util.Map;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;


/**
 * Service to call gigya api's using gigya's java classes
 */
public interface GigyaService
{

	/**
	 * Method to call raw gigya API with the provided gigya configuration
	 *
	 * @param method
	 *           The method name of gigya
	 * @param params
	 *           The various params
	 * @param gigyaConfigModel
	 *           The gigya config model
	 * @param trys
	 *           The number of trys
	 * @param tryNum
	 *           The current try number
	 * @return GSResponse The GS response
	 */
	GSResponse callRawGigyaApiWithConfig(String method, Map<String, Object> params, GigyaConfigModel gigyaConfigModel, int trys,
			int tryNum);

	/**
	 * Method to call gigya API with the configuration and gigya object
	 *
	 * @param method
	 *           The method name
	 * @param gsObject
	 *           The gigya object
	 * @param gigyaConfigModel
	 *           The gigya configuration model
	 * @param trys
	 *           The number of trys
	 * @param tryNum
	 *           The current try number
	 * @return GSResponse The gigya response
	 */
	GSResponse callRawGigyaApiWithConfigAndObject(String method, GSObject gsObject, GigyaConfigModel gigyaConfigModel, int trys,
			int tryNum);

}
