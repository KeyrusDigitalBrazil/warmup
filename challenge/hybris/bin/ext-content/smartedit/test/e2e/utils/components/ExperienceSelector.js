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
module.exports = (function() {

    var ExperienceSelectorObject = {

        actions: {
            clickInIframe: function() {
                browser.switchToIFrame();
                browser.click(element(by.css('.noOffset1')));
                browser.switchToParent();
            },
            clickInApplication: function() {
                browser.click(element(by.css('.ySmartEditAppLogo')));
            },
            selectExpectedDate: function() {

                browser.click(element(by.css('div[class*=\'datepicker-days\'] th[class*=\'picker-switch\']')));
                browser.click(element(by.css('div[class*=\'datepicker-months\'] th[class*=\'picker-switch\']')));
                browser.click(element(by.cssContainingText('span[class*=\'year\']', '2016')));
                browser.click(element(by.css('span[class*=\'month\']:first-child')));
                browser.click(element(by.xpath('.//*[.="1" and contains(@class,\'day\') and not(contains(@class, \'old\')) and not(contains(@class, \'new\'))]')));
                browser.click(element(by.css('span[class*=\'glyphicon-time\']')));
                browser.click(element(by.css('div[class=\'timepicker-picker\'] .timepicker-hour')));
                browser.click(element(by.cssContainingText('td[class*=\'hour\']', '01')));
                browser.click(element(by.css('div[class=\'timepicker-picker\'] .timepicker-minute')));
                browser.click(element(by.cssContainingText('td[class*=\'minute\']', '00')));

                var periodToggleElement = element(by.cssContainingText('div[class*=\'timepicker\'] button[class*=\'btn\']', 'AM'));
                periodToggleElement.isPresent().then(function(result) {
                    if (result) {
                        browser.click(periodToggleElement);
                    }
                });

            },

            catalog: {
                selectDropdown: function() {
                    return browser.click(element(by.id('previewCatalog')));
                },
                selectOption: function(option) {
                    var optionElement = element(by.cssContainingText('[id=\'previewCatalog-list\'] li[role=\'option\'] span', option));
                    browser.waitUntil(function() {
                        return optionElement.click().then(function() {
                            var catalogLabel = element(by.id("previewCatalog-label"));
                            browser.click(catalogLabel);
                            return true;
                        }, function() {
                            return false;
                        }).then(function(clickable) {
                            return clickable;
                        });
                    }, 'Option not clickable');
                }
            },

            calendar: {
                setDate: function(date) {
                    var timeField = element(by.css('input[name=\'time\']', 'Experience Selector Date and Time Field not found'));
                    browser.waitForVisibility(timeField);
                    browser.click(timeField);
                    timeField.sendKeys(date);
                    var timeLabel = element(by.id("time-label"));
                    return browser.click(timeLabel);
                }
            },

            language: {
                selectDropdown: function() {
                    return browser.click(element(by.id('language')));
                },
                selectOption: function(option) {
                    var optionElement = element(by.cssContainingText('[id=\'language-list\'] li[role=\'option\'] span', option));

                    browser.waitUntil(function() {
                        return optionElement.click().then(function() {
                            var languageLabel = element(by.id("language-label"));
                            browser.click(languageLabel);
                            return true;
                        }, function() {
                            return false;
                        }).then(function(clickable) {
                            return clickable;
                        });
                    }, 'Option not clickable');
                }
            },

            widget: {
                openExperienceSelector: function() {
                    return browser.waitUntilNoModal().then(function() {
                        return browser.click(by.id('experience-selector-btn', 'Experience Selector button not found')).then(function() {
                            return browser.waitUntil(EC.visibilityOf(element(by.xpath("//item-printer[@id='language-selected']/div/span"))), 'cannot load catalog item');
                        });
                    });
                },
                submit: function() {
                    return browser.click(by.id('submit', 'Experience Selector Apply Button not found'));
                },
                cancel: function() {
                    return browser.click(by.id('cancel', 'Experience Selector Apply Button not found'));
                }
            },

            productCatalogs: {
                openMultiProductCatalogVersionsSelectorWidget: function() {
                    browser.executeScript('arguments[0].click();', element(by.xpath("//*[@id='multi-product-catalog-versions-selector']")).getWebElement());
                    browser.waitForVisibility(element(by.id('y-modal-dialog')));
                },

                selectOptionFromMultiProductCatalogVersionsSelectorWidget: function(catalogId, catalogVersion) {
                    return browser.click(element(by.css('[data-id="' + catalogId + '"]'))).then(function() {
                        return browser.click(element(by.cssContainingText('[id="' + catalogId + '-list"] li[role=\'option\'] span', catalogVersion)));
                    });
                },

                clickModalWindowDone: function() {
                    return browser.click(by.id('done'));
                }
            },

            switchToCatalogVersion: function(catalogVersion) {

                ExperienceSelectorObject.actions.widget.openExperienceSelector();

                ExperienceSelectorObject.actions.catalog.selectDropdown();
                ExperienceSelectorObject.actions.catalog.selectOption(catalogVersion);

                ExperienceSelectorObject.actions.widget.submit();
                browser.waitForWholeAppToBeReady();

                ExperienceSelectorObject.actions.widget.openExperienceSelector();
            }

        },

        assertions: {

            catalog: {
                assertOptionText: function(index, expectedText) {
                    browser.wait(
                        function() {
                            return ExperienceSelectorObject.elements.catalog.option(index).getText()
                                .then(
                                    // resolved
                                    function(text) {
                                        return text;
                                    },
                                    // rejected
                                    function() {
                                        return '';
                                    })
                                .then(
                                    // resolved
                                    function(actualText) {
                                        return actualText === expectedText;
                                    }
                                );
                        }.bind(this), 3000, 'Dropdown options missing');
                },

                assertNumberOfOptions: function(length) {
                    browser.waitUntil(function() {
                        return ExperienceSelectorObject.elements.catalog.options().count().then(function(count) {
                            return count;
                        }, function() {
                            return '';
                        }).then(function(actualValue) {
                            return actualValue === length;
                        });
                    }.bind(this), 'dropdown failed to contain ' + length + ' elements');
                }

            },

            language: {
                assertOptionText: function(index, expectedText) {
                    browser.waitUntil(function() {
                        return ExperienceSelectorObject.elements.language.option(index).getText().then(function(text) {
                            return text;
                        }, function() {
                            return '';
                        }).then(function(actualText) {
                            return actualText === expectedText;
                        });
                    }.bind(this), 'Dropdown options missing');
                },

                assertNumberOfOptions: function(length) {
                    browser.waitUntil(function() {
                        return ExperienceSelectorObject.elements.language.options().count().then(function(count) {
                            return count;
                        }, function() {
                            return '';
                        }).then(function(actualValue) {
                            return actualValue === length;
                        });
                    }.bind(this), 'dropdown failed to contain ' + length + ' elements');
                }

            }
        },

        constants: {},

        elements: {

            widget: {
                button: function() {
                    return element(by.id('experience-selector-btn', 'Experience Selector button not found'));
                },
                text: function() {
                    return element(by.css('[class*=\'yWebsiteSelectBtn--text \']', 'Selector widget not found')).getText();
                },
                getExperienceMenu: function() {
                    return element(by.css("experience-selector-button div.dropdown-menu"));
                }
            },

            catalog: {
                label: function() {
                    return element(by.id('previewCatalog-label', 'Experience Selector Catalog Field Label not found'));
                },
                selectedOption: function() {
                    return element(by.css('[id=\'previewCatalog-selected\']', 'Experience Selector Catalog Field not found'));
                },
                dropdown: function() {
                    return element(by.css('[id=\'previewCatalog\'] [class*=\'ui-select-container\'] > a'));
                },
                option: function(index) {
                    return element(by.css('[id=\'previewCatalog\'] ul[role=\'listbox\'] li[role=\'option\']:nth-child(' + index + ') span'));
                },
                options: function() {
                    return element.all(by.css('[id=\'previewCatalog\'] ul[role=\'listbox\'] li[role=\'option\'] span'));
                }
            },

            dateAndTime: {
                label: function() {
                    return element(by.id('time-label', 'Experience Selector Date and Time Field Label not found'));
                },
                field: function() {
                    var timeField = element(by.css('input[name=\'time\']', 'Experience Selector Date and Time Field not found'));
                    browser.waitForVisibility(timeField);
                    return timeField;
                },
                button: function() {
                    return element(by.css('[id=\'time\'] div[class*=\'date\'] span[class*=\'input-group-addon\']'));
                }
            },

            language: {
                label: function() {
                    var languageLabel = element(by.id('language-label', 'Experience Selector Language Field Label not found'));
                    browser.waitForVisibility(languageLabel);
                    return languageLabel;
                },
                selectedOption: function() {
                    var languageField = element(by.css('[id=\'language-selected\'] span[class*=\'y-select-default-item\']', 'Experience Selector Language Field not found'));
                    browser.waitForVisibility(languageField);
                    return languageField;
                },
                dropdown: function() {
                    return element(by.css('[id=\'language\'] [class*=\'ui-select-container\'] > a'));
                },
                option: function(index) {
                    return element(by.css('[id=\'language\'] ul[role=\'listbox\'] li[role=\'option\']:nth-child(' + index + ') span'));
                },
                options: function() {
                    return element.all(by.css('[id=\'language\'] ul[role=\'listbox\'] li[role=\'option\'] span'));
                }
            },

            productCatalogs: {
                label: function() {
                    return element(by.id('productCatalogVersions-label', 'Experience Selector Product Catalogs Field Label not found'));
                }
            },

            singleProductCatalogVersionSelector: {
                selectedOption: function() {
                    return element(by.css('[id=\'productCatalogVersions-selected\']', 'Experience Selector Catalog Field not found'));
                },
                dropdown: function() {
                    return element(by.css('[id=\'productCatalogVersions\'] [class*=\'ui-select-container\'] > a'));
                },
                option: function(index) {
                    return element(by.css('[id=\'productCatalogVersions\'] ul[role=\'listbox\'] li[role=\'option\']:nth-child(' + index + ') span'));
                },
                options: function() {
                    return element.all(by.css('[id=\'productCatalogVersions\'] ul[role=\'listbox\'] li[role=\'option\'] span'));
                }
            },

            multiProductCatalogVersionsSelector: {
                selectedOptions: function() {
                    return element(by.css("input[name='productCatalogVersions']", 'Experience Selector Product Catalogs not found')).getAttribute("value");
                },

                getSelectedOptionFromMultiProductCatalogVersionsSelectorWidget: function(catalogId) {
                    return element(by.css('[id="' + catalogId + '-selected"]', 'Experience Selector Product Catalogs not found'));
                }
            },

            otherFields: {
                label: function(fieldName) {
                    return element(by.id(fieldName + '-label', 'Experience Selector ' + fieldName + ' Label not found'));
                },
                field: function(fieldName) {
                    return element(by.css("input[name='" + fieldName + "']", 'Experience Selector ' + fieldName + ' not found'));
                }
            },

            buttons: {
                ok: function() {
                    return element(by.id('submit', 'Experience Selector Apply Button not found'));
                },
                cancel: function() {
                    return element(by.id('cancel', 'Experience Selector Cancel Button not found'));
                }
            },

            page: {
                iframe: function() {
                    return element(by.css('#js_iFrameWrapper iframe', 'iFrame not found'));
                }
            }

        }

    };

    return ExperienceSelectorObject;

})();
