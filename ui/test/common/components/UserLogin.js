import React from "react";
import { shallow } from "enzyme";
import assert from "assert";
import { UserLogin } from "../../../src/common/components/UserLogin";

// unit tests for the UserEdit component
describe('UserLogin component', () => {
  describe('render()', () => {
    it('should render the login user form', () => {
      const props = {user: {}, handleSubmit: ()=>{}};
      const wrapper = shallow(<UserLogin {...props} />);
      assert.equal(wrapper.length, 1);
    });
  });
});
