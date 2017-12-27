import React, { Component, PropTypes } from "react";
import { Modal, Button } from "react-bootstrap";

// User delete component
export default class UserLogoutPrompt extends Component {
  // render
  render() {
    const {show, user, hideLogout, userLogout} = this.props;
    return (
      <Modal show={show}>
        <Modal.Header>
          <Modal.Title>
            Are you sure you want to logout <strong>{user.username}</strong>?
          </Modal.Title>
        </Modal.Header>
        <Modal.Footer>
          <Button onClick={hideLogout}>No</Button>
          <Button bsStyle="primary" onClick={userLogout}>Yes</Button>
        </Modal.Footer>
      </Modal>
    );
  }
}

// prop checks
UserLogoutPrompt.propTypes = {
  show: PropTypes.bool.isRequired,
  user: PropTypes.object.isRequired,
  hideLogout: PropTypes.func.isRequired,
  userLogout: PropTypes.func.isRequired,
}
