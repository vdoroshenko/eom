import { call, put } from "redux-saga/effects";
import assert from "assert";
import { checkLogin, userLogin, userLogout } from "../../../src/officemap/sagas/users";
import ApiUsers from "../../../src/common/api/users";

// unit tests for the users saga
/*
describe('Users saga', () => {

  describe('userLogin() - add', () => {
    const action = {
      user: {},
      callbackSuccess: () => {},
    };
    const generator = userLogin(action);

    it('should return the ApiUsers.addEdit call', () => {
      assert.deepEqual(generator.next().value, call(ApiUsers.addEdit));
    });

    it('should return the USERS_ADD_SAVE action', () => {
      assert.deepEqual(generator.next().value, put({
        type: 'USERS_ADD_SAVE',
        user: action.user,
      }));
    });

    it('should be finished', () => {
      assert.equal(generator.next().done, true);
    });
  });

  describe('checkLogin() - edit', () => {
    const action = {
      user: {id: 1},
      callbackSuccess: () => {},
    };
    const generator = checkLogin(action);

    it('should return the ApiUsers.addEdit call', () => {
      assert.deepEqual(generator.next().value, call(ApiUsers.addEdit));
    });

    it('should return the USERS_EDIT_SAVE action', () => {
      assert.deepEqual(generator.next().value, put({
        type: 'USERS_EDIT_SAVE',
        user: action.user,
      }));
    });

    it('should be finished', () => {
      assert.equal(generator.next().done, true);
    });
  });

  describe('userLogout()', () => {
    const action = {
      user_id: 1,
    };
    const generator = usersDelete(action);

    it('should return the ApiUsers.delete call', () => {
      assert.deepEqual(generator.next().value, call(ApiUsers.delete));
    });

    it('should return the USERS_DELETE_SAVE action', () => {
      assert.deepEqual(generator.next().value, put({
        type: 'USERS_DELETE_SAVE',
        user_id: action.user_id,
      }));
    });

    it('should be finished', () => {
      assert.equal(generator.next().done, true);
    });
  });
});
*/
