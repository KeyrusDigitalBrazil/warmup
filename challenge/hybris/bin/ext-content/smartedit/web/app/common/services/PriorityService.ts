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
import {IPrioritized} from 'smarteditcommons';
import {SeInjectable} from './dependencyInjection/di';
/**
 * @ngdoc service
 * @name smarteditServicesModule.service:PriorityService
 * @description
 * The PriorityService handles arrays of {@link smarteditServicesModule.interface:IPrioritized IPrioritized} elements
 */
@SeInjectable()
export class PriorityService {

	constructor(private encode: any) {

	}
	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:PriorityService#sort<T>
	 * @methodOf smarteditServicesModule.service:PriorityService
	 *
	 * @description
	 * Will sort the candidate array by ascendign or descending priority.
	 * Even if the priority is not defined for a number of elements, the sorting will still be consistent over invocations
	 * @param {T[]} candidate the array of @link smarteditServicesModule.interface:IPrioritized IPrioritized} elements to be sorted
	 * @param {boolean=} [ascending=true] if true, candidate will be sorted by ascending priority.
	 * @returns {T[]} A promise resolving to the username,
	 * previously mentioned as "principalUID", associated to the
	 * authenticated user.
	 */
	sort<T extends IPrioritized>(candidate: T[], ascending: boolean = true): T[] {

		return candidate.sort((item1: IPrioritized, item2: IPrioritized) => {
			let output: number = item1.priority - item2.priority;
			if (output === 0) {
				output = this.encode(item1).localCompare(this.encode(item2));
			}
			return output;
		});
	}

}
