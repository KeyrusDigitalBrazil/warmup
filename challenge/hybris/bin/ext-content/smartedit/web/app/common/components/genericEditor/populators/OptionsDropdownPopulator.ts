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
import {LanguageService, SeInjectable} from 'smarteditcommons';
import {
	DropdownPopulatorInterface,
} from "./DropdownPopulatorInterface";
import {
	DropdownPopulatorPayload,
	GenericEditorOption,
} from "smarteditcommons/components/genericEditor";
/**
 * @ngdoc service
 * @name dropdownPopulatorModule.service:optionsDropdownPopulator
 * @description
 * implementation of {@link dropdownPopulatorModule.DropdownPopulatorInterface DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
 * containing options attribute.
 */
@SeInjectable()
export class OptionsDropdownPopulator extends DropdownPopulatorInterface {

	constructor(
		lodash: lo.LoDashStatic,
		private $q: angular.IQService,
		public languageService: LanguageService
	) {
		super(lodash, languageService);
	}
	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.service:optionsDropdownPopulator#populate
	 * @methodOf dropdownPopulatorModule.service:optionsDropdownPopulator
	 *
	 * @description
	 * Implementation of the {@link dropdownPopulatorModule.DropdownPopulatorInterface#populate DropdownPopulatorInterface.populate} method
	 */
	populate(payload: DropdownPopulatorPayload): angular.IPromise<GenericEditorOption[]> {
		const options = this.populateAttributes(payload.field.options as GenericEditorOption[], payload.field.idAttribute, payload.field.labelAttributes);

		if (payload.search) {
			return this.search(options, payload.search);
		}

		return this.$q.when(options);
	}
}
