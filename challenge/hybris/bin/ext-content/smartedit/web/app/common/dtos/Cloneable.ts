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
import * as lo from 'lodash';
import {Payload, Primitive} from 'smarteditcommons';
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';
export type Cloneable = Primitive | Primitive[] | Payload;
/**
 * @ngdoc service
 * @name functionsModule.service:CloneableUtils
 *
 * @description
 * utility service around Cloneable objects
 */
@SeInjectable()
export class CloneableUtils {

	constructor(private lodash: lo.LoDashStatic) {
	}

	/**
	 * @ngdoc method
	 * @name functionsModule.service:CloneableUtils#makeCloneable
	 * @methodOf functionsModule.service:CloneableUtils
	 * @description
	 * returns a "cloneable" version of an object.
	 * Something is cloneable when it can be sent through W3C postMessage.
	 * To this purpose, functions must be removed from the cloneable candidate.
	 * @param {Object} json the object to be made cloneable
	 * @returns {Cloneable} the cloneable copy of the object
	 */
	makeCloneable(_json: any): Cloneable {

		const json = this.lodash.cloneDeepWith(_json, (value) => {
			if (value !== undefined && value !== null && !this.isPrimitive(json)) {
				// is a promise
				if (value.then) {
					return null;
				} else if (typeof value === 'function') {
					return null;
				} else if (this.lodash.isElement(value)) {
					return null;
					// is yjQuery
				} else if (typeof value !== 'string' && value.hasOwnProperty('length') && !value.forEach) {
					return null;
				} else {
					return value;
				}
			} else {
				return value;
			}
		});
		if (json === undefined || json === null || this.isPrimitive(json)) {
			return json;
		} else if (json.hasOwnProperty('length') || json.forEach) { // Array, already taken care of yjQuery
			return json.map((arrayElement: any) => this.makeCloneable(arrayElement)) as Cloneable;
		} else { // JSON
			return Object.keys(json).reduce((clone, directKey) => {
				if (directKey.indexOf("$") !== 0) {
					clone[directKey] = this.makeCloneable(json[directKey]);
				}
				return clone;
			}, {} as Payload);
		}
	}

	private isPrimitive(value: any) {
		return typeof value === 'number' || typeof value === 'string' || typeof value === 'boolean';
	}

}