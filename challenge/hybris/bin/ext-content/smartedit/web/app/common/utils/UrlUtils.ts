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
 * @ngdoc service
 * @name functionsModule.service:UrlUtils
 *
 * @description
 * A collection of utility methods for manipulating URLs
 */
export class UrlUtils {

    /**
     * @ngdoc method
     * @name functionsModule.service:UrlUtils#updateUrlParameter
     * @methodOf functionsModule.service:UrlUtils
     *
     * @description
     * Updates a URL to contain the query param and value provided. If already exists then it is updated,
     * if it did not previously exist, then it will be added.
     *
     * @param {String} url The url to be updated (this param will not be modified)
     * @param {String} key The query param key
     * @param {String} value The query param value
     *
     * @returns {String} The url with updated key/value
     */
	updateUrlParameter(url: string, key: string, value: string): string {
		const i = url.indexOf('#');
		const hash = i === -1 ? '' : url.substr(i);
		url = i === -1 ? url : url.substr(0, i);
		const regex = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
		const separator = url.indexOf('?') !== -1 ? "&" : "?";

		if (url.match(regex)) {
			url = url.replace(regex, '$1' + key + "=" + value + '$2');
		} else {
			url = url + separator + key + "=" + value;
		}
		return url + hash;
	}


}