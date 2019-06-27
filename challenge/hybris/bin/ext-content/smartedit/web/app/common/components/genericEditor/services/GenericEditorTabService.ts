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
import {SeInjectable, TypedMap} from "smarteditcommons";
import {
	GenericEditorStructure,
	GenericEditorTab,
	GenericEditorTabConfiguration
} from "smarteditcommons/components/genericEditor";
import {GenericEditorPredicate} from "smarteditcommons/components/genericEditor/types";

/**
 * @ngdoc service
 * @name genericEditorServicesModule.service:genericEditorTabService
 * @description
 * The genericEditorTabService is used to configure the way in which the tabs in the
 * {@link genericEditorModule.directive:genericEditor genericEditor} directive are rendered.
 *
 */
@SeInjectable()
export class GenericEditorTabService {

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	private static readonly MIN_PRIORITY = 0;
	private static readonly DEFAULT_TAB_ID = 'default';

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	private _tabsConfiguration: TypedMap<GenericEditorTabConfiguration> = {};
	private _defaultTabPredicates: GenericEditorPredicate[] = [];

	// --------------------------------------------------------------------------------------
	// Public Methods
	// --------------------------------------------------------------------------------------
	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:genericEditorTabService#configureTab
	 * @methodOf genericEditorServicesModule.service:genericEditorTabService
	 * @description
	 * This method stores the configuration of the tab identified by the provided ID.
	 *
	 * @param {String} tabId The ID of the tab to configure.
	 * @param {GenericEditorTabConfiguration} tabConfiguration The object containing the configuration of the tab.
	 * @param {Number=} tabConfiguration.priority The priority of the tab. Higher numbers represent higher priority. This property is used to
	 * sort tabs.
	 *
	 */
	configureTab(tabId: string, tabConfiguration: GenericEditorTabConfiguration): void {
		this._tabsConfiguration[tabId] = tabConfiguration;
	}

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:genericEditorTabService#getTabConfiguration
	 * @methodOf genericEditorServicesModule.service:genericEditorTabService
	 * @description
	 * This method retrieves the configuration of a tab.
	 *
	 * @param {String} tabId The ID of the tab for which to retrieve its configuration.
	 * @return {Object} The configuration of the tab. Can be null if no tab with the provided ID has been configured.
	 *
	 */
	getTabConfiguration(tabId: string): GenericEditorTabConfiguration {
		const result = this._tabsConfiguration[tabId];
		return result ? result : null;
	}

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:genericEditorTabService#sortTabs
	 * @methodOf genericEditorServicesModule.service:genericEditorTabService
	 * @description
	 * This method sorts in place the list of tabs provided. Sorting starts with tab priority. If two or more tabs have the same priority they
	 * will be sorted alphabetically by ID.
	 *
	 * @param {Object[]} tabsToSort The list of tabs to sort.
	 *
	 */
	sortTabs(tabsToSort: GenericEditorTab[]): GenericEditorTab[] {
		return tabsToSort.sort((tab1: GenericEditorTab, tab2: GenericEditorTab) => {
			const tab1Priority = this.getTabPriority(tab1);
			const tab2Priority = this.getTabPriority(tab2);

			if (tab2Priority - tab1Priority !== 0) {
				return tab2Priority - tab1Priority; // Sort descending priority
			} else {
				// Sort alphabetically
				if (tab1.id < tab2.id) {
					return -1;
				} else if (tab1.id > tab2.id) {
					return 1;
				}
				return 0;
			}

		});
	}

	// Meant to be used internally. No ng-doc.
	getComponentTypeDefaultTab(componentTypeStructure: GenericEditorStructure): string {
		let result = null;
		this._defaultTabPredicates.some((predicate: GenericEditorPredicate) => {
			result = predicate(componentTypeStructure);
			return result !== null;
		});

		return result !== null ? result : GenericEditorTabService.DEFAULT_TAB_ID;
	}

	// Meant to be used internally. No ng-doc.
	addComponentTypeDefaultTabPredicate(predicate: GenericEditorPredicate): void {
		if (!predicate || typeof predicate !== 'function') {
			throw new Error('genericEditorTabService - provided predicate must be a function.');
		}

		this._defaultTabPredicates.push(predicate);
	}

	// --------------------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------------------
	private getTabPriority(tab: GenericEditorTab): number {
		const tabId = tab.id;
		if (!tabId) {
			throw new Error('genericEditorTabService - Every tab must have an id.');
		}

		let tabPriority = GenericEditorTabService.MIN_PRIORITY;
		if (this._tabsConfiguration[tabId] && this._tabsConfiguration[tabId].priority) {
			tabPriority = this._tabsConfiguration[tabId].priority;
		}

		return tabPriority;
	}

}
