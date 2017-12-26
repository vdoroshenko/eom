import { call, put } from "redux-saga/effects";
import ApiUsers from "../api/users";

// add/edit a user
export function* checkLogin(action) {
  // call check login
  yield call(ApiUsers.check);
  //return action.callbackError("Some error");   // show an error when the API fails

  // update the state
  yield put({
    type: 'CHECK_USER_LOGIN',
    user: action.user,
  });

  // success
  action.callbackSuccess();
}

// add/edit a user
export function* userLogin(action) {
  // call the api to login the user
  yield call(ApiUsers.login);
  //return action.callbackError("Some error");   // show an error when the API fails

  // update the state by logging the user
  yield put({
    type: 'USER_LOGIN',
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
    type: 'USER_LOGOUT',
    user_id: action.user_id,
  });
}
