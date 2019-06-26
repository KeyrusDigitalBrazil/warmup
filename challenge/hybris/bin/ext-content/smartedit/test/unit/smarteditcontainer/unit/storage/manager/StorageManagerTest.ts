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
import {IStorageOptions} from "smarteditcommons";
import {
	IStorageMetaData,
	StorageManager,
	StoragePropertiesService
} from "smarteditcontainer/services";
import {promiseHelper, LogHelper} from "testhelpers";


describe('StorageManager', () => {

	const $q = promiseHelper.$q();
	const lodash = (window as any).smarteditLodash;
	const propsService = new StoragePropertiesService([], lodash);

	let storageManager: StorageManager;

	// Metas Data mocking
	let mockStorageType: string;
	let mockControllerType2: string;
	let mockStorageId: string;
	let mockStorageConfig: IStorageOptions;
	let localstorageKey: string;
	let mockMetaDataItem: IStorageMetaData;
	let mockMetaDataObject: {[index: string]: IStorageMetaData};
	let mockController: any;
	let mockController2: any;
	let mockController3: any;
	let mockStorage: any;

	beforeEach(() => {

		// mock storages
		mockStorage = {
			dispose: () => {
				return $q.when(true);
			}
		};

		// Mock controllers
		mockStorageType = "someStorageType";
		mockStorageId = "someStorageId";
		mockController = jasmine.createSpyObj(['getStorage', 'deleteStorage']);
		mockController.deleteStorage.and.returnValue($q.when(true));
		mockController.getStorage.and.returnValue($q.when(mockStorage));
		mockController.storageType = mockStorageType;
		mockControllerType2 = "2";
		mockController2 = jasmine.createSpyObj(['getStorage', 'deleteStorage']);
		mockController2.deleteStorage.and.returnValue($q.when(true));
		mockController2.getStorage.and.returnValue($q.when(mockStorage));
		mockController2.storageType = mockControllerType2;
		mockController3 = {storageType: "3"};

		// Mock config
		mockStorageConfig = {
			storageId: mockStorageId,
			storageType: mockStorageType
		};
		localstorageKey = propsService.getProperty("LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA");
		mockMetaDataItem = {
			storageType: mockStorageType,
			storageId: mockStorageId,
			storageVersion: '0',
			lastAccess: Date.now() - (1000 * 60)    // 1 min old
		};
		mockMetaDataObject = {};
		mockMetaDataObject[mockStorageId] = mockMetaDataItem;

		window.localStorage.setItem(localstorageKey, JSON.stringify(mockMetaDataObject));
		storageManager = new StorageManager($q, new LogHelper(), propsService);
	});

	afterEach(() => {
		window.localStorage.clear();
	});

	describe("controller registration + storage type metadata", () => {

		it('Finds correct controller', (done) => {
			storageManager.registerStorageController(mockController2);
			storageManager.registerStorageController(mockController);
			storageManager.registerStorageController(mockController3);

			// don't care about errors, as long as getStorage was called on the correct storageController, then success
			try {
				storageManager.getStorage(mockStorageConfig);
			} catch (e) {
				// ignore
			} finally {
				expect(mockController.getStorage).toHaveBeenCalledWith(mockStorageConfig);
				done();
			}
		});

		it('Error on no controllers', () => {
			const expectedError = StorageManager.ERR_NO_STORAGE_TYPE_CONTROLLER(mockStorageType);
			expect(() => storageManager.getStorage(mockStorageConfig)).toThrowError(expectedError.message);
		});

		it('Error on no matching controller type', () => {

			storageManager.registerStorageController(mockController2);
			storageManager.registerStorageController(mockController3);
			const expectedError = StorageManager.ERR_NO_STORAGE_TYPE_CONTROLLER(mockStorageType);
			expect(() => storageManager.getStorage(mockStorageConfig)).toThrowError(expectedError.message);
		});

	});

	describe('getStorage', () => {

		it('Gets the storage from the controller', (done) => {
			storageManager.registerStorageController(mockController);
			storageManager.getStorage(mockStorageConfig).then((storage: any) => {
				expect(storage).toBe(mockStorage);
				done();
			});
		});

		it("Correctly delegates the Storage's dispose method to the StorageManager", (done) => {
			storageManager.registerStorageController(mockController);
			storageManager.getStorage(mockStorageConfig).then((storage: any) => {
				expect(storage).toBe(mockStorage);
				spyOn(storageManager, 'deleteStorage').and.callThrough();
				storage.dispose().then(() => {
					expect(storageManager.deleteStorage).toHaveBeenCalled();
					done();
				});
			});
		});

		it("Subsequent storage retrievals in a single session should pull from memory instead of controller", (done) => {
			storageManager.registerStorageController(mockController);
			storageManager.getStorage(mockStorageConfig).then(() => {
				storageManager.getStorage(mockStorageConfig).then(() => {
					expect(mockController.getStorage).toHaveBeenCalledTimes(1);
					done();
				});
			});
		});

	});

	describe('deleteStorage', () => {

		it('Deletes storage from controller and remove storage manager metadata', (done) => {
			storageManager.registerStorageController(mockController);
			storageManager.deleteStorage(mockStorageId).then(() => {
				expect(mockController.deleteStorage).toHaveBeenCalledWith(mockStorageId);
				expect(JSON.parse(window.localStorage.getItem(localstorageKey))).toEqual({});
				done();
			});
		});

		it('Delete force will remove metadata when controller is not available', (done) => {
			// don't register controller
			storageManager.deleteStorage(mockStorageId, true).then(() => {
				const metaData = JSON.parse(window.localStorage.getItem(localstorageKey));
				expect(metaData[mockStorageId]).toBe(undefined);
				done();
			});
		});

		it('Delete no-force will not remove metadata when controller is not available', (done) => {
			// don't register controller
			storageManager.deleteStorage(mockStorageId, false).then(() => {
				const metaData = JSON.parse(window.localStorage.getItem(localstorageKey));
				expect(metaData[mockStorageId]).toEqual(mockMetaDataItem);
				done();
			});
		});

		it('Gracefully handles unknown storageId', (done) => {
			storageManager.deleteStorage("some unknown StorageId").then(() => {
				// as long as the promise resolves, its good
				done();
			});
		});

	});

	describe('Mismatch Version, and and  Expired storages', () => {

		it('Removes expired storages', () => {

			// put an expired storage in local storage, and make sure its removed

			storageManager.registerStorageController(mockController);

			const expiredStorageId = "expired";
			const expiredMockMetaDataItem: IStorageMetaData = {
				storageId: expiredStorageId,
				storageType: mockStorageType,
				storageVersion: "0",
				lastAccess: Date.UTC(2000, 1)
			};
			mockMetaDataObject = {};
			mockMetaDataObject[mockStorageId] = mockMetaDataItem;
			mockMetaDataObject[expiredStorageId] = expiredMockMetaDataItem;
			window.localStorage.setItem(localstorageKey, JSON.stringify(mockMetaDataObject));

			expect(storageManager.hasStorage(mockStorageId)).toBe(true);
			expect(storageManager.hasStorage(expiredStorageId)).toBe(true);

			// trigger SM constructor, which flushes expired caches
			storageManager.deleteExpiredStorages(true).then(() => {
				expect(storageManager.hasStorage(mockStorageId)).toBe(true);
				expect(storageManager.hasStorage(expiredStorageId)).toBe(false);
			});

		});

		it('Removes old storage on version change', (done) => {

			// get a storage with different version and expect the metaData is the updated one

			storageManager.registerStorageController(mockController);

			// the original is already persisted in localstorage at this point
			mockStorageConfig.storageVersion = "some other version";

			spyOn(storageManager, 'deleteStorage').and.callThrough();
			storageManager.getStorage(mockStorageConfig).then(() => {

				expect(storageManager.deleteStorage).toHaveBeenCalled();

				// check if last access was updated (and this a new record was created)
				const metaData = JSON.parse(window.localStorage.getItem(localstorageKey));
				expect(metaData[mockStorageId].lastAccess).not.toEqual(mockMetaDataItem.lastAccess);
				done();
			});

		});


		it('Removes old storage on controller type change', (done) => {

			// get a storage with different version and expect the metaData is the updated one

			storageManager.registerStorageController(mockController);
			storageManager.registerStorageController(mockController2);

			// the original is already persisted in localstorage at this point
			mockStorageConfig.storageType = mockControllerType2;

			spyOn(storageManager, 'deleteStorage').and.callThrough();

			storageManager.getStorage(mockStorageConfig).then(() => {
				expect(storageManager.deleteStorage).toHaveBeenCalled();

				// check if last access was updated (and this a new record was created)
				const metaData = JSON.parse(window.localStorage.getItem(localstorageKey));
				expect(metaData[mockStorageId].lastAccess).not.toEqual(mockMetaDataItem.lastAccess);
				done();
			});

		});

	});
});
