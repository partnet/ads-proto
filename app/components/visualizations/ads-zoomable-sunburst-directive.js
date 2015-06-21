/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

/**
 * @ngdoc function
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
            label: '@',
            onClick: '&'
          },
          link: function (scope) {
            var d3 = $window.d3;

            var width = 960,
              height = 700,
              radius = Math.min(width, height) / 2;

            var x = d3.scale.linear()
              .range([0, 2 * Math.PI]);

            var y = d3.scale.sqrt()
              .range([0, radius]);

            var color = d3.scale.category20c();

            var svg = d3.select("body").append("svg")
              .attr("width", width)
              .attr("height", height)
              .append("g")
              .attr("transform", "translate(" + width / 2 + "," + (height / 2 + 10) + ")");

            var partition = d3.layout.partition()
              .sort(null)
              .value(function(d) { return 1; });

            var arc = d3.svg.arc()
              .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
              .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
              .innerRadius(function(d) { return Math.max(0, y(d.y)); })
              .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

            $window.onresize = function () {
              scope.$apply();
            };

            var stash = function (d) {
              d.x0 = d.x;
              d.dx0 = d.dx;
            }

            var arcTweenZoom = function (d) {
              var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
                yd = d3.interpolate(y.domain(), [d.y, 1]),
                yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
              return function(d, i) {
                return i
                  ? function(t) { return arc(d); }
                  : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
              };
            };

            var node;

            scope.render = function (root) {
              node = root;
              var path = svg.datum(node).selectAll("path")
                .data(partition.nodes)
                .enter().append("path")
                .attr("d", arc)
                .style("fill", function(d) { return color((d.children ? d : d.parent).name); })
                .on("click", click)
                .each(stash);

              function click(d) {
                node = d;
                path.transition()
                  .duration(1000)
                  .attrTween("d", arcTweenZoom(d));
              }
            };

            scope.render(scope.data);
          }
        }
      }]);
}());
