import assert from "assert";
import users from "../../../src/officemap/reducers/users";
import * as types from "../../../src/common/constants/actions/UserActionTypes";

// unit tests for the users reducers
// mocha - http://mochajs.org/#getting-started
// assert - https://nodejs.org/api/assert.html#assert_assert_deepequal_actual_expected_message
describe('Users reducer', () => {
  describe(types.USER_LOGIN_SUCCESS, () => {
    it('should return a logged user', () => {
      assert.deepEqual(
        users({}, {
          type: types.USER_LOGIN_SUCCESS,
          user: {
            username: 'LoginName',
            password: 'secret',
          },
          data: {access_token:"123",token_type: 'bearer'}
        }), {
          user: {
            username: 'LoginName',
            password: 'secret',
          },
          auth: {access_token:"123",token_type: 'bearer'}
        }
      );
    });
  });

  describe(types.USER_LOGOUT_DONE, () => {
    it('should return an empty object', () => {
      assert.deepEqual(
        users({
            user: {
              username: 'LoginName',
              password: 'secret',
            },
            auth: {access_token:"123",token_type: 'bearer'}
          },
          {
            type: types.USER_LOGOUT_DONE
          }),
        {}
      );
    });
  });
});
