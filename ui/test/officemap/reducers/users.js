import assert from "assert";
import users from "../../../src/officemap/reducers/users";
import * as types from "../../../src/common/constants/actions/UserActionTypes";

// unit tests for the users reducers
// mocha - http://mochajs.org/#getting-started
// assert - https://nodejs.org/api/assert.html#assert_assert_deepequal_actual_expected_message
describe('Users reducer', () => {
  describe(types.USER_LOGIN_DONE, () => {
    it('should return a logged user', () => {
      assert.deepEqual(
        users({}, {
          type: types.USER_LOGIN_DONE,
          user: {
            username: 'LoginName',
            password: 'secret',
          },
        }), {
          user: {
            username: 'LoginName',
            password: 'secret',
          }
        }
      );
    });
  });

  describe(types.USER_CHECK_LOGIN_DONE, () => {
    it('should return a already logged user', () => {
      assert.deepEqual(
        users({}, {
          type: types.USER_CHECK_LOGIN_DONE,
          user: {
            username: 'LoginName',
            password: 'secret',
          },
        }), {
          user: {
            username: 'LoginName',
            password: 'secret',
          }
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
            }
          },
          {
            type: types.USER_LOGOUT_DONE
          }),
        {user: {}}
      );
    });
  });
});
