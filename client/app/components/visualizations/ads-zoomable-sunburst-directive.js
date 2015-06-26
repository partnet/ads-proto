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
            label: '=',
            total: '='
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
                case 'Recovered/resolved':
                case 'Determined an unrelated reaction to this event':
                  return '#dff0d8';
                case 'Recovering/resolving':
                  return '#d9edf7';
                case 'Not recovered/not resolved':
                  return '#fcf8e3';
                case 'Fatal':
                  return '#f2dede';
                default:
                  return '#faf0e6';
              }
            };

            var svg = d3.select('ads-zoom-sunburst').append('svg')
              .attr('width', width)
              .attr('height', height)
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

            $window.onresize = function () {
              scope.$apply();
            };

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

            var node;

            var render = function (root) {
              node = root;

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

              scope.label = svg.select('g').text();

              function click(d) {
                node = d;
                path.transition()
                  .duration(1000)
                  .attrTween('d', arcTweenZoom(d));

                if (d.size && !d.children) {
                  scope.label = d.size + ' ' + d.name + ' reaction outcomes';
                } else if (d.parent) {
                  scope.label = d.children.length + ' reactions reported for ' + d.name;
                } else {
                  scope.label = d.name;
                }

                scope.$apply();
              }
            };

            render(scope.data);
          }
        }
      }]);
}());
