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
import * as angular from "angular";
import {IUriContext, Payload, SeComponent} from "smarteditcommons";
import {
	GenericEditorAPI,
	GenericEditorStructure,
	IGenericEditor,
	IGenericEditorConstructor
} from "smarteditcommons/components/genericEditor";

/* @internal  */
interface GenericEditorComponentScope extends angular.IScope {
	componentForm?: angular.IFormController;
}

/**
 * @ngdoc directive
 * @name genericEditorModule.directive:genericEditor
 * @scope
 * @restrict E
 * @element generic-editor
 *
 * @description
 * Component responsible for generating custom HTML CRUD form for any smarteditComponent type.
 *
 * The controller has a method that creates a new instance for the {@link genericEditorModule.service:GenericEditor GenericEditor}
 * and sets the scope of smarteditComponentId and smarteditComponentType to a value that has been extracted from the original DOM element in the storefront.
 *
 * @param {= String} id Id of the current generic editor.
 * @param {= String} smarteditComponentType The SmartEdit component type that is to be created, read, updated, or deleted.
 * @param {= String} smarteditComponentId The identifier of the SmartEdit component that is to be created, read, updated, or deleted.
 * @param {< String =} structureApi The data binding to a REST Structure API that fulfills the contract described in the  {@link genericEditorModule.service:GenericEditor GenericEditor} service. Only the Structure API or the local structure must be set.
 * @param {< String =} structure The data binding to a REST Structure JSON that fulfills the contract described in the {@link genericEditorModule.service:GenericEditor GenericEditor} service. Only the Structure API or the local structure must be set.
 * @param {= String} contentApi The REST API used to create, read, update, or delete content.
 * @param {= Object} content The model for the generic editor (the initial content when the component is being edited).
 * @param {< Object =} uriContext is an optional parameter and is used to pass the uri Params which can be used in making
 * api calls in custom widgets. It is an optional parameter and if not found, generic editor will find an experience in
 * sharedDataService and set this uriContext.
 * @param {= Function =} submit It exposes the inner submit function to the invoker scope. If this parameter is set, the directive will not display an inner submit button.
 * @param {= Function =} reset It exposes the inner reset function to the invoker scope. If this parameter is set, the directive will not display an inner cancel button.
 * @param {= Function =} isDirty Indicates if the the generic editor is in a pristine state (for example: has been modified).
 * @param {= Function =} isValid Indicates if all of the containing forms and controls in the generic editor are valid.
 * @param {& Function =} getApi Exposes the generic editor's api object
 * @param {= Function =} getComponent @deprecated since 6.5, use getComponent() method of getApi. Returns the current model for the generic editor.
 * @param {< Function =} updateCallback Callback called at the end of a successful submit. It is invoked with two arguments: the pristine object and the response from the server.
 * @param {= Function =} customOnSubmit It exposes the inner onSubmit function to the invoker scope. If the parameter is set, the inner onSubmit function is overridden by the custom function and the custom function must return a promise in the response format expected by the generic editor.
 * @param {< String =} editorStackId When working with nested components, a generic editor can be opened from within another editor. This parameter is used to specify the stack of nested editors.
 */
@SeComponent({
	templateUrl: 'genericEditorComponentTemplate.html',
	inputs: [
		'id:=',
		'smarteditComponentId:=',
		'smarteditComponentType:=?',
		'contentApi:=',
		'content:=',
		'uriContext',
		'submit:=?',
		'reset:=?',
		'isDirty:=?',
		'isValid:=?',
		'getApi:&?',
		'getComponent:=?',
		'customOnSubmit:=?',
		'structureApi',
		'structure',
		'updateCallback',
		'editorStackId'
	]
})
export class GenericEditorComponent {

	public id: string;
	public smarteditComponentId: string;
	public smarteditComponentType: string;
	public structureApi: string;
	public structure: GenericEditorStructure;
	public contentApi: string;
	public content: Payload;
	public uriContext: angular.IPromise<IUriContext>;
	public submit: () => void;
	public reset: () => void;
	public isDirty: () => boolean;
	public isValid: () => boolean;
	public getApi: (api: {$api: GenericEditorAPI}) => void;
	public getComponent: () => Payload;
	public updateCallback: (pristine: Payload, results: Payload) => void;
	public customOnSubmit: () => angular.IPromise<any>;
	public editorStackId: string;
	public $doCheck: () => void;
	public showNoEditSupportDisclaimer: () => boolean;
	public editor: IGenericEditor;

	private showResetButton: boolean;
	private showSubmitButton: boolean;

	constructor(
		private $scope: GenericEditorComponentScope,
		private GenericEditor: IGenericEditorConstructor,
		private isBlank: (value: any) => boolean,
		private generateIdentifier: any,
		private yjQuery: JQueryStatic,
		private $element: angular.IAugmentedJQuery,
		private $attrs: angular.IAttributes
	) {}

	$onChanges(): void {
		if (this.editor) {
			this.editor._finalize();
		}

		this.editor = new this.GenericEditor({
			id: this.id || this.generateIdentifier(),
			smarteditComponentType: this.smarteditComponentType,
			smarteditComponentId: this.smarteditComponentId,
			editorStackId: this.editorStackId,
			structureApi: this.structureApi,
			structure: this.structure,
			contentApi: this.contentApi,
			updateCallback: this.updateCallback,
			content: this.content,
			uriContext: this.uriContext,
			customOnSubmit: this.customOnSubmit
		});

		this.editor.init();

		this.showResetButton = this.isBlank(this.$attrs.reset);
		this.showSubmitButton = this.isBlank(this.$attrs.submit);

		// #################################################################################################################

		if (typeof this.getApi === 'function') {
			/**
			 * @ngdoc method
			 * @name genericEditorModule.service:GenericEditor#getApi
			 * @methodOf genericEditorModule.service:GenericEditor
			 *
			 * @description
			 * Returns the generic editor's api object defining all public functionality
			 *
			 * @return {Object} api The {@link genericEditorModule.object:genericEditorApi GenericEditorApi} object
			 */
			this.getApi({
				$api: this.editor.api
			});
		}

		// TODO: Remove angular
		let previousContent = angular.toJson(this.editor.api.getContent());

		this.$doCheck = () => {
			// TODO: Remove angular
			const newContent = angular.toJson(this.editor.api.getContent());
			if (previousContent !== newContent) {
				previousContent = newContent;
				this.editor.api.onContentChange();
				this.editor._populateFieldsNonPristineStates();
			}
		};

		this.isDirty = () => {
			return this.editor ? this.editor.isDirty() : false;
		};

		this.reset = () => {
			if (this.editor.onReset) {
				this.editor.onReset();
			}
			return this.editor.reset();
		};

		this.submit = () => {
			return this.editor.submit();
		};

		this.getComponent = () => {
			return this.editor.getComponent();
		};

		this.isValid = () => {
			return this.editor.isValid();
		};

		/*
		 *  The generic editor wraps fields in "fieldsMap" that are instantiated after init
		 *  So we only want to display the warning if fieldsMap exists (init is finished)
		 *  but we still have no fields (holder is empty)
		 *
		 * @returns {Boolean} True if we should display the disclaimer message to the user that either
		 * the type is blacklisted or has no editable fields (there's no structure fields in technical terms)
		 */
		this.showNoEditSupportDisclaimer = () => {
			return this.editor &&
				this.editor.fieldsMap &&
				Object.keys(this.editor.fieldsMap).length === 0;
		};

		/*
		 * componentForm is normally accessed in $postLink, but in case of an $onChanges, we reaccess it here
		 */
		if (this.$scope.componentForm) {
			this.editor.componentForm = this.$scope.componentForm;
		}
	}

	$onDestroy(): void {
		this.editor._finalize();
	}

	// FIXME : unregister event on destroy
	$postLink(): void {
		// Prevent enter key from triggering form submit
		this.yjQuery(this.$element.find('.no-enter-submit')[0]).bind('keypress', (key: JQueryEventObject) => {
			return key.key !== 'Enter';
		});
		this.editor.componentForm = this.$scope.componentForm;
	}

	showCommands(): boolean {
		return this.showCancel() || this.showSubmit();
	}

	showCancel(): boolean {
		return this.editor.alwaysShowReset || (this.showResetButton === true && this.editor.isDirty() && this.editor.isValid());
	}

	showSubmit(): boolean {
		return this.editor.alwaysShowSubmit || (this.showSubmitButton === true && this.editor.isDirty() && this.editor.isValid());
	}

	isSubmitDisabled(): boolean {
		return this.editor.isSubmitDisabled();
	}

}
