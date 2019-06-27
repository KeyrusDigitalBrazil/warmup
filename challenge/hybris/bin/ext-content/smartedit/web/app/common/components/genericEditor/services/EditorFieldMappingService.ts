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
import {SeInjectable} from "smarteditcommons";
import * as angular from "angular";
import * as lo from "lodash";
import {
	GenericEditorField, GenericEditorMapping, GenericEditorMappingConfiguration, GenericEditorStructure
} from "smarteditcommons/components/genericEditor/types";

/**
 * FLOAT PRECISION
 */
/* @internal  */
export const DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION = '0.01';

/**
 * @ngdoc service
 * @name genericEditorServicesModule.service:editorFieldMappingService
 * @description
 * The editorFieldMappingService contains the strategies that the {@link genericEditorModule.directive:genericEditor genericEditor} directive
 * uses to control the rendering of a field. When the genericEditor directive is about to display a field, it queries the
 * editorFieldMappingService service to retrieve the right strategies to use. Internally, this service makes this selection based on three
 * matchers:
 * <ul>
 * 	<li><b>structureTypeNameMatcher</b>:	The matcher for the cmsStructureType of a field.</li>
 * 	<li><b>componentTypeNameMatcher</b>:	The matcher for the smarteditComponentType of the CMS component containing the field.</li>
 * <li><b>discriminatorMatcher</b>:			The matcher for the qualifier used by the genericEditor to identify the field.</li>
 * </ul>
 * These three matchers are used together to provide finer-grained control on the selection of strategies. A matcher itself is either a
 * string or a function that will be used to determine whether a value is considered a match or not. The following list describes
 * how a matcher behaves depending on its type:
 * <ul>
 *  <li><b>String</b>:                      Matches only the parameters that equal the provided string. </li>
 *  <li><b>Null</b>:                        Matches any parameter provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
 *  <li><b>Function</b>:                    Matches only the parameters for which the provided callback returns true. This option allows more
 *                                          control over the parameters to match.
 * </li>
 * </ul>
 * <br/>
 *
 * Currently, there are two types of strategies that the genericEditor uses to control the way a field is rendered:
 * <ul>
 *  <li><b>editor field mapping</b>:        This strategy is used to select and customize which property editor is to be used for this field.</li>
 *  <li><b>tab field mapping</b>:           This strategy is used to select the tab in the genericEditor where a field will be positioned.</li>
 * </ul>
 *
 */

/**
 * @ngdoc object
 * @name genericEditorServicesModule.service:PropertyEditorTemplate
 * @description
 * The purpose of the property editor template is to assign a value to model[qualifier].
 * In order to do this, all templates receive the following entities in their scope:
 */
/**
 * @ngdoc property
 * @name field
 * @propertyOf genericEditorServicesModule.service:PropertyEditorTemplate
 * @description
 * The field description of the field being edited as defined by the structure API described in {@link genericEditorModule.service:GenericEditor genericEditor}
 */
/**
 * @ngdoc property
 * @name qualifier
 * @propertyOf genericEditorServicesModule.service:PropertyEditorTemplate
 * @description
 * If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de...
 */
/**
 * @ngdoc property
 * @name model
 * @propertyOf genericEditorServicesModule.service:PropertyEditorTemplate
 * @description
 * If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier].
 *
 */
@SeInjectable()
export class EditorFieldMappingService {

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	private static readonly WILDCARD = '*';
	private static readonly MATCH = {
		NONE: 0,
		PARTIAL: 1,
		EXACT: 4 // An exact match is always better than a partial.
	};

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	public _editorsFieldMapping: GenericEditorMapping[] = [];
	public _fieldsTabsMapping: GenericEditorMapping[] = [];

	private _initialized: boolean;

	constructor(
		private GENERIC_EDITOR_FLOAT_PRECISION: string,
		private $log: angular.ILogService,
		private lodash: lo.LoDashStatic
	) {}

	// --------------------------------------------------------------------------------------
	// Deprecated API
	// --------------------------------------------------------------------------------------
	/**
	 * @deprecated since 6.6 Use {@link genericEditorServicesModule.service:editorFieldMappingService#getEditorFieldMapping getEditorFieldMapping()} instead.
	 *
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:editorFieldMappingService#getFieldMapping
	 * @methodOf genericEditorServicesModule.service:editorFieldMappingService
	 * @description
	 * Deprecated. Use {@link genericEditorServicesModule.service:editorFieldMappingService#getEditorFieldMapping getEditorFieldMapping()}
	 * instead.
	 *
	 * This method retrieves the property editor rendered for the specified cmsStructureType in a generic editor.
	 *
	 * Note:
	 * Currently, all templates in SmartEdit use the short form. Before returning a response, this method ensures that
	 * the template provided to the generic editor is in short form. For example:
	 * - A template 'genericEditor/templates/shortStringTemplate.html' will be transformed to 'shortStringTemplate.html'
	 *
	 * @param {String} structureTypeName The cmsStructureType to retrieve the property editor for.
	 * @param {String=} componentTypeName The cmsComponentType to retrieve the property editor for.
	 * @param {String=} discriminator The field name of the SmartEdit component type to retrieve the discriminator for.
	 */
	getFieldMapping(structureTypeName: string, componentTypeName: string, discriminator: string, componentTypeStructure: GenericEditorStructure): any {
		const field = {
			cmsStructureType: structureTypeName,
			smarteditComponentType: componentTypeName,
			qualifier: discriminator
		};

		return this.getEditorFieldMapping(field, componentTypeStructure);
	}

	// --------------------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------------------
	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:editorFieldMappingService#addFieldMapping
	 * @methodOf genericEditorServicesModule.service:editorFieldMappingService
	 * @description
	 * This method overrides the default strategy of the {@link genericEditorModule.directive:genericEditor genericEditor} directive
	 * used to choose the property editor for a given field. Internally, this selection is based on three matchers:
	 * <ul>
	 * 	<li><b>structureTypeNameMatcher</b>:	The matcher for the cmsStructureType of a field.</li>
	 * 	<li><b>componentTypeNameMatcher</b>:	The matcher for the smarteditComponentType of the CMS component containing the field.</li>
	 * <li><b>discriminatorMatcher</b>:			The matcher for the qualifier used by the genericEditor to identify the field.</li>
	 * </ul>
	 * Only the fields that match all three matchers will be overriden.
	 *
	 * The following example shows how some sample fields would match some mappings:
	 * <pre>
	 * const field1 = {
	 * 		cmsStructureType: 'ShortString',
	 * 		smarteditComponentType: 'CmsParagraphComponent',
	 * 		qualifier: 'name'
	 * };
	 *
	 *  const field2 = {
	 *      cmsStructureType: 'Boolean',
	 *      smarteditComponentType: 'CmsParagraphComponent',
	 *      qualifier: 'visible'
	 *  };
	 * </pre>
	 *
	 * <pre>
	 * // This mapping won't match any of the fields. They don't match all three matchers.
	 * editorFieldMappingService.addFieldMapping(
	 *  'ShortString', 'CmsParagraphComponent', 'visible', configuration);
	 *
	 * // This mapping will match field2. It matches all three matchers perfectly.
	 * editorFieldMappingService.addFieldMapping(
	 *  'Boolean', 'CmsParagraphComponent', 'visible', configuration);
	 *
	 * // This mapping will match both fields. They match all three matchers.
	 * // Note that both null and '*' represent a wildcard that accepts any value.
	 * editorFieldMappingService.addFieldMapping(
	 *  null, 'CmsParagraphComponent', '*', configuration);
	 * </pre>
	 *
	 * <b>Note:</b> <br/>
	 * The genericEditor has some predefined editors for the following cmsStructureTypes:
	 * <ul>
	 * 		<li><b>ShortString</b>:			Displays a text input field.</li>
	 * 		<li><b>LongString</b>:  		Displays a text area.</li>
	 * 		<li><b>RichText</b>:    		Displays an HTML/rich text editor.</li>
	 * 		<li><b>Boolean</b>:     		Displays a check box.</li>
	 * 		<li><b>DateTime</b>:        	Displays an input field with a date-time picker.</li>
	 * 		<li><b>Media</b>:       		Displays a filterable dropdown list of media</li>
	 * 		<li><b>Enum</b>:		 		Displays a filterable dropdown list of the enum class values identified by cmsStructureEnumType property.
	 * 		<li><b>EditableDropdown</b>: 	Displays a configurable dropdown list that is enabled by {@link seDropdownModule.directive:seDropdown seDropdown} directive.
	 * </ul>
	 * <br />
	 * You can program the {@link genericEditorModule.directive:genericEditor genericEditor} to use other property editors for these
	 * cmsStructureTypes. You can also add custom cmsStructureTypes.
	 * All default and custom property editors are HTML templates. These templates must adhere to the PropertyEditorTemplate {@link genericEditorServicesModule.service:PropertyEditorTemplate contract}.
	 *
	 * @param {String|Function} structureTypeNameMatcher The matcher used to identify the cmsStructureTypes for which a custom property editor is
	 * required.
	 * There are three possible values for this parameter:
	 *  <li><b>String</b>:                      Matches only the cmsStructureTypes whose name equals this string. </li>
	 *  <li><b>Null</b>:                        Matches any cmsStructureType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
	 *  <li><b>Function</b>:                    Matches only the cmsStructureTypes for which the provided callback returns true. This option allows more
	 *                                          control over the types to match.
	 *
	 * This function will be called with three parameters:
	 * <ul>
	 *  <li><b>cmsStructureType:</b>           The cmsStructureType of the field.</li>
	 *  <li><b>field:</b>                      The field to evaluate.</li>
	 *  <li><b>componentTypeStructure:</b>     The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param {String | Function} componentTypeNameMatcher The matcher used to identify the smarteditComponentType for which a custom property editor is
	 * required.
	 * There are three possible values for this parameter:
	 *  <li><b>String</b>:                      Matches only the smarteditComponentType whose name equals this string. </li>
	 *  <li><b>Null</b>:                        Matches any smarteditComponentType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
	 *  <li><b>Function</b>:                    Matches only the smarteditComponentType for which the provided callback returns true. This option allows more
	 *                                          control over the types to match.
	 *
	 * This function will be called with three parameters:
	 * <ul>
	 *  <li><b>componentTypeName:</b>         The smarteditComponentType name of the field. </li>
	 *  <li><b>field:</b>                     The field to evaluate. </li>
	 *  <li><b>componentTypeStructure:</b>    The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param {String | Function} discriminatorMatcher The matcher used to identify the discriminator for which a custom property editor is
	 * required.
	 * There are three possible values for this parameter:
	 *  <li><b>String</b>:                      Matches only the discriminators whose name equals this string. </li>
	 *  <li><b>Null</b>:                        Matches any discriminator provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
	 *  <li><b>Function</b>:                    Matches only the discriminators for which the provided callback returns true. This option allows more
	 *                                          control over the types to match.
	 *
	 * This function will be called with three parameters:
	 * <ul>
	 *  <li><b>discriminator</b>:               The discriminator of the field to evaluate. </li>
	 *  <li><b>field:</b>                       The field to evaluate. </li>
	 *  <li><b>componentTypeStructure:</b>      The smarteditComponentType of the component that contains the field to evaluate.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param {Object} configuration The holder that contains the override instructions. Cannot be null.
	 * @param {String} configuration.template The path to the HTML template used in the override. Cannot be null.
	 * @param {Function=} configuration.customSanitize Custom sanitize function for a custom property editor. It's provided with a payload and
	 * an optional {@link functionsModule.sanitize sanitize} function.
	 */
	addFieldMapping(structureTypeName: string, componentTypeName: string, discriminator: string, configuration: GenericEditorMappingConfiguration): void {
		this._addMapping(structureTypeName, componentTypeName, discriminator, configuration, this._editorsFieldMapping);
	}

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:editorFieldMappingService#getEditorFieldMapping
	 * @methodOf genericEditorServicesModule.service:editorFieldMappingService
	 * @description
	 *
	 * This method is used by the genericEditor to retrieve the property editor to be rendered in a generic editor, along with its configuration.
	 * If more than one property editor could be applied to the provided field, the one with the most accurate match will be used.
	 *
	 * Note:
	 * Currently, all templates in SmartEdit use the short form. Before returning a response, this method ensures that
	 * the template provided to the generic editor is in short form. For example:
	 * - A template 'genericEditor/templates/shortStringTemplate.html' will be transformed to 'shortStringTemplate.html'
	 *
	 * @param {Object} field The object that represents the field that the property editor is retrieved for.
	 * @param {String} field.cmsStructureType The cmsStructureType that the property editor is retrieved for.
	 * @param {String=} field.smarteditComponentType The smarteditComponentType that the property editor is retrieved for.
	 * @param {String=} field.qualifier The field name of the smarteditComponentType that the property editor is retrieved for.
	 * @param {Object} componentTypeStructure The smarteditComponentType structure of the componenent that contains the field that the property editor
	 * is retrieved for.
	 * @return {Object} The configuration of the property editor to be used for this field. Can be null if no adequate match is found.
	 *
	 */
	getEditorFieldMapping(field: GenericEditorField, componentTypeStructure: GenericEditorStructure): any {
		let fieldMapping = this._getMapping(field, componentTypeStructure, this._editorsFieldMapping);
		if (!fieldMapping) {
			this.$log.warn('editorFieldMappingService - Cannot find suitable field mapping for type ', field.cmsStructureType);
			fieldMapping = null;
		} else {
			fieldMapping.template = this._cleanTemplate(fieldMapping.template);
		}

		return fieldMapping;
	}

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:editorFieldMappingService#addFieldTabMapping
	 * @methodOf genericEditorServicesModule.service:editorFieldMappingService
	 * @description
	 * This method overrides the default strategy of the {@link genericEditorModule.directive:genericEditor genericEditor} directive
	 * used to choose the tab where to render a field in the generic editor. Internally, this selection is based on three elements:
	 * <ul>
	 * 	<li><b>structureTypeName</b>:			The cmsStructureType of a field.</li>
	 * 	<li><b>componentTypeName</b>:			The smarteditComponentType of the component containing the field.</li>
	 * <li><b>discriminator</b>:			    The qualifier used by the genericEditor to identify the field.</li>
	 * </ul>
	 * Only the fields that match all three elements will be overriden.
	 *
	 * The following example shows how sample fields would match some mappings:
	 * <pre>
	 *  const field1 = {
	 *      cmsStructureType: 'ShortString',
	 *      smarteditComponentType: 'CmsParagraphComponent',
	 *      qualifier: 'name'
	 *  };
	 *
	 *  const field2 = {
	 *      cmsStructureType: 'Boolean',
	 *      smarteditComponentType: 'CmsParagraphComponent',
	 *      qualifier: 'visible'
	 *  };
	 * </pre>
	 *
	 * <pre>
	 * // This mapping won't match any of the fields. They don't match all three matchers.
	 * editorFieldMappingService.addFieldTabMapping(
	 *  'ShortString', 'CmsParagraphComponent', 'visible', tabId);
	 *
	 * // This mapping will match field2. It matches all three matchers perfectly.
	 * editorFieldMappingService.addFieldTabMapping(
	 *  'Boolean', 'CmsParagraphComponent', 'visible', tabId);
	 *
	 * // This mapping will match both fields. They match all three matchers.
	 * // Note that both null and '*' represent a wildcard that accepts any value.
	 * editorFieldMappingService.addFieldTabMapping(
	 *  null, 'CmsParagraphComponent', '*', tabId);
	 * </pre>
	 *
	 * @param {String|Function} structureTypeNameMatcher The matcher used to identify the cmsStructureTypes for which to find its tab.
	 * There are three possible values for this parameter:
	 *  <li><b>String</b>:                      Matches only the cmsStructureTypes whose name equals this string. </li>
	 *  <li><b>Null</b>:                        Matches any cmsStructureType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
	 *  <li><b>Function</b>:                    Matches only the cmsStructureTypes for which the provided callback returns true. This option allows more
	 *                                          control over the types to match.
	 *
	 * This function will be called with three parameters:
	 * <ul>
	 *  <li><b>cmsStructureType:</b>           The cmsStructureType of the field.</li>
	 *  <li><b>field:</b>                      The field to evaluate.</li>
	 *  <li><b>componentTypeStructure:</b>     The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param {String | Function} componentTypeNameMatcher The matcher used to identify the smarteditComponentType for which for which to find its tab.
	 * There are three possible values for this parameter:
	 *  <li><b>String</b>:                      Matches only the smarteditComponentType whose name equals this string. </li>
	 *  <li><b>Null</b>:                        Matches any smarteditComponentType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
	 *  <li><b>Function</b>:                    Matches only the smarteditComponentType for which the provided callback returns true. This option allows more
	 *                                          control over the types to match.
	 *
	 * This function will be called with three parameters:
	 * <ul>
	 *  <li><b>componentTypeName:</b>         The smarteditComponentType name of the field. </li>
	 *  <li><b>field:</b>                     The field to evaluate. </li>
	 *  <li><b>componentTypeStructure:</b>    The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param {String | Function} discriminatorMatcher The matcher used to identify the discriminator for which for which to find its tab.
	 * There are three possible values for this parameter:
	 *  <li><b>String</b>:                      Matches only the discriminators whose name equals this string. </li>
	 *  <li><b>Null</b>:                        Matches any discriminator provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
	 *  <li><b>Function</b>:                    Matches only the discriminators for which the provided callback returns true. This option allows more
	 *                                          control over the types to match.
	 *
	 * This function will be called with three parameters:
	 * <ul>
	 *  <li><b>discriminator</b>:               The discriminator of the field to evaluate. </li>
	 *  <li><b>field:</b>                       The field to evaluate. </li>
	 *  <li><b>componentTypeStructure:</b>      The smarteditComponentType of the component that contains the field to evaluate.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param {String} tabId The ID of the tab where the field must be rendered in the generic editor.
	 */
	addFieldTabMapping(structureTypeName: string, componentTypeName: string, discriminator: string, tabId: string): void {
		this._addMapping(structureTypeName, componentTypeName, discriminator, tabId, this._fieldsTabsMapping);
	}

	/**
	 * @ngdoc method
	 * @name genericEditorServicesModule.service:editorFieldMappingService#getFieldTabMapping
	 * @methodOf genericEditorServicesModule.service:editorFieldMappingService
	 * @description
	 *
	 * This method is used by the genericEditor to retrieve the tab where the field will be rendered in the generic editor.
	 * If more than one tab matches the field provided, then the tab with the most accurate match will be used.
	 *
	 * @param {Object} field The object that represents the field that the tab is retrieved for.
	 * @param {String} field.cmsStructureType The cmsStructureType that the tab is retrieved for.
	 * @param {String=} field.smarteditComponentType The smarteditComponentType that the tab is retrieved for.
	 * @param {String=} field.qualifier The field name of the smarteditComponentType that the tab is retrieved for.
	 * @param {Object} componentTypeStructure The smarteditComponentType structure of the component that contains the field that the tab is retrieved for.
	 * @return {String} The ID of the tab where this field must reside. Can be null if no adequate match is found.
	 *
	 */
	getFieldTabMapping(field: GenericEditorField, componentTypeStructure: GenericEditorStructure): any {
		return this._getMapping(field, componentTypeStructure, this._fieldsTabsMapping);
	}

	// --------------------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------------------
	_addMapping(structureTypeMatcher: string, componentTypeMatcher: string, discriminatorMatcher: string, mappedValue: any, collection: GenericEditorMapping[]): void {
		structureTypeMatcher = this._validateField(structureTypeMatcher);
		componentTypeMatcher = this._validateField(componentTypeMatcher);
		discriminatorMatcher = this._validateField(discriminatorMatcher);

		const newMapping = {
			structureTypeMatcher,
			componentTypeMatcher,
			discriminatorMatcher,
			value: mappedValue
		};

		this.lodash.remove(collection, (element: GenericEditorMapping) => {
			return (
				element.structureTypeMatcher === structureTypeMatcher &&
				element.componentTypeMatcher === componentTypeMatcher &&
				element.discriminatorMatcher === discriminatorMatcher);
		});

		collection.push(newMapping);
	}

	_validateField(field: string): string {
		if (field === null) {
			field = EditorFieldMappingService.WILDCARD;
		} else if (typeof field !== 'string' && typeof field !== 'function') {
			throw new Error('editorFieldMappingService: Mapping matcher must be of type string or function.');
		}

		return field;
	}

	_getMapping(field: GenericEditorField, componentTypeStructure: GenericEditorStructure, collection: GenericEditorMapping[]): any {
		let result = null;
		let maxValue = 0;
		collection.forEach((mappingEntry) => {
			const mappingValue = this._evaluateMapping(mappingEntry, field, componentTypeStructure);
			if (mappingValue > maxValue) {
				result = mappingEntry.value;
				maxValue = mappingValue;
			}
		});

		return result;
	}

	_evaluateMapping(mappingEntry: GenericEditorMapping, field: GenericEditorField, componentTypeStructure: GenericEditorStructure): number {
		let structureTypeMatch;
		let componentTypeMatch;
		let discriminatorMatch;

		let mappingMatch = EditorFieldMappingService.MATCH.NONE;

		structureTypeMatch = this._evaluateMatcher(mappingEntry.structureTypeMatcher, field.cmsStructureType, field, componentTypeStructure);
		if (structureTypeMatch !== EditorFieldMappingService.MATCH.NONE) {
			componentTypeMatch = this._evaluateMatcher(mappingEntry.componentTypeMatcher, field.smarteditComponentType, field, componentTypeStructure);
			if (componentTypeMatch !== EditorFieldMappingService.MATCH.NONE) {
				discriminatorMatch = this._evaluateMatcher(mappingEntry.discriminatorMatcher, field.qualifier, field, componentTypeStructure);
			}
		}

		if (structureTypeMatch !== EditorFieldMappingService.MATCH.NONE && componentTypeMatch !== EditorFieldMappingService.MATCH.NONE && discriminatorMatch !== EditorFieldMappingService.MATCH.NONE) {
			mappingMatch = structureTypeMatch + componentTypeMatch + discriminatorMatch;
		}

		return mappingMatch;
	}

	_evaluateMatcher(matcher: string | ((...args: any[]) => void), actualValue: string, field: GenericEditorField, componentTypeStructure: GenericEditorStructure): number {
		if (typeof matcher === 'string') {
			if (matcher === EditorFieldMappingService.WILDCARD) {
				return EditorFieldMappingService.MATCH.PARTIAL;
			} else {
				return (this._exactValueMatchPredicate(matcher, actualValue)) ? EditorFieldMappingService.MATCH.EXACT : EditorFieldMappingService.MATCH.NONE;
			}
		} else {
			return (matcher(actualValue, field, componentTypeStructure)) ? EditorFieldMappingService.MATCH.EXACT : EditorFieldMappingService.MATCH.NONE;
		}
	}

	_registerDefaultFieldMappings(): void {
		if (!this._initialized) {
			this._initialized = true;
			this.addFieldMapping('Boolean', null, null, {
				template: 'booleanWrapperTemplate.html'
			});

			this.addFieldMapping('ShortString', null, null, {
				template: 'shortStringTemplate.html'
			});

			this.addFieldMapping('LongString', null, null, {
				template: 'longStringTemplate.html'
			});

			this.addFieldMapping('RichText', null, null, {
				template: 'richTextTemplate.html'
			});

			this.addFieldMapping('Number', null, null, {
				template: 'numberTemplate.html'
			});

			this.addFieldMapping('Float', null, null, {
				template: 'floatTemplate.html',
				precision: this.GENERIC_EDITOR_FLOAT_PRECISION
			});

			this.addFieldMapping('Dropdown', null, null, {
				template: 'dropdownTemplate.html'
			});

			this.addFieldMapping('EditableDropdown', null, null, {
				template: 'dropdownWrapperTemplate.html'
			});

			this.addFieldMapping('DateTime', null, null, {
				template: 'dateTimePickerWrapperTemplate.html'
			});

			this.addFieldMapping('Enum', null, null, {
				template: 'enumTemplate.html'
			});
		}

	}

	_cleanTemplate(template: string): string {
		const index = (template) ? template.lastIndexOf('/') : -1;
		if (index !== -1) {
			template = template.substring(index + 1);
		}

		return template;
	}

	// --------------------------------------------------------------------------------------
	// Predicates
	// --------------------------------------------------------------------------------------
	_exactValueMatchPredicate(expectedValue: string, actualValue: string): boolean {
		return expectedValue === actualValue;
	}

}
