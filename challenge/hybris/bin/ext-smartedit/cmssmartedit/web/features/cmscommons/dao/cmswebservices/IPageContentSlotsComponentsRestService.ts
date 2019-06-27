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
import * as angular from 'angular';
import {TypedMap} from 'smarteditcommons';
import {ICMSComponent} from 'cmscommons/services/ICMSComponent';

/**
 * @description
 * Rest Service to retrieve page content slots for components.
 */
export abstract class IPageContentSlotsComponentsRestService {

	/**
	 * @description
	 * Clears the slotId - components list map in the cache.
	 */
	clearCache(): void {
		'proxyFunction';
	}

	/**
	 * @description
	 * Retrieves a list of pageContentSlotsComponents associated to a page and Converts the list of pageContentSlotsComponents to slotId - components list map.
	 * If the map is already stored in the cache, it will return the cache info.
	 *
	 * @param {string} pageUid The uid of the page to retrieve the content slots to components map.
	 * @return {Promise} A promise that resolves to slotId - components list map.
	 */
	getSlotsToComponentsMapForPageUid(pageUid: string): angular.IPromise<TypedMap<ICMSComponent[]>> {
		'proxyFunction';
		return null;
	}

	/**
	 * @description
	 * Retrieves a list of all components for a given slot which is part of the page being loaded.
	 * It returns all the components irrespective of their visibility.
	 *
	 * @param {string} slotUuid The uid of the slot to retrieve the list of components.
	 * @return {Promise} A promise that resolves to components list.
	 */
	getComponentsForSlot(slotUuid: string): angular.IPromise<ICMSComponent[]> {
		'proxyFunction';
		return null;
	}

}