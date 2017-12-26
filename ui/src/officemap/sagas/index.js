import { takeLatest } from "redux-saga";
import { fork } from "redux-saga/effects";
import { checkLogin, userLogin, userLogout } from "./users";

// main saga generators
export function* sagas() {
  yield [
    fork(takeLatest, 'CHECK_USER_LOGIN', checkLogin),
    fork(takeLatest, 'USER_LOGIN', userLogin),
    fork(takeLatest, 'USER_LOGOUT', userLogout),
  ];
}
