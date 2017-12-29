import * as types from "../../common/constants/actions/UserActionTypes";
// users reducer
export default function users(state = {}, action) {
  switch (action.type) {

    case types.USER_CHECK_LOGIN_DONE:
    case types.USER_LOGIN_DONE:
      const user = action.user;
      return {...state, user};

    case types.USER_LOGOUT_DONE: {
      let {user, ...newState} = state;
      user = null;
      newState.user = {};
      return newState;
    }

    // initial state
    default:
      return state;
  }
}
