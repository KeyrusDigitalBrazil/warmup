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
import { UpdatePasswordComponent } from './update-password.component';
import { UserService }          from '../shared/occ/user.service';
import { ValidationService }    from '../shared/error-handling/validation.service';

describe('Component - Update password', () => {
    let updatePasswordComponent: UpdatePasswordComponent;
    let userService: UserService;
    let cookieService: CookieService;
    let fixture: ComponentFixture<UpdatePasswordComponent>;
    
    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ UpdatePasswordComponent ],
            providers:    [ CookieService, Http, OAuth2Service, OCCService, GlobalVarService, UserService, ValidationService, {provide: ConnectionBackend, useClass: MockBackend}, {provide: RequestOptions, useClass: BaseRequestOptions}],
            imports: [ CookieModule.forRoot(), FormsModule, ErrorHandlingModule, RouterTestingModule ]
        })
    }));
    
    beforeEach(() => {
        fixture = TestBed.createComponent(UpdatePasswordComponent);
        updatePasswordComponent = fixture.componentInstance;
        userService = fixture.debugElement.injector.get(UserService);
        cookieService = fixture.debugElement.injector.get(CookieService);
    });
    
    it('Should not call user service if field validation fails', fakeAsync(() => {
        // Given
        prepareSpies();
        
        // When
        clickSubmitButton();
        
        // Then
        expect(updatePasswordComponent.onSubmit).toHaveBeenCalled()
        expect(updatePasswordComponent.validateForm).toHaveBeenCalled();
        expect(updatePasswordComponent.clearFormMessages).toHaveBeenCalled();
        expect(updatePasswordComponent.validateFieldEmpty).toHaveBeenCalledWith('currentPassword', undefined);
        expect(updatePasswordComponent.validateFieldEmpty).toHaveBeenCalledWith('password', undefined);
        expect(updatePasswordComponent.errorMsgs.length).toBe(1);
        expect(userService.changePassword).not.toHaveBeenCalled();
    }));
    
    it('Should call user service when field validation passes', fakeAsync(() => {
        // Given
        prepareSpies();
        setPassword();
        
        // When
        clickSubmitButton();
        
        // Then
        expect(updatePasswordComponent.onSubmit).toHaveBeenCalled()
        expect(updatePasswordComponent.validateForm).toHaveBeenCalled();
        expect(updatePasswordComponent.clearFormMessages).toHaveBeenCalled();
        expect(updatePasswordComponent.validateFieldEmpty).toHaveBeenCalledWith('currentPassword', '123456');
        expect(updatePasswordComponent.validateFieldEmpty).toHaveBeenCalledWith('password', 'abcdef');
        expect(updatePasswordComponent.validateFieldEmpty).toHaveBeenCalledWith('confirmedNewPassword', 'abcdef');
        expect(updatePasswordComponent.errorMsgs.length).toBe(0);
        expect(userService.changePassword).toHaveBeenCalled();
    }));
    
    
    function clickSubmitButton() {
        let updateButton = fixture.debugElement.query(By.css('form'));
        updateButton.triggerEventHandler('ngSubmit', null);
    }
    
    function prepareSpies() {
        spyOn(userService, 'changePassword').and.returnValue( Observable.of({}));
        spyOn(updatePasswordComponent, 'onSubmit').and.callThrough();
        spyOn(updatePasswordComponent, 'validateForm').and.callThrough();
        spyOn(AbstractComponent.prototype, 'clearFormMessages').and.callThrough();
        spyOn(AbstractComponent.prototype, 'validateFieldEmpty').and.callThrough();
    }
    
    function setPassword() {
        fixture.detectChanges();
        updatePasswordComponent.currentPassword = '123456';
        updatePasswordComponent.newPassword = 'abcdef';
        updatePasswordComponent.confirmedNewPassword = 'abcdef';
    }
});
