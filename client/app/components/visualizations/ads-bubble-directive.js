/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

/**
 * @ngdoc overview
 * @author brandony-pn
 * @name components:ads-bubble-directive.js
 *
 * @description
 * # adsBubble
 * A directive for rendering an SVG image for a bubble chart using d3js.
 */
(function () {
  'use strict';
  angular.module('components')
    .directive('adsBubble', ['$window',
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

            var diameter = 960,
              format = d3.format(",d"),
              color = d3.scale.category20c();

            var bubble = d3.layout.pack()
              .sort(null)
              .size([diameter, diameter])
              .padding(1.5);

            var svg = d3.select("body").append("svg")
              .attr("width", diameter)
              .attr("height", diameter)
              .attr("class", "bubble");

            $window.onresize = function () {
              scope.$apply();
            };

            //scope.$watch(function () {
            //  return angular.element($window)[0].innerWidth;
            //}, function () {
            //  scope.render(scope.data);
            //});
            //
            //scope.$watch('data', function (newData) {
            //  scope.render(newData);
            //}, true);

            // Returns a flattened hierarchy containing all leaf nodes under the root.
            var classes = function (root) {
              var classes = [];

              function recurse(name, node) {
                if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
                else classes.push({packageName: name, className: node.name, value: node.size});
              }

              recurse(null, root);
              return {children: classes};
            };

            scope.render = function (data) {
              var node = svg.selectAll(".node")
                .data(bubble.nodes(classes(data))
                  .filter(function(d) { return !d.children; }))
                .enter().append("g")
                .attr("class", "node")
                .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

              node.append("title")
                .text(function(d) { return d.className + ": " + format(d.value); });

              node.append("circle")
                .attr("r", function(d) { return d.r; })
                .style("fill", function(d) { return color(d.packageName); });

              node.append("text")
                .attr("dy", ".3em")
                .style("text-anchor", "middle")
                .text(function(d) { return d.className.substring(0, d.r / 3); });
            };

            scope.render(scope.data);
          }
        }
      }]);
}());
