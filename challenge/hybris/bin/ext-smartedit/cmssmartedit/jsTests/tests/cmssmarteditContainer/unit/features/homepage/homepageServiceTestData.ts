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
	ICatalogs
} from 'smarteditcommons';

export const homepageServiceTestData: ICatalogs = {
	catalogs: [{
		catalogId: "electronicsContentCatalog",
		name: {
			en: "Electronics Content Catalog",
			de: "Elektronikkatalog",
			ja: "エレクトロニクス コンテンツ カタログ",
			zh: "电子产品内容目录"
		},
		versions: [{
			active: true,
			catalogId: "electronicsContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog",
				de: "Elektronikkatalog",
				ja: "エレクトロニクス コンテンツ カタログ",
				zh: "电子产品内容目录"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronicsContentCatalog/Online",
					name: "Homepage",
					uid: "homepage"
				},
				old: {
					catalogVersionUuid: "electronicsContentCatalog/Online",
					name: "Homepage",
					uid: "homepage"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oMmMvaDUxLzg3OTY4ODg3OTMxMTgucG5nfGM5ZWIzMGIzMzYxYzliYjllMmRlNzNjMmQzNzg2ZmUwZWExMzQxYzZmNTY0Mzg3YWE3MWQxYmVmYmJkZTliMDU&attachment=true",
			uuid: "electronicsContentCatalog/Online",
			version: "Online"
		}, {
			active: false,
			catalogId: "electronicsContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog",
				de: "Elektronikkatalog",
				ja: "エレクトロニクス コンテンツ カタログ",
				zh: "电子产品内容目录"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronicsContentCatalog/Staged",
					name: "Homepage",
					uid: "homepage"
				},
				old: {
					catalogVersionUuid: "electronicsContentCatalog/Online",
					name: "Homepage",
					uid: "homepage"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oMmMvaDUxLzg3OTY4ODg3OTMxMTgucG5nfGM5ZWIzMGIzMzYxYzliYjllMmRlNzNjMmQzNzg2ZmUwZWExMzQxYzZmNTY0Mzg3YWE3MWQxYmVmYmJkZTliMDU&attachment=true",
			uuid: "electronicsContentCatalog/Staged",
			version: "Staged"
		}]
	}, {
		catalogId: "electronics-euContentCatalog",
		name: {
			en: "Electronics Content Catalog EU",
			de: "Elektronikkatalog EU",
			ja: "エレクトロニクス コンテンツ カタログ EU",
			zh: "电子产品内容目录 EU"
		},
		versions: [{
			active: true,
			catalogId: "electronics-euContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog EU",
				de: "Elektronikkatalog EU",
				ja: "エレクトロニクス コンテンツ カタログ EU",
				zh: "电子产品内容目录 EU"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				},
				fallback: {
					catalogVersionUuid: "electronicsContentCatalog/Online",
					name: "Homepage",
					uid: "homepage"
				},
				old: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oMTkvaGQyLzg3OTcwMDU1MTI3MzQucG5nfDllOGUwNDZjYWEzNjZlZjEwMTA2NDdjNGJkMDY4OGE4NmMyYWNlZDliMzIzMDk5YTAxYWI1OWM5YWJhYWYyYWM&attachment=true",
			uuid: "electronics-euContentCatalog/Online",
			version: "Online"
		}, {
			active: false,
			catalogId: "electronics-euContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog EU",
				de: "Elektronikkatalog EU",
				ja: "エレクトロニクス コンテンツ カタログ EU",
				zh: "电子产品内容目录 EU"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronics-euContentCatalog/Staged",
					name: "Homepage EU",
					uid: "homepage-eu"
				},
				fallback: {
					catalogVersionUuid: "electronicsContentCatalog/Online",
					name: "Homepage",
					uid: "homepage"
				},
				old: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oMTkvaGQyLzg3OTcwMDU1MTI3MzQucG5nfDllOGUwNDZjYWEzNjZlZjEwMTA2NDdjNGJkMDY4OGE4NmMyYWNlZDliMzIzMDk5YTAxYWI1OWM5YWJhYWYyYWM&attachment=true",
			uuid: "electronics-euContentCatalog/Staged",
			version: "Staged"
		}]
	}, {
		catalogId: "electronics-ukContentCatalog",
		name: {
			en: "Electronics Content Catalog UK",
			de: "Elektronikkatalog UK",
			ja: "エレクトロニクス コンテンツ カタログ UK",
			zh: "电子产品内容目录 UK"
		},
		versions: [{
			active: true,
			catalogId: "electronics-ukContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog UK",
				de: "Elektronikkatalog UK",
				ja: "エレクトロニクス コンテンツ カタログ UK",
				zh: "电子产品内容目录 UK"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronics-ukContentCatalog/Online",
					name: "Homepage UK",
					uid: "homepage-uk"
				},
				fallback: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				},
				old: {
					catalogVersionUuid: "electronics-ukContentCatalog/Online",
					name: "Homepage UK",
					uid: "homepage-uk"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oODgvaGUxLzg3OTcwMTIxNjQ2MzgucG5nfGUwMWM3NjFmNzViNjRlMTkzZTBlM2Q1ZDVkNGE2Yjk3NDdmYzliMTk4ZjQ0NTBlYWRiMGVjM2IxY2Y2NWVkMDk&attachment=true",
			uuid: "electronics-ukContentCatalog/Online",
			version: "Online"
		}, {
			active: false,
			catalogId: "electronics-ukContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog UK",
				de: "Elektronikkatalog UK",
				ja: "エレクトロニクス コンテンツ カタログ UK",
				zh: "电子产品内容目录 UK"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronics-ukContentCatalog/Staged",
					name: "n1",
					uid: "cmsitem_00003001"
				},
				fallback: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				},
				old: {
					catalogVersionUuid: "electronics-ukContentCatalog/Online",
					name: "Homepage UK",
					uid: "homepage-uk"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			uuid: "electronics-ukContentCatalog/Staged",
			version: "Staged"
		}]
	}, {
		catalogId: "electronics-frContentCatalog",
		name: {
			en: "Electronics Content Catalog FR"
		},
		versions: [{
			active: true,
			catalogId: "electronics-frContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog FR"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oODgvaGUxLzg3OTcwMTIxNjQ2MzgucG5nfGUwMWM3NjFmNzViNjRlMTkzZTBlM2Q1ZDVkNGE2Yjk3NDdmYzliMTk4ZjQ0NTBlYWRiMGVjM2IxY2Y2NWVkMDk&attachment=true",
			uuid: "electronics-frContentCatalog/Online",
			version: "Online"
		}, {
			active: false,
			catalogId: "electronics-frContentCatalog",
			catalogName: {
				en: "Electronics Content Catalog FR"
			},
			homepage: {
				current: {
					catalogVersionUuid: "electronics-euContentCatalog/Online",
					name: "Homepage EU",
					uid: "homepage-eu"
				}
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			uuid: "electronics-frContentCatalog/Staged",
			version: "Staged"
		}]
	}, {
		catalogId: "electronics-noHomepage",
		name: {
			en: "Electronics Content Catalog no homepage"
		},
		versions: [{
			active: true,
			catalogId: "electronics-noHomepage",
			catalogName: {
				en: "Electronics Content Catalog no homepage"
			},
			pageDisplayConditions: [{
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "ProductPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}],
				typecode: "CategoryPage"
			}, {
				options: [{
					id: "PRIMARY",
					label: "page.displaycondition.primary"
				}, {
					id: "VARIATION",
					label: "page.displaycondition.variation"
				}],
				typecode: "ContentPage"
			}],
			thumbnailUrl: "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3wxNTU4MDR8aW1hZ2UvcG5nfGltYWdlcy9oODgvaGUxLzg3OTcwMTIxNjQ2MzgucG5nfGUwMWM3NjFmNzViNjRlMTkzZTBlM2Q1ZDVkNGE2Yjk3NDdmYzliMTk4ZjQ0NTBlYWRiMGVjM2IxY2Y2NWVkMDk&attachment=true",
			uuid: "electronics-noHomepage/Online",
			version: "Online"
		}]
	}]
};



