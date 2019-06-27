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
import {ISeComponent, SeComponent, TypedMap} from "smarteditcommons";
import {GenericEditorField} from "smarteditcommons/components/genericEditor";
import {
	ISEDropdownService,
	ISEDropdownServiceConstructor, SEDropdownAPI
} from "smarteditcommons/components/genericEditor/components/dropdown/types";

/**
 * @ngdoc directive
 * @name seDropdownModule.directive:seDropdown
 * @scope
 * @restrict E
 * @element se-dropdown
 *
 * @description
 * This directive generates a custom dropdown (standalone or dependent on another one) for the {@link genericEditorModule.service:GenericEditor genericEditor}.
 * It is an implementation of the PropertyEditorTemplate {@link genericEditorServicesModule.service:PropertyEditorTemplate contract}.
 * <br/>{@link genericEditorServicesModule.service:editorFieldMappingService editorFieldMappingService} maps seDropdown by default to the "EditableDropdown" cmsStructureType.
 * <br/>The dropdown will be configured and populated based on the field structure retrieved from the Structure API.
 * The following is an example of the 4 possible field structures that can be returned by the Structure API for seDropdown to work:
 * <pre>
 * [
 * ...
 * {
 * 		cmsStructureType: "EditableDropdown",
 * 		qualifier: "someQualifier1",
 * 		i18nKey: 'i18nkeyForsomeQualifier1',
 * 		idAttribute: "id",
 * 		labelAttributes: ["label"],
 * 		paged: false,
 * 		options: [{
 *      	id: '1',
 *      	label: 'option1'
 *      	}, {
 *      	id: '2',
 *      	label: 'option2'
 *      	}, {
 *      	id: '3',
 *      	label: 'option3'
 *      }],
 * }, {
 * 		cmsStructureType: "EditableDropdown",
 * 		qualifier: "someQualifier2",
 * 		i18nKey: 'i18nkeyForsomeQualifier2',
 * 		idAttribute: "id",
 * 		labelAttributes: ["label"],
 * 		paged: false,
 * 		uri: '/someuri',
 * 		dependsOn: 'someQualifier1'
 * }, {
 * 		cmsStructureType: "EditableDropdown",
 * 		qualifier: "someQualifier2",
 * 		i18nKey: 'i18nkeyForsomeQualifier2',
 * 		idAttribute: "id",
 * 		labelAttributes: ["label"],
 * 		paged: false,
 * }, {
 * 		cmsStructureType: "EditableDropdown",
 * 		qualifier: "someQualifier3",
 * 		i18nKey: 'i18nkeyForsomeQualifier3',
 * 		idAttribute: "id",
 * 		labelAttributes: ["label"],
 * 		paged: false,
 * 		propertyType: 'somePropertyType',
 * }
 * ...
 * ]
 * </pre>
 *
 * <br/>If uri, options and propertyType are not set, then seDropdown will look for an implementation of {@link dropdownPopulatorModule.DropdownPopulatorInterface DropdownPopulatorInterface} with the following AngularJS recipe name:
 * <pre>smarteditComponentType + qualifier + "DropdownPopulator"</pre>
 * and default to:
 * <pre>smarteditComponentType + "DropdownPopulator"</pre>
 * If no custom populator can be found, an exception will be raised.
 * <br/><br/>For the above example, since someQualifier2 will depend on someQualifier1, then if someQualifier1 is changed, then the list of options
 * for someQualifier2 is populated by calling the populate method of {@link dropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator}.
 *
 * @param {= Object} field The field description of the field being edited as defined by the structure API described in {@link genericEditorModule.service:GenericEditor genericEditor}.
 * @param {Array =} field.options An array of options to be populated.
 * @param {String =} field.uri The uri to fetch the list of options from a REST call, especially if the dropdown is dependent on another one.
 * @param {String =} field.propertyType If a propertyType is defined, the seDropdown will use the populator associated to it with the following AngularJS recipe name : <pre>propertyType + "DropdownPopulator"</pre>.
 * @param {String =} field.dependsOn The qualifier of the parent dropdown that this dropdown depends on.
 * @param {String =} field.idAttribute The name of the id attribute to use when populating dropdown items.
 * @param {Array =} field.labelAttributes An array of attributes to use when determining the label for each item in the dropdown
 * @param {Boolean =} field.paged A boolean to determine if we are in paged mode as opposed to retrieving all items at once.
 * @param {= String} qualifier If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de...
 * @param {= Object} model If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier].
 * @param {= String} id An identifier of the generated DOM element.
 * @param {< String =} itemTemplateUrl the path to the template that will be used to display items in both the dropdown menu and the selection.
 * @param {& Function =} getApi Exposes the seDropdown's api object
 */
@SeComponent({
	templateUrl: 'seDropdownTemplate.html',
	inputs: [
		'field:=',
		'qualifier:=',
		'model:=',
		'id:=',
		'getApi:&?',
		'itemTemplateUrl',
	]
})
export class SeDropdownComponent implements ISeComponent {

	public field: GenericEditorField;
	public qualifier: string;
	public model: TypedMap<any>;
	public id: string;
	public itemTemplateUrl: string;
	public getApi: ($api: {$api: SEDropdownAPI}) => void;
	public dropdown: ISEDropdownService;

	constructor(
		private SEDropdownService: ISEDropdownServiceConstructor,
		private CONTEXT_CATALOG: string,
		private CONTEXT_CATALOG_VERSION: string,
		private yjQuery: JQueryStatic
	) {}

	$onInit(): void {
		this.field.params = this.field.params || {};
		this.field.params.catalogId = this.field.params.catalogId || this.CONTEXT_CATALOG;
		this.field.params.catalogVersion = this.field.params.catalogVersion || this.CONTEXT_CATALOG_VERSION;

		this.dropdown = new this.SEDropdownService({
			field: this.field,
			qualifier: this.qualifier,
			model: this.model,
			id: this.id,
			onClickOtherDropdown: this.onClickOtherDropdown.bind(this),
			getApi: this.getApi
		});

		this.dropdown.init();
	}

	onClickOtherDropdown(): void {
		this.closeSelect();
	}

	closeSelect(): void {
		const uiSelectCtrl = this.getUiSelectCtrl();
		if (uiSelectCtrl) {
			uiSelectCtrl.open = false;
		}
	}

	getUiSelectCtrl(): any {
		const uiSelectId = "#" + this.field.qualifier + "-selector";

		return this.yjQuery(uiSelectId).controller("uiSelect");
	}

}
