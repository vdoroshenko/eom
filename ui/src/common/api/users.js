import axios from "axios";
import * as uri from "../constants/uris/UserURI";

const baseURI = "http://localhost:4000/";
// API Users static class
export default class ApiUsers {
  // add/edit a user
  static login(username='', password='') {
    let url = baseURI+uri.USER_LOGIN_URI;
    let params = new URLSearchParams();
    params.append('grant_type', "password");
    params.append('username', username);
    params.append('password', password);
    return axios.post(url, params, {headers:{'Authorization':'Basic '+uri.AUTH_TOKEN}});
  }

  static _setDefaultAuth(token) {
    axios.defaults.headers.common['Authorization'] = token;
  }

  // refresh token
  static refresh(refreshToken='') {
    let url = baseURI+uri.USER_LOGIN_URI;
    let params = new URLSearchParams();
    params.append('grant_type', "refresh_token");
    params.append('refresh_token', refreshToken);
    return axios.post(url, params, {headers:{'Authorization':'Basic '+uri.AUTH_TOKEN}});
  }

  static introspection(token='') {
    let url = baseURI+uri.USER_INTROS_URI;
    let params = new URLSearchParams();
    params.append('token_type_hint', "access_token");
    params.append('token', token);
    return axios.post(url, params, {headers:{'Authorization':'Basic '+uri.AUTH_TOKEN}});
  }

  // revoke token
  static logout() {
    /*
    return new Promise(resolve => {
      setTimeout(() => {
        // do something here
        resolve();
      }, 500);
    });
    */
    let url = baseURI+uri.USER_LOGOUT_URI;
    return axios.get(url);
  }
  static revoke(token='', refresh_token='') {
    let url = baseURI+uri.USER_LOGOUT_URI;
    let paramsAccessToken = new URLSearchParams();
    paramsAccessToken.append('token_type_hint', "access_token");
    paramsAccessToken.append('token', token);
    let paramsRefreshToken = new URLSearchParams();
    paramsRefreshToken.append('token_type_hint', "refresh_token");
    paramsRefreshToken.append('token', refresh_token);
    return axios.post(url, paramsRefreshToken).then(() => {
      return axios.post(url,paramsAccessToken);
    });
  }
  // check is user logged or not
  static check() {
    let url = baseURI+uri.USER_CHECK_URI;
    return axios.get(url);
  }
}
