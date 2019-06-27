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
import {SystemEventService} from "smarteditcommons";
import {
	GenericEditorField,
	GenericEditorOption,
	IDropdownPopulator,
	ISEDropdownService,
	SEDropdownAPI,
	SEDropdownConfiguration
} from "smarteditcommons/components/genericEditor";

/**
 * @ngdoc service
 * @name seDropdownModule.service:SEDropdownService
 *
 * @description
 * The SEDropdownService handles the initialization and the rendering of the {@link seDropdownModule.directive:seDropdown seDropdown} Angular component.
 */
export const SEDropdownServiceFactory = (
	$q: angular.IQService,
	$injector: angular.auto.IInjectorService,
	$log: angular.ILogService,
	isBlank: (value: any) => boolean,
	isFunctionEmpty: any,
	LINKED_DROPDOWN: string,
	CLICK_DROPDOWN: string,
	DROPDOWN_IMPLEMENTATION_SUFFIX: string,
	systemEventService: SystemEventService,
	getKeyHoldingDataFromResponse: any,
	VALIDATION_MESSAGE_TYPES: any
) => {
	'ngInject';

	class SEDropdownService implements ISEDropdownService {

		public getApi: ($api: {$api: SEDropdownAPI}) => void;
		public setYSelectAPI: any;
		public $api: SEDropdownAPI;
		public resultsHeaderTemplateUrl: string;
		public resultsHeaderTemplate: string;
		public isMultiDropdown: boolean;
		public initialized: boolean;
		public qualifier: string;
		public reset: () => void;

		private field: GenericEditorField;
		private model: any;
		private id: string;
		private onClickOtherDropdown: (key?: string, qualifier?: string) => void;
		private items: GenericEditorOption[];
		private ySelectAPI: any;
		private selection: any;
		private eventId: string;
		private clickEventKey: string;
		private populator: IDropdownPopulator;
		private isPaged: boolean;
		private fetchStrategy: any;

		constructor(conf: SEDropdownConfiguration) {
			this.field = conf.field;
			this.qualifier = conf.qualifier;
			this.model = conf.model;
			this.id = conf.id;
			this.onClickOtherDropdown = conf.onClickOtherDropdown;
			this.items = [];
			this.getApi = conf.getApi;
			this.setYSelectAPI = ($api: any) => {
				this.ySelectAPI = $api;
			};

			/**
			 * @ngdoc object
			 * @name seDropdownModule.object:seDropdownApi
			 * @description
			 * The ySelector's api object exposing public functionality
			 */
			this.$api = {
				/**
				 * @ngdoc method
				 * @name setResultsHeaderTemplateUrl
				 * @methodOf seDropdownModule.object:seDropdownApi
				 * @description
				 * A method that sets the URL of the template used to display results the dropdown.
				 *
				 * @param {String} resultHeadersTemplateUrl The URL of the template used to display the dropdown result headers section.
				 */
				setResultsHeaderTemplateUrl: (resultsHeaderTemplateUrl: string) => {
					this.resultsHeaderTemplateUrl = resultsHeaderTemplateUrl;
				},
				/**
				 * @ngdoc method
				 * @name setResultsHeaderTemplate
				 * @methodOf seDropdownModule.object:seDropdownApi
				 * @description
				 * A method that sets the template used to display results the dropdown.
				 *
				 * @param {String} resultsHeaderTemplate The template used to display the dropdown result headers section.
				 */
				setResultsHeaderTemplate: (resultsHeaderTemplate: string) => {
					this.resultsHeaderTemplate = resultsHeaderTemplate;
				}
			};
		}

		_respondToChange(key: any, handle: any) {
			if (this.field.dependsOn && this.field.dependsOn.split(",").indexOf(handle.qualifier) > -1) {
				this.selection = handle.optionObject;
				if (this.reset) {
					this.reset();
				}
			}
		}

		_respondToOtherClicks(key: any, qualifier: string) {
			if (this.field.qualifier !== qualifier && typeof this.onClickOtherDropdown === "function") {
				this.onClickOtherDropdown(key, qualifier);
			}
		}

		/**
		 * @ngdoc method
		 * @name seDropdownModule.service:SEDropdownService#triggerAction
		 * @methodOf seDropdownModule.service:SEDropdownService
		 *
		 * @description
		 * Publishes an asynchronous event for the currently selected option
		 */
		triggerAction() {
			const selectedObj = this.items.filter((option: GenericEditorOption) => {
				return option.id === this.model[this.qualifier];
			})[0];
			const handle = {
				qualifier: this.qualifier,
				optionObject: selectedObj
			};

			if (this.ySelectAPI) {
				this.ySelectAPI.setValidationState(this.getState(this.field));
			}

			systemEventService.publishAsync(this.eventId, handle);
		}

		onClick() {
			systemEventService.publishAsync(this.clickEventKey, this.field.qualifier);
		}

		/**
		 * @ngdoc method
		 * @name seDropdownModule.service:SEDropdownService#fetchAll
		 * @methodOf seDropdownModule.service:SEDropdownService
		 *
		 * @description
		 * Uses the configured implementation of {@link dropdownPopulatorModule.DropdownPopulatorInterface DropdownPopulatorInterface}
		 * to populate the seDropdown items using {@link dropdownPopulatorModule.DropdownPopulatorInterface:populate populate}
		 *
		 * @returns {Promise} A promise that resolves to a list of options to be populated
		 */
		fetchAll(search: string): angular.IPromise<GenericEditorOption[]> {
			return this.populator.populate({
				field: this.field,
				model: this.model,
				selection: this.selection,
				search
			}).then((options: GenericEditorOption[]) => {
				this.items = options;
				return this.items;
			});
		}

		/**
		 * @ngdoc method
		 * @name seDropdownModule.service:SEDropdownService#fetchEntity
		 * @methodOf seDropdownModule.service:SEDropdownService
		 *
		 * @description
		 * Uses the configured implementation of {@link dropdownPopulatorModule.DropdownPopulatorInterface DropdownPopulatorInterface}
		 * to populate a single item {@link dropdownPopulatorModule.DropdownPopulatorInterface:getItem getItem}
		 *
		 * @param {String} id The id of the option to fetch
		 *
		 * @returns {Promise} A promise that resolves to the option that was fetched
		 */
		fetchEntity(id: string): angular.IPromise<GenericEditorOption> {
			return this.populator.getItem({
				field: this.field,
				id,
				model: this.model
			});
		}

		/**
		 * @ngdoc method
		 * @name seDropdownModule.service:SEDropdownService#fetchPage
		 * @methodOf seDropdownModule.service:SEDropdownService
		 *
		 * @param {String} search The search to filter options by
		 * @param {Number} pageSize The number of items to be returned
		 * @param {Number} currentPage The page to be returned
		 *
		 * @description
		 * Uses the configured implementation of {@link dropdownPopulatorModule.DropdownPopulatorInterface DropdownPopulatorInterface}
		 * to populate the seDropdown items using {@link dropdownPopulatorModule.DropdownPopulatorInterface:fetchPage fetchPage}
		 *
		 * @returns {Promise} A promise that resolves to an object containing the array of items and paging information
		 */
		fetchPage(search: string, pageSize: number, currentPage: number) {
			return this.populator.fetchPage({
				field: this.field,
				model: this.model,
				selection: this.selection,
				search,
				pageSize,
				currentPage
			}).then((page) => {
				const holderProperty = getKeyHoldingDataFromResponse(page);
				page.results = page[holderProperty];

				delete page[holderProperty];
				this.items = page.results;
				return page;
			}).catch((err) => {
				$log.error(`SEDropdownService.fetchPage() - Failed to fetch items and paging information. ${err}`);
			});
		}

		/**
		 * @ngdoc method
		 * @name seDropdownModule.service:SEDropdownService#init
		 * @methodOf seDropdownModule.service:SEDropdownService
		 *
		 * @description
		 * Initializes the seDropdown with a configured dropdown populator based on field attributes used when instantiating
		 * the {@link  seDropdownModule.service:SEDropdownService}.
		 */
		init(): void {
			this.initializeAPI();
			this.isMultiDropdown = this.field.collection ? this.field.collection : false;

			this.triggerAction = this.triggerAction.bind(this);

			let populatorName: string;

			this.eventId = (this.id || '') + LINKED_DROPDOWN;
			this.clickEventKey = (this.id || '') + CLICK_DROPDOWN;

			if (this.field.dependsOn) {
				systemEventService.subscribe(this.eventId, this._respondToChange.bind(this));
			}

			systemEventService.subscribe(this.clickEventKey, this._respondToOtherClicks.bind(this));

			if (this.field.options && this.field.uri) {
				throw new Error("se.dropdown.contains.both.uri.and.options");
			} else if (this.field.options) {
				populatorName = "options" + DROPDOWN_IMPLEMENTATION_SUFFIX;
				this.isPaged = false;
			} else if (this.field.uri) {
				populatorName = "uri" + DROPDOWN_IMPLEMENTATION_SUFFIX;
				this.isPaged = this.field.paged ? this.field.paged : false;
			} else if (this.field.propertyType) {
				if ($injector.has(this.field.propertyType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
					populatorName = this.field.propertyType + DROPDOWN_IMPLEMENTATION_SUFFIX;
					this.isPaged = this.isPopulatorPaged(populatorName);
				} else {
					throw new Error("sedropdown.no.populator.found");
				}
			} else if ($injector.has(this.field.cmsStructureType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
				populatorName = this.field.cmsStructureType + DROPDOWN_IMPLEMENTATION_SUFFIX;
				this.isPaged = this.field.paged ? this.field.paged : false;
			} else {
				if ($injector.has(this.field.smarteditComponentType + this.field.qualifier + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
					populatorName = this.field.smarteditComponentType + this.field.qualifier + DROPDOWN_IMPLEMENTATION_SUFFIX;
				} else if ($injector.has(this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
					populatorName = this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX;
				} else {
					throw new Error("se.dropdown.no.populator.found");
				}
				this.isPaged = this.isPopulatorPaged(populatorName);
			}

			this.populator = $injector.get(populatorName) as IDropdownPopulator;

			this.fetchStrategy = {
				fetchEntity: this.fetchEntity.bind(this)
			};

			if (this.isPaged) {
				this.fetchStrategy.fetchPage = this.fetchPage.bind(this);
			} else {
				this.fetchStrategy.fetchAll = this.fetchAll.bind(this);
			}

			this.initialized = true;
		}

		private getState(field: GenericEditorField) {
			return (field.hasErrors) ? VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR :
				(field.hasWarnings) ? VALIDATION_MESSAGE_TYPES.WARNING : undefined;
		}

		private isPopulatorPaged(populatorName: string) {
			const populator = $injector.get(populatorName) as IDropdownPopulator;
			return populator.isPaged && populator.isPaged();
		}

		private initializeAPI() {
			if (typeof this.getApi === 'function') {
				this.getApi({
					$api: this.$api
				});
			}
		}

	}

	return SEDropdownService;
};