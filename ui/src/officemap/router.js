import React from "react";
import {IndexRoute, Route, Router} from "react-router";
import {history} from "./store.js";
import App from "./components/App";
import Home from "./components/Home";
import NotFound from "../common/components/NotFound";
import UserLogin from "../common/components/UserLogin"
import UserLogout from "../common/components/UserLogout"

// build the router
const router = (
  <Router onUpdate={() => window.scrollTo(0, 0)} history={history}>
    <Route path="/" component={App}>
      <IndexRoute component={Home}/>
      <Route path="/login" component={UserLogin}/>
      <Route path="/logout" component={UserLogout}/>
      <Route path="*" component={NotFound}/>
    </Route>
  </Router>
);

// export
export { router };
