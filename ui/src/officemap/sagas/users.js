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
    type: types.USER_CHECK_LOGIN_DONE,
    user: user,
  });

}

// add/edit a user
export function* userLogin(action) {
  // call the api to login the user
  let user = {};
  try {
    user = yield call(ApiUsers.login, action.user.username, action.user.password);
  } catch (e) {
    return action.callbackError(e);   // show an error when the API fails
  }

  // update the state by logging the user
  yield put({
    type: types.USER_LOGIN_DONE,
    user: user,
  });

  // success
  action.callbackSuccess();
}

// delete a user
export function* userLogout(action) {
  // call the api to logout the user
  yield call(ApiUsers.logout);

  // update the state logout the user
  yield put({
    type: types.USER_LOGOUT_DONE
  });

  // success
  action.callbackSuccess();
}
