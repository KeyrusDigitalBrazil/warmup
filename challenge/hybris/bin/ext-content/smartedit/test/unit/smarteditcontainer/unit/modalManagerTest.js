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
describe('modalServiceModule', function() {

    var $q;
    var $log;
    var $rootScope;
    var modalService;
    var holder;
    var MODAL_BUTTON_ACTIONS;
    var MODAL_BUTTON_STYLES;

    // Default modal vars for easy re-use - call doDefaultOpen(..)
    var defaultModalManager;
    var defaultModalConfig = {
        templateInline: "<span>Hello</span>"
    };
    var defaultButtonConfig = {
        id: 'test_button'
    };
    var closingFunctions = {
        close: function() {},
        dismiss: function() {}
    };
    var setupTranslateMock = function() {
        var deferred = $q.defer();
        holder.$translate.and.returnValue(deferred.promise);
    };
    var doDefaultOpen = function(conf) {
        setupTranslateMock();
        var configToUse = conf || defaultModalConfig;
        modalService.open(configToUse);
        $rootScope.$digest();
        defaultModalManager = modalService._modalManager;
    };

    beforeEach(angular.mock.module('modalServiceModule', function($provide) {
        holder = jasmine.createSpyObj('holder', ['$translate']);
        $provide.value('$translate', holder.$translate);
        $provide.value('translateFilter', function(data) {
            return data;
        });
    }));

    beforeEach(inject(function(_modalService_, _$q_, _$rootScope_, _$log_, _MODAL_BUTTON_ACTIONS_, _MODAL_BUTTON_STYLES_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        $log = _$log_;
        modalService = _modalService_;
        MODAL_BUTTON_ACTIONS = _MODAL_BUTTON_ACTIONS_;
        MODAL_BUTTON_STYLES = _MODAL_BUTTON_STYLES_;
    }));

    it('add buttons', function() {

        doDefaultOpen();

        expect(defaultModalManager._hasButtons()).toBe(false);
        expect(defaultModalManager.getButtons().length).toBe(0);

        defaultModalManager.addButton(defaultButtonConfig);

        expect(defaultModalManager.getButton(defaultButtonConfig.id).id).toBe(defaultButtonConfig.id);
        expect(defaultModalManager._hasButtons()).toBe(true);
        expect(defaultModalManager.getButtons().length).toBe(1);

        var id2 = 'another.ID';
        defaultModalManager.addButton({
            id: id2
        });

        expect(defaultModalManager.getButton(id2).id).toBe(id2);
        expect(defaultModalManager._hasButtons()).toBe(true);
        expect(defaultModalManager.getButtons().length).toBe(2);
    });

    it('remove a button', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1'
        });
        defaultModalManager.addButton({
            id: '2'
        });
        defaultModalManager.addButton({
            id: '3'
        });

        expect(defaultModalManager._hasButtons()).toBe(true);
        expect(defaultModalManager.getButtons().length).toBe(3);

        defaultModalManager.removeButton('2');

        expect(defaultModalManager.getButtons().length).toBe(2);
        expect(defaultModalManager.getButton('2')).toBeNull();
    });


    it('remove all buttons', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1'
        });
        defaultModalManager.addButton({
            id: '2'
        });
        defaultModalManager.addButton({
            id: '3'
        });

        expect(defaultModalManager._hasButtons()).toBe(true);
        expect(defaultModalManager.getButtons().length).toBe(3);

        defaultModalManager.removeAllButtons();

        expect(defaultModalManager._hasButtons()).toBe(false);
        expect(defaultModalManager.getButtons().length).toBe(0);
    });

    it('disable a button', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1',
            disabled: false
        });

        expect(defaultModalManager.getButton('1').disabled).toBe(false);

        defaultModalManager.disableButton('1');

        expect(defaultModalManager.getButton('1').disabled).toBe(true);
    });

    it('enable a button', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1',
            disabled: true
        });

        expect(defaultModalManager.getButton('1').disabled).toBe(true);

        defaultModalManager.enableButton('1');

        expect(defaultModalManager.getButton('1').disabled).toBe(false);
    });

    it('show/hide the dismiss X button', function() {

        doDefaultOpen();

        defaultModalManager.setShowHeaderDismiss(true);
        expect(defaultModalManager._showDismissButton()).toBe(true);

        defaultModalManager.setShowHeaderDismiss(false);
        expect(defaultModalManager._showDismissButton()).toBe(false);
    });

    it('close a modal with a default close button', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.CLOSE
        });

        spyOn(defaultModalManager, "close").and.callThrough();
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));
        expect(defaultModalManager.close).toHaveBeenCalled();
    });

    it('dismiss a modal with a default dismiss button', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.DISMISS
        });

        spyOn(defaultModalManager, "dismiss").and.callThrough();
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));
        expect(defaultModalManager.dismiss).toHaveBeenCalled();
    });

    it('press a button with action NONE and the modal will not be closed or dismissed', function() {

        doDefaultOpen();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.NONE
        });

        spyOn(defaultModalManager, "close").and.callThrough();
        spyOn(defaultModalManager, "dismiss").and.callThrough();
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));
        expect(defaultModalManager.close).not.toHaveBeenCalled();
        expect(defaultModalManager.dismiss).not.toHaveBeenCalled();
    });

    it('dismiss the modal with the default dismiss X button', function() {

        doDefaultOpen();

        spyOn(defaultModalManager, "dismiss").and.callThrough();
        defaultModalManager._handleDismissButton();
        expect(defaultModalManager.dismiss).toHaveBeenCalled();

    });

    it('use a callback to cancel the dismiss action of the dismiss X button', function() {

        doDefaultOpen();

        var deferred = $q.defer();
        var testDismissCallback = function() {
            return deferred.promise;
        };

        spyOn(defaultModalManager, "dismiss").and.callThrough();
        defaultModalManager.setDismissCallback(testDismissCallback);
        defaultModalManager._handleDismissButton();

        deferred.reject(); // <--- blocks the dismiss
        $rootScope.$digest();

        expect(defaultModalManager.dismiss).not.toHaveBeenCalled();
    });

    it('use a callback to allow the dismiss action of the dismiss X button', function() {

        doDefaultOpen();

        var deferred = $q.defer();
        var testDismissCallback = function() {
            return deferred.promise;
        };

        spyOn(defaultModalManager, "dismiss").and.callThrough();
        defaultModalManager.setDismissCallback(testDismissCallback);
        defaultModalManager._handleDismissButton();

        deferred.resolve(); // <---- allows the dismiss
        $rootScope.$digest();

        expect(defaultModalManager.dismiss).toHaveBeenCalled();
    });

    it('keep a modal open using a global button callback when button action is NONE', function() {

        var returnData = "bla";

        var globalCallback = function() {
            var deferred = $q.defer();
            deferred.resolve(returnData);
            return deferred.promise;
        };

        spyOn(closingFunctions, "close").and.returnValue();
        doDefaultOpen();
        spyOn(defaultModalManager, 'close');
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.NONE
        });
        defaultModalManager.setButtonHandler(globalCallback);
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.close).not.toHaveBeenCalledWith(returnData);
    });

    it('dismiss a modal using a global button callback and get the callbacks return data', function() {

        var returnData = "bla";

        var globalCallback = function() {
            var deferred = $q.defer();
            deferred.resolve(returnData);
            return deferred.promise;
        };

        doDefaultOpen();
        spyOn(defaultModalManager, "dismiss").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.DISMISS
        });
        defaultModalManager.setButtonHandler(globalCallback);
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.dismiss).toHaveBeenCalledWith(returnData);
    });

    it('close a modal using a global button callback and get the callbacks return data', function() {

        var returnData = "bla";

        var globalCallback = function() {
            var deferred = $q.defer();
            deferred.resolve(returnData);
            return deferred.promise;
        };

        doDefaultOpen();
        spyOn(defaultModalManager, "close").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.CLOSE
        });
        defaultModalManager.setButtonHandler(globalCallback);
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.close).toHaveBeenCalledWith(returnData);
    });

    it('cancel the dismiss of a modal using a global button callback when promise is rejected', function() {

        var globalCallback = function() {
            var deferred = $q.defer();
            deferred.reject();
            return deferred.promise;
        };

        doDefaultOpen();
        spyOn(defaultModalManager, "dismiss").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.DISMISS
        });
        defaultModalManager.setButtonHandler(globalCallback);
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.dismiss).not.toHaveBeenCalled();
    });

    it('cancel the close of a modal using a global button callback when promise is rejected', function() {

        var globalCallback = function() {
            var deferred = $q.defer();
            deferred.reject();
            return deferred.promise;
        };

        doDefaultOpen();
        spyOn(defaultModalManager, "close").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.CLOSE
        });
        defaultModalManager.setButtonHandler(globalCallback);
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.close).not.toHaveBeenCalled();
    });

    it('keep a modal open using a button callback when button action is NONE', function() {

        doDefaultOpen();
        spyOn(defaultModalManager, "close").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.NONE,
            callback: function() {
                var deferred = $q.defer();
                deferred.resolve();
                return deferred.promise;
            }
        });
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.close).not.toHaveBeenCalled();
    });

    it('dismiss a modal using a button callback and get the callbacks return data', function() {

        doDefaultOpen();
        spyOn(defaultModalManager, "dismiss").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.DISMISS,
            callback: function() {
                var deferred = $q.defer();
                deferred.resolve("bla");
                return deferred.promise;
            }
        });
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.dismiss).toHaveBeenCalledWith("bla");
    });

    it('close a modal using a button callback and get the callbacks return data', function() {

        doDefaultOpen();
        spyOn(defaultModalManager, "close").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.CLOSE,
            callback: function() {
                var deferred = $q.defer();
                deferred.resolve("bla");
                return deferred.promise;
            }
        });
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.close).toHaveBeenCalledWith("bla");
    });

    it('cancel the dismiss of a modal using a global button callback when promise is rejected', function() {

        doDefaultOpen();
        spyOn(defaultModalManager, "dismiss").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.DISMISS,
            callback: function() {
                var deferred = $q.defer();
                deferred.reject();
                return deferred.promise;
            }
        });
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.dismiss).not.toHaveBeenCalled();
    });

    it('cancel the close of a modal using a button callback when promise is rejected', function() {

        doDefaultOpen();
        spyOn(defaultModalManager, "close").and.returnValue();
        defaultModalManager.addButton({
            id: '1',
            action: MODAL_BUTTON_ACTIONS.CLOSE,
            callback: function() {
                var deferred = $q.defer();
                deferred.reject();
                return deferred.promise;
            }
        });
        defaultModalManager._buttonPressed(defaultModalManager.getButton('1'));

        $rootScope.$digest(); // trigger the promise processing

        expect(defaultModalManager.close).not.toHaveBeenCalled();
    });

    it('add a button with no values and get a default button', function() {
        doDefaultOpen();
        expect(defaultModalManager._createButton()).toEqual(defaultModalManager._defaultButtonOptions);
    });

    it('add a button with an invalid style and get an exception', function() {
        doDefaultOpen();
        var anonCreateButtonFunction = function() {
            defaultModalManager._createButton({
                style: "some_invalid_style"
            });
        };
        expect(anonCreateButtonFunction).toThrow('modalService.ModalManager._createButton.illegal.button.style');
    });

    it('add a button with an invalid action and get an exception', function() {
        doDefaultOpen();
        var anonCreateButtonFunction = function() {
            defaultModalManager._createButton({
                action: "some_invalid_action"
            });
        };
        expect(anonCreateButtonFunction).toThrow('modalService.ModalManager._createButton.illegal.button.action');
    });


});
