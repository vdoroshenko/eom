import React,{ Component } from "react";
import { connect } from "react-redux";
import { push } from "react-router-redux";
import { Field, SubmissionError, reduxForm } from "redux-form";
import { PageHeader, Form } from "react-bootstrap";
import FormSubmit from "./FormSubmit";
import * as types from "../constants/actions/UserActionTypes";

// User add/edit page component
export class UserLogout extends Component {
  // constructor
  constructor(props) {
    super(props);

    // bind <this> to the event method
    this.formSubmit = this.formSubmit.bind(this);
  }

  // render
  render() {
    const {user, handleSubmit, error, invalid, submitting} = this.props;
    return (
      <div className="page-user-login">
        <PageHeader>User logout: {user.username}</PageHeader>
        <Form horizontal onSubmit={handleSubmit(this.formSubmit)}>
          <FormSubmit error={error} invalid={invalid} submitting={submitting} buttonSaveLoading="Submitting..."
            buttonSave="Logout"/>
        </Form>
      </div>
    );
  }

  // submit the form
  formSubmit(values) {
    const {dispatch} = this.props;
    const auth = this.props.auth;
    return new Promise((resolve, reject) => {
      dispatch({
        type: types.USER_LOGOUT,
        user: {
          username: values.username,
          password: values.password,
        },
        auth: auth,
        callbackError: (error) => {
          reject(new SubmissionError({_error: error}));
        },
        callbackSuccess: () => {
          dispatch(push('/'));
          resolve();
        }
      });
    });
  }
}

// decorate the form component
const UserLogoutForm = reduxForm({
  form: 'user_login',
  validate: function (values) {
    const errors = {};
    if (!values.username) {
      errors.username = 'Username is required';
    }
    return errors;
  },
})(UserLogout);

// export the connected class
function mapStateToProps(state, own_props) {
  const user = state.security.user;
  const auth = state.security.auth;
  return {
    user: user,
    auth: auth,
    initialValues: user
  };
}
export default connect(mapStateToProps)(UserLogoutForm);
