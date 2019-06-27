import { platformBrowser }    from '@angular/platform-browser';
import { AppModuleNgFactory } from '../aot_gen/src/app/app.module.ngfactory';

platformBrowser().bootstrapModuleFactory(AppModuleNgFactory);