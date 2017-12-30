import axios from "axios";
import * as uri from "../constants/uris/UserURI";

const baseURI = "http://localhost:4000/";
// API Users static class
export default class ApiUsers {
  // add/edit a user
  static login(username='', password='') {
    let url = baseURI+uri.USER_LOGIN_URI;
    let params = new URLSearchParams();
    params.append('username', username);
    params.append('password', password);
    return axios.post(url, params);
  }

  // delete a user
  static logout() {
    return new Promise(resolve => {
      setTimeout(() => {
        // do something here
        resolve();
      }, 500);
    });
  }
  // check is user logged or not
  static check() {
    let url = baseURI+uri.USER_CHECK_URI;
    return axios.get(url);
  }
}
