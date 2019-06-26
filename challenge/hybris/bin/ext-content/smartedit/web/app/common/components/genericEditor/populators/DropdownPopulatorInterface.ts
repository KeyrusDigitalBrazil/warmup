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
import {
	DropdownPopulatorFetchPageResponse,
	DropdownPopulatorItemPayload,
	DropdownPopulatorPagePayload,
	DropdownPopulatorPayload,
	GenericEditorOption,
	IDropdownPopulator
} from "smarteditcommons/components/genericEditor";
import {LanguageService, TypedMap} from 'smarteditcommons';
import * as lo from "lodash";

/**
 * @ngdoc service
 * @name dropdownPopulatorModule.DropdownPopulatorInterface
 *
 * @description
 * Interface describing the contract of a DropdownPopulator fetched through dependency injection by the
 * {@link genericEditorModule.service:GenericEditor GenericEditor} to populate the dropdowns of {@link seDropdownModule.directive:seDropdown seDropdown}.
 */
export class DropdownPopulatorInterface implements IDropdownPopulator {

	constructor(
		public lodash: lo.LoDashStatic,
		public languageService: LanguageService) {

	}

	getItem(payload: DropdownPopulatorItemPayload): angular.IPromise<GenericEditorOption> {
		return null;
	}
	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.DropdownPopulatorInterface#populate
	 * @methodOf dropdownPopulatorModule.DropdownPopulatorInterface
	 * @description
	 * Will returns a promise resolving to a list of items.
	 * this method is deprecated, use {@link dropdownPopulatorModule.DropdownPopulatorInterface#fetchAll, fetchAll}.
	 * @param {Object} payload contains the field, model and additional attributes.
	 * @param {Object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
	 * @param {Object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
	 * @param {Object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
	 * @param {String} payload.search The search key when the user types in the dropdown (optional).
	 * @returns {GenericEditorOption[]} a list of objects.
	 */
	populate(payload: DropdownPopulatorPayload): angular.IPromise<GenericEditorOption[]> {
		return this.fetchAll(payload);
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.DropdownPopulatorInterface#fetchAll
	 * @methodOf dropdownPopulatorModule.DropdownPopulatorInterface
	 * @deprecated
	 * @description
	 * Will returns a promise resolving to a list of items.
	 * The items must all contain a property <b>id</b>.
	 * @param {Object} payload contains the field, model and additional attributes.
	 * @param {String} payload.field.options The original array of options (used by {@link dropdownPopulatorModule.service:optionsDropdownPopulator optionsDropdownPopulator})
	 * @param {String} payload.field.uri The uri used to make a rest call to fetch data (used by {@link dropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator})
	 * @param {Object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
	 * @param {String} payload.field.dependsOn A comma separated list of attributes to include from the model when building the request params
	 * @param {String} payload.field.idAttribute The name of the attribute to use when setting the id attribute
	 * @param {String} payload.field.labelAttributes A list of attributes to use when setting the label attribute
	 * @param {Object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
	 * @param {Object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
	 * @param {String} payload.search The search key when the user types in the dropdown (optional).
	 * @returns {GenericEditorOption[]} a list of objects.
	 */
	fetchAll(payload: DropdownPopulatorPayload): angular.IPromise<GenericEditorOption[]> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.DropdownPopulatorInterface#fetchPage
	 * @methodOf dropdownPopulatorModule.DropdownPopulatorInterface
	 *
	 * @description
	 * Will returns a promise resolving to a {@link Page.object:Page page} of items.
	 * The items must all contain a property <b>id</b>.
	 * @param {Object} payload contains the field, model and additional attributes.
	 * @param {Object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
	 * @param {String} payload.field.options The original array of options (used by {@link dropdownPopulatorModule.service:optionsDropdownPopulator optionsDropdownPopulator})
	 * @param {String} payload.field.uri The uri used to make a rest call to fetch data (used by {@link dropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator})
	 * @param {String} payload.field.dependsOn A comma separated list of attributes to include from the model when building the request params
	 * @param {String} payload.field.idAttribute The name of the attribute to use when setting the id attribute
	 * @param {String} payload.field.labelAttributes A list of attributes to use when setting the label attribute
	 * @param {Object} payload.field.params An object containing properties to append as query string while making a call.
	 * @param {Object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
	 * @param {Object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
	 * @param {String} payload.search The search key when the user types in the dropdown (optional).
	 * @param {String} payload.pageSize number of items in the page.
	 * @param {String} payload.currentPage current page number.
	 * @returns {Object} a {@link Page.object:Page page}
	 */
	fetchPage(payload: DropdownPopulatorPagePayload): angular.IPromise<DropdownPopulatorFetchPageResponse> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.DropdownPopulatorInterface#isPaged
	 * @methodOf dropdownPopulatorModule.DropdownPopulatorInterface
	 *
	 * @description
	 * Specifies whether this populator is meant to work in paged mode as opposed to retrieve lists. Optional, default is false
	 */
	isPaged(): boolean {
		return false;
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.DropdownPopulatorInterface#populateAttributes
	 * @methodOf dropdownPopulatorModule.DropdownPopulatorInterface
	 *
	 * @description
	 * Populates the id and label property for each item in the list. If the label property is not already set,
	 * then we use an ordered list of attributes to use when determining the label for each item.
	 * @param {GenericEditorOption[]} items The array of items to set the id and label attributes on
	 * @param {String} idAttribute The name of the id attribute
	 * @param {Array} orderedLabelAttributes The ordered list of label attributes
	 * @returns {GenericEditorOption[]} the modified list of items
	 */
	populateAttributes(items: GenericEditorOption[], idAttribute: string, orderedLabelAttributes: string[]): GenericEditorOption[] {
		return this.lodash.map(items, (item: GenericEditorOption) => {
			if (idAttribute && this.lodash.isEmpty(item.id)) {
				item.id = item[idAttribute];
			}

			if (orderedLabelAttributes && this.lodash.isEmpty(item.label)) {
				// Find the first attribute that the item object contains
				const labelAttribute = this.lodash.find(orderedLabelAttributes, (attr: string) => {
					return !this.lodash.isEmpty(item[attr]);
				});

				// If we found an attribute, set the label
				if (labelAttribute) {
					item.label = item[labelAttribute];
				}
			}

			return item;
		});
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.DropdownPopulatorInterface#search
	 * @methodOf dropdownPopulatorModule.DropdownPopulatorInterface
	 *
	 * @description
	 * Searches a list and returns a promise resolving to only items with a label attribute that matches the search term.
	 * @param {GenericEditorOption[]} items The list of items to search
	 * @param {String} searchTerm The search term to filter items by
	 * @returns {angular.IPromise<GenericEditorOption[]>} the filtered list of items
	 */
	search(items: GenericEditorOption[], searchTerm: string): angular.IPromise<GenericEditorOption[]> {
		return this.languageService.getResolveLocale().then((isocode: string) => {
			return this.lodash.filter(items, (item: GenericEditorOption) => {
				let labelValue: string;
				if (this.lodash.isObject(item.label)) {
					isocode = (item.label as TypedMap<string>)[isocode] ? isocode : Object.keys(item.label as TypedMap<string>)[0];
					labelValue = (item.label as TypedMap<string>)[isocode];
				} else {
					labelValue = (item.label as string);
				}
				return labelValue && (labelValue as string).toUpperCase().indexOf(searchTerm.toUpperCase()) > -1;
			});
		});
	}

}
