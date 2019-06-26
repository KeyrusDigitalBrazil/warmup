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
import {SeInjectable} from "smarteditcommons/services/dependencyInjection/di";
import {Configuration, ConfigurationItem} from 'smarteditcontainer/services/bootstrap/Configuration';
import IResource = angular.resource.IResource;

/** @internal */
@SeInjectable()
export class ConfigurationService {

	// Constants
	public ABSOLUTE_URI_NOT_APPROVED = "URI_EXCEPTION";
	public ABSOLUTE_URI_REGEX = /(\"[A-Za-z]+:\/|\/\/)/;

	private editorCRUDService: angular.resource.IResourceClass<IResource<Configuration>>;
	private configuration: Configuration;
	private pristine: Configuration;
	private loadCallback: () => void;

	constructor(
		private $resource: angular.resource.IResourceService,
		private copy: any,
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private loadConfigManagerService: any,
		private ParseError: any,
		private CONFIGURATION_URI: any,
		private isBlank: any

	) {
		this.editorCRUDService = this.$resource(this.CONFIGURATION_URI, {}, {
			update: {
				method: 'PUT',
				cache: false,
				isArray: false
			},
			remove: {
				method: 'DELETE',
				cache: false,
				isArray: false
			},
			save: {
				method: 'POST',
				cache: false,
				isArray: false
			}
		});
		this.configuration = [];
	}
	_reset(configurationForm?: any) {
		this.configuration = this.copy(this.pristine);
		if (configurationForm) {
			configurationForm.$setPristine();
		}
		if (this.loadCallback) {
			this.loadCallback();
		}
	}
	_addError(entry: ConfigurationItem, type: string, message: string) {
		entry.errors = entry.errors || {};
		entry.errors[type] = entry.errors[type] || [];
		entry.errors[type].push({
			message
		});
	}
	_addKeyError(entry: ConfigurationItem, message: string) {
		this._addError(entry, "keys", message);
	}
	_addValueError(entry: ConfigurationItem, message: string) {
		this._addError(entry, "values", message);
	}
	_prettify(array: ConfigurationItem[]) {
		const configuration = this.copy(array);
		configuration.forEach((entry: ConfigurationItem) => {
			try {
				entry.value = JSON.stringify(JSON.parse(entry.value), null, 2);
			} catch (parseError) {
				this._addValueError(entry, 'se.configurationform.json.parse.error');
			}
		});
		return configuration;
	}
    /**
     * for editing purposes
     */
	loadAndPresent(): angular.IPromise<any> {
		const deferred = this.$q.defer();
		this.loadConfigManagerService.loadAsArray().then((response: ConfigurationItem[]) => {
			this.pristine = this._prettify(response);
			this._reset();
			deferred.resolve();
		}, () => {
			this.$log.log("load failed");
			deferred.reject();
		});
		return deferred.promise;
	}
    /*
     * The Add Entry method adds an entry to the list of configurations.
     *
     */
	addEntry() {
		this.configuration.unshift({
			key: '',
			value: '',
			isNew: true
		});
	}
    /*
     * The Remove Entry method deletes the specified entry from the list of configurations. The method does not delete the actual configuration, but just removes it from the array of configurations.
     * The entry will be deleted when a user clicks the Submit button but if the entry is new we can are removing it from the configuration
     *
     * @param {Object} entry The object to be deleted
     * @param {Object} configurationForm The form object which is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}
     * that provides methods to monitor and control the state of the form.
     */
	removeEntry(entry: ConfigurationItem, configurationForm: any) {
		if (entry.isNew) {
			this.configuration = this.configuration.filter((confEntry: ConfigurationItem) => {
				return !confEntry.isNew || confEntry.key !== entry.key;
			});
		} else {
			configurationForm.$setDirty();
			entry.toDelete = true;
		}
	}
    /*
     * Method that returns a list of configurations by filtering out only those configurations whose 'toDelete' parameter is set to false.
     *
     * @returns {Object} A list of filtered configurations.
     */
	filterConfiguration() {
		return this.configuration.filter((instance: ConfigurationItem) => {
			return instance.toDelete !== true;
		});
	}
	_validate(entry: ConfigurationItem) {
		try {
			if (entry.requiresUserCheck && !entry.isCheckedByUser) {
				throw new Error(this.ABSOLUTE_URI_NOT_APPROVED);
			}
			return JSON.stringify(JSON.parse(entry.value));
		} catch (parseError) {
			throw new this.ParseError(entry.value);
		}
	}
	_isValid(configurationForm: any) {
		this.configuration.forEach((entry: ConfigurationItem) => {
			delete entry.errors;
		});
		if (configurationForm.$invalid) {
			this.configuration.forEach((entry: ConfigurationItem) => {
				if (this.isBlank(entry.key)) {
					this._addKeyError(entry, 'se.configurationform.required.entry.error');
					entry.hasErrors = true;
				}
				if (this.isBlank(entry.value)) {
					this._addValueError(entry, 'se.configurationform.required.entry.error');
					entry.hasErrors = true;
				}
			});
		}
		return configurationForm.$valid && !this.configuration.reduce(function(confHolder: any, nextConfiguration: ConfigurationItem) {
			if (confHolder.keys.indexOf(nextConfiguration.key) > -1) {
				this._addKeyError(nextConfiguration, 'se.configurationform.duplicate.entry.error');
				confHolder.errors = true;
			} else {
				confHolder.keys.push(nextConfiguration.key);
			}
			return confHolder;
		}.bind(this), {
				keys: [],
				errors: false
			}).errors;
	}
	_validateUserInput(entry: ConfigurationItem) {
		if (entry.value) {
			entry.requiresUserCheck = (entry.value.match(this.ABSOLUTE_URI_REGEX)) ? true : false;
		}
	}
    /*
     * The Submit method saves the list of available configurations by making a REST call to a web service.
     * The method is called when a user clicks the Submit button in the configuration editor.
     *
     * @param {Object} configurationForm The form object that is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}.
     * It provides methods to monitor and control the state of the form.
     */
	submit(configurationForm: any) {
		const deferred = this.$q.defer();
		if (configurationForm.$dirty && this._isValid(configurationForm)) {
			this.configuration.forEach((entry: ConfigurationItem, i: number) => {
				try {
					let payload = this.copy(entry);
					delete payload.toDelete;
					delete payload.errors;
					const method = entry.toDelete === true ? 'remove' : (payload.isNew === true ? 'save' : 'update');
					payload.secured = false; // needed for yaas configuration service
					delete payload.isNew;
					let params;
					switch (method) {
						case 'save':
							payload.value = this._validate(payload);
							params = {};
							break;
						case 'update':
							payload.value = this._validate(payload);
							params = {
								key: payload.key
							};
							break;
						case 'remove':
							params = {
								key: payload.key
							};
							payload = undefined;
							break;
					}
					(this.editorCRUDService as any)[method](params, payload).$promise.then(function(entity: ConfigurationItem, index: number, meth: string) {
						switch (meth) {
							case 'save':
								delete entity.isNew;
								break;
							case 'remove':
								this.configuration.splice(index, 1);
								break;
						}
					}.bind(this, entry, i, method), () => {
						this._addValueError(entry, 'configurationform.save.error');
						deferred.reject();
					});
					entry.hasErrors = false;
				} catch (error) {
					if (error instanceof this.ParseError) {
						this._addValueError(entry, 'se.configurationform.json.parse.error');
						deferred.reject();
					}
					entry.hasErrors = true;
				}
			});
			deferred.resolve();
			configurationForm.$setPristine();
		} else {
			deferred.reject();
		}
		return deferred.promise;
	}
    /*
     * The init method initializes the configuration editor and loads all the configurations so they can be edited.
     *
     * @param {Function} loadCallback The callback to be executed after loading the configurations.
     */
	init(_loadCallback: () => void): angular.IPromise<any> {
		this.loadCallback = _loadCallback;
		return this.loadAndPresent();
	}
}