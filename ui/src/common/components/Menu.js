import React, { Component } from "react";
import { Nav, NavItem, Glyphicon } from "react-bootstrap";
import { IndexLinkContainer, LinkContainer } from "react-router-bootstrap";

// Menu component
export default class Menu extends Component {
  // render
  render() {
    return (
      <Nav bsStyle="pills">
        <IndexLinkContainer to="/">
          <NavItem>
            Home
          </NavItem>
        </IndexLinkContainer>
        <LinkContainer to="/login">
          <NavItem>
            Login <Glyphicon glyph="plus-sign"/>
          </NavItem>
        </LinkContainer>
        <LinkContainer to="/logout">
          <NavItem>
            Logout <Glyphicon glyph="minus-sign"/>
          </NavItem>
        </LinkContainer>
      </Nav>
    );
  }
}
