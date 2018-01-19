import * as types from "../../common/constants/actions/UserActionTypes";
// users reducer
export default function users(state = {}, action) {
  switch (action.type) {

    case types.USER_CHECK_LOGIN_SUCCESS:
    case types.USER_LOGIN_SUCCESS: {
      const newState = {user:action.user,auth:action.data};
      return newState;
    };

    case types.USER_LOGOUT_DONE: {
      return {};
    }

    // initial state
    default:
      return state;
  }
}
