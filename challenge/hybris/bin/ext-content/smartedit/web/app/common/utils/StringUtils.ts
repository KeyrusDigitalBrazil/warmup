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
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';

/**
 * @ngdoc service
 * @name functionsModule.service:StringUtils
 *
 * @description
 * utility service around Strings.
 */
@SeInjectable()
export class StringUtils {

    /**
     * @ngdoc method
     * @name functionsModule.regExpFactory
     * @name functionsModule.service:StringUtils#regExpFactory
     * @methodOf functionsModule.service:StringUtils
     *
     * @description
     * <b>regExpFactory</b> will convert a given pattern into a regular expression.
     * This method will prepend and append a string with ^ and $ respectively replaces
     * and wildcards (*) by proper regex wildcards.
     *
     * @param {String} pattern any string that needs to be converted to a regular expression.
     *
     * @returns {RegExp} a regular expression generated from the given string.
     *
     */

	regExpFactory(pattern: string): RegExp {

		const onlyAlphanumericsRegex = new RegExp(/^[a-zA-Z\d]+$/i);
		const antRegex = new RegExp(/^[a-zA-Z\d\*]+$/i);

		let regexpKey;
		if (onlyAlphanumericsRegex.test(pattern)) {
			regexpKey = ['^', '$'].join(pattern);
		} else if (antRegex.test(pattern)) {
			regexpKey = ['^', '$'].join(pattern.replace(/\*/g, '.*'));
		} else {
			regexpKey = pattern;
		}

		return new RegExp(regexpKey, 'g');
	}

}

export const stringUtils = new StringUtils();
