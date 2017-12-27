import { takeLatest } from "redux-saga";
import { fork } from "redux-saga/effects";
import * as types from "../../common/constants/UserActionTypes";
import { checkLogin, userLogin, userLogout } from "./users";

// main saga generators
export function* sagas() {
  yield [
    fork(takeLatest, types.USER_CHECK_LOGIN, checkLogin),
    fork(takeLatest, types.USER_LOGIN, userLogin),
    fork(takeLatest, types.USER_LOGOUT, userLogout),
  ];
}
