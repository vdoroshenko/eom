var webpack = require("webpack");
var ExtractTextPlugin = require('extract-text-webpack-plugin');
module.exports = require('./webpack.config.js');    // inherit from the main config file

// disable the hot reload
module.exports.entry = [
  'babel-polyfill',
  __dirname + '/' + module.exports.app_root + '/officemap' + '/index.js'
];

// override process env to production
module.exports.plugins[0].process.env.NODE_ENV = JSON.stringify('production');
module.exports.plugins[0].process.env.API_AUTH_TOKEN = JSON.stringify(process.env.API_AUTH_TOKEN);
module.exports.plugins[0].process.env.API_URI = JSON.stringify(process.env.API_URI);


// compress the js file
module.exports.plugins.push(
  new webpack.optimize.UglifyJsPlugin({
    comments: false,
    compressor: {
      warnings: false
    }
  })
);

// export css to a separate file
module.exports.module.loaders[1] = {
  test: /\.scss$/,
  loader: ExtractTextPlugin.extract('css!sass'),
};

module.exports.plugins.push(
  new ExtractTextPlugin('../css/main.css')
);

