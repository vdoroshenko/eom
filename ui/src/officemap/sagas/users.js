import { call, put } from "redux-saga/effects";
import ApiUsers from "../../common/api/users";
import * as types from "../../common/constants/actions/UserActionTypes";

// add/edit a user
export function* checkLogin(action) {
  // call check login
  let user = {};
  try {
    user = yield call(ApiUsers.check);
  } catch(e) {
    //return action.callbackError("Some error");   // show an error when the API fails
  }

  // update the state
  yield put({
    type: types.USER_CHECK_LOGIN_SUCCESS,
    user: user,
  });

}

// add/edit a user
export function* userLogin(action) {
  // call the api to login the user
  let res = {};
  try {
    res = yield call(ApiUsers.login, action.user.username, action.user.password);
    ApiUsers._setDefaultAuth(res.data.token_type+' '+res.data.access_token);
    // update the state by logging the user
    yield put({
      type: types.USER_LOGIN_SUCCESS,
      data: res.data,
      user: action.user
    });

    // success
    action.callbackSuccess();
  } catch (err) {
    let cause = err.message;
    if (err.response && err.response.data && err.response.data.error) {
      cause = err.response.data.error;
    }
    action.callbackError("Login is failed with cause: "+cause);   // show an error when the API fails
  }
}

// Revoke user's token i.e. logout
export function* userLogout(action) {
  // call the api to logout the user
  yield call(ApiUsers.logout);

  ApiUsers._setDefaultAuth(undefined);
  // update the state logout the user
  yield put({
    type: types.USER_LOGOUT_DONE
  });

  // success
  action.callbackSuccess();
}
