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
import {IContextualMenuButton, IContextualMenuConfiguration, PriorityService, SeInjectable, SystemEventService, TypedMap} from 'smarteditcommons';
import {ContextualMenu} from 'smartedit/services';
/**
 * @ngdoc service
 * @name smarteditServicesModule.ContextualMenuService
 *
 * @description
 * The ContextualMenuService is used to add contextual menu items for each component.
 *
 * To add items to the contextual menu, you must call the addItems method of the contextualMenuService and pass a map
 * of the component-type array of contextual menu items mapping. The component type names are the keys in the mapping.
 * The component name can be the full name of the component type, an ant-like wildcard (such as  *middle*Suffix), or a
 * valid regex that includes or excludes a set of component types.
 *
 */
@SeInjectable()
export class ContextualMenuService {

	private _contextualMenus: TypedMap<IContextualMenuButton[]> = {};
	private _featuresList: string[];

	/* @internal */
	constructor(
		private $q: angular.IQService,
		private priorityService: PriorityService,
		private REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT: string,
		private uniqueArray: any,
		private regExpFactory: any,
		private systemEventService: SystemEventService) {

	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.ContextualMenuService#addItems
	 * @methodOf smarteditServicesModule.ContextualMenuService
	 *
	 * @description
	 * The method called to add contextual menu items to component types in the SmartEdit application.
	 * The contextual menu items are then retrieved by the contextual menu decorator to wire the set of menu items to the specified component.
	 *
	 * Sample Usage:
	 * <pre>
	 * contextualMenuService.addItems({
	 * '.*Component': [{
	 *  key: 'itemKey',
	 *  i18nKey: 'CONTEXTUAL_MENU',
	 *  condition: function(componentType, componentId) {
	 * 	return componentId === 'ComponentType';
	 * 	},
	 *  callback: function(componentType, componentId) {
	 * 	alert('callback for ' + componentType + "_" + componentId);
	 * 	},
	 *  displayClass: 'democlass',
	 *  iconIdle: '.../icons/icon.png',
	 *  iconNonIdle: '.../icons/icon.png',
	 * }]
	 * });
	 * </pre>
	 *
	 * @param {TypedMap<IContextualMenuButton[]>} contextualMenuItemsMap A map of componentType regular experessions to list of {@link IContextualMenuButton IContextualMenuButton} contextual menu items
	 *
	 * The object contains a list that maps component types to arrays of {@link IContextualMenuButton IContextualMenuButton} contextual menu items. The mapping is a key-value pair.
	 * The key is the name of the component type, for example, Simple Responsive Banner Component, and the value is an array of {@link IContextualMenuButton IContextualMenuButton} contextual menu items, like add, edit, localize, etc.
	 */
	addItems(contextualMenuItemsMap: TypedMap<IContextualMenuButton[]>) {

		try {
			if (contextualMenuItemsMap !== undefined) {
				this._featuresList = this._getFeaturesList(this._contextualMenus);
				const componentTypes = Object.keys(contextualMenuItemsMap);
				componentTypes.forEach(this._initContextualMenuItems.bind(this, contextualMenuItemsMap));
			}
		} catch (e) {
			throw new Error("addItems() - Cannot add items. " + e);
		}
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.ContextualMenuService#removeItemByKey
	 * @methodOf smarteditServicesModule.ContextualMenuService
	 *
	 * @description
	 * This method removes the menu items identified by the provided key.
	 *
	 * @param {String} itemKey The key that identifies the menu items to remove.
	 */
	removeItemByKey(itemKey: string) {

		Object.keys(this._contextualMenus).forEach((contextualMenuKey) => {
			const contextualMenuItems = this._contextualMenus[contextualMenuKey];
			this._contextualMenus[contextualMenuKey] = contextualMenuItems.filter((item: IContextualMenuButton) => item.key !== itemKey);

			if (this._contextualMenus[contextualMenuKey].length === 0) {
				// Remove if the contextual menu is empty.
				delete this._contextualMenus[contextualMenuKey];
			}
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.ContextualMenuService#getContextualMenuByType
	 * @methodOf smarteditServicesModule.ContextualMenuService
	 *
	 * @description
	 * Will return an array of contextual menu items for a specific component type.
	 * For each key in the contextual menus' object, the method converts each component type into a valid regex using the regExpFactory of the function module and then compares it with the input componentType and, if matched, will add it to an array and returns the array.
	 *
	 * @param {String} componentType The type code of the selected component
	 *
	 * @returns {Array} An array of contextual menu items assigned to the type.
	 *
	 */
	getContextualMenuByType(componentType: string): IContextualMenuButton[] {
		let contextualMenuArray: IContextualMenuButton[] = [];
		if (this._contextualMenus) {
			Object.keys(this._contextualMenus).forEach((regexpKey) => {
				if (this.regExpFactory(regexpKey).test(componentType)) {
					contextualMenuArray = this._getUniqueItemArray(contextualMenuArray, this._contextualMenus[regexpKey]);
				}
			});
		}

		return this.priorityService.sort(contextualMenuArray);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.ContextualMenuService#getContextualMenuItems
	 * @methodOf smarteditServicesModule.ContextualMenuService
	 *
	 * @description
	 * Will return an object that contains a list of contextual menu items that are visible and those that are to be added to the More... options.
	 *
	 * For each component and display limit size, two arrays are generated.
	 * One array contains the menu items that can be displayed and the other array contains the menu items that are available under the more menu items action.
	 *
	 * @param {IContextualMenuConfiguration} configuration the {@link IContextualMenuConfiguration IContextualMenuConfiguration}
	 * @returns {Promise} A promise that resolves to an array of contextual menu items assigned to the component type.
	 *
	 * The returned object contains the following properties
	 * - leftMenuItems : An array of menu items that can be displayed on the component.
	 * - moreMenuItems : An array of menu items that are available under the more menu items action.
	 *
	 */
	getContextualMenuItems(configuration: IContextualMenuConfiguration): angular.IPromise<ContextualMenu> {
		const iLeftBtns = configuration.iLeftBtns;
		delete configuration.iLeftBtns;

		const newMenuItems: IContextualMenuButton[] = [];
		const newMoreItems: IContextualMenuButton[] = [];
		const menuItems = this.getContextualMenuByType(configuration.componentType);

		const promisesToResolve: angular.IPromise<boolean>[] = [];

		menuItems.forEach((item: IContextualMenuButton) => {
			const deferred = this.$q.defer<boolean>();
			promisesToResolve.push(deferred.promise);
			this.$q.when(item.condition ? item.condition(configuration) : true)
				.then(function(deferredPromise: angular.IDeferred<boolean>, menuItem: IContextualMenuButton, isItemEnabled: boolean) {
					const collection = newMenuItems.length < iLeftBtns ? newMenuItems : newMoreItems;
					if (isItemEnabled) {
						collection.push(menuItem);
					}
					deferredPromise.resolve();
				}.bind(undefined, deferred, item));
		});

		return this.$q.all(promisesToResolve).then(function() {
			return {
				leftMenuItems: newMenuItems,
				moreMenuItems: newMoreItems
			};
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.ContextualMenuService#refreshMenuItems
	 * @methodOf smarteditServicesModule.ContextualMenuService
	 *
	 * @description
	 * This method can be used to ask SmartEdit to retrieve again the list of items in the enabled contextual menus.
	 */
	refreshMenuItems() {
		this.systemEventService.publishAsync(this.REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT);
	}

	// Helper Methods
	private _getFeaturesList(_contextualMenus: TypedMap<IContextualMenuButton[]>) {
		// Would be better to use a set for this, but it's not currently supported by all browsers.
		let featuresList: string[] = [];
		Object.keys(_contextualMenus).forEach((key: string) => {
			featuresList = featuresList.concat(_contextualMenus[key].map(function(entry: IContextualMenuButton) {
				return entry.key;
			}));
		});

		return featuresList.reduce(function(previous, current) {
			if (previous.indexOf(current) === -1) {
				previous.push(current);
			}

			return previous;
		}, []);
	}

	private _validateItem(item: IContextualMenuButton) {

		// fix legacy callback param, deprecated in 6.5, especially when called from JS
		if (!item.action) {
			item.action = {};
		}
		if (item.callback) {
			item.action.callback = item.callback;
			delete item.callback;
		}
		if (item.callbacks) {
			item.action.callbacks = item.callbacks;
			delete item.callbacks;
		}
		if (item.templateUrl) {
			item.action.templateUrl = item.templateUrl;
			delete item.templateUrl;
		}

		if (!item.action) {
			throw new Error("Contextual menu item must provide an action: " + item);
		}
		// FIXME: missing case for callbacks
		if ((!!item.action.callback && !!item.action.template)
			|| (!!item.action.callback && !!item.action.templateUrl)
			|| (!!item.action.template && !!item.action.templateUrl)
		) {
			throw new Error("Contextual menu item must have exactly ONE of action callback|callbacks|template|templateUrl");
		}
	}

	private _getUniqueItemArray(array1: IContextualMenuButton[], array2: IContextualMenuButton[]) {
		let currItem: IContextualMenuButton;

		array2.forEach(function(item) {
			currItem = item;
			if (array1.every((it: IContextualMenuButton) => currItem.key !== it.key)) {
				array1.push(currItem);
			}
		});

		return array1;
	}

	private _initContextualMenuItems(map: TypedMap<IContextualMenuButton[]>, componentType: string) {

		const componentTypeContextualMenus = map[componentType].filter((item: IContextualMenuButton) => {
			this._validateItem(item);
			if (!item.key) {
				throw new Error("Item doesn't have key.");
			}

			if (this._featuresList.indexOf(item.key) !== -1) {
				throw new Error("Item with that key already exist.");
			}
			return true;
		});

		this._contextualMenus[componentType] = this.uniqueArray((this._contextualMenus[componentType] || []), componentTypeContextualMenus);
	}

}