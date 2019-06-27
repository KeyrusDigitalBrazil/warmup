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
/* jshint unused:false */
angular
    .module('componentTypesMocks', ['ngMockE2E'])
    .run(
        function($httpBackend, parseQuery, backendMocksUtils) {

            var isNavigationNodeEditable = JSON.parse(sessionStorage.getItem('navigationNodeEditable')) !== false;
            var isProductsEditable = JSON.parse(sessionStorage.getItem('productsEditable')) !== false;
            var isCategoriesEditable = JSON.parse(sessionStorage.getItem('categoriesEditable')) !== false;

            var cmsLinkToComponentAttribute = {
                "cmsStructureType": "CMSLinkToSelect",
                "collection": false,
                "editable": true,
                "i18nKey": "type.cmslinkcomponent.linkto.name",
                "mode": "DEFAULT",
                "idAttribute": "id",
                "labelAttributes": ['label'],
                "options": [{
                    "label": "se.cms.linkto.option.content",
                    "id": "content"
                }, {
                    "label": "se.cms.linkto.option.product",
                    "id": "product"
                }, {
                    "label": "se.cms.linkto.option.category",
                    "id": "category"
                }, {
                    "label": "se.cms.linkto.option.external",
                    "id": "external"
                }],
                'paged': false,
                'qualifier': 'linkTo',
                'required': true
            };


            var componentTypes = [{
                attributes: [{
                    cmsStructureType: 'RichText',
                    i18nKey: 'type.cmsparagraphcomponent.content.name',
                    localized: false,
                    qualifier: 'content'
                }],
                category: 'COMPONENT',
                code: 'CMSParagraphComponent',
                i18nKey: 'type.cmsparagraphcomponent.name',
                name: 'Paragraph'
            }, {
                attributes: [{
                    cmsStructureType: "NavigationNodeSelector",
                    i18nKey: "type.footernavigationcomponent.navigationnode.name",
                    localized: false,
                    qualifier: 'navigationNode'
                }],
                category: 'COMPONENT',
                code: 'FooterNavigationComponent',
                i18nKey: 'type.footernavigationcomponent.name',
                name: 'Footer Navigation Component'
            }, {
                attributes: [{
                    cmsStructureType: 'Media',
                    i18nKey: 'type.simplebannercomponent.media.name',
                    localized: true,
                    qualifier: 'media',
                    containedTypes: ['Media']
                }, {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.simplebannercomponent.urllink.name',
                    localized: false,
                    qualifier: 'urlLink'
                }, {
                    cmsStructureType: 'Boolean',
                    i18nKey: 'type.simplebannercomponent.external.name',
                    localized: false,
                    qualifier: 'external'
                }],
                category: 'COMPONENT',
                code: 'SimpleBannerComponent',
                i18nKey: 'type.simplebannercomponent.name',
                name: 'Simple Banner Component'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    qualifier: 'id',
                    i18nKey: 'type.cmsparagraphcomponent.id.name'
                }, {
                    cmsStructureType: 'LongString',
                    qualifier: 'headline',
                    i18nKey: 'type.cmsparagraphcomponent.headline.name'
                }],
                category: 'NotToBeFound',
                code: 'XYZComponent',
                i18nKey: 'type.xyzcomponent.name',
                name: 'XYZ Component'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    qualifier: 'id',
                    i18nKey: 'type.thesmarteditcomponenttype.id.name'
                }, {
                    cmsStructureType: 'LongString',
                    qualifier: 'headline',
                    i18nKey: 'type.thesmarteditcomponenttype.headline.name'
                }, {
                    cmsStructureType: 'Boolean',
                    qualifier: 'active',
                    i18nKey: 'type.thesmarteditcomponenttype.active.name'
                }, {
                    cmsStructureType: 'Date',
                    qualifier: 'activationDate',
                    i18nKey: 'type.thesmarteditcomponenttype.activationDate.name'
                }, {
                    cmsStructureType: 'RichText',
                    qualifier: 'content',
                    i18nKey: 'type.thesmarteditcomponenttype.content.name'
                }, {
                    cmsStructureType: "LinkToggle",
                    qualifier: "linkToggle",
                    i18nKey: "se.editor.linkto.label",
                    localized: false
                }],
                category: 'NotToBeFound',
                code: 'thesmarteditComponentType',
                i18nKey: 'type.abccomponent.name',
                name: 'ABC Component'

            }, {
                attributes: [{
                    cmsStructureType: 'Media',
                    qualifier: 'media',
                    i18nKey: 'type.typewithmedia.media.name',
                    localized: true,
                    required: true,
                    containedTypes: ['Media']
                }],
                category: 'NotToBeFound',
                code: 'TypeWithMedia',
                i18nKey: 'type.TypeWithMedia.name',
                name: 'TypeWithMedia Component'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    qualifier: 'afield',
                    i18nKey: 'a field',
                    localized: false
                }, {
                    cmsStructureType: 'MediaContainer',
                    qualifier: 'media',
                    i18nKey: 'type.typewithmediacontainer.media.name',
                    localized: true,
                    required: true,
                    options: [{
                        "id": "widescreen",
                        "label": "se.media.format.widescreen"
                    }, {
                        "id": "desktop",
                        "label": "se.media.format.desktop"
                    }, {
                        "id": "tablet",
                        "label": "se.media.format.tablet"
                    }, {
                        "id": "mobile",
                        "label": "se.media.format.mobile"
                    }],
                    containedTypes: [
                        'MediaContainer',
                        'MediaFormat'
                    ]
                }],
                category: 'NotToBeFound',
                code: 'TypeWithMediaContainer',
                name: 'TypeWithMediaContainer Component'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    qualifier: 'id',
                    i18nKey: 'type.thesmarteditComponentType.id.name',
                    localized: false
                }, {
                    cmsStructureType: 'Media',
                    qualifier: 'media',
                    i18nKey: 'type.thesmarteditComponentType.media.name',
                    localized: true,
                    containedTypes: ['Media']
                }, {
                    cmsStructureType: 'Enum',
                    cmsStructureEnumType: 'de.mypackage.Orientation',
                    qualifier: 'orientation',
                    i18nKey: 'type.thesmarteditcomponenttype.orientation.name',
                    localized: false,
                    required: true
                }, {
                    cmsStructureType: 'LongString',
                    qualifier: 'headline',
                    i18nKey: 'type.thesmarteditComponentType.headline.name',
                    localized: false
                }, {
                    cmsStructureType: 'Boolean',
                    qualifier: 'active',
                    i18nKey: 'type.thesmarteditComponentType.active.name',
                    localized: false
                }, {
                    cmsStructureType: 'RichText',
                    qualifier: 'content',
                    i18nKey: 'type.thesmarteditComponentType.content.name',
                    localized: true
                }, {
                    cmsStructureType: "LinkToggle",
                    qualifier: "linkToggle",
                    i18nKey: "se.editor.linkto.label",
                    localized: false
                }],
                category: 'NotToBeFound',
                code: 'componentToValidateType',
                name: 'Validation Component'
            }, {
                attributes: [{
                    cmsStructureType: 'NavigationNodeSelector',
                    qualifier: 'navigationComponent',
                    i18nKey: 'type.thesmarteditcomponenttype.navigationComponent.name',
                    localized: false,
                    required: true,
                    editable: isNavigationNodeEditable
                }],
                category: 'NotToBeFound',
                code: 'NavigationComponentType',
                name: 'Navigation Component'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    collection: false,
                    editable: true,
                    i18nKey: 'type.productcarouselcomponent.title.name',
                    localized: true,
                    mode: 'DEFAULT',
                    paged: false,
                    qualifier: 'title',
                    required: false
                }, {
                    cmsStructureType: 'MultiProductSelector',
                    collection: false,
                    editable: isProductsEditable,
                    i18nKey: 'type.productcarouselcomponent.products.name',
                    localized: false,
                    mode: 'DEFAULT',
                    paged: false,
                    qualifier: 'products',
                    required: false
                }, {
                    cmsStructureType: 'MultiCategorySelector',
                    collection: false,
                    editable: isCategoriesEditable,
                    i18nKey: 'type.productcarouselcomponent.categories.name',
                    localized: false,
                    mode: 'DEFAULT',
                    paged: false,
                    qualifier: 'categories',
                    required: false
                }],
                category: 'COMPONENT',
                code: 'ProductCarouselComponent',
                i18nKey: 'type.productcarouselcomponent.name',
                name: 'Product Carousel',
                type: 'productCarouselComponentData'
            }, {
                attributes: [{
                    'cmsStructureType': 'ShortString',
                    'collection': false,
                    'editable': true,
                    'i18nKey': 'type.cmslinkcomponent.linkname.name',
                    'localized': true,
                    'mode': 'DEFAULT',
                    'paged': false,
                    'qualifier': 'linkName',
                    'required': true
                }, cmsLinkToComponentAttribute],
                category: 'COMPONENT',
                code: 'CMSLinkComponent',
                i18nKey: 'type.cmslinkcomponent.name',
                name: 'Link',
                type: 'cmsLinkComponentData'
            }, {
                attributes: [{
                    'cmsStructureType': 'SingleOnlineProductSelector',
                    'i18nKey': 'name',
                    'mode': 'DEFAULT',
                    'qualifier': 'product',
                    'required': true
                }],
                'category': 'COMPONENT',
                'code': 'TestSingleOnlineProductSelector',
                'i18nKey': 'type.testsingleonlineproductselector.name',
                'name': 'TestSingleOnlineProductSelector',
                'type': 'testComponentData'
            }, {
                attributes: [{
                    'cmsStructureType': 'SingleOnlineCategorySelector',
                    'i18nKey': 'name',
                    'mode': 'DEFAULT',
                    'qualifier': 'category',
                    'required': true
                }],
                'category': 'COMPONENT',
                'code': 'TestSingleOnlineCategorySelector',
                'i18nKey': 'type.testsingleonlinecategoryselector.name',
                'name': 'TestSingleOnlineCategorySelector',
                'type': 'testComponentData'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractcmscomponent.name.name',
                    localized: false,
                    qualifier: 'name'
                }],
                'category': 'NotToBeFound',
                'code': 'AbstractCMSComponent',
                'i18nKey': 'type.abstractcmscomponent.name',
                'name': 'abstractcmscomponent',
                'type': 'abstractcmscomponenttype'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractcmscomponent.name.name',
                    localized: false,
                    qualifier: 'name'
                }],
                'category': 'NotToBeFound',
                'code': 'componentType1',
                'i18nKey': 'type.abstractcmscomponent.name',
                'name': 'abstractcmscomponent',
                'type': 'componentType1'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.tabset.name.name',
                    localized: false,
                    qualifier: 'name'
                }, {
                    cmsStructureType: 'CMSItemDropdown',
                    i18nKey: 'type.tabset.topbanner.name',
                    localized: false,
                    idAttribute: 'uuid',
                    paged: true,
                    qualifier: 'banner',
                    params: {
                        typeCode: 'BannerComponent'
                    },
                    subTypes: {
                        'BannerComponent': 'type.banner.name',
                        'ResponsiveBannerComponent': 'type.responsivebanner.name'
                    }
                }, {
                    cmsStructureType: 'CMSItemDropdown',
                    i18nKey: 'type.tabset.tabs.name',
                    collection: true,
                    localized: false,
                    qualifier: 'tabs',
                    idAttribute: 'uuid',
                    paged: true,
                    required: false,
                    params: {
                        typeCode: 'CmsTab'
                    },
                    subTypes: {
                        'CmsTab': 'type.cmstab.name'
                    }
                }, {
                    cmsStructureType: "CMSItemDropdown",
                    i18nKey: "type.tabset.links.name",
                    collection: true,
                    localized: false,
                    qualifier: 'links',
                    idAttribute: 'uuid',
                    paged: true,
                    required: false,
                    params: {
                        typeCode: 'CMSLinkComponent'
                    },
                    subTypes: {
                        'CMSLinkComponent': 'type.cmslinkcomponent.name'
                    }
                }],
                'category': 'COMPONENT',
                'code': 'TabsetComponent', // Example of a nested component
                'i18nKey': 'type.tabset.name',
                'name': 'TabsetComponent',
                'type': 'TabsetComponentData'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.banner.name.name',
                    localized: false,
                    qualifier: 'name'
                }, {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.tabset.imagepath.name',
                    localized: false,
                    qualifier: 'image'
                }],
                'category': 'COMPONENT',
                'code': 'BannerComponent',
                'i18nKey': 'type.tabset.name',
                'name': 'BannerComponent',
                'type': 'BannerComponentData'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.responsivebannercomponent.name.name',
                    localized: false,
                    qualifier: 'name'
                }, {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.tabset.imagepath.name',
                    localized: false,
                    qualifier: 'image'
                }, {
                    cmsStructureType: 'Boolean',
                    i18nKey: 'type.tabset.rotate.name',
                    localized: false,
                    qualifier: 'rotate'
                }],
                'category': 'COMPONENT',
                'code': 'ResponsiveBannerComponent',
                'i18nKey': 'type.tabset.name',
                'name': 'ResponsiveBannerComponent',
                'type': 'ResponsiveBannerComponentData'
            }, {
                attributes: [{
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.tabset.name.name',
                    localized: false,
                    qualifier: 'name'
                }, {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.tabset.title.name',
                    localized: false,
                    qualifier: 'title'
                }, {
                    cmsStructureType: 'CMSItemDropdown',
                    i18nKey: 'type.tabset.tabs.name',
                    collection: true,
                    localized: false,
                    qualifier: 'tabs',
                    idAttribute: 'uuid',
                    paged: true,
                    required: false,
                    params: {
                        typeCode: 'CmsTab'
                    },
                    subTypes: {
                        'CmsTab': 'type.cmstab.name'
                    }
                }],
                'category': 'COMPONENT',
                'code': 'CmsTab',
                'i18nKey': 'type.tab.name',
                'name': 'CmsTab',
                'type': 'CmsTabData'
            }];

            componentTypes.forEach(function(componentType) {
                componentType.attributes.unshift({
                    cmsStructureType: 'Boolean',
                    i18nKey: 'type.component.abstractcmscomponent.visible.name',
                    localized: false,
                    qualifier: 'visible',
                    postfix: 'se.cms.visible.postfix.text'
                });
            });

            var catalogStructureApiMocks = $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSLinkComponent\&mode=CATEGORY/);
            catalogStructureApiMocks.respond(function(method, url, data, headers) {
                var componentType = {
                    attributes: [{
                            'cmsStructureType': 'ShortString',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.linkname.name',
                            'localized': true,
                            'paged': false,
                            'qualifier': 'linkName',
                            'required': true
                        },
                        cmsLinkToComponentAttribute, {
                            'cmsStructureType': 'SingleOnlineCategorySelector',
                            'i18nKey': 'name',
                            'qualifier': 'category',
                            'required': true
                        }, {
                            'cmsStructureType': 'Boolean',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.target.name',
                            'localized': false,
                            'paged': false,
                            'qualifier': 'target',
                            'required': true
                        }
                    ],
                    'category': 'COMPONENT',
                    'code': 'CMSLinkComponent',
                    'i18nKey': 'type.cmslinkcomponent.name',
                    'name': 'Link',
                    'type': 'cmsLinkComponentData'
                };
                return [200, componentType];
            });

            var productStructureApiMocks = $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSLinkComponent\&mode=PRODUCT/);
            productStructureApiMocks.respond(function(method, url, data, headers) {
                var componentType = {
                    attributes: [{
                            'cmsStructureType': 'ShortString',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.linkname.name',
                            'localized': true,
                            'paged': false,
                            'qualifier': 'linkName',
                            'required': true
                        },
                        cmsLinkToComponentAttribute, {
                            'cmsStructureType': 'SingleOnlineProductSelector',
                            'i18nKey': 'name',
                            'qualifier': 'product',
                            'required': true
                        }, {
                            'cmsStructureType': 'Boolean',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.target.name',
                            'localized': false,
                            'paged': false,
                            'qualifier': 'target',
                            'required': true
                        }
                    ],
                    'category': 'COMPONENT',
                    'code': 'CMSLinkComponent',
                    'i18nKey': 'type.cmslinkcomponent.name',
                    'name': 'Link',
                    'type': 'cmsLinkComponentData'
                };
                return [200, componentType];
            });

            var contentStructureApiMocks = $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSLinkComponent\&mode=CONTENT/);
            contentStructureApiMocks.respond(function(method, url, data, headers) {
                var componentType = {
                    attributes: [{
                            'cmsStructureType': 'ShortString',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.linkname.name',
                            'localized': true,
                            'paged': false,
                            'qualifier': 'linkName',
                            'required': true
                        },
                        cmsLinkToComponentAttribute, {
                            'cmsStructureType': 'EditableDropdown',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.contentpage.name',
                            'localized': false,
                            'paged': true,
                            'qualifier': 'contentPage',
                            'required': true,
                            'idAttribute': 'uid',
                            'labelAttributes': ['name']
                        }, {
                            'cmsStructureType': 'Boolean',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.target.name',
                            'localized': false,
                            'paged': false,
                            'qualifier': 'target',
                            'required': true
                        }
                    ],
                    'category': 'COMPONENT',
                    'code': 'CMSLinkComponent',
                    'i18nKey': 'type.cmslinkcomponent.name',
                    'name': 'Link',
                    'type': 'cmsLinkComponentData'
                };
                return [200, componentType];
            });

            var externalLinkStructureApiMocks = $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=CMSLinkComponent\&mode=EXTERNAL/);
            externalLinkStructureApiMocks.respond(function(method, url, data, headers) {
                var componentType = {
                    attributes: [{
                            'cmsStructureType': 'ShortString',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.linkname.name',
                            'localized': true,
                            'paged': false,
                            'qualifier': 'linkName',
                            'required': true
                        },
                        cmsLinkToComponentAttribute, {
                            'cmsStructureType': 'ShortString',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.url.name',
                            'localized': false,
                            'paged': false,
                            'qualifier': 'url',
                            'required': true
                        }, {
                            'cmsStructureType': 'Boolean',
                            'collection': false,
                            'editable': true,
                            'i18nKey': 'type.cmslinkcomponent.target.name',
                            'localized': false,
                            'paged': false,
                            'qualifier': 'target',
                            'required': true
                        }
                    ],
                    'category': 'COMPONENT',
                    'code': 'CMSLinkComponent',
                    'i18nKey': 'type.cmslinkcomponent.name',
                    'name': 'Link',
                    'type': 'cmsLinkComponentData'
                };
                return [200, componentType];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?category\=COMPONENT/).respond({
                componentTypes: componentTypes.filter(function(type) {
                    return type.category === 'COMPONENT';
                })
            });

            var componentTypesGenericGET = $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=(.*)\&mode=DEFAULT/).respond(function(method, url, data, headers) {
                var typeCode = /cmswebservices\/v1\/types\?code=(.*)\&mode=DEFAULT/.exec(url)[1];

                var componentType = componentTypes.find(function(type) {
                    return type.code === typeCode;
                });
                return [200, componentType];
            });

            backendMocksUtils.storeBackendMock('componentTypesGenericGET', componentTypesGenericGET);

            $httpBackend.whenGET(/cmswebservices\/v1\/types\/(.*)/).respond(function(method, url, data, headers) {

                var typeCode = /cmswebservices\/v1\/types\/(.*)/.exec(url)[1];

                var componentType = componentTypes.find(function(type) {
                    return type.code === typeCode;
                });
                return [200, componentType];

            });

            var orientationEnums = {
                enums: [{
                    code: 'vertical',
                    label: 'Vertical'
                }, {
                    code: 'horizontal',
                    label: 'Horizontal'
                }, ]
            };

            $httpBackend.whenGET(/cmswebservices\/v1\/enums/).respond(function(method, url, data, headers) {
                var enumClass = parseQuery(url).enumClass;
                if (enumClass === 'de.mypackage.Orientation') {
                    return [200, orientationEnums];
                } else {
                    return [404];
                }
            });

            var componentTypesPermissionsGET = $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/(.*)\/types\?permissionNames=create,change,read,remove&types=(.*)/).respond({
                "permissionsList": [{
                    "id": "ProductCarouselComponent",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "CMSParagraphComponent",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "FooterNavigationComponent",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "CMSLinkComponent",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "SimpleBannerComponent",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "SimpleResponsiveBannerComponent",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "componentType1",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "componentType2",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "false"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "false"
                    }]
                }, {
                    "id": "componentType4",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "componentType10",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "ContentSlot",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "ContentPage",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "CategoryPage",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "ProductPage",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "CMSVersion",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }]
            });

            backendMocksUtils.storeBackendMock('componentTypesPermissionsGET', componentTypesPermissionsGET);
        });
try {
    angular.module('smarteditloader').requires.push('componentTypesMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('componentTypesMocks');
} catch (e) {}
