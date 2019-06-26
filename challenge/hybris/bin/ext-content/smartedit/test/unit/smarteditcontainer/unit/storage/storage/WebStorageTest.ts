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
	IStorage,
	IStorageController,
	IStorageManager
} from "smarteditcommons";

import {
	defaultStorageProperties,
	LocalStorageController,
	SessionStorageController,
	StorageManager,
	StoragePropertiesService
} from "smarteditcontainer/services/storage";

import {promiseHelper, LogHelper} from "testhelpers";


describe("WebStorage", () => {

	const lodash = (window as any).smarteditLodash;
	const propsService = new StoragePropertiesService([], lodash);

	const $q = promiseHelper.$q();

	let storageManager: IStorageManager;
	let localCtrl: IStorageController;
	let sessionCtrl: IStorageController;

	beforeEach(() => {
		window.localStorage.removeItem(defaultStorageProperties.LOCAL_STORAGE_ROOT_KEY);
		window.sessionStorage.removeItem(defaultStorageProperties.SESSION_STORAGE_ROOT_KEY);

		storageManager = new StorageManager($q, new LogHelper(), propsService);
		localCtrl = new LocalStorageController($q, propsService);
		sessionCtrl = new SessionStorageController($q, propsService);

		storageManager.registerStorageController(localCtrl);
		storageManager.registerStorageController(sessionCtrl);
	});

	describe("localStorage", () => {

		// STORAGE
		const storageId = "someId";
		let stringStorage: IStorage<string, string>;
		let objStorage: IStorage<number, TestType>;

		// STRING MOCKS
		const sTestInvalidKey = "invalidKey";
		const sTestKey = "someKey";
		const sTestKey2 = "someKey2";
		const sTestValue = "some data";
		const sTestValue2 = "some data2";

		// OBJ MOCKS
		const oTestKey = 4;
		const oTestKey2 = -1;
		const oInvalidKey = 9;
		const mockTTString = "blabla";
		const mockTTNumber = 4326436;
		const oTestValue: TestType = {
			mockTTString,
			mockTTNumber
		};
		const o = {a: 3};
		const oTestValue2: TestType = {
			mockTTObj: o,
			mockTTRef: oTestValue   // ref other item
		};

		interface TestType {
			[index: string]: any;
			mockTTString?: string;
			mockTTNumber?: number;
			mockTTRef?: TestType;
			mockTTObj?: any;
		}


		beforeEach((done) => {

			storageManager.getStorage({
				storageId,
				storageType: defaultStorageProperties.STORAGE_TYPE_LOCAL_STORAGE
			}).then((storage: IStorage<any, any>) => {
				stringStorage = storage as IStorage<string, string>;
				objStorage = storage as IStorage<number, TestType>;
				done();
			});
		});

		describe("length", () => {

			it("Length after put/get/remove", (done) => {
				objStorage.getLength().then((len1: number) => {
					expect(len1).toBe(0);
					objStorage.put(oTestValue, oTestKey).then(() => {
						objStorage.put(oTestValue2, oTestKey2).then(() => {
							objStorage.getLength().then((len2: number) => {
								expect(len2).toBe(2);
								objStorage.remove(oTestKey).then(() => {
									objStorage.getLength().then((len3: number) => {
										expect(len3).toBe(1);
										done();
									});
								});
							});
						});
					});
				});
			});

			it("Length after multiple puts", (done) => {
				objStorage.put(oTestValue, oTestKey).then(() => {
					objStorage.put(oTestValue2, oTestKey).then(() => {
						objStorage.getLength().then((len: number) => {
							expect(len).toBe(1);
							done();
						});
					});
				});
			});
		});

		describe("remove/clear", () => {

			it("remove is silent on invalid key", (done) => {
				stringStorage.remove(sTestInvalidKey).then(() => {
					// as long as finished then we're good!
					done();
				});
			});

			it("Removes a value", (done) => {
				stringStorage.put(sTestValue, sTestKey).then(() => {
					stringStorage.get(sTestKey).then((returnedValue: string) => {
						expect(returnedValue).toEqual(sTestValue);
						stringStorage.remove(sTestKey).then(() => {
							stringStorage.get(sTestKey).then((emptyReturn: string) => {
								expect(emptyReturn).not.toBeDefined();
								done();
							});
						});
					});
				});
			});

			it("Clear removes all values", (done) => {
				stringStorage.put(sTestValue, sTestKey).then(() => {
					stringStorage.put(sTestValue2, sTestKey2).then(() => {
						stringStorage.getLength().then((len1: number) => {
							expect(len1).toBe(2);
							stringStorage.clear().then(() => {
								stringStorage.getLength().then((len2: number) => {
									expect(len2).toBe(0);
									done();
								});
							});
						});
					});
				});
			});
		});

		describe("ENTRIES", () => {

			it("empty", (done) => {
				stringStorage.entries().then((entries: any[]) => {
					expect(entries.length).toBe(0);
					done();
				});
			});

			it("returns all keys and values", (done) => {
				objStorage.put(oTestValue, oTestKey).then(() => {
					objStorage.put(oTestValue2, oTestKey2).then(() => {
						objStorage.entries().then((entries: any[]) => {
							objStorage.entries().then((entryResults: [any, any][]) => {
								expect(entryResults.length).toBe(2);
								const objTuple1 = entryResults.filter((tuple: [any, any]) => {
									return JSON.stringify(tuple[0]) === JSON.stringify(oTestKey);
								})[0];
								const objTuple2 = entryResults.filter((tuple: [any, any]) => {
									return JSON.stringify(tuple[0]) === JSON.stringify(oTestKey2);
								})[0];
								expect(objTuple1[1]).toEqual(oTestValue);
								expect(objTuple2[1]).toEqual(oTestValue2);
								done();
							});
						});
					});
				});
			});

		});

		describe("GET/PUT", () => {


			describe("string, string", () => {

				it("get of missing string key returns undefined", (done) => {
					stringStorage.get(sTestInvalidKey).then((returnedValue: string) => {
						expect(returnedValue).not.toBeDefined();
						done();
					});
				});

				it("Get a string", (done) => {
					stringStorage.put(sTestValue, sTestKey).then(() => {
						stringStorage.get(sTestKey).then((returnedValue: string) => {
							expect(returnedValue).toEqual(sTestValue);
							done();
						});
					});
				});

				it("Put updates existing string", (done) => {
					stringStorage.put(sTestValue2, sTestKey2).then(() => {
						stringStorage.get(sTestKey2).then((firstReturnedValue: string) => {
							expect(firstReturnedValue).toEqual(sTestValue2);
							stringStorage.put(sTestValue, sTestKey).then(() => {
								stringStorage.get(sTestKey).then((returnedValue: string) => {
									expect(returnedValue).toEqual(sTestValue);
									done();
								});
							});
						});
					});
				});
			});

			describe("number, object", () => {
				it("get of missing object key returns undefined", (done) => {
					objStorage.get(oInvalidKey).then((returnedValue: TestType) => {
						expect(returnedValue).not.toBeDefined();
						done();
					});
				});

				it("get an object value", (done) => {
					objStorage.put(oTestValue, oTestKey).then(() => {
						objStorage.get(oTestKey).then((returnedValue: TestType) => {
							expect(returnedValue).toEqual(oTestValue);
							done();
						});
					});
				});

				it("Put updates existing string", (done) => {
					objStorage.put(oTestValue2, oTestKey2).then(() => {
						objStorage.get(oTestKey2).then((firstReturnedValue: TestType) => {
							expect(firstReturnedValue).toEqual(oTestValue2);
							objStorage.put(oTestValue, oTestKey).then(() => {
								objStorage.get(oTestKey).then((returnedValue: TestType) => {
									expect(returnedValue).toEqual(oTestValue);
									done();
								});
							});
						});
					});
				});

			});
		});


	});


});