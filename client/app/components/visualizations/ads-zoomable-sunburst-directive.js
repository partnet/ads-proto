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
                case 1:
                case 4:
                  return '#dff0d8';
                case 2:
                  return '#d9edf7';
                case 3:
                  return '#fcf8e3';
                case 5:
                  return '#f2dede';
                default:
                  return '#e6e6fa';
              }
            };

            var outcomeName = function (term) {
              switch (term) {
                case 1:
                  return 'Recovered';
                case 2:
                  return 'Recovering';
                case 3:
                  return 'Ongoing';
                case 4:
                  return 'Determined to be unrelated';
                case 5:
                  return 'Fatal';
                default:
                  return 'Unknown';
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

              scope.label = svg.select('g').text();

              function click(d) {
                node = d;
                path.transition()
                  .duration(1000)
                  .attrTween('d', arcTweenZoom(d));

                if (d.size && !d.children) {
                  scope.node = {
                    label: outcomeName(d.name),
                    size: d.size,
                    isLeaf: true,
                    isRoot: false
                  };
                } else if (d.parent) {
                  scope.node = {
                    label: d.name,
                    size: d.size,
                    isLeaf: false,
                    isRoot: false
                  };
                } else {
                  scope.node = {
                    label: d.name,
                    isLeaf: false,
                    isRoot: true
                  };
                }

                scope.$apply();
              }
            };

            render(scope.data);
          }
        }
      }]);
}());
