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
    .directive('adsBubble', ['$window', '$compile',
      function ($window, $compile) {
        return {
          restrict: 'E',
          scope: {
            data: '='
          },
          link: function (scope, element) {
            var d3 = $window.d3;

            var diameter = 480,
              format = d3.format(',d');

            var color = d3.scale.category20c();

            var bubble = d3.layout.pack()
              .sort(null)
              .size([diameter, diameter])
              .padding(1.5);

            var svg = d3.select('ads-bubble')
              .append('div')
              .attr('class', 'bubble')
              .classed('svg-container', true) //container class to make it responsive
              .append('svg')
              .attr('preserveAspectRatio', 'xMinYMin meet')
              .attr('viewBox', '0 0 480 480')
              .classed('svg-content-responsive', true);

            var classes = function (root) {
              var classes = [];

              function recurse(name, node) {
                if (node.children) node.children.forEach(function (child) {
                  recurse(node.name, child);
                });
                else classes.push({packageName: name, className: node.name, value: node.size});
              }

              recurse(null, root);
              return {children: classes};
            };

            var insertFormattedName = function (d) {
              if (d.r >= 35) {
                var el = d3.select(this);
                var words = d.className.split(' ');
                el.text('');

                var axisYAdj = words.length > 1 ? (words.length - 1) * -5 : 0;

                for (var i = 0; i < words.length; i++) {
                  var tspan = el.append('tspan').text(words[i]);
                  tspan.attr('x', 0).attr('dy', i == 0 ? axisYAdj : 15);
                }
              }
            };

            scope.render = function (data) {
              var node = svg.selectAll('.node')
                .data(bubble.nodes(classes(data))
                  .filter(function (d) {
                    return !d.children;
                  }))
                .enter().append('g')
                .attr('class', 'node')
                .attr('transform', function (d) {
                  return 'translate(' + d.x + ',' + d.y + ')';
                }).attr('popover', function(d) {
                  return d.className + ': ' + format(d.value);
                }).attr('popover-append-to-body', true);

              node.append('title')
                .text(function (d) {
                  return d.className + ': ' + format(d.value);
                });

              node.append('circle')
                .attr('r', function (d) {
                  return d.r;
                })
                .style('fill', function (d) {
                  return color(d.className);
                });

              node.append('text')
                .attr('dy', '.3em')
                .style('text-anchor', 'middle');
            };

            scope.render(scope.data);
            svg.selectAll('g text').each(insertFormattedName);

            $compile(element.contents())(scope);
          }
        }
      }]);
}());
