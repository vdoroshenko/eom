import axios from "axios";
import * as uri from "../constants/uris/UserURI";

const baseURI = "http://localhost:4000/";
// API Users static class
export default class ApiUsers {
  // add/edit a user
  static login() {
    return new Promise(resolve => {
      setTimeout(() => {
        // do something here
        resolve();
      }, 500);
    });
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
    return axios.get(baseURI+uri.USER_CHECK_URI);
  }
}
