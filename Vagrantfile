# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
   # All Vagrant configuration is done here. The most common configuration
   # options are documented and commented below. For a complete reference,
   # please see the online documentation at vagrantup.com.

   # The url from where the 'config.vm.box' box will be fetched if it
   # doesn't already exist on the user's system. The following URL is
   # relying on artifactory's REST API to pull the latest released
   # version of the artifact.

   box_url = "https://artifactory.part.net/artifactory/simple/software-installations/vagrant-boxes/centos-6.6-base-vbox/%5BRELEASE%5D/centos-6.6-base-vbox-%5BRELEASE%5D.box"

   config.vm.define "ads-server-p1" do |vagrant|
      vagrant.vm.box = "centos-6.6-base"
      vagrant.vm.box_url = box_url

      # Disable certificate verification
      vagrant.vm.box_download_insecure = true

      vagrant.vm.hostname = "ads-server"
      vagrant.vm.network "forwarded_port", guest: 8080, host: 8880  # wildfly/jboss
      vagrant.vm.network "forwarded_port", guest: 8443, host: 8443  # wildfly/jboss SSL (not currently in use)
      vagrant.vm.network "forwarded_port", guest: 9990, host: 9990  # wildfly/jboss web management interface
      vagrant.vm.network "private_network", ip: "192.168.7.2"

      vagrant.vm.synced_folder ".", "/vagrant", disabled: true
      vagrant.vm.synced_folder "server/build/libs", "/vagrant/wildfly-deployments", mount_options: ["dmode=777","fmode=666"]

      vagrant.vm.provider "virtualbox" do |vb|
         vb.customize [
            "modifyvm", :id,
            "--name", "ads-server",
            "--memory", "1024",
            "--ostype", "RedHat_64",
            "--usb", "off",
            "--usbehci", "off"
         ]
      end

      vagrant.vm.provision "ansible" do |ansible|
         ansible.inventory_path = 'ansible/inventory/dev-virt.ini'
         ansible.playbook = 'ansible/playbooks/site.yml'
         ansible.verbose = 'vvvv'
      end

   end

end
