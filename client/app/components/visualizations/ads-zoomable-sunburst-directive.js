/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

/**
 * @ngdoc overview
 * @author brandony-pn
 * @name components:ads-zoomable-sunburst-directive.js
 *
 * @description
 * # adsZoomSunburst
 * A directive for rendering an SVG image for a zoomable sunburst graph using d3js.
 */
(function () {
  'use strict';
  angular.module('components')
    .directive('adsZoomSunburst', ['$window', '$timeout',
      function ($window) {
        return {
          restrict: 'E',
          scope: {
            data: '=',
            node: '='
          },
          link: function (scope) {
            var d3 = $window.d3;

            var width = 640,
              height = 467,
              radius = Math.min(width, height) / 2;

            var x = d3.scale.linear()
              .range([0, 2 * Math.PI]);

            var y = d3.scale.sqrt()
              .range([0, radius]);

            var nodeColor = d3.scale.category20c();
            var leafColor = function (name) {
              switch (name) {
                case 'Recovered':
                case 'Determined to be unrelated':
                  return '#dff0d8';
                case 'Recovering':
                  return '#d9edf7';
                case 'Ongoing':
                  return '#fcf8e3';
                case 'Fatal':
                  return '#f2dede';
                default:
                  return '#e6e6fa';
              }
            };

            var svg = d3.select('ads-zoom-sunburst')
              .append('div')
              .classed('svg-container', true) //container class to make it responsive
              .append('svg')
              .attr('preserveAspectRatio', 'xMinYMin meet')
              .attr('viewBox', '0 0 640 467')
              .classed('svg-content-responsive', true)
              .append('g')
              .attr('transform', 'translate(' + width / 2 + ',' + (height / 2) + ')');

            var partition = d3.layout.partition()
              .sort(null)
              .value(function (d) {
                return 1;
              });

            var arc = d3.svg.arc()
              .startAngle(function (d) {
                return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
              })
              .endAngle(function (d) {
                return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
              })
              .innerRadius(function (d) {
                return Math.max(0, y(d.y));
              })
              .outerRadius(function (d) {
                return Math.max(0, y(d.y + d.dy));
              });

            var arcTweenZoom = function (d) {
              var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
                yd = d3.interpolate(y.domain(), [d.y, 1]),
                yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
              return function (d, i) {
                return i
                  ? function (t) {
                  return arc(d);
                }
                  : function (t) {
                  x.domain(xd(t));
                  y.domain(yd(t)).range(yr(t));
                  return arc(d);
                };
              };
            };

            var addLabel = function (label) {
              var node;
              var isChild;
              var className = 'ads-svg-labeled';
              if (angular.isDefined(label)) {
                node = svg.selectAll('g').filter(function (d) {
                  if( d.name === label){
                    if(!d.children) {
                      className = 'ads-svg-labeled-leaf';
                    }
                    return true;
                  }
                });
                isChild = true;
              } else {
                node = svg.select('g');
                isChild = false;
              }

              var text = node.append('text')
                .classed(className, true)
                .text(function (d) {
                  return d.name;
                });

              if(isChild){
                text.attr('dy', '-3em');
              }
            };

            var removeLabels = function () {
              svg.selectAll('g').select('text').remove();
            }

            var render = function (root) {
              scope.node = {
                label: root.name,
                isLeaf: false,
                isRoot: true
              };

              var g = svg.selectAll('g')
                .data(partition.value(function (d) {
                  return d.size
                }).nodes(root))
                .enter().append('g');

              var path = g.append('path')
                .attr('d', arc)
                .style('fill', function (d) {
                  if (d.children) {
                    return nodeColor(d.name);
                  } else {
                    return leafColor(d.name);
                  }
                })
                .on('click', click);

              g.append('svg:title').text(function (d) {
                return d.name;
              });

              addLabel();

              function click(d) {
                path.transition()
                  .duration(1000)
                  .attrTween('d', arcTweenZoom(d));

                if (d.size && !d.children) {
                  scope.node = {
                    label: d.name,
                    size: d.size,
                    isLeaf: true,
                    isRoot: false
                  };
                  removeLabels();
                  addLabel(d.name);
                } else if (d.parent) {
                  scope.node = {
                    label: d.name,
                    size: d.size,
                    isLeaf: false,
                    isRoot: false
                  };
                  removeLabels();
                  addLabel(d.name);
                } else {
                  scope.node = {
                    label: d.name,
                    isLeaf: false,
                    isRoot: true
                  };
                  removeLabels();
                  addLabel();
                }

                scope.$apply();
              }
            };

            render(scope.data);
          }
        }
      }]);
}());
