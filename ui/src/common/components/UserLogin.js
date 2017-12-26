import React from "react";
import { connect } from "react-redux";
import { push } from "react-router-redux";
import { Field, SubmissionError, reduxForm } from "redux-form";
import { PageHeader, Form } from "react-bootstrap";
import FormField from "./FormField";
import FormSubmit from "./FormSubmit";

// User add/edit page component
export class UserLogin extends React.Component {
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
        <PageHeader>User login</PageHeader>
        <Form horizontal onSubmit={handleSubmit(this.formSubmit)}>
          <Field component={FormField} name="username" label="Username" doValidate={true}/>
          <Field component={FormField} name="password" label="Password"/>
          <FormSubmit error={error} invalid={invalid} submitting={submitting} buttonSaveLoading="Submitting..."
            buttonSave="Login"/>
        </Form>
      </div>
    );
  }

  // submit the form
  formSubmit(values) {
    const {dispatch} = this.props;
    return new Promise((resolve, reject) => {
      dispatch({
        type: 'USER_LOGIN',
        user: {
          username: values.username,
          password: values.password,
        },
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
const UserLoginForm = reduxForm({
  form: 'user_login',
  validate: function (values) {
    const errors = {};
    if (!values.username) {
      errors.username = 'Username is required';
    }
    return errors;
  },
})(UserLogin);

// export the connected class
function mapStateToProps(state, own_props) {
  //const user = state.users.find(x => Number(x.id) === Number(own_props.params.id)) || {};
  const user = state.user;
  return {
    user: user,
    initialValues: user,
  };
}
export default connect(mapStateToProps)(UserLoginForm);