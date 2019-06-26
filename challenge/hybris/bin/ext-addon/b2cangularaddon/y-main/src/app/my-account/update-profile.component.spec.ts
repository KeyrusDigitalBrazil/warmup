import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import { AbstractComponent } from './abstract.component';
import { By }              from '@angular/platform-browser';
import { BaseRequestOptions, ConnectionBackend, Http, RequestOptions} from "@angular/http";
import { CookieModule, CookieService }  from 'ngx-cookie';
import { ErrorHandlingModule }    from '../shared/error-handling/error-handling.module';
import { FormsModule} from "@angular/forms";
import { OAuth2Service } from '../shared/occ/oauth2.service';
import { Observable } from "rxjs/Rx";
import { OCCService }     from '../shared/occ/occ.service';
import { GlobalVarService }  from '../shared/occ/global-var.service';
import { MockBackend} from "@angular/http/testing";
import { RouterTestingModule } from '@angular/router/testing';
import { Title }               from '../shared/occ/title';
import { Titles }               from '../shared/occ/titles';
import { UpdateProfileComponent } from './update-profile.component';
import { User }               from '../shared/occ/user';
import { UserService }          from '../shared/occ/user.service';
import { ValidationService }    from '../shared/error-handling/validation.service';

describe('Component - Update profile', () => {
    let updateProfileComponent: UpdateProfileComponent;
    let userService: UserService;
    let fixture: ComponentFixture<UpdateProfileComponent>;
    
    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ UpdateProfileComponent ],
            providers:    [ CookieService, Http, OAuth2Service, OCCService, GlobalVarService, UserService, ValidationService, {provide: ConnectionBackend, useClass: MockBackend}, {provide: RequestOptions, useClass: BaseRequestOptions}],
            imports: [ CookieModule.forRoot(), FormsModule, ErrorHandlingModule, RouterTestingModule ]
        })
    }));
    
    beforeEach(() => {
        fixture = TestBed.createComponent(UpdateProfileComponent);
        updateProfileComponent = fixture.componentInstance;
        userService = fixture.debugElement.injector.get(UserService);
    });
    
    it('Should not call user service if field validation fails', fakeAsync(() => {
        // Given
        prepareSpies();
        setTitleAndUser();
        updateProfileComponent.user.firstName = '';
        updateProfileComponent.user.lastName = '';
        
        // When
        clickSubmitButton();
        
        // Then
        expect(updateProfileComponent.onSubmit).toHaveBeenCalled()
        expect(updateProfileComponent.validateForm).toHaveBeenCalled();
        expect(updateProfileComponent.clearFormMessages).toHaveBeenCalled();
        expect(updateProfileComponent.validateFieldEmpty).toHaveBeenCalledWith('firstName', '');
        expect(updateProfileComponent.validateFieldEmpty).toHaveBeenCalledWith('lastName', '');
        expect(updateProfileComponent.errorMsgs.length).toBe(1);
        expect(userService.update).not.toHaveBeenCalled();
    }));
    
    it('Should call user service when field validation passes', fakeAsync(() => {
        // Given
        prepareSpies();
        setTitleAndUser();
        
        // When
        clickSubmitButton();
        
        // Then
        expect(updateProfileComponent.onSubmit).toHaveBeenCalled()
        expect(updateProfileComponent.validateForm).toHaveBeenCalled();
        expect(updateProfileComponent.clearFormMessages).toHaveBeenCalled();
        expect(updateProfileComponent.validateFieldEmpty).toHaveBeenCalledWith('firstName', 'firstName');
        expect(updateProfileComponent.validateFieldEmpty).toHaveBeenCalledWith('lastName', 'lastName');
        expect(updateProfileComponent.errorMsgs.length).toBe(0);
        expect(userService.update).toHaveBeenCalled();
    }));
    
    function clickSubmitButton() {
        let updateButton = fixture.debugElement.query(By.css('#updateProfileForm'));
        updateButton.triggerEventHandler('ngSubmit', null);
    }
    
    function prepareSpies() {
        spyOn(userService, 'update').and.returnValue( Observable.of({}));
        spyOn(updateProfileComponent, 'ngOnInit').and.callThrough();
        spyOn(updateProfileComponent, 'onSubmit').and.callThrough();
        spyOn(updateProfileComponent, 'validateForm').and.callThrough();
        spyOn(AbstractComponent.prototype, 'clearFormMessages').and.callThrough();
        spyOn(AbstractComponent.prototype, 'validateFieldEmpty').and.callThrough();
    }
    
    function setTitleAndUser() {
        let title:Title = new Title();
        title.code = 'code';
        title.name = 'name';
        
        let titles:Titles = new Titles();
        titles.titles = [title];
        titles.titles.push(title);
        updateProfileComponent.titles = titles;
        
        let usr: User = new User();
        usr.uid = 'uid';
        usr.firstName = 'firstName';
        usr.lastName = 'lastName';
        usr.titleCode = 'titleCode';
        updateProfileComponent.user = usr;
        fixture.detectChanges();
    }
});
