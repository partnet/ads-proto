#! /usr/bin/env bash

deployments=~/wildfly/current/standalone/deployments

if [ ! -L "$deployments" ]; then
  mv $deployments $deployments.sv
  ln -s /vagrant/wildfly-deployments $deployments
fi
