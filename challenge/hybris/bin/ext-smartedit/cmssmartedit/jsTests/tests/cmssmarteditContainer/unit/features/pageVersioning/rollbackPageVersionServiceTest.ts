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
import 'jasmine';
import {IAlertService, IExperienceService, IPageInfoService, SystemEventService} from 'smarteditcommons';
import {PageVersioningService} from 'cmssmarteditcontainer/services';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';
import {RollbackPageVersionService} from 'cmssmarteditcontainer/components/versioning/services/RollbackPageVersionService';
import {promiseHelper, PromiseType} from 'testhelpers';

describe('test RollbackPageVersionService', () => {

	// ---------------------------------------------------------------------------
	// Constants
	// ---------------------------------------------------------------------------
	const PAGE_UUID = '1234';
	const PAGE_VERSION = {
		uid: 'someVersion',
		itemUUID: 'someItem',
		label: 'label',
		description: 'description',
		creationtime: new Date()
	};
	const NEW_PAGE_VERSION = {
		uid: 'someOtherVersion',
		itemUUID: 'someItem',
		label: 'label',
		description: 'description',
		creationtime: new Date()
	};

	const emptyPromise = promiseHelper.buildPromise<string>('emptyPromise', PromiseType.RESOLVES);
	const rejectedPromise = promiseHelper.buildPromise<string>('rejectedPromise', PromiseType.REJECTS);
	const promisePageUUID = promiseHelper.buildPromise<string>('promisePageUUID', PromiseType.RESOLVES, PAGE_UUID);

	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------
	let alertService: jasmine.SpyObj<IAlertService>;
	let confirmationModalService: any;
	let experienceService: jasmine.SpyObj<IExperienceService>;
	let pageInfoService: jasmine.SpyObj<IPageInfoService>;
	let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
	let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;
	const systemEventService: jasmine.SpyObj<SystemEventService> = jasmine.createSpyObj('systemEventService', ['publishAsync']);
	const $log: jasmine.SpyObj<angular.ILogService> = jasmine.createSpyObj('$log', ['error']);
	const EVENT_CONTENT_CATALOG_UPDATE: string = 'EVENT_CONTENT_CATALOG_UPDATE';

	let rollbackPageVersionService: RollbackPageVersionService;

	// ---------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------
	beforeEach(() => {
		alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showSuccess']);
		confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
		experienceService = jasmine.createSpyObj('experienceService', ['updateExperience']);
		pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUUID']);
		pageVersioningService = jasmine.createSpyObj<PageVersioningService>('pageVersioningService', ['rollbackPageVersion']);
		pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>('pageVersionSelectionService', ['deselectPageVersion', 'getSelectedPageVersion']);

		rollbackPageVersionService = new RollbackPageVersionService($log, alertService, confirmationModalService, experienceService,
			pageInfoService, pageVersioningService, pageVersionSelectionService, systemEventService, EVENT_CONTENT_CATALOG_UPDATE);
	});

	it('WHEN a page version is rolled back THEN the confirmation dialog is opened, the experience is updated, and the page version is deselected', () => {
		// GIVEN
		confirmationModalService.confirm.and.returnValue(emptyPromise);
		pageVersioningService.rollbackPageVersion.and.returnValue(emptyPromise);
		experienceService.updateExperience.and.returnValue(emptyPromise);
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue(PAGE_VERSION);
		const confirmationParam = {
			title: "se.cms.actionitem.page.version.rollback.confirmation.title",
			description: "se.cms.actionitem.page.version.rollback.confirmation.description",
			descriptionPlaceholders: {
				versionLabel: PAGE_VERSION.label
			}
		};

		// WHEN
		rollbackPageVersionService.rollbackPageVersion();

		// THEN
		expect(confirmationModalService.confirm).toHaveBeenCalledWith(confirmationParam);
		expect(pageVersioningService.rollbackPageVersion).toHaveBeenCalledWith(PAGE_UUID, PAGE_VERSION.uid);
		expect(experienceService.updateExperience).toHaveBeenCalledWith({});
		expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_CONTENT_CATALOG_UPDATE);
		expect(alertService.showSuccess).toHaveBeenCalled();
		expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();
	});

	it('GIVEN No version is selected WHEN a page version is rolled back THEN the experience is not updated', () => {
		// GIVEN
		confirmationModalService.confirm.and.returnValue(emptyPromise);
		pageVersioningService.rollbackPageVersion.and.returnValue(emptyPromise);
		experienceService.updateExperience.and.returnValue(emptyPromise);
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue(null);

		// WHEN
		rollbackPageVersionService.rollbackPageVersion();

		// THEN
		expect(experienceService.updateExperience).not.toHaveBeenCalledWith({});
		expect(pageVersionSelectionService.deselectPageVersion).not.toHaveBeenCalled();
	});

	it('GIVEN a different page version is selected WHEN a page version is rolled back THEN the experience is updated', () => {
		// GIVEN
		confirmationModalService.confirm.and.returnValue(emptyPromise);
		pageVersioningService.rollbackPageVersion.and.returnValue(emptyPromise);
		experienceService.updateExperience.and.returnValue(emptyPromise);
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue(NEW_PAGE_VERSION);

		// WHEN
		rollbackPageVersionService.rollbackPageVersion(PAGE_VERSION);

		// THEN
		expect(experienceService.updateExperience).toHaveBeenCalledWith({});
		expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();
	});

	it('WHEN a page version rollback fails THEN the experience is not updated and the page version stays selected', () => {
		// GIVEN
		confirmationModalService.confirm.and.returnValue(emptyPromise);
		pageVersioningService.rollbackPageVersion.and.returnValue(rejectedPromise);
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue(PAGE_VERSION);
		const confirmationParam = {
			title: "se.cms.actionitem.page.version.rollback.confirmation.title",
			description: "se.cms.actionitem.page.version.rollback.confirmation.description",
			descriptionPlaceholders: {
				versionLabel: PAGE_VERSION.label
			}
		};

		// WHEN
		rollbackPageVersionService.rollbackPageVersion();

		// THEN
		expect(confirmationModalService.confirm).toHaveBeenCalledWith(confirmationParam);
		expect(pageVersioningService.rollbackPageVersion).toHaveBeenCalledWith(PAGE_UUID, PAGE_VERSION.uid);
		expect(experienceService.updateExperience).not.toHaveBeenCalled();
		expect(alertService.showSuccess).not.toHaveBeenCalled();
		expect(pageVersionSelectionService.deselectPageVersion).not.toHaveBeenCalled();
	});
});