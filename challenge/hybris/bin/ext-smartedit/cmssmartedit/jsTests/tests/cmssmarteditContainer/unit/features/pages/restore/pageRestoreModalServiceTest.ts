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

import {promiseHelper, PromiseType} from 'testhelpers';
import {CrossFrameEventService, ValidationError} from 'smarteditcommons';
import {PageRestoreModalService} from "cmssmarteditcontainer/services/pages";
import {PageRestoredAlertService} from 'cmssmarteditcontainer/services/actionableAlert';
import {CMSPageStatus, CMSPageTypes, ICMSPage} from 'cmscommons';

describe('pageRestoreModalService', () => {
	// ---------------------------------------------------------------------------
	// Constants
	// ---------------------------------------------------------------------------
	const GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT = "some validation messages event";
	const EVENTS = {
		PAGE_RESTORED: 'some page restored'
	};
	const PAGE_UID = 'some uid';
	const PAGE_UUID = 'some uuid';
	const PAGE_TYPE_CODE = CMSPageTypes.ContentPage;

	const INVALID_ERROR_MSG_1 = 'INVALID ERROR MSG 1';
	const INVALID_ERROR_MSG_2 = 'INVALID ERROR MSG 2';

	const EVENT_CONTENT_CATALOG_UPDATE: string = 'EVENT_CONTENT_CATALOG_UPDATE';

	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;

	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------
	let pageInfo: ICMSPage;
	let invalidError1: ValidationError;
	let invalidError2: ValidationError;
	let actionableError1: ValidationError;
	let actionableError2: ValidationError;
	let nonActionableError: ValidationError;

	let alertService: any;
	let confirmationModalService: any;
	let genericEditorModalService: any;
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
	let pageRestoredAlertService: jasmine.SpyObj<PageRestoredAlertService>;
	let $translate: jasmine.SpyObj<angular.translate.ITranslateService>;
	let $timeout: any;

	let pageRestoreModalService: PageRestoreModalService;

	// ---------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------
	beforeEach(() => {
		pageInfo = {
			name: "MOCKED_PAGE_NAME",
			uid: PAGE_UID,
			uuid: PAGE_UUID,
			componentUuid: PAGE_UUID,
			masterTemplate: "some master template",
			title: null,
			defaultPage: true,
			creationtime: null,
			modifiedtime: null,
			typeCode: PAGE_TYPE_CODE,
			pageStatus: CMSPageStatus.ACTIVE,
			restrictions: [],
			homepage: true,
			catalogVersion: 'anyVersion',
		};

		actionableError1 = buildValidationError('name', 'field.already.exist');
		actionableError2 = buildValidationError('label', 'default.page.does.not.exist');

		nonActionableError = buildValidationError('typeCode', 'default.page.does.not.exist');

		invalidError1 = buildValidationError("invalid subject", "some error code", INVALID_ERROR_MSG_1);
		invalidError2 = buildValidationError("invalid subject", "some error code", INVALID_ERROR_MSG_2);
	});

	beforeEach(() => {
		alertService = jasmine.createSpyObj('alertService', ['showDanger']);

		confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);

		genericEditorModalService = jasmine.createSpyObj('genericEditorModalService', ['open']);
		genericEditorModalService.open.and.returnValue(promiseHelper.buildPromise<string>('resolvedPromise', PromiseType.RESOLVES));

		crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', ['publish']);

		pageRestoredAlertService = jasmine.createSpyObj<PageRestoredAlertService>('pageRestoredAlertService', ['displayPageRestoredSuccessAlert']);

		$translate = jasmine.createSpyObj<angular.translate.ITranslateService>('$translate', ['instant']);

		$timeout = jasmine.createSpy('$timeout');
		$timeout.and.callFake((callback: any, timeout: number) => {
			callback();
		});

		pageRestoreModalService = new PageRestoreModalService(
			lodash, alertService, confirmationModalService, genericEditorModalService, crossFrameEventService,
			pageRestoredAlertService, $translate, $timeout, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, EVENTS, EVENT_CONTENT_CATALOG_UPDATE);
	});

	// ---------------------------------------------------------------------------
	// Tests
	// ---------------------------------------------------------------------------
	it('GIVEN restore produced an unsupported error WHEN handleRestoreValidationErrors is called THEN it shows an error alert', () => {
		// GIVEN 
		const validationErrors: ValidationError[] = [nonActionableError, invalidError1, actionableError1, invalidError2];
		const expectedErrorMsg = INVALID_ERROR_MSG_1 + " " + INVALID_ERROR_MSG_2;

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, validationErrors);

		// THEN 
		expect(alertService.showDanger).toHaveBeenCalledWith(expectedErrorMsg);
	});

	it('GIVEN restored produced non-actionable error WHEN handleRestoreValidationErrors is called THEN it shows a confirmation message', () => {
		// GIVEN 
		const validationErrors: ValidationError[] = [nonActionableError, actionableError1];
		const expectedMsg = "some translated msg";
		$translate.instant.and.returnValue(expectedMsg);

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, validationErrors);

		// THEN 
		expect($translate.instant).toHaveBeenCalledWith('se.cms.page.restore.noprimaryforvariation.error');
		expect(confirmationModalService.confirm).toHaveBeenCalledWith({
			description: expectedMsg,
			showOkButtonOnly: true,
			title: 'se.cms.page.restore.error.confirmationmodal.title'
		});
	});

	it('GIVEN restore only produced actionable errors WHEN handleRestoreValidationErrors is called THEN it shows an editor modal', () => {
		// GIVEN 
		const validationErrors: ValidationError[] = [actionableError1];

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, validationErrors);

		// THEN 
		expect(genericEditorModalService.open).toHaveBeenCalled();

		const editorData = genericEditorModalService.open.calls.argsFor(0)[0];
		expect(editorData.content).toBe(pageInfo);
		expect(editorData.title).toBe("se.cms.page.restore.page.title");
		expect(editorData.componentId).toBe(pageInfo.uid);
		expect(editorData.componentUuid).toBe(pageInfo.uuid);
		expect(editorData.componentType).toBe(pageInfo.typeCode);
		expect(editorData.structure.category).toBe('PAGE');
		expect(editorData.structure.attributes.length).toBe(1);

		expect(crossFrameEventService.publish).toHaveBeenCalledWith(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
			messages: validationErrors,
			targetGenericEditorId: pageInfo.uuid
		});
	});

	it('GIVEN page has a duplicated name issue WHEN handleRestoreValidationErrors is called THEN it shows an editor with a ShortString widget', () => {
		// GIVEN 
		const error = buildValidationError("name", "field.already.exist");

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, [error]);

		// THEN
		const editorData = genericEditorModalService.open.calls.argsFor(0)[0];
		expect(editorData.structure.attributes.length).toBe(1);
		expect(editorData.structure.attributes[0]).toEqual({
			cmsStructureType: "ShortString",
			collection: false,
			editable: true,
			i18nKey: "type.cmsitem.name.name",
			localized: false,
			paged: false,
			qualifier: "name",
			required: true
		});
	});

	it('GIVEN page has a duplicated label issue WHEN handleRestoreValidationErrors is called THEN it shows an editor with a DuplicatePrimaryContentPage widget', () => {
		// GIVEN 
		const error = buildValidationError("label", "default.page.label.already.exist");

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, [error]);

		// THEN
		const editorData = genericEditorModalService.open.calls.argsFor(0)[0];
		expect(editorData.structure.attributes.length).toBe(1);
		expect(editorData.structure.attributes[0]).toEqual({
			cmsStructureType: "DuplicatePrimaryContentPage",
			collection: false,
			editable: true,
			i18nKey: "se.cms.page.restore.content.duplicate.primaryforvariation.label",
			localized: false,
			paged: false,
			qualifier: "label",
			required: true
		});
	});

	it('GIVEN page has no primary page for a label issue WHEN handleRestoreValidationErrors is called THEN it shows an editor with a MissingPrimaryContentPage widget', () => {
		// GIVEN 
		const error = buildValidationError("label", "default.page.does.not.exist");

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, [error]);

		// THEN
		const editorData = genericEditorModalService.open.calls.argsFor(0)[0];
		expect(editorData.structure.attributes.length).toBe(1);
		expect(editorData.structure.attributes[0]).toEqual({
			cmsStructureType: "MissingPrimaryContentPage",
			collection: false,
			editable: true,
			i18nKey: "se.cms.page.restore.content.noprimaryforvariation.label",
			localized: false,
			paged: false,
			qualifier: "label",
			required: true
		});
	});

	it('GIVEN page has a duplicate category page issue WHEN handleRestoreValidationErrors is called THEN it shows an editor with a DuplicatePrimaryNonContentPageMessage widget', () => {
		// GIVEN 
		const error = buildValidationError("typeCode", "default.page.already.exist");

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, [error]);

		// THEN
		const editorData = genericEditorModalService.open.calls.argsFor(0)[0];
		expect(editorData.structure.attributes.length).toBe(1);
		expect(editorData.structure.attributes[0]).toEqual({
			cmsStructureType: "DuplicatePrimaryNonContentPageMessage",
			collection: false,
			editable: true,
			i18nKey: "type.cmsitem.label.name",
			localized: false,
			paged: false,
			qualifier: "replace",
			required: true
		});
	});

	it('GIVEN handleRestoreValidationErrors opened an editor modal WHEN the editor modal succeds THEN it must show a success alert', () => {
		// GIVEN 
		const validationErrors: ValidationError[] = [actionableError1];

		// WHEN 
		pageRestoreModalService.handleRestoreValidationErrors(pageInfo, validationErrors);
		const editorSuccessCallback = genericEditorModalService.open.calls.argsFor(0)[2];
		editorSuccessCallback();

		// THEN 
		expect(pageRestoredAlertService.displayPageRestoredSuccessAlert).toHaveBeenCalledWith(pageInfo);
		expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_RESTORED);
	});

	it(`GIVEN handleRestoreValidationErrors was called with actionable errors AND it is called again with invalid data 
		WHEN editor modal is called it rejects the promise 
		THEN the editor is reloaded with a new structure`, () => {
			// GIVEN 
			const initialValidationErrors: ValidationError[] = [actionableError1];
			const finalValidationErrors: ValidationError[] = [actionableError2];

			const ge = {
				structure: null as any
			};

			// WHEN 
			pageRestoreModalService.handleRestoreValidationErrors(pageInfo, initialValidationErrors);
			crossFrameEventService.publish.calls.reset();

			const editorFailCallback = genericEditorModalService.open.calls.argsFor(0)[3];
			editorFailCallback(finalValidationErrors, ge);

			// THEN 
			expect(ge.structure.category).toBe('PAGE');
			expect(ge.structure.attributes.length).toBe(2);
			expect(ge.structure.attributes[0]).toEqual({
				cmsStructureType: "ShortString",
				collection: false,
				editable: true,
				i18nKey: "type.cmsitem.name.name",
				localized: false,
				paged: false,
				qualifier: "name",
				required: true
			});
			expect(ge.structure.attributes[1]).toEqual({
				cmsStructureType: "MissingPrimaryContentPage",
				collection: false,
				editable: true,
				i18nKey: "se.cms.page.restore.content.noprimaryforvariation.label",
				localized: false,
				paged: false,
				qualifier: "label",
				required: true
			});

			expect(crossFrameEventService.publish).toHaveBeenCalledWith(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
				messages: finalValidationErrors,
				targetGenericEditorId: pageInfo.uuid
			});
		});

	// ---------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------
	const buildValidationError = function(subject: string, errorCode: string, message = ""): ValidationError {
		return {
			subject,
			message,
			errorCode,
			reason: "some reason",
			subjectType: "some subject type",
			type: "ValidationError",
		};
	};
});