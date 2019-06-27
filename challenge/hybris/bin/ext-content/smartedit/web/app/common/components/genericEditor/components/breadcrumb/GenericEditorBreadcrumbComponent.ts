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

import {SeComponent} from 'smarteditcommons';
import {GenericEditorInfo, GenericEditorStackService} from 'smarteditcommons/components/genericEditor';
import {GenericEditorComponent} from 'smarteditcommons/components/genericEditor/GenericEditorComponent';

/**
 * @ngdoc directive
 * @name genericEditorBreadcrumbModule.component:genericEditorBreadcrumb
 * @element generic-editor-breadcrumb
 *
 * @description
 * Component responsible for rendering a breadcrumb on top of the generic editor.
 * @param {< String} editorStackId The string that identifies the stack of editors being edited together.
 */
@SeComponent({
	templateUrl: 'genericEditorBreadcrumbComponentTemplate.html',
	require: {
		ge: '^^genericEditor'
	}
})
export class GenericEditorBreadcrumbComponent {
	private editorsStack: GenericEditorInfo[];
	private ge: GenericEditorComponent;

	constructor(
		private $translate: angular.translate.ITranslateService,
		private genericEditorStackService: GenericEditorStackService
	) {}

	getEditorsStack(): GenericEditorInfo[] {
		if (!this.editorsStack) {
			this.editorsStack = this.genericEditorStackService.getEditorsStack(this.ge.editorStackId);
		}

		return this.editorsStack;
	}

	showBreadcrumb(): boolean {
		return this.getEditorsStack().length > 1;
	}

	getComponentName(breadcrumbItem: GenericEditorInfo): any {
		if (!breadcrumbItem.component.name) {
			return this.$translate.instant('se.breadcrumb.name.empty');
		}

		return breadcrumbItem.component.name;
	}

	get arrowIconUrl(): string {
		return 'static-resources/images/arrow_right_inactive.png';
	}

}
