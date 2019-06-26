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
import {
	IStorageManager,
	IStorageOptions,
	StorageManagerFactory,
	StorageNamespaceConverter
} from "smarteditcommons";

import {
	StorageManager,
	StoragePropertiesService
} from "smarteditcontainer/services/storage";

import {promiseHelper, LogHelper} from "testhelpers";

import 'jasmine';


describe('NamepsaceStorageManager', () => {

	const $q = promiseHelper.$q();

	const lodash = (window as any).smarteditLodash;
	const propsService = new StoragePropertiesService([], lodash);


	let storageManagerFactory: StorageManagerFactory;
	let baseSM: IStorageManager;

	beforeEach(() => {
		baseSM = new StorageManager($q, new LogHelper(), propsService);
		spyOn(baseSM, 'registerStorageController');
		spyOn(baseSM, 'hasStorage');
		spyOn(baseSM, 'deleteStorage');
		spyOn(baseSM, 'deleteExpiredStorages');
		spyOn(baseSM, 'getStorage');

		storageManagerFactory = new StorageManagerFactory(baseSM);
	});

	describe('Namespace Validation', () => {
		let expectedError;

		function getStorageManager(input: any) {
			expectedError = StorageManagerFactory.ERR_INVALID_NAMESPACE(input);
			expect(() => storageManagerFactory.getStorageManager(input)).toThrowError(expectedError.message);
		}

		it("Throws error for Undefined namespace", () => {
			getStorageManager(undefined);
		});

		it("Throws error for Empty string", () => {
			getStorageManager("");
		});

		it("Throws error for Not a string namespace", () => {
			getStorageManager({});
		});

		it("Does NOT throw error for Valid namespace", () => {
			expect(() => storageManagerFactory.getStorageManager("someNs")).not.toThrow();
		});
	});

	describe('Decorates the StorageManager', () => {

		let storageManager: IStorageManager;
		const dummyController: any = null;

		const ns = "someNs";
		const storageId = "sid";
		const storageType = "stype";
		const namespacedStorageId = StorageNamespaceConverter.getNamespacedStorageId(ns, storageId);

		const storageConf: IStorageOptions = {
			storageId,
			storageType
		};

		function tryFinally(fnTry: any, fnFinally: any): void {
			try {
				fnTry();
			} catch (e) {
				// ignore
			} finally {
				fnFinally();
			}
		}

		beforeEach(() => {
			storageManager = storageManagerFactory.getStorageManager(ns);
		});

		it('decorates registerStorageController method', (done) => {
			tryFinally(() => storageManager.registerStorageController(dummyController),
				() => {
					expect(baseSM.registerStorageController).toHaveBeenCalledWith(dummyController);
					done();
				}
			);
		});

		it('decorates getStorage method', (done) => {
			tryFinally(() => storageManager.getStorage(storageConf),
				() => {
					expect(baseSM.getStorage).toHaveBeenCalledWith(jasmine.objectContaining({
						storageId: namespacedStorageId
					}));
					done();
				}
			);
		});

		it('decorates hasStorage method', (done) => {
			tryFinally(() => storageManager.hasStorage(storageId),
				() => {
					expect(baseSM.hasStorage).toHaveBeenCalledWith(namespacedStorageId);
					done();
				}
			);
		});

		it('decorates deleteStorage method - no force', (done) => {
			tryFinally(() => {
				storageManager.deleteStorage(storageId);
			}, () => {
				expect(baseSM.deleteStorage).toHaveBeenCalledWith(namespacedStorageId, false);
				done();
			}
			);
		});
		it('decorates deleteStorage method - force true', (done) => {
			tryFinally(() => {
				storageManager.deleteStorage(storageId, true);
			}, () => {
				expect(baseSM.deleteStorage).toHaveBeenCalledWith(namespacedStorageId, true);
				done();
			}
			);
		});
		it('decorates deleteStorage method - force false', (done) => {
			tryFinally(() => {
				storageManager.deleteStorage(storageId, false);
			}, () => {
				expect(baseSM.deleteStorage).toHaveBeenCalledWith(namespacedStorageId, false);
				done();
			}
			);
		});

		it('decorates deleteExpiredStorages method - no force', (done) => {
			tryFinally(() => {
				storageManager.deleteExpiredStorages();
			}, () => {
				expect(baseSM.deleteExpiredStorages).toHaveBeenCalledWith(false);
				done();
			}
			);
		});

		it('decorates deleteExpiredStorages method - force true', (done) => {
			tryFinally(() => {
				storageManager.deleteExpiredStorages(true);
			}, () => {
				expect(baseSM.deleteExpiredStorages).toHaveBeenCalledWith(true);
				done();
			}
			);
		});

		it('decorates deleteExpiredStorages method -  force false', (done) => {
			tryFinally(() => {
				storageManager.deleteExpiredStorages(false);
			}, () => {
				expect(baseSM.deleteExpiredStorages).toHaveBeenCalledWith(false);
				done();
			}
			);
		});

	});


});