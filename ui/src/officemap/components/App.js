import React from "react";
import { connect } from "react-redux";
import { ProgressBar } from "react-bootstrap";
import Menu from "../../common/components/Menu";
import "../../common/stylesheets/common.scss";
import "../stylesheets/main.scss";

// app component
export class App extends React.Component {
  // pre-render logic
  componentWillMount() {
    // the first time we load the app, we need that check
    this.props.dispatch({type: 'CHECK_USER_LOGIN'});
  }
  // render
  render() {
    // show the loading state while we wait for the app to load
    const {user, children} = this.props;
    /*
    if (!user) {
      return (
        <ProgressBar active now={100}/>
      );
    }
    */
    // render
    return (
      <div className="container">
        <div>
          <Menu/>
        </div>
        <div>
          {children}
        </div>
        <div className="footer">
          <img src="/media/logo.svg"/>
          <span>
            TBD
          </span>
        </div>
      </div>
    );
  }
}

// export the connected class
function mapStateToProps(state) {
  return {
    users: state.users || [],
  };
}
export default connect(mapStateToProps)(App);
