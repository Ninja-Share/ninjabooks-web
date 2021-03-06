import { Component, OnInit } from '@angular/core';
import { User } from '../models/index';
import { UserService, BookService } from '../services/index';

@Component({
    templateUrl: './notifications.component.html'
})

export class NotificationComponent implements OnInit {
  users: User[] = [];
  currentUser: User;

   constructor(private userService: UserService,
              private bookService: BookService) {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
 }
   ngOnInit() {
       // get users from secure api end point
       this.userService.getUsers()
           .subscribe(users => {
               this.users = users;
           });
      // this.bookService.notification(this.currentUser.id).
      // subscribe(data => {
      //   this.data = data;
      //   console.log(this.data);
      // })
   }
 }
