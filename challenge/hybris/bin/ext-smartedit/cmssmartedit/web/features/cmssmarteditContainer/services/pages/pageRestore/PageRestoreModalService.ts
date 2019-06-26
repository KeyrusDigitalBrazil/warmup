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
import {LoDashStatic} from 'lodash';
import {CrossFrameEventService, SeInjectable, TypedMap, ValidationError} from 'smarteditcommons';
import {ICMSPage} from 'cmscommons';
import {CMSItemStructureField} from 'cmscommons/dtos';
import {PageRestoredAlertService} from 'cmssmarteditcontainer/services/actionableAlert';

interface ErrorMapping {
	subject: string;
	errorCode: string;
}

interface ActionableErrorMapping extends ErrorMapping {
	structure: CMSItemStructureField;
}

interface NonActionableErrorMapping extends ErrorMapping {
	messageKey: string;
}

@SeInjectable()
export class PageRestoreModalService {
	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------
	ACTIONABLE_ERRORS: ActionableErrorMapping[] = [{
		subject: 'name',
		errorCode: 'field.already.exist',
		structure: {
			cmsStructureType: "ShortString",
			collection: false,
			editable: true,
			i18nKey: "type.cmsitem.name.name",
			localized: false,
			paged: false,
			qualifier: "name",
			required: true
		}
	}, {
		subject: 'label',
		errorCode: 'default.page.label.already.exist',
		structure: {
			cmsStructureType: "DuplicatePrimaryContentPage",
			collection: false,
			editable: true,
			i18nKey: "se.cms.page.restore.content.duplicate.primaryforvariation.label",
			localized: false,
			paged: false,
			qualifier: "label",
			required: true
		}
	}, {
		subject: 'label',
		errorCode: 'default.page.does.not.exist',
		structure: {
			cmsStructureType: "MissingPrimaryContentPage",
			collection: false,
			editable: true,
			i18nKey: "se.cms.page.restore.content.noprimaryforvariation.label",
			localized: false,
			paged: false,
			qualifier: "label",
			required: true
		}
	}, {
		subject: 'typeCode',
		errorCode: 'default.page.already.exist',
		structure: {
			cmsStructureType: "DuplicatePrimaryNonContentPageMessage",
			collection: false,
			editable: true,
			i18nKey: "type.cmsitem.label.name",
			localized: false,
			paged: false,
			qualifier: "replace",
			required: true
		}
	}];

	NON_ACTIONABLE_ERRORS: NonActionableErrorMapping[] = [{
		subject: 'typeCode',
		errorCode: 'default.page.does.not.exist',
		messageKey: 'se.cms.page.restore.noprimaryforvariation.error'
	}];

	// ---------------------------------------------------------------------------
	// Constructor
	// ---------------------------------------------------------------------------
	constructor(
		private lodash: LoDashStatic,
		private alertService: any,
		private confirmationModalService: any,
		private genericEditorModalService: any,
		private crossFrameEventService: CrossFrameEventService,
		private pageRestoredAlertService: PageRestoredAlertService,
		private $translate: angular.translate.ITranslateService,
		private $timeout: angular.ITimeoutService,
		private GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT: string,
		private EVENTS: TypedMap<string>,
		private EVENT_CONTENT_CATALOG_UPDATE: string,
	) {}

	// ---------------------------------------------------------------------------
	// Public API
	// ---------------------------------------------------------------------------
	public handleRestoreValidationErrors(pageInfo: ICMSPage, errors: ValidationError[]): void {
		const actionableErrors: ValidationError[] = this.getActionableErrors(errors);
		const nonActionableErrors: ValidationError[] = this.getNonActionableError(errors);
		const unsupportedErrors: ValidationError[] = this.lodash.difference(errors, actionableErrors, nonActionableErrors);

		if (unsupportedErrors.length > 0) {
			this.showErrorAlert(unsupportedErrors);
		} else if (nonActionableErrors.length > 0) {
			this.showConfirmationMessage(nonActionableErrors);
		} else {
			this.showRestoreEditor(pageInfo, actionableErrors).then(() => {
				this.crossFrameEventService.publish(this.EVENT_CONTENT_CATALOG_UPDATE);
			});
		}
	}

	// ---------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------
	private showRestoreEditor(pageInfo: ICMSPage, errors: ValidationError[]): angular.IPromise<void> {
		let structureFields = this.getRestoreEditorStructureFields(errors);
		const editorData: any = {
			content: pageInfo,
			title: "se.cms.page.restore.page.title",
			componentId: pageInfo.uid,
			componentUuid: pageInfo.uuid,
			componentType: pageInfo.typeCode,
			structure: this.buildStructure(structureFields)
		};

		this.forceErrorsDisplayInEditor(pageInfo.componentUuid, errors);

		return this.genericEditorModalService.open(editorData, null, () => {
			this.pageRestoredAlertService.displayPageRestoredSuccessAlert(pageInfo);
			this.crossFrameEventService.publish(this.EVENTS.PAGE_RESTORED);
		}, (newErrors: ValidationError[], ge: any) => {
			const actionableErrors: ValidationError[] = this.getActionableErrors(newErrors);
			const unsupportedErrors: ValidationError[] = this.lodash.difference(newErrors, actionableErrors);

			if (unsupportedErrors.length > 0) {
				this.showErrorAlert(unsupportedErrors);
			} else {
				structureFields = this.lodash.concat(structureFields, this.getRestoreEditorStructureFields(newErrors));
				ge.structure = this.buildStructure(structureFields);
				this.forceErrorsDisplayInEditor(pageInfo.componentUuid, newErrors);
			}
		});

	}

	private forceErrorsDisplayInEditor(editorId: string, errors: ValidationError[]) {
		this.$timeout(() => {
			this.crossFrameEventService.publish(this.GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
				messages: errors,
				targetGenericEditorId: editorId
			});
		}, 0);
	}

	private buildStructure(structureFields: any[]): any {
		return {
			attributes: structureFields,
			category: 'PAGE'
		};
	}

	private getRestoreEditorStructureFields(errors: ValidationError[]): any {
		return errors.reduce((accumulator: any[], currentError) => {
			const errorMapping = this.ACTIONABLE_ERRORS.find((actionableErrorMapping: ActionableErrorMapping) => {
				return this.isSupportedError(currentError) && this.isEqualTo(currentError, actionableErrorMapping);
			});

			accumulator.push(errorMapping.structure);
			return accumulator;
		}, []);
	}

	private showConfirmationMessage(errors: ValidationError[]): void {
		const message = errors.reduce((accumulator, currentError) => {
			const errorMapping = this.NON_ACTIONABLE_ERRORS.find((nonActionableErrorMapping: NonActionableErrorMapping) => {
				return this.isSupportedError(currentError) && this.isEqualTo(currentError, nonActionableErrorMapping);
			});

			return accumulator + this.$translate.instant(errorMapping.messageKey) + " ";
		}, "");

		const modalConfig: any = {};
		modalConfig.description = message.trim();
		modalConfig.showOkButtonOnly = true;
		modalConfig.title = "se.cms.page.restore.error.confirmationmodal.title";

		this.confirmationModalService.confirm(modalConfig);
	}

	private showErrorAlert(errors: ValidationError[]): void {
		const errorMessage = errors.reduce((accumulator, currentError) => {
			return accumulator + currentError.message + " ";
		}, "");

		this.alertService.showDanger(errorMessage.trim());
	}

	private getActionableErrors(errors: ValidationError[]): ValidationError[] {
		return errors.filter((error) => {
			return this.ACTIONABLE_ERRORS.some((actionableErrorMapping: ActionableErrorMapping) => {
				return this.isSupportedError(error) && this.isEqualTo(error, actionableErrorMapping);
			});
		});
	}

	private getNonActionableError(errors: ValidationError[]): ValidationError[] {
		return errors.filter((error) => {
			return this.NON_ACTIONABLE_ERRORS.some((nonActionableErrorMapping: NonActionableErrorMapping) => {
				return this.isSupportedError(error) && this.isEqualTo(error, nonActionableErrorMapping);
			});
		});
	}

	private isSupportedError(error: ValidationError): boolean {
		return error.type === 'ValidationError';
	}

	private isEqualTo(error1: ValidationError, error2: ErrorMapping): boolean {
		return error1.subject === error2.subject && error1.errorCode === error2.errorCode;
	}
}