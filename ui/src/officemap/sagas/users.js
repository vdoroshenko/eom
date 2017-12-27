import { call, put } from "redux-saga/effects";
import ApiUsers from "../../common/api/users";
import * as types from "../../common/constants/UserActionTypes";

// add/edit a user
export function* checkLogin(action) {
  // call check login
  yield call(ApiUsers.check);

  // update the state
  yield put({
    type: types.USER_CHECK_LOGIN_DONE,
    user: action.user,
  });

}

// add/edit a user
export function* userLogin(action) {
  // call the api to login the user
  yield call(ApiUsers.login);
  //return action.callbackError("Some error");   // show an error when the API fails

  // update the state by logging the user
  yield put({
    type: types.USER_LOGIN_DONE,
    user: action.user,
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
