import React from "react";
import { ProgressBar } from "react-bootstrap";
import "../stylesheets/main.scss";

// app component
export default class App extends React.Component {
  // pre-render logic
  componentWillMount() {
    // the first time we load the app, we need that users list
    //this.props.dispatch({type: 'USERS_FETCH_LIST'});
  }
  // render
  render() {
    return (
      <div className="container">
        {this.props.children}
      </div>
    );
  }
}
