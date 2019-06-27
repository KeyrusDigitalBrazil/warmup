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
package de.hybris.platform.gigya.gigyaservices.login;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;


/**
 * Service to carry out Gigya login related activity
 */
public interface GigyaLoginService
{

	/**
	 * Method to verify gigya call
	 *
	 * @param gigyaConfigModel
	 *           the gigyaConfigModel
	 * @param uid
	 *           the uid
	 * @param uidSignature
	 *           the uid signature
	 * @param signatureTimeStamp
	 *           the signature timestamp
	 * @return boolean - true if successfully verified
	 */
	boolean verifyGigyaCall(GigyaConfigModel gigyaConfigModel, String uid, String uidSignature, String signatureTimeStamp);

	/**
	 * Find customer using gigya UID
	 *
	 * @param uid
	 *           the uid
	 * @return UserModel the user model
	 */
	UserModel findCustomerByGigyaUid(String uid);

	/**
	 * Notify gigya about logout information
	 *
	 * @param gigyaConfig
	 *           The gigyaConfig model
	 * @param uid
	 *           - unique identifier
	 */
	void notifyGigyaOfLogout(GigyaConfigModel gigyaConfig, String uid);

	/**
	 * Fetch user information from gigya
	 *
	 * @param gigyaConfig
	 *           the gigyaConfig model
	 * @param uid
	 *           the uid
	 * @return GigyaUserObject the gigya user object
	 */
	GigyaUserObject fetchGigyaInfo(GigyaConfigModel gigyaConfig, final String uid);

	/**
	 * Sends gigya user info in commerce to gigya
	 *
	 * @param userModel
	 *           The gigya user
	 * @return boolean - true if successfully sent to gigya
	 */
	boolean sendUserToGigya(UserModel userModel);

}
