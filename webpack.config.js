/**
 * Para compilar el proyecto ejecute:
 *      
 *    npx webpack --mode=development
 * 
 *  Para sacar los archivos con optimizaciones ejecutar:
 * 
 *   npx webpack --mode=production
 **/

const Webpack = require("webpack");
const Path = require("path");
const TerserPlugin = require("terser-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const HardSourceWebpackPlugin = require("hard-source-webpack-plugin");
const FileManagerPlugin = require("filemanager-webpack-plugin");
const FixStyleOnlyEntriesPlugin = require("webpack-fix-style-only-entries");
const MomentLocalesPlugin = require('moment-locales-webpack-plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

const opts = {
    rootDir: process.cwd(),
};


module.exports = (env, args) => {
    return {
        // archivos de salida que Webpack debe producir 
        entry: {
            "app": "./src/frontend/js/app.js",
            "app.min": "./src/frontend/js/app.js",
            "settings": "./src/frontend/js/settings.js",
            "anda": "./src/frontend/css/app.scss"
        },
        // si no se indica el modo se asume el modo 'produccion'
        mode: args.mode === "production" ? "production" : "development",
        // si el modo es 'production' se ponen los archivos fuentes como un archivo separado, sino se agrega al archivo generado por webpack
        devtool:
            args.mode === "production" ? "source-map" : "inline-source-map",
        // definicion de donde deben guardarse los archivos generados por webpack
        output: {
            path: Path.join(opts.rootDir, "src/main/resources/static"),
            filename: "js/[name].js"
        },
        performance: {hints: false},
        optimization: {
            minimize: args.mode === "production" ? true : false,
            minimizer: [
                new TerserPlugin({
                    parallel: true,
                    terserOptions: {
                        ecma: 5
                    }
                }),
                new OptimizeCSSAssetsPlugin({}),
                new UglifyJsPlugin({
                  include: /\.min\.js$/
                })
            ]
        },
        plugins: [
            // Solo toma los archivos con contenido
            new FixStyleOnlyEntriesPlugin(),
            // Aplica la minificacion del css
            new MiniCssExtractPlugin({
                filename: "css/[name].css",
                chunkFilename: "css/[id].css"
            }),
            // importaciones globales
            new Webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                jquery: "jquery",
                "window.jQuery": "jquery",
                "window.$": "jquery",
                feather: "feather-icons",
                moment: "moment",
                toastr: 'toastr/toastr',
                dragula: "dragula",
                Chart: 'chart.js',
                ApexCharts: "apexcharts"
            }),
            // Copia fuentes e imagenes necesarias para las librerias a src/main/resources
            new CopyWebpackPlugin([
                {from: "src/frontend/fonts", to: "fonts"},
                {from: "src/frontend/json", to: "json"}
            ]),
            // Hace mas rapido el build
            new HardSourceWebpackPlugin(),
            // Copia archivos .docs
            new FileManagerPlugin({
                onEnd: {
                    copy: [{source: "./dist/**/*", destination: "./docs"}]
                }
            })
            // Solo mantiene las locales de Moment.js
            , new MomentLocalesPlugin({
                localesToKeep: ['es-us']
            })
        ],
        module: {
            rules: [
                // Css-loader & sass-loader
                {
                    test: /\.(sa|sc|c)ss$/,
                    use: [
                        MiniCssExtractPlugin.loader,
                        "css-loader",
                        "postcss-loader",
                        "sass-loader"
                    ]
                },
                // Load fonts
                {
                    test: /\.(woff(2)?|ttf|eot|svg)(\?v=\d+\.\d+\.\d+)?$/,
                    use: [
                        {
                            loader: "file-loader",
                            options: {
                                name: "/[name].[ext]",
                                outputPath: "fonts/",
                                publicPath: "../fonts/"
                            }
                        }
                    ]
                },
                // Load images
                {
                    test: /\.(png|jpg|jpeg|gif)(\?v=\d+\.\d+\.\d+)?$/,
                    use: [
                        {
                            loader: "file-loader",
                            options: {
                                name: "[name].[ext]",
                                outputPath: "img/",
                                publicPath: "../img/"
                            }
                        }
                    ]
                },
                // Expose loader
                {
                    test: require.resolve("jquery"),
                    use: [
                        {
                            loader: "expose-loader",
                            options: "jQuery"
                        },
                        {
                            loader: "expose-loader",
                            options: "$"
                        }
                    ]
                }
            ]
        },
        resolve: {
            extensions: [".js", ".scss"],
            modules: ["node_modules"],
            alias: {
                request$: "xhr"
            }
        }
    }
};
