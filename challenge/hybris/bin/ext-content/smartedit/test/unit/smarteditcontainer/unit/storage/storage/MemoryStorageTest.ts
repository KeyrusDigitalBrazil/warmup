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
import {IStorage, IStorageManager} from "smarteditcommons";
import {MemoryStorageController, StorageManager, StoragePropertiesService} from "smarteditcontainer/services/storage";
import {promiseHelper, LogHelper} from "testhelpers";


import * as angular from "angular";

describe("MemoryStorage", () => {

	const $q = promiseHelper.$q();

	const lodash = (window as any).smarteditLodash;
	const propsService = new StoragePropertiesService([], lodash);


	let storageManager: IStorageManager;
	let storage: IStorage<any, any>;
	let memoryStorageController: MemoryStorageController;

	// MOCK Storage Data
	const storageId = "bla";

	// MOCK KEY/VALUE
	const sKey = 'sKey';
	const sValue = 'sValue';
	const oKey = {k: "some key"};
	const oValue = ["some value", {x: 1}];
	const nKey = 4;
	const nValue = 5;

	function testInputOutput(key: any, value: any): angular.IPromise<any> {
		return storage.put(value, key).then(() => {
			return storage.get(key).then((returnedValue: any) => {
				expect(value).toBe(returnedValue);
			});
		});
	}

	function expectLength(expectedLength: number): angular.IPromise<any> {
		return storage.getLength().then((length) => {
			expect(length).toBe(expectedLength);
			return length;
		});
	}

	beforeEach((done) => {
		memoryStorageController = new MemoryStorageController($q, propsService);
		storageManager = new StorageManager($q, new LogHelper(), propsService);
		storageManager.registerStorageController(memoryStorageController);
		storageManager.getStorage({
			storageId,
			storageType: propsService.getProperty("STORAGE_TYPE_IN_MEMORY")
		}).then((newStorage: IStorage<any, any>) => {
			storage = newStorage;
			done();
		});
	});


	it("GET - Returns undefined for unknown key", (done) => {
		storage.get("anything").then((val: any) => {
			expect(val).not.toBeDefined();
			done();
		});
	});

	it("GET/PUT - returns correct key/value", (done) => {
		testInputOutput(sKey, sValue).then(() =>
			testInputOutput(oKey, oValue).then(() =>
				testInputOutput(nKey, nValue).then(() => done())));
	});

	it("PUT - multiple overwrites previous", (done) => {
		// same key
		testInputOutput(sKey, sValue).then(() =>
			testInputOutput(sKey, nValue).then(() => done()));
	});

	it("REMOVE - is silent for unknown key", (done) => {
		storage.remove("some unknown key").then(() => {
			// as long as promise resolves its fine
			done();
		});
	});

	it("REMOVE - removes a value", (done) => {
		testInputOutput(oKey, oValue).then(() => {
			storage.remove(oKey).then(() => {
				storage.get(oKey).then((returnedResult: any) => {
					expect(returnedResult).not.toBeDefined();
					done();
				});
			});
		});
	});

	it("FIND - like a get but array", (done) => {
		testInputOutput(oKey, oValue).then(() => {
			storage.find(oKey).then((returnedResult: any) => {
				expect(returnedResult).toEqual([oValue]);
				done();
			});
		});
	});

	it("CLEAR - removes all items", (done) => {
		testInputOutput(sKey, sValue).then(() => {
			testInputOutput(oKey, oValue).then(() => {
				storage.clear().then(() => {
					storage.getLength().then((length: number) => {
						expect(length).toBe(0);
						done();
					});
				});
			});
		});
	});

	it("LENGTH - lengthy mclengthyface", (done) => {
		expectLength(0).then(() => {
			testInputOutput(sKey, sValue).then(() => {
				testInputOutput(oKey, oValue).then(() => {
					expectLength(2).then(() => {
						storage.clear().then(() => {
							expectLength(0).then(() => {
								done();
							});
						});
					});
				});
			});
		});
	});

	it("ENTRIES - empty", (done) => {
		storage.entries().then((entries: any[]) => {
			expect(entries.length).toBe(0);
			done();
		});
	});

	it("ENTRIES - returns all keys and values", (done) => {
		testInputOutput(sKey, sValue).then(() => {
			testInputOutput(oKey, oValue).then(() => {
				storage.entries().then((entries: [any, any][]) => {
					expect(entries.length).toBe(2);
					const stringTuple = entries.filter((tuple: [any, any]) => {
						return tuple[0] === sKey;
					})[0];
					const objectTuple = entries.filter((tuple: [any, any]) => {
						return JSON.stringify(tuple[0]) === JSON.stringify(oKey);
					})[0];
					expect(stringTuple[1]).toEqual(sValue);
					expect(objectTuple[1]).toEqual(oValue);
					done();
				});
			});
		});
	});




});