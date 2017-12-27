import React from "react";
import { shallow } from "enzyme";
import assert from "assert";
import UserLogoutPrompt from "../../../src/common/components/UserLogoutPrompt";

// unit tests for the UserDeletePrompt component
describe('UserLogoutPrompt component', () => {
  describe('render()', () => {
    it('should render the component', () => {
      const props = {show: true, user: {}, hideDelete: ()=>{}, userDelete: ()=>{}};
      const wrapper = shallow(<UserLogoutPrompt {...props}/>);
      assert.equal(wrapper.length, 1);
    });
  });
});
