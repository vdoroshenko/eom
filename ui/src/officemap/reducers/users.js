// users reducer
export default function users(state = {}, action) {
  switch (action.type) {

    case 'CHECK_USER_LOGIN':
    case 'USER_LOGIN':
      const user = action.user;
      return [...state, user];

    case 'USER_LOGOUT': {
      let {user, ...newState} = state;
      user = null;
      return newState;
    }

    // initial state
    default:
      return state;
  }
}
